package de.uni.due.ltl.interactiveStance.io;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;

public class EvaluationDataSet {

	private long numberOfFavor;
	private long numberOfAgainst;
	private long numberOfNone;
	private long numberOfInstances;

	private CollectionReaderDescription reader;

	public EvaluationDataSet(String path) throws ResourceInitializationException {
		FrequencyDistribution<String> classDistribution = new FrequencyDistribution<>();

		reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, path, TaskATweetReader.PARAM_PATTERNS, "*.xml",
				TaskATweetReader.PARAM_LANGUAGE, "en", TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

		for (JCas jcas : new JCasIterable(reader)) {
			classDistribution.inc(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance());
			numberOfInstances++;
		}
		System.out.println("found instances "+numberOfInstances);
		if (classDistribution.contains("FAVOR")) {
			numberOfFavor = classDistribution.getCount("FAVOR");
		}
		if (classDistribution.contains("AGAINST")) {
			numberOfAgainst = classDistribution.getCount("AGAINST");
		}
		if (classDistribution.contains("NONE")) {
			numberOfNone = classDistribution.getCount("NONE");
		}

	}

	public CollectionReaderDescription getDataReader() {
		return reader;
	}

	public long getNumberOfFavor() {
		return numberOfFavor;
	}

	public long getNumberOfAgainst() {
		return numberOfAgainst;
	}

	public long getNumberOfNone() {
		return numberOfNone;
	}

	public long getNumberOfInstances() {
		return numberOfInstances;
	}

}
