package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
/**
 * https://www.cs.uic.edu/~liub/FBS/sentiment-analysis.html#lexicon
 * @author michael
 *
 */
public class BingLiuSentimentLexicon extends SentimentLexicon {

	public BingLiuSentimentLexicon(String bingLiuPath) {
		this.lexicon=generateLexicon(bingLiuPath);
	}

	@Override
	public HashMap<String, Float> generateLexicon(String path) {
		HashMap<String, Float> lexicon = new HashMap<String, Float>();
		try (FileReader fr = new FileReader(path+"/positive-words.txt"); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
					lexicon.put(currentString, 1.0f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (FileReader fr = new FileReader(path+"/negative-words.txt"); BufferedReader br = new BufferedReader(fr)) {
			String currentString = "";
			while ((currentString = br.readLine()) != null) {
					lexicon.put(currentString, -1.0f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lexicon;
	}

}
