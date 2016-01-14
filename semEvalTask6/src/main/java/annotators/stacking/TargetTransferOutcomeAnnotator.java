package annotators.stacking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.core.ml.ModelSerialization_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.core.ml.TCMachineLearningAdapter;
import de.tudarmstadt.ukp.dkpro.tc.fstore.simple.DenseFeatureStore;
import de.tudarmstadt.ukp.dkpro.tc.ml.modelpersist.ModelPersistUtil;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorUtil;
import types.ClassifiedConceptOutcome;
import types.TransferClassificationOutcome;

public class TargetTransferOutcomeAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_TRANSFER_TARGET = "TransferClassificationTarget";
	@ConfigurationParameter(name = PARAM_TRANSFER_TARGET, mandatory = true)
	protected String target;

	public static final String PARAM_TC_READER = "TransferClassificationTCReader";
	@ConfigurationParameter(name = PARAM_TC_READER, mandatory = true, defaultValue = "true")
	protected boolean useTcReader;

	private Map<String, AnalysisEngine> targetToModel_FavorAgainst;
	private Map<String, List<String>> targetToTopINouns;
	private String learningMode = Constants.LM_SINGLE_LABEL;
	private String featureMode = Constants.FM_DOCUMENT;
	private TCMachineLearningAdapter mlAdapter;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			targetToTopINouns = readTargetWiseTopINouns();
			// System.out.println("Top 60 Nouns for Targets:");
			// for(String key:
			// targetToTopINouns.keySet())System.out.println(key+"
			// "+targetToTopINouns.get(key));
		} catch (IOException e) {
			e.printStackTrace();
		}

		targetToModel_FavorAgainst = new HashMap<String, AnalysisEngine>();
		// System.out.println(tcModelLocations);
		for (File modelFile : getModelsWithoutTarget(target)) {
			try {
				mlAdapter = ModelPersistUtil.initMachineLearningAdapter(modelFile);
				List<Object> parameters = ModelPersistUtil.initParameters(modelFile);
				List<String> featureExtractors = ModelPersistUtil.initFeatureExtractors(modelFile);

				AnalysisEngineDescription connector = getSaveModelConnector(parameters, modelFile.getAbsolutePath(),
						mlAdapter.getDataWriterClass().toString(), learningMode, featureMode,
						DenseFeatureStore.class.getName(), featureExtractors.toArray(new String[0]));

				AnalysisEngine engine = UIMAFramework.produceAnalysisEngine(connector,
						TcAnnotatorUtil.getModelFeatureAwareResourceManager(modelFile), null);
				targetToModel_FavorAgainst.put(modelFile.getName(), engine);

			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}
	}

	private List<File> getModelsWithoutTarget(String target) {
		List<File> result = new ArrayList<File>();
		File modelFolder = new File("src/main/resources/trainedModels/favorVsAgainst_wo_transfer");
		for (File file : modelFolder.listFiles()) {
			if (!file.getName().equals(target))
				result.add(file);
		}
		return result;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		for (String target : targetToModel_FavorAgainst.keySet()) {

			if (jcasContainsTopINoun(jcas, target)) {
				System.out.println(target);

			}
		}
	}

	/**
	 * annotate the jcas using the concept the model give a target
	 * 
	 * @param jcas
	 * @param target
	 */
	private void annotateConceptWithPrediction(JCas jcas, String target) throws AnalysisEngineProcessException {
		// System.out.println("gold "+JCasUtil.selectSingle(jcas,
		// TextClassificationOutcome.class).getOutcome());
		String goldOutcome = "";
		if (useTcReader) {
			goldOutcome = JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome();
			JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).removeFromIndexes();
			TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
			outcome.setOutcome("");
			outcome.addToIndexes();
		} else {
			if (JCasUtil.select(jcas, TextClassificationOutcome.class).size() == 0) {
				TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
				outcome.setOutcome("");
				outcome.addToIndexes();
			}
		}
		// System.out.println("'' "+JCasUtil.selectSingle(jcas,
		// TextClassificationOutcome.class).getOutcome());
		// create new UIMA annotator in order to separate the parameter spaces
		// this annotator will get initialized with its own set of parameters
		// loaded from the model
		try {
			targetToModel_FavorAgainst.get(target).process(jcas);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
//		 annotate
		TransferClassificationOutcome annotation = new TransferClassificationOutcome(jcas);
		
		annotation.setOutcome(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
		annotation.setModel(target);
		annotation.setBegin(0);
		annotation.setEnd(jcas.getDocumentText().length());
		annotation.addToIndexes();

		System.out.println(jcas.getDocumentText()+ " "+JCasUtil.selectSingle(jcas, TransferClassificationOutcome.class).getOutcome()+ " "+JCasUtil.selectSingle(jcas, TransferClassificationOutcome.class).getModel());
		
		// drop outcome from stacked classification
		if (useTcReader) {
			JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).removeFromIndexes();
			TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
			// in case were running tc use the gold outcome
			outcome.setOutcome(goldOutcome);
			outcome.addToIndexes();
		} else {
			JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).removeFromIndexes();
		}
		// System.out.println("again gold "+JCasUtil.selectSingle(jcas,
		// TextClassificationOutcome.class).getOutcome());
		// System.out.println("----------");
	}

	private Map<String, List<String>> readTargetWiseTopINouns() throws FileNotFoundException, IOException {
		Map<String, List<String>> targetToTopINouns = new HashMap<String, List<String>>();
		File folder = new File("src/main/resources/top60Nouns");
		for (File target : folder.listFiles()) {
			targetToTopINouns.put(target.getName(), getTop60Nouns(target));
		}
		return targetToTopINouns;
	}

	private List<String> getTop60Nouns(File target) throws FileNotFoundException, IOException {
		List<String> top60Nouns = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(target.getAbsolutePath()))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				for (String noun : line.split(","))
					top60Nouns.add(noun.replace(" ", ""));
			}
		}
		return top60Nouns;
	}

	private boolean jcasContainsTopINoun(JCas jcas, String target) {
		List<String> topINouns = targetToTopINouns.get(target);
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if (t.getPos().getPosValue().equals("NN") || t.getPos().getPosValue().equals("NNS")
					|| t.getPos().getPosValue().equals("NP") || t.getPos().getPosValue().equals("NPS")
					|| t.getPos().getPosValue().equals("NPS")) {
				if (topINouns.contains(t.getCoveredText().toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param featureExtractorClassNames
	 * @return A fully configured feature extractor connector
	 * @throws ResourceInitializationException
	 */
	private AnalysisEngineDescription getSaveModelConnector(List<Object> parameters, String outputPath,
			String dataWriter, String learningMode, String featureMode, String featureStore,
			String... featureExtractorClassNames) throws ResourceInitializationException {
		// convert parameters to string as external resources only take string
		// parameters
		List<Object> convertedParameters = TcAnnotatorUtil.convertParameters(parameters);

		List<ExternalResourceDescription> extractorResources = TcAnnotatorUtil
				.loadExternalResourceDescriptionOfFeatures(outputPath, featureExtractorClassNames, convertedParameters);

		// add the rest of the necessary parameters with the correct types
		parameters.addAll(Arrays.asList(TcAnnotatorDocument.PARAM_TC_MODEL_LOCATION, outputPath,
				ModelSerialization_ImplBase.PARAM_OUTPUT_DIRECTORY, outputPath,
				ModelSerialization_ImplBase.PARAM_DATA_WRITER_CLASS, dataWriter,
				ModelSerialization_ImplBase.PARAM_LEARNING_MODE, learningMode,
				ModelSerialization_ImplBase.PARAM_FEATURE_EXTRACTORS, extractorResources,
				ModelSerialization_ImplBase.PARAM_FEATURE_FILTERS, null, ModelSerialization_ImplBase.PARAM_IS_TESTING,
				true, ModelSerialization_ImplBase.PARAM_FEATURE_MODE, featureMode,
				ModelSerialization_ImplBase.PARAM_FEATURE_STORE_CLASS, featureStore));

		return AnalysisEngineFactory.createEngineDescription(mlAdapter.getLoadModelConnectorClass(),
				parameters.toArray());
	}

}
