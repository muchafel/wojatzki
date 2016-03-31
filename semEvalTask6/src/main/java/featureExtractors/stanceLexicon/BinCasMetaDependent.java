package featureExtractors.stanceLexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.meta.MetaCollector;
import org.dkpro.tc.api.features.meta.MetaDependent;
import featureExtractors.BinCasMetaCollector;
import types.FunctionalPartAnnotation;
import util.StanceConstants;



public abstract class BinCasMetaDependent extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor, MetaDependent, StanceConstants{
	
	public enum RelevantTokens {
		ALL, HASHTAG, SENTENCE, SENTENCE_FILTERED, SENTENCE_NOUNS_VEBS_ADJECTIVES, SENTENCE_NOUNS,
	}
	
	public static final String PARAM_BINCAS_DIR = "BinCasDirLexicon";
	@ConfigurationParameter(name = PARAM_BINCAS_DIR, mandatory = true)
	protected String binCasDir;
	
	
	@Override
	public List<Class<? extends MetaCollector>> getMetaCollectorClasses() {
		List<Class<? extends MetaCollector>> metaCollectorClasses = new ArrayList<Class<? extends MetaCollector>>();
		metaCollectorClasses.add(BinCasMetaCollector.class);

		return metaCollectorClasses;
	}
	
	/**
	 * return
	 * 
	 * @param jcas
	 * @param tokenMode
	 * @return
	 */
	protected Collection<Token> getRelevantTokens(JCas jcas, RelevantTokens tokenMode) {
		if (tokenMode.equals(RelevantTokens.ALL))
			return JCasUtil.select(jcas, Token.class);
		else if (tokenMode.equals(RelevantTokens.SENTENCE))
			return getFunctionalTokens(jcas, SENTENCE_FUNCTION);
		else if (tokenMode.equals(RelevantTokens.HASHTAG))
			return getFunctionalTokens(jcas, TAG_FUNCTION);
		else if (tokenMode.equals(RelevantTokens.SENTENCE_FILTERED))
			return filterTokens(getFunctionalTokens(jcas, SENTENCE_FUNCTION));
		else if (tokenMode.equals(RelevantTokens.SENTENCE_NOUNS))
			return keepOnlyNouns(getFunctionalTokens(jcas, SENTENCE_FUNCTION));
		else if (tokenMode.equals(RelevantTokens.SENTENCE_NOUNS_VEBS_ADJECTIVES))
			return filterTokensNounsVerbsAdjectives(getFunctionalTokens(jcas, SENTENCE_FUNCTION));
		else
			return null;
	}
	
	/**
	 * returns only tokens that have been annotated with the specified function
	 * 
	 * @param jcas
	 * @param function
	 * @return
	 */
	protected Collection<Token> getFunctionalTokens(JCas jcas, String function) {
		Collection<Token> tokens = new HashSet<Token>();
		for (FunctionalPartAnnotation part : JCasUtil.select(jcas, FunctionalPartAnnotation.class)) {
			if (part.getFunction().equals(function)) {
				tokens.addAll(JCasUtil.selectCovered(Token.class, part));
			}
		}
//		System.out.println("getFunctionalTokens "+JCasUtil.toText(tokens));
		return tokens;
	}
	
	/**
	 * returns only tokens that have been annotated with the specified function
	 * 
	 * @param jcas
	 * @param function
	 * @return
	 */
	protected Collection<Token> keepOnlyNouns(Collection<Token> input) {
		Collection<Token> tokens = new HashSet<Token>();
		for (Token t : input) {
			String pos = t.getPos().getClass().getSimpleName();
//			System.out.println(t.getCoveredText()+ " "+ pos);
			if (pos.equals("NE") ||pos.equals("NN")||pos.equals("NP") ) {
				tokens.add(t);
			}
		}
		return tokens;
	}
	
	
	/**
	 * returns only tokens with POS tags: NE, NN, V and ADJ
	 * @param input
	 * @return
	 */
	private Collection<Token> filterTokensNounsVerbsAdjectives(Collection<Token> input) {
		Collection<Token> tokens = new HashSet<Token>();
		for (Token t : input) {
			String pos = t.getPos().getClass().getSimpleName();
			if (pos.equals("NP") ||pos.equals("NN") || pos.equals("V") || pos.equals("ADJ")) {
				tokens.add(t);
			}
		}
		return tokens;
	}

	/**
	 * firlters tokens that have an POS tag '.',',',':','DT','IN'
	 * @param select
	 * @return
	 */
	private Collection<Token> filterTokens(Collection<Token> input) {
		Collection<Token> tokens = new HashSet<Token>();
		for (Token t : input) {
			String pos = t.getPos().getPosValue();
			if (!pos.equals(".") && !pos.equals(",") && !pos.equals(":") && !pos.equals("DT") && !pos.equals("IN")
					&& !pos.equals("-LRB-") && !pos.equals("-RRB-")) {
				tokens.add(t);
			}
		}
		return tokens;
	}
	
	
}
