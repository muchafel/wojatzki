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

import com.mysql.cj.api.x.Result;

/**
 * Wrapper for the generated stance lexicons
 * provides reading-from-text methods
 * and getter functionalities
 * @author michael
 *
 */
public class StanceLexicon {

	private Map<String, Float> lexicon;
	private boolean sorted=false;
	
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
	public float getStancePolarity(String word) {
		float value = 0.0f;
		if (lexicon.containsKey(word)) {
			value = lexicon.get(word);
		}
		return value;
	}
	
	/**
	 * returns positive bound - n percent
	 * @param i
	 * @return
	 */
	public float getNthPositivePercent(int i){
		lexicon = sort();
		// keyset size -1 (as it is not length)
		int lastIndex = (lexicon.keySet().size()-1);
		float bound=this.lexicon.get((this.lexicon.keySet().toArray())[lastIndex]);
		float result= bound - (bound*i/100);
//		System.out.println(i + "th positive item " + (this.lexicon.keySet().toArray())[i]);
		return result;
	}
	
	/**
	 * returns negative bound - n percent
	 * @param i
	 * @return
	 */
	public float getNthNegativePercent(int i){
		lexicon= sort();
		float bound= this.lexicon.get((this.lexicon.keySet().toArray())[0]);
		float result= bound - (bound*i/100);
		return result;
	}
	
	
	public float getNthPositive(int i){
		lexicon = sort();
		// keyset size -1 (as it is not length)
		i = (lexicon.keySet().size() - 1) - i;
//		System.out.println(i + "th positive item " + (this.lexicon.keySet().toArray())[i]);
		return this.lexicon.get((this.lexicon.keySet().toArray())[i]);
		
	}
	
	public float getNthNegative(int i){
		lexicon= sort();
//		System.out.println(i+"th negative item "+(this.lexicon.keySet().toArray())[i]);
		return this.lexicon.get((this.lexicon.keySet().toArray())[i]);
		
	}
	
	public Set<String> getKeys() {
		return lexicon.keySet();
	}
	
	public String prettyPrint() {
		lexicon= sort();
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
	private Map<String, Float> sort() {
		if(sorted){
			return this.lexicon;
		}
		this.sorted=true;
		Map<String, Float> sortedMap = lexicon.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return sortedMap;
	}
	
}
