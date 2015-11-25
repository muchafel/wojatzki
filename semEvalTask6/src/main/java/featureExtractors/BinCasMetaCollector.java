package featureExtractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasWriter;
import de.tudarmstadt.ukp.dkpro.tc.api.features.meta.MetaCollector;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.features.ngram.LuceneNGramDFE;
import util.PreprocessingPipeline;
import util.StanceConstants;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;

public class BinCasMetaCollector extends MetaCollector {

	@ConfigurationParameter(name = SummedStanceDFE.PARAM_BINCAS_DIR, mandatory = true)
    protected String wordStanceDir;
	protected AnalysisEngine writer;
	
	
	
	@Override
	public Map<String, String> getParameterKeyPairs() {
		 Map<String, String> mapping = new HashMap<String, String>();
	     mapping.put(SummedStanceDFE.PARAM_BINCAS_DIR, "wordStanceLexicon");
	     return mapping;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		AnalysisEngine writer = null;
		try {
			writer = createEngine(
			        BinaryCasWriter.class, 
			        BinaryCasWriter.PARAM_FORMAT, "6", 
			        BinaryCasWriter.PARAM_TARGET_LOCATION, wordStanceDir,
			        BinaryCasWriter.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
        writer.process(jcas);
        writer.collectionProcessComplete();
		
	}

}
