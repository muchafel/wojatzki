package assertionRegression.wordembeddings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class WordEmbeddingLexicon {

	private Map<String, List<Double>> lexicon;
	
	
	public Map<String, List<Double>> getLexicon() {
		return lexicon;
	}

	public WordEmbeddingLexicon(Map<String, List<Double>> lexicon){
		this.lexicon= lexicon;
	}
	
	public WordEmbeddingLexicon(String path) throws Exception{
		this.lexicon= readLexicon(path);
	}
	private Map<String, List<Double>> readLexicon(String path) throws Exception {
		Map<String, List<Double>> lexicon=new HashMap<String, List<Double>>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String line = "";
			while ((line = br.readLine()) != null) {
				int i=0;
				String key="";
				List<Double> value= new ArrayList<Double>();
				for(String subString : line.split(" ")){
					if(i==0)key=subString;
					else{
						value.add(Double.parseDouble(subString));
					}
					i++;
				}
				lexicon.put(key, value);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return lexicon;
	}
	public void addEntry(String key,List<Double> value ){
		this.lexicon.put(key, value);
	}
	public List<Double> getEmbedding(String key) {
		if(lexicon.containsKey(key)){
			return this.lexicon.get(key);
		}
		else {
//			System.out.println("empty vector for "+key);
			return emptyVector();
		}
	}
	
	
	private List<Double> emptyVector() {
		List<Double> emptyVector=new ArrayList<Double>();
		for(int i=0; i< getDimensionality(); i++){
			emptyVector.add(0.0);
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
