package assembly;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import annotators.MergedArktweetTokenizer;
import annotators.TwitterSpecificAnnotator;
import annotators.taskBAnnotators.FavorAgainstOutcomeAnnotator_TASKB;
import annotators.taskBAnnotators.NoneStanceAnnotator_TASKB;
import annotators.taskBAnnotators.RemovePreprocessingAnnotator_TASKB;
import annotators.taskBAnnotators.StanceResolver_TASKB;
import dataInspection.OutcomeInspection;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorDocument;
import io.StanceResultWriter;
import types.TaskBStanceAnnotation;
import util.PreprocessingPipeline;

public class PipelineTaskB {

	public static void main(String[] args) throws FileNotFoundException, IOException, ResourceInitializationException, AnalysisEngineProcessException {
		ArrayList<String> targets = new ArrayList<String>(Arrays.asList("Atheism", "FeministMovement",
				"ClimateChangeisaRealConcern", "HillaryClinton", "LegalizationofAbortion"));
		// Map<String,List<String>> targetToTopINouns=readTargetWiseTopINouns();
//		List<String> topiTrumpNouns = getTop60Nouns(new File("src/main/resources/top60Nouns_taskB/DonaldTrump"));
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
//		String tweetsToClassify = baseDir + "/semevalTask6/testTweetsTaskB/";
		String tweetsToClassify = "/Users/michael/ArgumentMiningCoprora/semEval2016/SemEval2016-Task6-testdata/xmls/tweets/taskB/";
		
		PipelineTaskB pipelineTaskB = new PipelineTaskB();
		pipelineTaskB.run(targets, tweetsToClassify);
	}

	private void run(List<String> targets, String path)
			throws ResourceInitializationException, AnalysisEngineProcessException {

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				UnclassifiedTweetReader.class, UnclassifiedTweetReader.PARAM_SOURCE_LOCATION, path,
				UnclassifiedTweetReader.PARAM_PATTERNS, "*.xml", UnclassifiedTweetReader.PARAM_LANGUAGE, "en");
		AnalysisEngine trumpEngine = getTrumpEngine();
		AnalysisEngine resolvingEngine=getResolvingEngine("DonaldTrump");
		Map<String,AnalysisEngine> targetToEngine= getTargetEngines(targets);
		System.out.println("all engines built");
		for (JCas jcas : new JCasIterable(reader)) {
				// preprocessing
				// annotate trump Stance vs None
				// remove all Annos except for Stance_taskB anno
				trumpEngine.process(jcas);
				
//				System.out.println(jcas.getDocumentText()+ " "+JCasUtil.selectSingle(jcas, TaskBStanceAnnotation.class).getStance());
			for (String target : targets) {
				// preprocessing(target)
//				AnalysisEngine targetEngine = getTargetEngine(target);
//				targetEngine.process(jcas);
				targetToEngine.get(target).process(jcas);
				// annotate target Stance vs None
				// annotate Favor vs Against for Stance by using saved Model
				// remove all Annos except for Stance_taskB anno
			}
//			System.out.println(jcas.getDocumentText());
//			for(TaskBStanceAnnotation anno: JCasUtil.select(jcas, TaskBStanceAnnotation.class)){
//				System.out.println(anno.getTarget()+" "+anno.getStance());
//			}
			// resolve all annos
			resolvingEngine.process(jcas);
		}
	}

	private AnalysisEngine getResolvingEngine(String target) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(StanceResolver_TASKB.class),
					createEngineDescription(StanceResultWriter.class, StanceResultWriter.PARAM_RESULT_OUTPUT_TARGET,target)
						)
					);
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

	private Map<String, AnalysisEngine> getTargetEngines(List<String> targets) {
		Map<String, AnalysisEngine> result= new HashMap<String, AnalysisEngine>();
		for (String target : targets) {
			result.put(target, getTargetEngine(target));
		}
		return result;
	}

	private AnalysisEngine getTargetEngine(String target) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(PreprocessingPipeline.getFullPreProcessing(target, false)),
					createEngineDescription(NoneStanceAnnotator_TASKB.class, NoneStanceAnnotator_TASKB.PARAM_TOPI_NOUNS,"src/main/resources/top60Nouns/"+target, NoneStanceAnnotator_TASKB.PARAM_STANCE_TARGET,target),
					createEngineDescription(FavorAgainstOutcomeAnnotator_TASKB.class,FavorAgainstOutcomeAnnotator_TASKB.PARAM_TC_MODEL_LOCATION, "src/main/resources/trainedModels/favorVsAgainst/"+target, FavorAgainstOutcomeAnnotator_TASKB.PARAM_TARGET, target),
					createEngineDescription(RemovePreprocessingAnnotator_TASKB.class)
						)
					);
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

	private AnalysisEngine getTrumpEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
					createEngineDescription(MergedArktweetTokenizer.class),
					createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default"),
					createEngineDescription(TwitterSpecificAnnotator.class),
					createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_PRINT_TAGSET, true),
					createEngineDescription(NoneStanceAnnotator_TASKB.class, NoneStanceAnnotator_TASKB.PARAM_TOPI_NOUNS,"src/main/resources/top60Nouns_taskB/DonaldTrump", NoneStanceAnnotator_TASKB.PARAM_STANCE_TARGET,"DonaldTrump"),
					createEngineDescription(RemovePreprocessingAnnotator_TASKB.class)
						)
					);
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

	private static Map<String, List<String>> readTargetWiseTopINouns() throws FileNotFoundException, IOException {
		Map<String, List<String>> targetToTopINouns = new HashMap<String, List<String>>();
		File folder = new File("src/main/resources/top60Nouns");
		for (File target : folder.listFiles()) {
			targetToTopINouns.put(target.getName(), getTop60Nouns(target));
		}
		return targetToTopINouns;
	}

	private static List<String> getTop60Nouns(File target) throws FileNotFoundException, IOException {
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
}
