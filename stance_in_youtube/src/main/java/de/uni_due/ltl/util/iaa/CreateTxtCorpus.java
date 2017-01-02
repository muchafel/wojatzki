package de.uni_due.ltl.util.iaa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.token.type.Sentence_Type;

import curated.Direct_Insult;
import curated.Explicit_Stance_Set1;
import curated.Explicit_Stance_Set2;
import curated.NonTextual_Content;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.uni_due.ltl.util.TargetSets;

public class CreateTxtCorpus {

	
	
	static ArrayList<String> insultTags = new ArrayList<String>(Arrays.asList("Group"
			,"Person"
			,"User"
			));
	
	static ArrayList<String> nonTextualReference = new ArrayList<String>(Arrays.asList("Video"
			,"Foreigen Source"
			));
	
	
	public static void main(String[] args) throws ResourceInitializationException, IOException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
//		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
//				XmiReader.PARAM_SOURCE_LOCATION, baseDir+"/youtubeStance/corpus_minorityVote/xmi", XmiReader.PARAM_PATTERNS, "*.xmi", XmiReader.PARAM_LANGUAGE,
//				"en");
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, "/Users/michael/Dropbox/explicit targets PHASE II/curated2_deathPenalty_data/annotation_unzipped/xmis", XmiReader.PARAM_PATTERNS, "*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");
		File f = new File("corpus_curated.txt"); 
		String header="";
		for(String target1:TargetSets.targets_Set1){
			header+="\t"+target1;
		}
		for(String target2:TargetSets.targets_Set2){
			header+="\t"+target2;
		}
		for(String insultTag:insultTags){
			header+="\t"+insultTag;
		}
		for(String ref:nonTextualReference){
			header+="\t"+ref;
		}
		header+="\n";
		FileUtils.writeStringToFile(f, header,"UTF-8", true);
		for (JCas jcas : new JCasIterable(reader)) {
			System.out.println(JCasUtil.select(jcas,Sentence.class).size());
			for(Sentence sentence:JCasUtil.select(jcas,Sentence.class)){
				String toPrint=JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
				String text=sentence.getCoveredText().replace(System.lineSeparator(), "");
				text=text.replace("\t", " ");
				text=text.replace("+", "'+");
				toPrint+="\t"+text;
//				System.out.println(text);
				curated.Debate_Stance mainT=JCasUtil.selectCovered(jcas,curated.Debate_Stance.class,sentence).iterator().next();
				toPrint+="\t"+"DEBATE_STANCE:"+mainT.getPolarity();
				
				for(String target1:TargetSets.targets_Set1){
					toPrint+="\t"+getPolarity_Set1(target1,JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,sentence));
				}
				for(String target2:TargetSets.targets_Set2){
					toPrint+="\t"+getPolarity_Set2(target2,JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set2.class,sentence));
				}
				for(String insultTag:insultTags){
					toPrint+="\t"+getPolarity_Insult(insultTag,JCasUtil.selectCovered(jcas, curated.Direct_Insult.class,sentence));
				}
				for(String ref:nonTextualReference){
					toPrint+="\t"+getPolarity_Ref(ref,JCasUtil.selectCovered(jcas, curated.NonTextual_Content.class,sentence));
				}
				toPrint+="\n";
				System.out.println(toPrint);
				FileUtils.writeStringToFile(f, toPrint,"UTF-8", true);
			}
		}

	}


	private static String getPolarity_Ref(String ref, List<NonTextual_Content> selectCovered) {
		for(curated.NonTextual_Content refrence: selectCovered){
			if(refrence.getSource().equals(ref)){
				return ref;
			}
		}
		return "NONE";
	}


	private static String getPolarity_Insult(String insultTag, List<Direct_Insult> selectCovered) {
		for(curated.Direct_Insult insult: selectCovered){
			if(insult.getInsultTarget().equals(insultTag)){
				return insultTag;
			}
		}
		return "NONE";
	}


	private static String getPolarity_Set2(String target2, List<Explicit_Stance_Set2> selectCovered) {
		for(curated.Explicit_Stance_Set2 subTarget: selectCovered){
			if(subTarget.getTarget().equals(target2)){
				return subTarget.getPolarity();
			}
		}
		return "NONE";
	}


	private static String getPolarity_Set1(String target1, List<Explicit_Stance_Set1> list) {
		for(curated.Explicit_Stance_Set1 subTarget: list){
			if(subTarget.getTarget().equals(target1)){
				return subTarget.getPolarity();
			}
		}
		return "NONE";
	}

}
