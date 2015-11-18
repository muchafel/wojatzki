package annotators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import lexicons.StanceLexicon;
import types.StanceAnnotation;
import types.WordStancePolarity;
import util.StanceConstants;

/**
 * checks whether each token is part of the target-specific stance lexicons
 * creates WordStancePolarity annotation
 * if tokens are found in the lists the stance polarity is set according to the lexicon
 * @author michael
 *
 */
public class TokenStancePolarityAnnotator extends PolarityAnnotator_base implements StanceConstants {

	public static final String PARAM_ATHEISMSTANCES_FILE_PATH = "atheismLexFilePath";
	public static final String PARAM_ABORTIONSTANCES_FILE_PATH = "abortionLexFilePath";
	public static final String PARAM_HILLARYSTANCES_FILE_PATH = "hillaryLexFilePath";
	public static final String PARAM_FEMINISTSTANCES_FILE_PATH = "feministLexFilePath";
	public static final String PARAM_CLIMATESTANCES_FILE_PATH = "climateLexFilePath";
	
	@ConfigurationParameter(name = PARAM_ATHEISMSTANCES_FILE_PATH, mandatory = true)
	private String atheismFilePath;
	
	@ConfigurationParameter(name = PARAM_ABORTIONSTANCES_FILE_PATH, mandatory = true)
	private String abortionFilePath;
	
	@ConfigurationParameter(name = PARAM_HILLARYSTANCES_FILE_PATH, mandatory = true)
	private String hillaryFilePath;
	
	@ConfigurationParameter(name = PARAM_FEMINISTSTANCES_FILE_PATH, mandatory = true)
	private String feministFilePath;

	@ConfigurationParameter(name = PARAM_CLIMATESTANCES_FILE_PATH, mandatory = true)
	private String climateFilePath;
	

	@Override
	public void initialize(org.apache.uima.UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		atheismStances = new StanceLexicon(atheismFilePath);
		abortionStances = new StanceLexicon(abortionFilePath);
		hillaryStances = new StanceLexicon(hillaryFilePath);
		feministStances = new StanceLexicon(feministFilePath);
		climateStances = new StanceLexicon(climateFilePath);
	};
	
	
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		StanceLexicon relevantLexicon = null;
		try {
			relevantLexicon=chooseLexicon(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		Collection<Token> tokens = null;
		if(useFunctionalPartition){
			tokens=getFunctionalTokens(jcas,SENTENCE_FUNCTION);
		}
		else tokens=JCasUtil.select(jcas, Token.class);
		
		for(Token token: tokens){
//			System.out.println(token.getCoveredText()+ " "+relevantLexicon.getStance(token.getCoveredText()));
			WordStancePolarity anno= new WordStancePolarity(jcas);
			anno.setBegin(token.getBegin());
			anno.setEnd(token.getEnd());
			anno.setPolarity(relevantLexicon.getStance(token.getCoveredText()));
			anno.addToIndexes();
		}
	}



	

	
	
}
