package featureExtractors.stanceLexicon;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import featureExtractors.stanceLexicon.BinCasMetaDependent.RelevantTokens;
import lexicons.StanceLexicon;
import lexicons.TokenThesaurus;

public class StanceLexiconDFE_Tokens_normalized extends SummedStance_base{

	private TokenThesaurus TokenThesaurus;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			//FIXME remove stopwords from DFEs
			if (useStopwords) {
				stopwords = init("src/main/resources/lists/stop-words_english_6_en.txt");
			}
			if (useStances) {
				wordStanceLexicon = readLexicon(binCasDir,RelevantTokens.SENTENCE);
			}

		} catch (IOException | UIMAException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	@Override
	public StanceLexicon readLexicon(String binCasDir, RelevantTokens tokenMode) throws UIMAException, IOException{
		// create favor and against fds foreach target
				FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
				FrequencyDistribution<String> against = new FrequencyDistribution<String>();

				CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
						BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
						BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

				// iterate over all CASes that have been stored by the meta collector
				while (reader.hasNext()) {
					JCas jcas = JCasFactory.createJCas();
					reader.getNext(jcas.getCas());

					Collection<Token> relevantTokens = getRelevantTokens(jcas, tokenMode);
					
					if(usePolarity){
						// if tweet is against add tokens to favor frequency distribution
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("FAVOR")) {
							favor = incAll(favor, relevantTokens, stopwords, useStopwords);
						}
						
						// if tweet is against add tokens to favor frequency distribution
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
								.equals("AGAINST")) {
							against = incAll(against, relevantTokens, stopwords, useStopwords);
						}
					}else{
						//STANCE VS NONE
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("STANCE")) {
							favor = incAll(favor, relevantTokens, stopwords, useStopwords);
						}
						//STANCE VS NONE
						if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome().equals("NONE")) {
							against = incAll(against, relevantTokens, stopwords, useStopwords);
						}
					}
				}
				System.out.println("original samples :"+ favor.getN());
//				writeLexicon("test1",createLexiconMap(favor, against));
				favor= new TokenThesaurus(favor).getNormalizedFrequencyDistribution();
//				writeLexicon("test2",createLexiconMap(favor, against));
				System.out.println("normalized samples :"+ favor.getN());
				Map<String, Float> lexicon = createLexiconMap(favor, against);
				return new StanceLexicon(lexicon);
	}
	
	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> features = new HashSet<Feature>();
		float tokenPolarity = 0;
		int numberOfPositiveTokens=0;
		int numberOfNegativeTokens=0;
		
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			if (useStances) {
				float stance= wordStanceLexicon.getStance_WithFallback(token.getCoveredText());
				tokenPolarity += stance;
				if(stance>0)numberOfPositiveTokens++;
				else if(stance<0)numberOfNegativeTokens++;
			}
		}
		
		features.add(new Feature("SummedTokenPolarity_normalized", tokenPolarity));
		features.add(new Feature("numberOfPositiveTokens_normalized", numberOfPositiveTokens));
		features.add(new Feature("numberOfNegativeTokens_normalized", numberOfNegativeTokens));
		return features;
	}
	
	/**
	 * writes to the specified resource in the form Token:Stance
	 * @param target
	 * @param lexcicon
	 */
		private static void writeLexicon(String target, Map<String, Float> lexcicon) {
			
			try (PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("src/main/resources/lists/stanceLexicons/" + target + "/stanceLexicon.txt", true)))) {
				for (String key : lexcicon.keySet()) {
					out.println(key + ":" + lexcicon.get(key));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

}
