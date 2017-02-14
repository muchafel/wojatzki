package de.uni_due.ltl.ensemble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.uni_due.ltl.util.Id2OutcomeUtil;
import de.uni_due.ltl.util.TargetSets;
import io.YouTubeReader;

public class YouTubeClassificationTypeReader_subdebateWise extends YouTubeReader {

	Map<String, Boolean> set1Occurrences = new HashMap<String, Boolean>();
	Map<String, Boolean> set2Occurrences = new HashMap<String, Boolean>();

	public static ArrayList<String> bothType = new ArrayList<String>(Arrays.asList("Death Penalty (Debate)",
			"The death penalty should apply as punishment for first-degree murder; an eye for an eye.",
			"The death penalty is a financial burden on the state.",
			"If Death Penalty is allowed, abortion should be legal, too.",
			"In certain cases, capital punishment shouldn’t have to be humane but more harsh",
			"Death Penalty should be done by the electric chair"));

	public static ArrayList<String> SVM_type = new ArrayList<String>(Arrays.asList(
			"Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",
			"The level of certainty that is necessary for Death Penalty is unachievable",
			"Execution helps alleviate the overcrowding of prisons.", "It helps the victims’ families achieve closure.",
			"State-sanctioned killing is wrong (state has not the right).",
			"The death penalty can produce irreversible miscarriages of justice.", "The death penalty deters crime.",
			"Wrongful convictions are irreversible.", "Life-long prison should be replaced by Death Penalty"));

	public static ArrayList<String> LSTM_type = new ArrayList<String>(Arrays.asList(
			"Execution prevents the accused from committing further crimes.",
			"Bodies of people sentenced to death should be used to repay society (e.g. medical experiments, organ donation)", // *
			"Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)",
			"Death Penalty should be done by gunshot", "Death Penalty should be done by hypoxia"));

	/**
	 * returns SVM, LSTM, BOTH or NONE
	 */
	@Override
	protected String getTextClassificationOutcome(JCas jcas, TextClassificationTarget unit) throws Exception {
		if (!targetLabel.equals("DEATH PENALTY")) {
			throw new Exception("Reader cannot process other lables than 'DEATH PENALTY'");
		}
		try {
			for (String target1 : TargetSets.targets_Set1) {
				set1Occurrences.put(target1, getOraclePolarity(unit, jcas, target1, true));
			}
			for (String target2 : TargetSets.targets_Set2) {
				set2Occurrences.put(target2, getOraclePolarity(unit, jcas, target2, false));
			}
		} catch (Exception e) {
			throw new TextClassificationException(e);
		}
		int svm_type_count=countType(SVM_type);
		int lstm_type_count=countType(LSTM_type);
		int both_type_count=countType(bothType);
		
		if(svm_type_count==0 && lstm_type_count == 0 && both_type_count ==0){
			return "NONE";
		}
		if(svm_type_count == lstm_type_count){
			return "BOTH";
		}
		if( svm_type_count > both_type_count + lstm_type_count){
			return "SVM";
		}
		if( lstm_type_count > both_type_count + svm_type_count ){
			return "LSTM";
		}
		return "BOTH";
		
//		boolean svm_type = is_Type(SVM_type);
//		boolean lstm_type = is_Type(LSTM_type);
//		boolean both_type = is_Type(bothType);
//
//		if (svm_type && lstm_type || both_type) {
//			return "BOTH";
//		}
//		if (svm_type) {
//			return "SVM";
//		}
//		if (lstm_type) {
//			return "LSTM";
//		}
//
//		return "NONE";
	}

	private int countType(ArrayList<String> type) {
		int count= 0;
		for (String target : type) {
			for (String t : set1Occurrences.keySet()) {
				if (target.equals(t) && set1Occurrences.get(t)) {
					count++;
				}
			}
			for (String t : set2Occurrences.keySet()) {
				if (target.equals(t) && set2Occurrences.get(t)) {
					count++;
				}
			}
		}
		return count;
	}

	private boolean is_Type(ArrayList<String> SVM_type) {
		boolean result = false;
		for (String target : SVM_type) {
			for (String t : set1Occurrences.keySet()) {
				if (target.equals(t) && set1Occurrences.get(t)) {
					return true;
				}
			}
			for (String t : set2Occurrences.keySet()) {
				if (target.equals(t) && set2Occurrences.get(t)) {
					return true;
				}
			}
		}
		return result;
	}

	private boolean getOraclePolarity(TextClassificationTarget unit, JCas jcas, String targetLabel, boolean targetSet1)
			throws Exception {
		int polarity = 0;
		if (targetSet1) {
			polarity = getOraclePolaritySet1(unit, jcas, targetLabel);
		} else {
			polarity = getOraclePolaritySet2(unit, jcas, targetLabel);
		}
		if (polarity == 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * get oracle polarity for target set1
	 * 
	 * @param unit
	 * @param jcas
	 * @param targetLabel
	 * @return
	 * @throws Exception
	 */
	private int getOraclePolaritySet1(TextClassificationTarget unit, JCas jcas, String targetLabel) throws Exception {
		for (curated.Explicit_Stance_Set1 subTarget : JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,
				unit)) {
			if (targetLabel.equals(subTarget.getTarget())) {
				String polarity = subTarget.getPolarity();
				return Id2OutcomeUtil.resolvePolarity(polarity);
			}
		}
		return 0;
	}

	/**
	 * get oracle polarity for target set2
	 * 
	 * @param unit
	 * @param jcas
	 * @param targetLabel
	 * @return
	 * @throws Exception
	 */
	private int getOraclePolaritySet2(TextClassificationTarget unit, JCas jcas, String targetLabel) throws Exception {
		for (curated.Explicit_Stance_Set2 subTarget : JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set2.class,
				unit)) {
			if (targetLabel.equals(subTarget.getTarget())) {
				String polarity = subTarget.getPolarity();
				return Id2OutcomeUtil.resolvePolarity(polarity);
			}
		}
		return 0;
	}

}
