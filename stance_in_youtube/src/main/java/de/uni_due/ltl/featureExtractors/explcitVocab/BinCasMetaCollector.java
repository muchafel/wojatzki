package de.uni_due.ltl.featureExtractors.explcitVocab;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasWriter;

import org.dkpro.tc.api.features.meta.MetaCollector;
import org.dkpro.tc.api.type.TextClassificationOutcome;

public class BinCasMetaCollector extends MetaCollector {

	
	public static final String PARAM_TARGET_BIN_CAS_DIR = "binCasDir";
	@ConfigurationParameter(name = PARAM_TARGET_BIN_CAS_DIR, mandatory = true)
	protected String bincasDir;
	
	

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		AnalysisEngine writer = null;
		try {
			writer = getWriterEngine(bincasDir);
		} catch (ResourceInitializationException e) {
			throw new AnalysisEngineProcessException(e);
		}
		writer.process(jcas);
        writer.collectionProcessComplete();
	}

	private static AnalysisEngine getWriterEngine(String dir) throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		System.out.println("write to "+dir);
		AnalysisEngine engine = null;
			builder.add(createEngineDescription(
					createEngineDescription(BinaryCasWriter.class, 
					        BinaryCasWriter.PARAM_FORMAT, "6", 
					        BinaryCasWriter.PARAM_TARGET_LOCATION, dir,
					        BinaryCasWriter.PARAM_OVERWRITE, true,
					        BinaryCasWriter.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin")
					));
			engine = builder.createAggregate();
		return engine;
	}
}
