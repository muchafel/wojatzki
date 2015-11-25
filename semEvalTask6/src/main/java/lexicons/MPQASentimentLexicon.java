package lexicons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
/**
 * lexicon is generated based on the mpqa sentiment lexicon (http://mpqa.cs.pitt.edu/lexicons/subj_lexicon/)
 * strong subjectivity is mapped by a polarity of 1.0 and weak subjectivity is mapped by a polarity of 0.5
 * lexicon generation according to 'Wagner, J., Arora, P., Cortes, S., Barman, U., Bogdanova, D., Foster, J., & Tounsi, L. (2014).
 * Dcu: Aspect-based polarity classification for semeval task 4. 
 * In Proceedings of the 8th International Workshop on Semantic Evaluation (SemEval 2014)' 
 * (pp. 223-229).
 * @author michael
 *
 */
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
//				System.out.println(currentString);
				String[] entry = currentString.split(" ");
				if(entry[5].split("=")[1].equals("positive")){
					if(entry[0].split("=")[1].equals("strongsubj"))lexicon.put(entry[2].split("=")[1], 1.0f);
					else lexicon.put(entry[2].split("=")[1], 0.5f);
				}
				if(entry[5].split("=")[1].equals("negative")){
					if(entry[0].split("=")[1].equals("strongsubj"))lexicon.put(entry[2].split("=")[1], -1.0f);
					else lexicon.put(entry[2].split("=")[1], -0.5f);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lexicon;
	}

}
