package annotators.stacking;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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

import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.core.ml.ModelSerialization_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.core.ml.TCMachineLearningAdapter;
import de.tudarmstadt.ukp.dkpro.tc.fstore.simple.DenseFeatureStore;
import de.tudarmstadt.ukp.dkpro.tc.ml.modelpersist.ModelPersistUtil;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorUtil;
import types.BiTriGramOutcomeFavorAgainst;

public class Bi_Tri_Gram_FavorAgainstStackingAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_TC_MODEL_LOCATION = "Bi_Tri_Gram_FavAgainstModel";
	@ConfigurationParameter(name = PARAM_TC_MODEL_LOCATION, mandatory = true)
	protected File tcModelLocation;

	public static final String PARAM_TC_READER = "BiGramStackingFavAgainstWithTCReader";
	@ConfigurationParameter(name = PARAM_TC_READER, mandatory = true, defaultValue = "true")
	protected boolean useTcReader;

	private String learningMode = Constants.LM_SINGLE_LABEL;
	private String featureMode = Constants.FM_DOCUMENT;

	// private List<FeatureExtractorResource_ImplBase> featureExtractors;
	private List<String> featureExtractors;
	private List<Object> parameters;

	private TCMachineLearningAdapter mlAdapter;

	private AnalysisEngine engine;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			mlAdapter = ModelPersistUtil.initMachineLearningAdapter(tcModelLocation);
			parameters = ModelPersistUtil.initParameters(tcModelLocation);
			featureExtractors = ModelPersistUtil.initFeatureExtractors(tcModelLocation);

			AnalysisEngineDescription connector = getSaveModelConnector(parameters, tcModelLocation.getAbsolutePath(),
					mlAdapter.getDataWriterClass().toString(), learningMode, featureMode,
					DenseFeatureStore.class.getName(), featureExtractors.toArray(new String[0]));

			engine = UIMAFramework.produceAnalysisEngine(connector,
					TcAnnotatorUtil.getModelFeatureAwareResourceManager(tcModelLocation), null);

		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
//		System.out.println("gold "+JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
		String goldOutcome = "";
		if (useTcReader) {
			goldOutcome = JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome();
			JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).removeFromIndexes();
			TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
			outcome.setOutcome("");
			outcome.addToIndexes();
		} else {
			if (JCasUtil.select(jcas, TextClassificationOutcome.class).size()==0) {
				TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
				outcome.setOutcome("");
				outcome.addToIndexes();
			}
		}
//		System.out.println("'' "+JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
		// create new UIMA annotator in order to separate the parameter spaces
		// this annotator will get initialized with its own set of parameters
		// loaded from the model
		try {
			engine.process(jcas);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
//		System.out.println("classified "+JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
		// annotate
		BiTriGramOutcomeFavorAgainst annotation = new BiTriGramOutcomeFavorAgainst(jcas);
		// System.out.println(jcas.getDocumentText()+ "
		// "+JCasUtil.selectSingle(jcas,
		// TextClassificationOutcome.class).getOutcome());
		annotation.setClassificationOutcome(JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
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
//		System.out.println("again gold "+JCasUtil.selectSingle(jcas, TextClassificationOutcome.class).getOutcome());
//		System.out.println("----------");
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
		parameters.addAll(Arrays.asList(TcAnnotatorDocument.PARAM_TC_MODEL_LOCATION, tcModelLocation,
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
