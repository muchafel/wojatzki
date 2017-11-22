package assertionRegression.featureExtractors;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADV;

public class AdjEndingFeatureExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_ENDING1 = "EndingAble";
	public static final String FN_ENDING2 = "EndingAl";
	public static final String FN_ENDING3 = "EndingFul";
	public static final String FN_ENDING4 = "EndingIble";
	public static final String FN_ENDING5 = "EndingLess";
	public static final String FN_ENDING6 = "EndingOus";
	public static final String FN_ENDING7 = "EndingIve";
	public static final String FN_ENDING8 = "EndingIc";
	public static final String FN_ENDING9 = "EndingLy"; // adverb, but anyway

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) {

		int able = 0;
		int al = 0;
		int ful = 0;
		int ible = 0;
		int ic = 0;
		int ive = 0;
		int less = 0;
		int ous = 0;
		int ly = 0;

		int n = 0;
		for (ADJ adj : JCasUtil.selectCovered(jcas, ADJ.class, target)) {
			n++;

			String text = adj.getCoveredText().toLowerCase();
			if (text.endsWith("able")) {
				able++;
			} else if (text.endsWith("al")) {
				al++;
			} else if (text.endsWith("ful")) {
				ful++;
			} else if (text.endsWith("ible")) {
				ible++;
			} else if (text.endsWith("ic")) {
				ic++;
			} else if (text.endsWith("ive")) {
				ive++;
			} else if (text.endsWith("less")) {
				less++;
			} else if (text.endsWith("ous")) {
				ous++;
			}
		}

		int m = 0;
		for (ADV adv : JCasUtil.select(jcas, ADV.class)) {
			m++;

			String text = adv.getCoveredText().toLowerCase();
			if (text.endsWith("ly")) {
				ly++;
			}
		}

		Set<Feature> featSet = new HashSet<Feature>();
		if (n > 0) {
			featSet.add(new Feature(FN_ENDING1, (double) able * 100 / n));
			featSet.add(new Feature(FN_ENDING2, (double) al * 100 / n));
			featSet.add(new Feature(FN_ENDING3, (double) ful * 100 / n));
			featSet.add(new Feature(FN_ENDING4, (double) ible * 100 / n));
			featSet.add(new Feature(FN_ENDING5, (double) less * 100 / n));
			featSet.add(new Feature(FN_ENDING6, (double) ous * 100 / n));
			featSet.add(new Feature(FN_ENDING7, (double) ive * 100 / n));
			featSet.add(new Feature(FN_ENDING8, (double) ic * 100 / n));
		}else{
			featSet.add(new Feature(FN_ENDING1, 0.0));
			featSet.add(new Feature(FN_ENDING2,  0.0));
			featSet.add(new Feature(FN_ENDING3,  0.0));
			featSet.add(new Feature(FN_ENDING4,  0.0));
			featSet.add(new Feature(FN_ENDING5,  0.0));
			featSet.add(new Feature(FN_ENDING6,  0.0));
			featSet.add(new Feature(FN_ENDING7, 0.0));
			featSet.add(new Feature(FN_ENDING8, 0.0));
		}
		if (m > 0) {
			featSet.add(new Feature(FN_ENDING9, (double) ly * 100 / m));
		}else{
			featSet.add(new Feature(FN_ENDING9, 0.0));
		}

		return featSet;
	}
}