package featureExtractors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import lexicons.WordEmbeddingLexicon;
import util.wordEmbeddingUtil.WordEmbeddingHelper;

public class WordEmbeddingDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {
	private WordEmbeddingLexicon lexicon;
	private List<String> stopwords;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		lexicon = new WordEmbeddingLexicon(
				"src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.50d.txt");
		System.out.println("word embedding with "+lexicon.getDimensionality()+ " dimensions");

		try {
			stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * read in a file and return a list of strings
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected List<String> init(String path) throws IOException {
		List<String> stopwords = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
		}
		return stopwords;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		
//		System.out.println(jcas.getDocumentText());
		WordEmbeddingHelper helper=new WordEmbeddingHelper(this.lexicon);
		Set<String> embeddingCandidates= new HashSet<String>();
		
		//TODO better filtering
		for(Token t: JCasUtil.select(jcas, Token.class)){
			//filter stopwords and punctuations
			String lowerCase = t.getCoveredText().toLowerCase();
//			if (!stopwords.contains(lowerCase) || !t.getPos().getPosValue().equals(",")
//					|| !t.getPos().getPosValue().equals(".") ||! t.getPos().getPosValue().equals("$")
//					|| !t.getPos().getPosValue().equals("'") ||! t.getPos().getPosValue().equals(":")){
//				embeddingCandidates.add(lowerCase);
//			}
			if (!stopwords.contains(lowerCase) && (t.getPos().getPosValue().equals("NN")
					|| t.getPos().getPosValue().equals("NNP")|| t.getPos().getPosValue().equals("NNS") || t.getPos().getPosValue().equals("NNPS")) ){
				embeddingCandidates.add(lowerCase);
			}
		}
//		System.out.prinstln(embeddingCandidates);
		List<Float> averagedSentenceVector= helper.getAveragedSentenceVector(embeddingCandidates);
		Set<Feature> featList = createFeatures(averagedSentenceVector);
		return featList;
	}

	private Set<Feature> createFeatures(List<Float> averagedVector) {
		Set<Feature> featList = new HashSet<Feature>();
		for(int i=0; i< averagedVector.size(); i++){
			featList.add(new Feature("embeddingDimension_"+i, averagedVector.get(i)));
		}
		return featList;
	}

	

}
