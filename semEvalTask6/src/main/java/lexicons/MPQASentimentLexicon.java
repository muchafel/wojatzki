package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class MPQASentimentLexicon extends SentimentLexicon {

	public MPQASentimentLexicon(String mpqaPath) {
		this.lexicon=generateLexicon(mpqaPath);
	}

	@Override
	public HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
				String[] entry = currentString.split(" ");
				if(entry[5].split("=")[1].equals("positive")){
					lexicon.put(entry[2].split("=")[1], 1.0f);
				}
				if(entry[5].split("=")[1].equals("negative")){
					lexicon.put(entry[2].split("=")[1], -1.0f);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}

}
