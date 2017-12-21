package assertionRegression.corpusProcessing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TweetTokenizedTextWriter extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		DocumentMetaData md= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
		File writeTo= new File("/Users/michael/Desktop/statuses/cleaned/"+md.getDocumentTitle()+"_cleaned.txt");
		for(Sentence sen: JCasUtil.select(jcas, Sentence.class)){
//			System.out.println(sen.getCoveredText());
			List<String> tokens= JCasUtil.toText(JCasUtil.selectCovered(Token.class,sen));
			String tokenString = StringUtils.join(tokens," ");
			try {
				FileUtils.write(writeTo, tokenString+"\n","UTF-8",true);
			} catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}
		
	}

}
