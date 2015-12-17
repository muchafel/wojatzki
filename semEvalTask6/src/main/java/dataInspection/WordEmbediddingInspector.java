package dataInspection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import lexicons.WordEmbeddingLexicon;
import util.wordEmbeddingUtil.WordEmbeddingHelper;

public class WordEmbediddingInspector extends JCasAnnotator_ImplBase{
	private WordEmbeddingLexicon lexicon;
	private List<String> stopwords;
	
	@Override
	public void initialize(org.apache.uima.UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		lexicon = new WordEmbeddingLexicon(
				"src/main/resources/wordEmbeddings/glove.twitter.27B/glove.twitter.27B.50d.txt");
		System.out.println("word embedding with "+lexicon.getDimensionality()+ " dimensions");

		try {
			stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		WordEmbeddingHelper helper=new WordEmbeddingHelper(this.lexicon);
//		System.out.println(helper.getAveragedSentenceVector(JCasUtil.select(jcas, Token.class)));
	}

}
