package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class NRCSentimentLexicon extends SentimentLexicon {

	public NRCSentimentLexicon(String nrcPath) {
		this.lexicon= generateLexicon(nrcPath);
	}

	
	
	/**
	 * generates the lexicon in the specified language
	 */
	public HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
				String[] entry = currentString.split("\t");
				if(entry[1].equals("positive")&&Integer.valueOf(entry[2])==1){
					lexicon.put(entry[0], 1.0f);
				}
				if(entry[1].equals("negative")&&Integer.valueOf(entry[2])==1){
					lexicon.put(entry[0], -1.0f);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}
}
