package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.SimilarityHelper;

public class WordEmbeddingLexicon {

	private Map<String, List<Float>> lexicon;
	
	public WordEmbeddingLexicon(String path){
		this.lexicon= readLexicon(path);
		
	}
	private Map<String, List<Float>> readLexicon(String path) {
		Map<String, List<Float>> lexicon=new HashMap<String, List<Float>>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String line = "";
			while ((line = br.readLine()) != null) {
				int i=0;
				String key="";
				List<Float> value= new ArrayList<Float>();
				for(String subString : line.split(" ")){
					if(i==0)key=subString;
					else{
						value.add(Float.parseFloat(subString));
					}
					i++;
				}
				lexicon.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lexicon;
	}
	public void addEntry(String key,List<Float> value ){
		this.lexicon.put(key, value);
	}
	public List<Float> getEmbedding(String key) {
		if(lexicon.containsKey(key)){
			return this.lexicon.get(key);
		}
		else {
//			System.out.println(key+ "not in embeddings");
			return emptyVector();
		}
	}
	
	/**
	 * returns 0 if there is no entry in the lexicon else returns stance value 
	 */
	public List<Float> getEmbedding_WithFallback(String key) {
		if (lexicon.containsKey(key)) {
			return this.lexicon.get(key);
		}
		return getFallBack(key);
	}

	private List<Float> getFallBack(String word) {
		for(String entry: lexicon.keySet()){
			if(SimilarityHelper.wordsAreSimilar(word, entry)) {
//				System.out.println(word+" not found; use similar word "+ entry+ " instead!");
				return lexicon.get(entry);
			}
		}
		return emptyVector();
	}
	
	private List<Float> emptyVector() {
		List<Float> emptyVector=new ArrayList<Float>();
		for(int i=0; i< getDimensionality(); i++){
			emptyVector.add(0.0f);
		}
		return emptyVector;
	}
	public int getDimensionality() {
		return this.lexicon.entrySet().iterator().next().getValue().size();
	}
	
	public Set<String> getKeySet() {
		return lexicon.keySet();
	}
}
