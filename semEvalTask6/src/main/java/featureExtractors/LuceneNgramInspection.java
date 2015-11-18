package featureExtractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.base.LuceneNgramFeatureExtractorBase;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.util.NGramUtils;
import types.StanceAnnotation;

/**
 * be careful when using! high computational costs!!!
 * 
 * @author michael
 *
 */
public class LuceneNgramInspection extends LuceneNgramFeatureExtractorBase implements DocumentFeatureExtractor {

	List<String> AGAINST_as_NONE = new ArrayList<String>(
			Arrays.asList("tweets1856.xml", "tweets2099.xml", "tweets1174.xml"));
	List<String> AGAINST_as_FAVOUR = new ArrayList<String>(
			Arrays.asList("tweets1441.xml", "tweets1739.xml", "tweets1687.xml"));

	List<String> NONE_as_FAVOUR = new ArrayList<String>(
			Arrays.asList("tweets2658.xml", "tweets1125.xml", "tweets2633.xml"));
	List<String> NONE_as_AGAINST = new ArrayList<String>(
			Arrays.asList("tweets2243.xml", "tweets1908.xml", "tweets2731.xml"));

	List<String> FAVOUR_as_AGAINST_atheism = new ArrayList<String>(Arrays.asList("tweets598.xml", "tweets598.xml",
			"tweets598.xml", "tweets134.xml", "tweets442.xml", "tweets122.xml", "tweets202.xml"));

	List<String> FAVOUR_as_AGAINST_hillary = new ArrayList<String>(
			Arrays.asList("tweets1993.xml", "tweets1691.xml", "tweets1711.xml","tweets2067.xml","tweets1890.xml","tweets1879.xml"));

	List<String> FAVOUR_as_AGAINST_abortion = new ArrayList<String>(
			Arrays.asList("tweets2640.xml","tweets2798.xml","tweets2418.xml","tweets2499.xml","tweets2477.xml","tweets2367.xml","tweets2809.xml"));
	
	List<String> FAVOUR_as_AGAINST_feminist = new ArrayList<String>(
			Arrays.asList("tweets1118.xml","tweets1606.xml","tweets1272.xml","tweets1307.xml","tweets1282.xml"));
	
	List<String> AGAINST_as_FAVOUR_climate = new ArrayList<String>(
			Arrays.asList("tweets906.xml","tweets908.xml","tweets688.xml","tweets660.xml","tweets949.xml"));
	
	List<String> FAVOUR_as_NONE = new ArrayList<String>(
			Arrays.asList("tweets1831.xml", "tweets2074.xml", "tweets471.xml"));

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		DocumentMetaData metaData = JCasUtil.selectSingle(jcas, DocumentMetaData.class);

		if (FAVOUR_as_AGAINST_abortion.contains(metaData.getDocumentId())) {
			System.out.println("---------------------");
			System.out.println(jcas.getDocumentText());
			System.out.println(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget());
			FrequencyDistribution<String> documentNgrams = null;
			documentNgrams = NGramUtils.getDocumentNgrams(jcas, ngramLowerCase, filterPartialStopwordMatches, ngramMinN,
					ngramMaxN, stopwords);

			mainloop: for (String topNgram : topKSet.getKeys()) {
				for (String ngram : documentNgrams.getKeys()) {

					if (topNgram.equals(ngram)) {

						System.out.println(ngram);
					}
					// print(jcas,ngram);
				}
			}
		}
		// System.out.println(NGramUtils.getDocumentNgrams(jcas, ngramLowerCase,
		// filterPartialStopwordMatches, ngramMinN, ngramMaxN,
		// stopwords).getMostFrequentSamples(15));
		// System.out.println(topKSet.getMostFrequentSamples(1000));
		return new Feature("tst", 0).asSet();
	}

	private void print(JCas jcas, String ngram) {
		System.out.println(ngram);
	}

}
