package de.uni_due.ltl.corpusInspection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.uni_due.ltl.util.TargetSets;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import de.unidue.ltl.evaluation.EvaluationData;
import io.YouTubeReader;

public class Inspect_SubDebate_Cooccurrence {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		EvaluationData<String> evalData= new EvaluationData<>();
		for (String explicitTarget : TargetSets.targets_Set1) {
			inspectCooccurence(explicitTarget,"1",baseDir+"/youtubeStance/corpus_curated/bin_preprocessed/",evalData);
		}
		for (String explicitTarget : TargetSets.targets_Set2) {
			inspectCooccurence(explicitTarget,"2",baseDir+"/youtubeStance/corpus_curated/bin_preprocessed/",evalData);
		}
		
		System.out.println(new ConfusionMatrix<>(evalData).toString());

	}

	private static void inspectCooccurence(String explicitTarget, String set, String path, EvaluationData<String> evalData) throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, path, YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,set);
	
		Map<String,FrequencyDistribution<String>> result= new HashMap<>();
		result.put(explicitTarget, new FrequencyDistribution<>());
		for (JCas jcas : new JCasIterable(reader)) {
			for (TextClassificationOutcome outcome : JCasUtil.select(jcas, TextClassificationOutcome.class)) {
//				if(!outcome.getOutcome().equals("AGAINST"))continue;
				if(set.equals("1")){
//					System.out.println(explicitTarget);
					for(curated.Explicit_Stance_Set1 explicitStance: JCasUtil.selectCovered(curated.Explicit_Stance_Set1.class, outcome)){
//						System.out.println(explicitStance.getTarget()+ " "+explicitStance.getPolarity());
						if(explicitStance.getTarget().equals(explicitTarget) && !explicitStance.getPolarity().equals("NONE")){
//							System.out.println("FOUND "+explicitTarget);
							result.get(explicitTarget).inc("OCC");
							evalData.register(explicitTarget, explicitTarget);
							for(curated.Explicit_Stance_Set1 explicitStanceInner: JCasUtil.selectCovered(curated.Explicit_Stance_Set1.class, outcome)){
								if(!explicitStanceInner.getTarget().equals(explicitTarget) && !explicitStanceInner.getPolarity().equals("NONE")){
									result.get(explicitTarget).inc(explicitStanceInner.getTarget());
									evalData.register(explicitTarget, explicitStanceInner.getTarget());
								}
							}
							for(curated.Explicit_Stance_Set2 explicitStanceInner: JCasUtil.selectCovered(curated.Explicit_Stance_Set2.class, outcome)){
								if(!explicitStanceInner.getTarget().equals(explicitTarget) && !explicitStanceInner.getPolarity().equals("NONE")){
									result.get(explicitTarget).inc(explicitStanceInner.getTarget());
									evalData.register(explicitTarget, explicitStanceInner.getTarget());
								}
							}
						}
					}
				}
				if(set.equals("2")){
					for (curated.Explicit_Stance_Set2 explicitStance : JCasUtil.selectCovered(curated.Explicit_Stance_Set2.class, outcome)) {
						if (explicitStance.getTarget().equals(explicitTarget)&& !explicitStance.getPolarity().equals("NONE")) {
							result.get(explicitTarget).inc("OCC");
							evalData.register(explicitTarget, explicitTarget);
							for(curated.Explicit_Stance_Set2 explicitStanceInner: JCasUtil.selectCovered(curated.Explicit_Stance_Set2.class, outcome)){
								if(!explicitStanceInner.getTarget().equals(explicitTarget) && !explicitStanceInner.getPolarity().equals("NONE")){
									result.get(explicitTarget).inc(explicitStanceInner.getTarget());
									evalData.register(explicitTarget, explicitStanceInner.getTarget());
								}
							}
							for(curated.Explicit_Stance_Set1 explicitStanceInner: JCasUtil.selectCovered(curated.Explicit_Stance_Set1.class, outcome)){
								if(!explicitStanceInner.getTarget().equals(explicitTarget) && !explicitStanceInner.getPolarity().equals("NONE")){
									result.get(explicitTarget).inc(explicitStanceInner.getTarget());
									evalData.register(explicitTarget, explicitStanceInner.getTarget());
								}
							}
						}
					}
				}
			}
		}
		for (String target : result.keySet()) {
			System.out.println("*****");
			FrequencyDistribution<String> restultFd = result.get(target);
			System.out.println(target+" "+restultFd.getCount("OCC"));
			for (String cooc : restultFd.getMostFrequentSamples(15)) {
				System.out.println("\t"+cooc + " " + restultFd.getCount(cooc));
			}
		}
	}

}
