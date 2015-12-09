package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		return this.lexicon.get(key);
	}
	
	public int getDimensionality() {
		return this.lexicon.entrySet().iterator().next().getValue().size();
	}
	
	public Set<String> getKeySet() {
		return lexicon.keySet();
	}
}
