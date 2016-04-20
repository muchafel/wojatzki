package util;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class CollocationMeasureHelper {
	private FrequencyDistribution<String> positiveFreqencyDistribution;
	private FrequencyDistribution<String> negativeFrequencyDistribution;
	private long N;

	/**
	 * g-mean measure(word, positive)- -measure(word,negative)
	 * 
	 * @param word
	 * @return
	 */
	public float getDiffOfGMeans(String word) {
		return getGMean(word, positiveFreqencyDistribution) - getGMean(word, negativeFrequencyDistribution);
	}

	/**
	 * g-mean measure= found count of word/root(N*expected count of word)
	 * 
	 * @param word
	 * @param positiveFreqencyDistribution2
	 * @return
	 */
	private float getGMean(String word, FrequencyDistribution<String> freq) {
		double foundCount = freq.getCount(word);
		float expectedCount = getExpectedCount(word, freq);
		return (float) (foundCount / Math.sqrt(N * expectedCount));
	}

	/**
	 * dice measure= found count of word/ 2*sum of words in distribution + count
	 * of word in both distributions
	 * 
	 * @param word
	 * @param positiveFreqencyDistribution2
	 * @return
	 */
	private float getDice(String word, FrequencyDistribution<String> freq) {
		double foundCount = freq.getCount(word);
		float countInBoth = positiveFreqencyDistribution.getCount(word) + negativeFrequencyDistribution.getCount(word);
		return (float) (2 * foundCount / freq.getN() + countInBoth);
	}

	/**
	 * poisson stirling = found_count* (log found_count - log expected_count
	 * -1)/log(10)
	 * 
	 * @param word
	 * @param positiveFreqencyDistribution2
	 * @return
	 */
	private float getPoissonStirling(String word, FrequencyDistribution<String> freq) {
		float foundCount = (float) freq.getCount(word);
		float expectedCount = getExpectedCount(word, freq);
		float log_foundCount;
		if (foundCount == 0) {
			log_foundCount = 0;
		} else {
			log_foundCount = (float) Math.log(foundCount);
		}
		float log_expectedCount;
		if (expectedCount == 0) {
			log_expectedCount = 0;
		} else
			log_expectedCount = (float) Math.log(expectedCount);
		float result = foundCount * (log_foundCount - log_expectedCount - 1);
		return (float) (result / Math.log(10));
	}

	/**
	 * expected count= C1(all observed entities under attribute|all words in
	 * freq) *R1(all occurrences of attribute in the study | counts of word in
	 * both freqs) /N
	 * 
	 * @param word
	 * @param freq
	 * @return
	 */
	private float getExpectedCount(String word, FrequencyDistribution<String> freq) {
		float C1 = freq.getN();
		float R1 = positiveFreqencyDistribution.getCount(word) + negativeFrequencyDistribution.getCount(word);
		return C1 * R1 / N;
	}

	public CollocationMeasureHelper(FrequencyDistribution<String> positive, FrequencyDistribution<String> negative) {
		this.positiveFreqencyDistribution = positive;
		this.negativeFrequencyDistribution = negative;
		this.N = positive.getN() + negative.getN();
	}

	/**
	 * poisson stirling measure(word, positive) - measure(word,negative)
	 * 
	 * @param word
	 * @return
	 */
	public float getDiffOfPoissonStirling(String word) {
		// System.out.println("+:
		// "+getPoissonStirling(word,positiveFreqencyDistribution));
		// System.out.println("-:
		// "+getPoissonStirling(word,negativeFrequencyDistribution));
		return getPoissonStirling(word, positiveFreqencyDistribution)
				- getPoissonStirling(word, negativeFrequencyDistribution);
	}

	/**
	 * dice measure(word, positive) - measure(word,negative)
	 * 
	 * @param word
	 * @return
	 */
	public float getDiffOfDice(String word) {
		return getDice(word, positiveFreqencyDistribution) - getDice(word, negativeFrequencyDistribution);
	}

	/**
	 * dice measure(word, positive) - measure(word,negative)
	 * 
	 * @param word
	 * @return
	 */
	public float getDiffOfChi(String word) {
//		System.out.println("+: "+getChi(word,positiveFreqencyDistribution));
//		System.out.println("-: "+getChi(word,negativeFrequencyDistribution));
		return getChi(word, positiveFreqencyDistribution) - getChi(word, negativeFrequencyDistribution);
	}

	/**
	 * chi_squared= (found-count-excpected_count)^2/excpectedCount
	 * @param word
	 * @param negativeFrequencyDistribution2
	 * @return
	 */
	private float getChi(String word, FrequencyDistribution<String> freq) {
//		System.out.println(freq.getCount(word) +" | "+getExpectedCount(word, freq));
		float foundCount = (float) freq.getCount(word);
		float expectedCount = getExpectedCount(word, freq);
		return (float) Math.pow(foundCount-expectedCount,2)/expectedCount;
	}

}
