package featureExtractors;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.DocumentFeatureExtractor;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import featureExtractors.stanceLexicon.BinCasMetaDependent;
import featureExtractors.stanceLexicon.BinCasMetaDependent.RelevantTokens;

public class SimpleNounFreqencyDFE extends BinCasMetaDependent {

	private FrequencyDistribution<String> fd;
	
	public static final String PARAM_TOP_I_NOUNS = "top_i_nouns";
	@ConfigurationParameter(name = PARAM_TOP_I_NOUNS, mandatory = true)
	private int topI;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			fd=readFd(binCasDir);
			System.out.println(fd.getMostFrequentSamples(topI));
		} catch (IOException | UIMAException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	
	private FrequencyDistribution<String> readFd(String binCasDir) throws IOException, UIMAException {
		FrequencyDistribution<String> fd= new FrequencyDistribution<>();
		CollectionReader reader = CollectionReaderFactory.createReader(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, binCasDir, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		// iterate over all CASes that have been stored by the meta collector
		while (reader.hasNext()) {
			JCas jcas = JCasFactory.createJCas();
			reader.getNext(jcas.getCas());
			for(Token t : JCasUtil.select(jcas, Token.class)){
				String lowerCase= t.getCoveredText().toLowerCase();
				if(t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NNS")){
					fd.inc(lowerCase);
				}
			}
		}
		return fd;
	}


	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		int topIcontained=0;
		Set<Feature> features= new HashSet<Feature>();
		for(Token t : JCasUtil.select(jcas, Token.class)){
				if(fd.getMostFrequentSamples(topI).contains(t.getCoveredText().toLowerCase())){
					topIcontained++;
			}
		}
		features.add(new Feature("TopINounsContained", topIcontained));
		return features;
	}

}
