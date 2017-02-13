package de.uni_due.subdebateInfluence;

import java.io.IOException;
import java.util.HashSet;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uni_due.ltl.featureExtractors.wordembeddings.WordEmbeddingLexicon;
import de.uni_due.ltl.util.TargetSets;
import de.unidue.ltl.evaluation.ConfusionMatrix;
import io.YouTubeReader;

public class InspectClassificationRelevantProperties {
private static WordEmbeddingLexicon lexicon;

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		try {
			lexicon = new WordEmbeddingLexicon("src/main/resources/list/prunedEmbeddings_data_reddit_idebate.84B.300d.txt");
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		System.out.println("DKPRO_HOME: " + baseDir);
		ConfusionMatrix<String> confusionMatrix= new ConfusionMatrix<>();
		for (String explicitTarget : TargetSets.targets_Set1) {
			inspectProperties(explicitTarget,"1",baseDir+"/youtubeStance/corpus_curated/bin_preprocessed/",confusionMatrix);
		}
		for (String explicitTarget : TargetSets.targets_Set2) {
			inspectProperties(explicitTarget,"2",baseDir+"/youtubeStance/corpus_curated/bin_preprocessed/",confusionMatrix);
		}

	}

	private static void inspectProperties(String explicitTarget, String set, String path,
			ConfusionMatrix<String> confusionMatrix) throws ResourceInitializationException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, path, YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,set);
		
		HashSet<String> types= new HashSet<>();
		int numberOfTokens=0;
		int containedInEmbeddings=0;
		int instances=0;
		
		
		for (JCas jcas : new JCasIterable(reader)) {
			for (TextClassificationOutcome outcome : JCasUtil.select(jcas, TextClassificationOutcome.class)) {
				if (set.equals("1")) {
					for (curated.Explicit_Stance_Set1 explicitStance : JCasUtil
							.selectCovered(curated.Explicit_Stance_Set1.class, outcome)) {
						if (explicitStance.getTarget().equals(explicitTarget)
								&& !explicitStance.getPolarity().equals("NONE")) {
							instances++;
							for(Token t: JCasUtil.selectCovered(Token.class, outcome)){
								types.add(t.getCoveredText().toLowerCase());
								numberOfTokens++;
								if(lexicon.getLexicon().containsKey(t.getCoveredText().toLowerCase())){
									containedInEmbeddings++;
								}
							}
						}
					}
				}
				if (set.equals("2")) {
					for (curated.Explicit_Stance_Set2 explicitStance : JCasUtil
							.selectCovered(curated.Explicit_Stance_Set2.class, outcome)) {
						if (explicitStance.getTarget().equals(explicitTarget)
								&& !explicitStance.getPolarity().equals("NONE")) {
							instances++;
							for(Token t: JCasUtil.selectCovered(Token.class, outcome)){
								types.add(t.getCoveredText().toLowerCase());
								numberOfTokens++;
								if(lexicon.getLexicon().containsKey(t.getCoveredText().toLowerCase())){
									containedInEmbeddings++;
								}
							}
						}
					}
				}
			}
		}
		System.out.println(explicitTarget);
		System.out.println("\t types: "+types.size()+" numberOfTokens: "+numberOfTokens+ " numberOfTokens (normalized) "+(double)numberOfTokens/(double)instances);
		System.out.println("\t Ratio: "+(double)types.size()/(double)numberOfTokens);
		System.out.println("\t containedInEmbeddings: "+containedInEmbeddings+" artion "+(double)containedInEmbeddings/(double)numberOfTokens);
	}

}
