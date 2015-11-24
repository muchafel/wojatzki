package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import util.SimilarityHelper;
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
	
	/**
	 * returns 0 if there is no entry in the lexicon else returns stance value 
	 */
	public float getStance_WithFallback(String word) {
		if (lexicon.containsKey(word)) {
			return lexicon.get(word);
		}
		return getFallBack(word);
	}

	private float getFallBack(String word) {
		for(String entry: lexicon.keySet()){
			if(SimilarityHelper.WordsAreSimilar(word, entry)) {
//				System.out.println(word+" not found ; use similar word "+ entry+ " instead!");
				return lexicon.get(entry);
			}
		}
		return 0;
	}
}
