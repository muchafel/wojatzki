package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SentimentLexicon {
	private HashMap<String, Float> lexicon;
	public SentimentLexicon(String path) {
		lexicon = generateLexicon(path);
	}

	/**
	 * generates the lexicon in the specified language
	 */
	private HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
				String[] entry = currentString.split(" : ");
				lexicon.put(entry[0], Float.valueOf(entry[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}

	/**
	 * returns 0 if there is no entry in the lexicon or the entry is positiv and
	 * negativ (souldn't happen) else returns 1 for positive and -1 for negative
	 */
	public float getSentiment(String word) {
		float value = 0.0f;
		if (lexicon.containsKey(word)) {
			value = lexicon.get(word);
		}
		return value;
	}
}
