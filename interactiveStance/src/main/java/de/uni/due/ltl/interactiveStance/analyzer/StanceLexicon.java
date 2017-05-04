package de.uni.due.ltl.interactiveStance.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper for the generated stance lexicons
 * provides reading-from-text methods
 * and getter functionalities
 * @author michael
 *
 */
public class StanceLexicon {

	private Map<String, Float> lexicon;
	
	public StanceLexicon(String path) {
		lexicon=generateLexicon(path);
	}
	
	public StanceLexicon(Map<String, Float> lexicon) {
		this.lexicon=lexicon;
	}


	/**
	 * loads a serialized lexicon
	 * generates the lexicon according to the specified resource 
	 */
	private HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
				//escape colon/ tokens thta conatin colon
				if(currentString.split(":").length>2)continue;
				String[] entry = currentString.split(":");
				lexicon.put(entry[0], Float.valueOf(entry[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}

	/**
	 * returns 0 if there is no entry in the lexicon else returns stance value 
	 */
	public float getStance(String word) {
		float value = 0.0f;
		if (lexicon.containsKey(word)) {
			value = lexicon.get(word);
		}
		return value;
	}
	
	
	
	public Set<String> getKeys() {
		return lexicon.keySet();
	}
	
	public String prettyPrint() {
		lexicon= sort(lexicon);
		StringBuilder sb = new StringBuilder();
		for(String key: lexicon.keySet()){
			sb.append(key+" : "+lexicon.get(key)+"\n");
		}
		return sb.toString();
	}

	/**
	 * sorts the vector by value (weird java 8 style)
	 * 
	 * @param tempVector
	 * @return
	 */
	private Map<String, Float> sort(Map<String, Float> tempVector) {
		Map<String, Float> sortedMap = tempVector.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return sortedMap;
	}
	
}
