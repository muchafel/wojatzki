package annotators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import types.NegationAnnotation;
/**
 * checks whether each token is part of given list of negation words and creates NegationAnnotation
 * if tokens are found in the list the isNegation is set to true 
 * @author michael
 *
 */
public class NegationAnnotator extends JCasAnnotator_ImplBase{

	public static final String PARAM_NEGATIONWORDS_FILE_PATH = "modalVerbsFilePath";
	
	@ConfigurationParameter(name = PARAM_NEGATIONWORDS_FILE_PATH, mandatory = true)
	private String taskFilePath;

	private List<String> negationWords;

	@Override
	public void initialize(org.apache.uima.UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			negationWords = init(taskFilePath);
//			System.out.println(negationWords);
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	private List<String> init(String path) throws IOException {
		List<String> negationWords= new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				negationWords.add(line);
			}
		}
		return negationWords;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for(Token t: JCasUtil.select(jcas, Token.class)){
			NegationAnnotation anno= new NegationAnnotation(jcas);
			anno.setBegin(t.getBegin());
			anno.setEnd(t.getEnd());
			if(isNegation(t.getCoveredText().toLowerCase())){
//				JCasUtil.select(jcas, Token.class)
				anno.setIsNegation(true);
			}else anno.setIsNegation(false);
			anno.addToIndexes();
		}
	}

	private boolean isNegation(String tokenText) {
		
		if(negationWords.contains(tokenText)){
			return true;
		}
		// catch expressions such as hadn't, aren't etc...
		else if(tokenText.contains("'")&&tokenText.length()>1){
//			System.out.println(tokenText+" "+tokenText.split("'").length);
			if(tokenText.split("'").length>1){
				if(tokenText.split("'")[1].equals("t")) return true;
			}
		}
		return false;
	}
	

}
