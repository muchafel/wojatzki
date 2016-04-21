package annotationStudy.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import consolidatedTypes.SubTarget;
import curatedTypes.CuratedIrony;
import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import curatedTypes.CuratedUnderstandability;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;

public class InspectStanceDistributions {

	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList("secularism", "Same-sex marriage",
			"religious_freedom", "Conservative_Movement", "Freethinking", "Islam", "No_evidence_for_religion", "USA",
			"Supernatural_Power_Being", "Life_after_death", "Christianity"));
	
	public static void main(String[] args) throws IOException, ResourceInitializationException {
		InspectStanceDistributions inspection = new InspectStanceDistributions();
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
//		inspection.inspectDistributionForMainTarget(baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all");
		inspection.inspectPatterns(baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all");
	}

	private void inspectPatterns(String location) throws ResourceInitializationException{
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, location, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		for (JCas jcas : new JCasIterable(reader)) {
			//ignore irony and ununderstandability
			if (JCasUtil.select(jcas, CuratedIrony.class).isEmpty() && JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()) {
				
				//ignore NONE
				if(!JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity().equals("NONE")){
					String targetString=getTargetString(jcas);
					fd.inc(targetString);
				}
			}
		}
		for (String pattern : fd.getMostFrequentSamples(fd.getKeys().size())) {
			System.out.println(pattern + " " + fd.getCount(pattern));
		}
		
	}

	private String getTargetString(JCas jcas) {
		String targetString= "Atheism:"+JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity();
		for(String subTarget:subTargets){
			for(CuratedSubTarget target: JCasUtil.select(jcas,CuratedSubTarget.class)){
				if(target.getTarget().equals(subTarget) && !target.getPolarity().equals("NONE")){
					targetString+="_"+target.getTarget()+":"+target.getPolarity();
				}
			}
		}
		return targetString;
	}

	private void inspectDistributionForMainTarget(String location)
			throws ResourceInitializationException {
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		FrequencyDistribution<String> fd2 = new FrequencyDistribution<>();
		FrequencyDistribution<String> fd3 = new FrequencyDistribution<>();
		FrequencyDistribution<String> fd4 = new FrequencyDistribution<>();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, location, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		for (JCas jcas : new JCasIterable(reader)) {
			fd.inc(JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity());

			if (JCasUtil.select(jcas, CuratedIrony.class).isEmpty()) {
				fd2.inc(JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity());
			}
			if (JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()) {
				fd3.inc(JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity());
			}
			if (JCasUtil.select(jcas, CuratedIrony.class).isEmpty() && JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()) {
				fd4.inc(JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity());
			}

		}
		System.out.println("unfiltered "+ fd.getN());
		for (String polarity : fd.getMostFrequentSamples(3)) {
			System.out.println(polarity + " " + fd.getCount(polarity));
		}
		System.out.println("wo irony "+ fd2.getN());
		for (String polarity : fd2.getMostFrequentSamples(3)) {
			System.out.println(polarity + " " + fd2.getCount(polarity));
		}
		System.out.println("wo understandability "+ fd3.getN());
		for (String polarity : fd3.getMostFrequentSamples(3)) {
			System.out.println(polarity + " " + fd3.getCount(polarity));
		}
		System.out.println("wo both "+ fd4.getN());
		for (String polarity : fd4.getMostFrequentSamples(3)) {
			System.out.println(polarity + " " + fd4.getCount(polarity));
		}

	}

}
