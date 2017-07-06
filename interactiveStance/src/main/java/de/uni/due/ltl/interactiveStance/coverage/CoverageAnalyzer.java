package de.uni.due.ltl.interactiveStance.coverage;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;

public class CoverageAnalyzer {

	private StanceDB db;
	private EvaluationScenario evaluationScenario;
	protected AnalysisEngine engine;
	
	
	public CoverageAnalyzer(StanceDB db, EvaluationScenario evaluationScenario) {
		this.db=db;
		this.evaluationScenario= evaluationScenario;
		this.engine = getTokenizerEngine();
	}

	
	protected AnalysisEngine getTokenizerEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
	

	/**
	 * returns the coverage of the currently selected targets
	 * TODO: think of a coverage suggestion
	 * @param selectedFavorTargets
	 * @param selectedAgainstTargets
	 * @param availableTargets
	 * @param evaluateOnTrain
	 * @return
	 * @throws UIMAException
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	public CoverageResult analyseCoverage(HashMap<String, ExplicitTarget> selectedFavorTargets, HashMap<String, ExplicitTarget> selectedAgainstTargets, HashMap<String, ExplicitTarget> availableTargets, boolean evaluateOnTrain) throws UIMAException, NumberFormatException, SQLException {
//		HashMap<String, Set<String>> targetToText=getAvailableTargetsToText(availableTargets);
		Set<String> wordsInSelectedTargets= getWordsInSelectedTargets(selectedFavorTargets,selectedAgainstTargets);
		
		EvaluationDataSet dataSet= null;
		if(evaluateOnTrain){
			dataSet=evaluationScenario.getTrainData();
		}else{
			evaluationScenario.getTestData();
		}
		Set<String> wordsInData=getWordsInData(dataSet.getDataReader());
		
		double coverageSelection=getCoverage(wordsInData,wordsInSelectedTargets);
		HashMap<String, Double> targetToCoverage=null;
//		HashMap<String, Double> targetToCoverage=getTargetToCoverage(wordsInData,targetToText);
		System.out.println(coverageSelection);
		
		return new CoverageResult(coverageSelection,targetToCoverage);
	}


	private HashMap<String, Double> getTargetToCoverage(Set<String> wordsInData,HashMap<String, Set<String>> targetToText) {
		HashMap<String, Double> targetToCoverage= new HashMap<>();
		for(String target: targetToText.keySet()){
			targetToCoverage.put(target, getCoverage(wordsInData, targetToText.get(target)));
		}
		return targetToCoverage;
	}


	private double getCoverage(Set<String> wordsInData, Set<String> wordsInSelectedTargets) {
		Set<String> intersection = new HashSet(wordsInData);
		intersection.retainAll(wordsInSelectedTargets);
		return  (double)intersection.size()/wordsInData.size();
	}


	private Set<String> getWordsInData(CollectionReaderDescription dataReader) throws AnalysisEngineProcessException {
		Set<String> result= new HashSet<>();
		for (JCas jcas : new JCasIterable(dataReader)){
			this.engine.process(jcas);
			result.addAll(JCasUtil.toText(JCasUtil.select(jcas,Token.class)));
		}	
		return result;
	}


	private Set<String> getWordsInSelectedTargets(HashMap<String, ExplicitTarget> selectedFavorTargets, HashMap<String, ExplicitTarget> selectedAgainstTargets) throws UIMAException, NumberFormatException, SQLException {
		Set<String> result= new HashSet<>();
		for (String id : selectedFavorTargets.keySet()) {
			for(DataPoint point: db.getDataPointsByDataSetId(Integer.valueOf(id))){
				JCas jcas = JCasFactory.createText(point.getText(), "en");
				engine.process(jcas);
				result.addAll(JCasUtil.toText(JCasUtil.select( jcas,Token.class)));
			}
		}
		for (String id : selectedAgainstTargets.keySet()) {
			for(DataPoint point: db.getDataPointsByDataSetId(Integer.valueOf(id))){
				JCas jcas = JCasFactory.createText(point.getText(), "en");
				engine.process(jcas);
				JCasUtil.select( jcas,Token.class);
				result.addAll(JCasUtil.toText(JCasUtil.select( jcas,Token.class)));
			}
		}
		return result;
	}


	private HashMap<String, Set<String>> getAvailableTargetsToText(HashMap<String, ExplicitTarget> availableTargets) throws AnalysisEngineProcessException, NumberFormatException, UIMAException, SQLException {
		HashMap<String, Set<String>> targetToText= new HashMap<>();
		for (String id : availableTargets.keySet()) {
			for(DataPoint point: db.getDataPointsByDataSetId(Integer.valueOf(id))){
				JCas jcas = JCasFactory.createText(point.getText(), "en");
				engine.process(jcas);
				JCasUtil.select( jcas,Token.class);
				Set<String> text=new HashSet(JCasUtil.toText(JCasUtil.select( jcas,Token.class)));
				targetToText.put(id, text);
			}
		}
		return targetToText;
	}

}
