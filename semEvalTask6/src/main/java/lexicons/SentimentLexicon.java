package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public abstract class SentimentLexicon {
	
	protected HashMap<String, Float> lexicon;

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
	
	public abstract HashMap<String, Float> generateLexicon(String path);
}
