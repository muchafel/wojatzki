package de.uni_due.ltl.featureExtractors.explcitVocab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uni_due.ltl.featureExtractors.commentNgrams.CommentNGramMetaCollector;

import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.meta.MetaCollector;
import org.dkpro.tc.api.features.meta.MetaCollectorConfiguration;
import org.dkpro.tc.api.features.meta.MetaDependent;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.meta.LuceneNGramMetaCollector;

public abstract class BinCasMetaDependent extends FeatureExtractorResource_ImplBase
		implements FeatureExtractor, MetaDependent {

	public static final String PARAM_BINCAS_DIR = "BinCasDir";
	@ConfigurationParameter(name = PARAM_BINCAS_DIR, mandatory = true)
	protected String binCasDir;

	@Override
	public List<MetaCollectorConfiguration> getMetaCollectorClasses(Map<String, Object> parameterSettings)
			throws ResourceInitializationException {
		return Arrays.asList(new MetaCollectorConfiguration(BinCasMetaCollector.class, parameterSettings).addStorageMapping(BinCasMetaCollector.PARAM_TARGET_BIN_CAS_DIR, "BinCasDir", "BinCasDir"));
	}
}
