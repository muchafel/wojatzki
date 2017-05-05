package de.uni.due.ltl.interactiveStance.util;

import java.util.ArrayList;
import java.util.Arrays;

public class EvaluationScenarioUtil {

	private static ArrayList<String> targets = new ArrayList<String>(
		    Arrays.asList("Atheism","ClimateChangeisaRealConcern","FeministMovement","HillaryClinton","LegalizationofAbortion"));
	
	
	public static boolean targetIsValid(String target){
		if(target.contains(target)){
			return true;
		}else{
			return false;
		}
	}
	
	
}
