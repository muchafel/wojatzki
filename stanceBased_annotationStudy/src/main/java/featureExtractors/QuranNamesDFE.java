package featureExtractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;


/**
 * counts appearance of core-concepts strings
 * 
 */
public class QuranNamesDFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String NR_OF_MENTIONS = "nrOfMentions";

	public static final String PARAM_QURANNAMES_FILE_PATH = "quranNamesFilePath";
	@ConfigurationParameter(name = PARAM_QURANNAMES_FILE_PATH, mandatory = true)
	private File quranNamesFilePath;

	JCas jcas;
	Set<Feature> featList = new HashSet<Feature>();
	private Set<String> quranNames = new HashSet<String>();

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			quranNames = getCoreConcepts(quranNamesFilePath);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		double nrOfMentions = 0;

			for (String quranName : quranNames) {
				if (jcas.getDocumentText().contains(quranName)) {
					nrOfMentions++;
				}
			}

		featList.add(new Feature("Qurannames", nrOfMentions));
		return featList;
	}

	private Set<String> getCoreConcepts(File conceptsFilePath) throws IOException {
		Set<String> set = new HashSet<String>();
		for (String line : FileUtils.readLines(conceptsFilePath)) {
			set.add(line);
		}
		return set;
	}
}
