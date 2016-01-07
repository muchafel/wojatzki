package stanceClassificationTaskA;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import types.BiTriGramOutcomeFavorAgainst;

public class StackedBi_Tri_GramFavorAgainstDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		int bigramOutcome=0;
		
		//use comparison to STANCE and FAVOR to be applicable for stance vs none AND favor vs against classification
		if (JCasUtil.selectSingle(jcas, BiTriGramOutcomeFavorAgainst.class).getClassificationOutcome().equals("STANCE")
				|| JCasUtil.selectSingle(jcas, BiTriGramOutcomeFavorAgainst.class).getClassificationOutcome().equals("FAVOR")){
			bigramOutcome=1;
		}else{
			bigramOutcome=-1;
		}
			featList.add(new Feature("BI_TRI_GRAM_OUTCOME",bigramOutcome));
		return featList;
	}

}
