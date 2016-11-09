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

public class CreateTxtCorpus {

	static ArrayList<String> targets_Set1 = new ArrayList<String>(Arrays.asList(
			"Bodies of people sentenced to death should be used to repay society (e.g. medical experiments, organ donation)",	
			"Death Penalty (Debate)",
			"Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)",	
			"Death Penalty should be done by gunshot",	
			"Death Penalty should be done by hypoxia",	
			"Death Penalty should be done by the electric chair",	
			"Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",	
			"If Death Penalty is allowed, abortion should be legal, too.",	
			"If Death Penalty is allowed, euthanasia should be allowed",	
			"If one is against death penalty, one has to be against all state use of lethal force (e.g. military)",	
//			"If studies were to show that the Death Penalty is deterrend, it would be immoral to oppose it",	
			"In certain cases, capital punishment shouldn’t have to be humane but more harsh",
			"Life-long prison should be replaced by Death Penalty",	
			"The level of certainty that is necessary for Death Penalty is unachievable"
			,
			"There is currently no human form of Death Penalty"
//			,	
//			"Witnessing Death Penalty can have a negative impact on humans psyche (e.g. for the executioner)"
			));
	
	static ArrayList<String> targets_Set2 = new ArrayList<String>(Arrays.asList(
//			"Death Penalty (Debate)",
			"Execution helps alleviate the overcrowding of prisons.",	
			"Execution prevents the accused from committing further crimes.",	
			"It helps the victims’ families achieve closure.",	
			"State-sanctioned killing is wrong (state has not the right).",	
			"The death penalty can produce irreversible miscarriages of justice.",	
			"The death penalty deters crime.",	
			"The death penalty is a financial burden on the state.",	
			"The death penalty should apply as punishment for first-degree murder; an eye for an eye.",	
			"Wrongful convictions are irreversible."
			));
	
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
//		String loc=baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all"+"_wo_irony_understandability";
		String loc="/Users/michael/DKPRO_HOME/semevalTask6/annotationStudy/originalDebateStanceLabels/bin";
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, baseDir+"/youtubeStance/corpus_minorityVote/xmi", XmiReader.PARAM_PATTERNS, "*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");
		File f = new File("corpus_comparison.txt"); 
		String header="";
		for(String target1:targets_Set1){
			header+="\t"+target1;
		}
		for(String target2:targets_Set2){
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
			for(Sentence sentence:JCasUtil.select(jcas,Sentence.class)){
				String toPrint=JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
				String text=sentence.getCoveredText().replace(System.lineSeparator(), "");
				text=text.replace("\t", " ");
				text=text.replace("+", "'+");
				toPrint+="\t"+text;
				curated.Debate_Stance mainT=JCasUtil.selectCovered(jcas,curated.Debate_Stance.class,sentence).iterator().next();
				toPrint+="\t"+"DEBATE_STANCE:"+mainT.getPolarity().toLowerCase();
				
				for(String target1:targets_Set1){
					toPrint+="\t"+getPolarity_Set1(target1,JCasUtil.selectCovered(jcas, curated.Explicit_Stance_Set1.class,sentence));
				}
				for(String target2:targets_Set2){
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
