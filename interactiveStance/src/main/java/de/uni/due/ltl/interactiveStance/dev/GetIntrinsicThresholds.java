package de.uni.due.ltl.interactiveStance.dev;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;


import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer;
import de.uni.due.ltl.interactiveStance.analyzer.StanceLexicon;
import de.uni.due.ltl.interactiveStance.analyzer.TargetSearcher;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataSet;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;
import de.uni.due.ltl.interactiveStance.util.EvaluationUtil;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;
import opennlp.tools.util.eval.FMeasure;

public class GetIntrinsicThresholds {
	
	private static ArrayList<String> targets = new ArrayList<String>(
		    Arrays.asList(
		    		"Atheism"
		    		,
		    		"ClimateChangeisaRealConcern"
		    		,
		    		"FeministMovement"
		    		,
		    		"HillaryClinton"
		    		,
		    		"LegalizationofAbortion"
		    		));
	
	public static void main(String[] args) throws Exception {
		for(String target: targets){
			// set up DB and selection
			StanceDB db = new StanceDB("root", "", "jdbc:mysql://localhost/interactiveArgumentMining");
			HashMap<String, ExplicitTarget> selectedTargetsFavor = new HashMap<>();
			HashMap<String, ExplicitTarget> selectedTargetsAgainst = new HashMap<>();

			// load evaluation Data
			EvaluationScenario data = new EvaluationScenario(target);

			FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
			FrequencyDistribution<String> against = new FrequencyDistribution<String>();
			AnalysisEngine engine= getTokenizerEngine();
			
			for (JCas jcas : new JCasIterable(data.getTrainData().getDataReader())) {
				engine.process(jcas);
				if(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance().equals("FAVOR")){
					for(Token t: JCasUtil.select(jcas, Token.class)){
						favor.inc(t.getCoveredText().toLowerCase());
					}
				}
				else if(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance().equals("AGAINST")){
					for(Token t: JCasUtil.select(jcas, Token.class)){
						against.inc(t.getCoveredText().toLowerCase());
					}
				}
				else{
					// add to none distribution
				}
			} 
			
			
			
			// set up analyzer
			CollocationNgramAnalyzer analyzer = new CollocationNgramAnalyzer(db,data);
			StanceLexicon lexicon= analyzer.createLexiconFromDistributions(favor, against);
//			EvaluationDataSet dataSet =data.getTrainData();
			EvaluationDataSet dataSet =data.getTestData();

			Fscore<String> fmeasure= analyzer.evaluateUsingLexicon(lexicon, dataSet);
			System.out.println(target+" : semeval "+EvaluationUtil.getSemEvalMeasure(fmeasure)+" micro "+fmeasure.getMicroFscore()+" against "+fmeasure.getScoreForLabel("AGAINST")+" favor "+fmeasure.getScoreForLabel("FAVOR")+" none "+fmeasure.getScoreForLabel("NONE"));
//			Fscore<String> fmeasure2= analyzer.evaluateUsingLexiconAndFixedThreshold(lexicon, dataSet,90,90);
//			System.out.println("FIXED AT 90% "+target+" : semeval "+EvaluationUtil.getSemEvalMeasure(fmeasure2)+" micro "+fmeasure2.getMicroFscore()+" against "+fmeasure2.getScoreForLabel("AGAINST")+" favor "+fmeasure2.getScoreForLabel("FAVOR")+" none "+fmeasure2.getScoreForLabel("NONE"));
			
//			int percentageAgainst=(int) ((double)dataSet.getNumberOfAgainst()/dataSet.getNumberOfInstances()*100);
//			int percentageFavor=(int) ((double)dataSet.getNumberOfFavor()/dataSet.getNumberOfInstances()*100);
//			
//			percentageAgainst=100-percentageFavor;
//			percentageFavor= 100-percentageAgainst;
			
			Fscore<String> fmeasure2= analyzer.evaluateUsingLexiconAndFixedThreshold(lexicon, dataSet,75,75);
			System.out.println("FIXED AT class distribution 75% "+target+" : semeval "+EvaluationUtil.getSemEvalMeasure(fmeasure2)+" micro "+fmeasure2.getMicroFscore()+" against "+fmeasure2.getScoreForLabel("AGAINST")+" favor "+fmeasure2.getScoreForLabel("FAVOR")+" none "+fmeasure2.getScoreForLabel("NONE"));
			//test on test data 
//			analyzer.analyze(selectedTargetsFavor, selectedTargetsAgainst, 1,true);
		}
	

	}
	
	private static AnalysisEngine getTokenizerEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
//			builder.add(createEngineDescription(createEngineDescription(ArktweetTokenizer.class)));
			builder.add(createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

}
