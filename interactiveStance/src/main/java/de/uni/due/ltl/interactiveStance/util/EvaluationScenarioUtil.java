package de.uni.due.ltl.interactiveStance.util;

import java.util.ArrayList;
import java.util.Arrays;

public class EvaluationScenarioUtil {
	private static boolean formatted = false;
	private static ArrayList<String> newTargets = new ArrayList<>();
	private static ArrayList<String> targets = new ArrayList<String>(
		    Arrays.asList("Atheism","ClimateChangeisaRealConcern","FeministMovement","HillaryClinton","LegalizationofAbortion"));
	
	private static ArrayList<String> experimentalModes = new ArrayList<String>(
		    Arrays.asList("Fixed Threshold","Optmized Threshold","Distributional Threshold"));
	
	
	public static boolean targetIsValid(String target){
		if(target.contains(target)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * format each string which are shown in webpage.
	 * Example: ClimateChange --> Climate Change
	 * @return
	 */
	public static ArrayList<String> formatTargets() {
		if (formatted) {
			return newTargets;
		} else {
			String pattern = "^[A-Z][a-z]*$";

			// Add space before all upper-case letters that are not the beginning of the String.
			for (String target : targets) {
				newTargets.add(target.replaceAll("(.)([A-Z])", "$1 $2"));
			}

			formatted = true;
			return newTargets;
		}
	}

	/**
	 * returns all experimental modes
	 * @return
	 */
	public static ArrayList<String> getExperimentalModes() {
		return experimentalModes;
	}

	
}
