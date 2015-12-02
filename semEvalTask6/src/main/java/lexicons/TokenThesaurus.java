package lexicons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import util.SimilarityHelper;

public class TokenThesaurus {

	private Map<String, Set<String>> tokenBOWMapping;
	private FrequencyDistribution<String> originalFd;
	private FrequencyDistribution<String> normalizedFd;
	

	public TokenThesaurus(FrequencyDistribution<String> fd) {
		this.tokenBOWMapping= createTokenBOWMApping(fd);
		this.originalFd=fd;
		this.normalizedFd= normalizeFd(tokenBOWMapping);
	}

	private FrequencyDistribution<String> normalizeFd(Map<String, Set<String>> tokenBOWMapping) {
		FrequencyDistribution<String> fd= new FrequencyDistribution<String>();
		for(String token: tokenBOWMapping.keySet()){
			int counts=0;
			for(String bowWord: tokenBOWMapping.get(token)){
				counts+=originalFd.getCount(bowWord);
			}
			fd.addSample(token, counts);
		}
		return fd;
	}

	private Map<String, Set<String>> createTokenBOWMApping(FrequencyDistribution<String> fd) {
		Map<String, Set<String>> tokenBOWMapping= new HashMap<String, Set<String>>();
		for(String token : fd.getKeys()){
			tokenBOWMapping.put(token, new HashSet<String>());
			for(String bowCandidate : fd.getKeys()){
				if(SimilarityHelper.WordsAreSimilar(token,bowCandidate) || token.equals(bowCandidate))tokenBOWMapping.get(token).add(bowCandidate);
			}
		}
		return tokenBOWMapping;
	}

	public FrequencyDistribution<String> getNormalizedFrequencyDistribution() {
		return this.normalizedFd;
	}

}
