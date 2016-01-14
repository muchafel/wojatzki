package annotators.stacking;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.core.ml.ModelSerialization_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.core.ml.TCMachineLearningAdapter;
import de.tudarmstadt.ukp.dkpro.tc.fstore.simple.DenseFeatureStore;
import de.tudarmstadt.ukp.dkpro.tc.ml.modelpersist.ModelPersistUtil;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorUtil;
import types.BiTriGramOutcomeStanceNone;
import types.ClassifiedConceptOutcome;
import util.SimilarityHelper;
import util.concepts.ConceptUtils;

public class ClassifiedConceptOutcomeStackingAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_TC_MODEL_LOCATIONS = "ConceptClassificationModel";
	@ConfigurationParameter(name = PARAM_TC_MODEL_LOCATIONS, mandatory = true)
	protected File tcModelLocations;

	public static final String PARAM_TC_READER = "ConceptClassificationTCReader";
	@ConfigurationParameter(name = PARAM_TC_READER, mandatory = true, defaultValue = "true")
	protected boolean useTcReader;

	public static final String PARAM_CONCEPT_TARGET = "ConceptClassificationTarget";
	@ConfigurationParameter(name = PARAM_CONCEPT_TARGET, mandatory = true)
	protected String target;

	private String learningMode = Constants.LM_SINGLE_LABEL;
	private String featureMode = Constants.FM_DOCUMENT;

	// private List<FeatureExtractorResource_ImplBase> featureExtractors;

	private TCMachineLearningAdapter mlAdapter;
	private Map<String, AnalysisEngine> conceptToModel;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		conceptToModel = new HashMap<String, AnalysisEngine>();
		// System.out.println(tcModelLocations);
		for (File conceptFile : tcModelLocations.listFiles()) {
			try {
				mlAdapter = ModelPersistUtil.initMachineLearningAdapter(
						new File(tcModelLocations.getAbsolutePath() + "/" + conceptFile.getName()));
				List<Object> parameters = ModelPersistUtil
						.initParameters(new File(tcModelLocations.getAbsolutePath() + "/" + conceptFile.getName()));
				List<String> featureExtractors = ModelPersistUtil.initFeatureExtractors(
						new File(tcModelLocations.getAbsolutePath() + "/" + conceptFile.getName()));

				AnalysisEngineDescription connector = getSaveModelConnector(parameters,
						tcModelLocations.getAbsolutePath() + "/" + conceptFile.getName(),
						mlAdapter.getDataWriterClass().toString(), learningMode, featureMode,
						DenseFeatureStore.class.getName(), featureExtractors.toArray(new String[0]));

				AnalysisEngine engine = UIMAFramework.produceAnalysisEngine(connector,
						TcAnnotatorUtil.getModelFeatureAwareResourceManager(tcModelLocations), null);
				conceptToModel.put(conceptFile.getName(), engine);

			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		// annotate bipolar concepts
		for (String concept : conceptToModel.keySet()) {
			if (conceptContained(jcas, concept)) {
				annotateConceptWithPrediction(jcas, concept);
			} else {
				annotateBlankConcept(jcas, concept, "-", true);
			}
		}
		// annotate polar concepts
		for (String concept : ConceptUtils.getStrictlyPolarConcepts(target)) {
			if (conceptContained(jcas, concept)) {
				annotateBlankConcept(jcas, concept, ConceptUtils.getStrictlyPolarConceptPolarity(concept), false);
			} else {
				annotateBlankConcept(jcas, concept, "-", false);
			}
		}
	}

	/**
	 * in case the concept is not contained in the cas the value is set to '-'
	 * 
	 * @param jcas
	 * @param concept
	 */
	private void annotateBlankConcept(JCas jcas, String concept, String value, boolean biPolar) {
		ClassifiedConceptOutcome annotation = new ClassifiedConceptOutcome(jcas);
		annotation.setClassificationOutcome(value);
		annotation.setConceptName(concept);
		annotation.setBiPolar(biPolar);
		annotation.setBegin(0);
		annotation.setEnd(jcas.getDocumentText().length());
		annotation.addToIndexes();
	}

	/**
	 * annotate the concept by using the concept annotation(name,
	 * classification)
	 * 
	 * @param jcas
	 * @param concept
	 */
	private void annotateConceptWithPrediction(JCas jcas, String concept) throws AnalysisEngineProcessException {
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
			conceptToModel.get(concept).process(jcas);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		// System.out.println("classified "+JCasUtil.selectSingle(jcas,
		// TextClassificationOutcome.class).getOutcome());
		// annotate
		ClassifiedConceptOutcome annotation = new ClassifiedConceptOutcome(jcas);
		// System.out.println(jcas.getDocumentText()+ "
		// "+JCasUtil.selectSingle(jcas,
		// TextClassificationOutcome.class).getOutcome());
		annotation.setClassificationOutcome(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
		annotation.setConceptName(concept);
		annotation.setBiPolar(true);
		annotation.setBegin(0);
		annotation.setEnd(jcas.getDocumentText().length());
		annotation.addToIndexes();

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

	private boolean conceptContained(JCas jcas, String concept) {
		// check if one noun equals the concept
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if (t.getPos().getPosValue().equals("NN") || t.getPos().getPosValue().equals("NNS")
					|| t.getPos().getPosValue().equals("NP") || t.getPos().getPosValue().equals("NPS")
					|| t.getPos().getPosValue().equals("NPS")) {
				if (t.getCoveredText().equals(concept)
						|| SimilarityHelper.wordsAreSimilar(t.getCoveredText(), concept)) {
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
