package de.uni.due.ltl.interactiveStance.analyzer;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.features.ngram.util.NGramUtils;
import org.springframework.expression.spel.support.ReflectionHelper.ArgsMatchKind;

import com.google.common.collect.Multiset.Entry;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.experimentLogging.ExperimentLogging;
import de.uni.due.ltl.interactiveStance.experimentLogging.ThresholdEvent;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;
import de.uni.due.ltl.interactiveStance.util.CollocationMeasureHelper;
import de.uni.due.ltl.interactiveStance.util.EvaluationUtil;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import de.unidue.ltl.evaluation.measure.categorial.Fscore;
import edu.stanford.nlp.io.EncodingPrintWriter.out;

public class CollocationNgramAnalyzer_fixedThresholds extends CollocationNgramAnalyzerBase {

	private int fixedThreshold=75;
	
	public CollocationNgramAnalyzer_fixedThresholds(StanceDB db, EvaluationScenario scenario, int fixedThreshold,ExperimentLogging logging) {
		super(db, scenario,logging);
		this.fixedThreshold= fixedThreshold;
	}


	@Override
	protected EvaluationData<String> evaluateUsingLexicon(StanceLexicon stanceLexicon, EvaluationDataSet data) throws AnalysisEngineProcessException {
		return evaluateUsingLexicon_FixedThreshold(stanceLexicon, data, fixedThreshold, fixedThreshold);
	}

	
	
	/**
	 * runs an pure evaluation on the given data set with a fixed threshold
	 * @param stanceLexicon
	 * @param evaluationDataSet
	 * @param upperPercentage
	 * @param lowerPercentage
	 * @return
	 * @throws AnalysisEngineProcessException
	 */
	public EvaluationData<String> evaluateUsingLexicon_FixedThreshold(StanceLexicon stanceLexicon, EvaluationDataSet evaluationDataSet,int upperPercentage, int lowerPercentage) throws AnalysisEngineProcessException {
		EvaluationData<String> evalData = new EvaluationData<>();
		float upperBound = stanceLexicon.getNthPositivePercent(upperPercentage);
		float lowerBound = stanceLexicon.getNthNegativePercent(lowerPercentage);
		
		for (JCas jcas : new JCasIterable(evaluationDataSet.getDataReader())){
			this.engine.process(jcas);
			String goldOutcome= JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance();
			float commentPolarity = getPolarity(stanceLexicon, jcas);
			String outcome = ressolveOutcome(upperBound,lowerBound,commentPolarity);
			evalData.register(goldOutcome, outcome);
			
		}
		new ThresholdEvent(logging, upperPercentage+"_"+lowerPercentage,"fixed").persist();
		System.out.println("Using threshold config "+ upperPercentage+"_"+lowerPercentage+" : "+EvaluationUtil.getSemEvalMeasure(new Fscore<>(evalData)));
		System.out.println(new ConfusionMatrix<String>(evalData));
		return evalData;
	}


	public int getFixedThreshold() {
		return fixedThreshold;
	}


	public void setFixedThreshold(int fixedThreshold) {
		this.fixedThreshold = fixedThreshold;
	}


	

}
