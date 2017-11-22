package assertionRegression.similarity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;

public class InspectSimilarity {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		File judgments= new File("src/main/resources/rawMatrixTransposed/Black Lives Matter.tsv");
		WordVectors wordVectors = WordVectorSerializer.loadTxtVectors(judgments);

		System.out.println(wordVectors.wordsNearest("Racists_should_be_sentenced_to_jail_terms.",3));
		System.out.println(wordVectors.wordsNearest("Every_human_is_equal.",3));
		System.out.println(wordVectors.wordsNearest("Black_lives_matter_Is_more_Of_a_Movement_for_Attention,_and_obviously_more_money.",3));
		System.out.println(wordVectors.wordsNearest("People_should_not_be_discriminated_for_their_skin_color.",3));
	
		File judgments2= new File("src/main/resources/rawMatrixTransposed/Legalization of Marijuana.tsv");
		WordVectors wordVectors2 = WordVectorSerializer.loadTxtVectors(judgments2);

		System.out.println(wordVectors2.wordsNearest("Marijuana_is_less_harmful_than_consuming_legal_substances_such_as_alcohol_or_tobacco.",3));
		System.out.println(wordVectors2.wordsNearest("No_drugs_at_all_should_be_legalized.",3));
		System.out.println(wordVectors2.wordsNearest("Legal_authorities_will_benefit_from_legalizing_marijuana_as_it_will_reduce_illegal_use_and_prosecutions.",3));
		
	}

}
