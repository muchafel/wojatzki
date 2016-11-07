package de.uni_due.ltl.util.iaa;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.FleissKappaAgreement;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;
import org.dkpro.tc.api.type.JCasId;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import it.unimi.dsi.fastutil.io.InspectableFileCachedInputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import webanno.custom.Debate_Stance;
import webanno.custom.Direct_Insult;
import webanno.custom.Explicit_Stance_Set1;
import webanno.custom.Explicit_Stance_Set2;
import webanno.custom.NonTextual_Content;

public class InterAnnotatorAgreement {

	static boolean doUnzipping=false;
	static String annotationFolder="/youtubeStance/annotation/final_v1/";
	static ArrayList<String> annotators = new ArrayList<String>(Arrays.asList("NiklasMeyer"
			,"BenjaminGansert"
			,"MarvinHoltermann"
			));
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
	
	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		if(doUnzipping){
			extract(new File(baseDir+annotationFolder+"annotation"));
		}
		
		System.out.println("create webanno datastructure");
		Map<String,Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions = annotatorToSentences(baseDir+annotationFolder+"annotation_unzipped");
//		System.out.println("data:");
//		System.out.println(annotatorToSentenceToDecisions);
//		inspect(annotatorToSentenceToDecisions);

		System.out.println("calculate IAA");
		iaa(annotatorToSentenceToDecisions);
		
	}
	
	
	
	



	private static void iaa(Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions) throws Exception {
		Map<String,Double> fleissKappas1= new HashMap<>();
		Map<String,Double> fleissKappas2= new HashMap<>();
		Map<String,Double> fleissKappasInsult= new HashMap<>();
		Map<String,Double> fleissNonTextReference= new HashMap<>();
		
		IAAHelper iaaHelper= new IAAHelper();
		
		/**
		 * this block calculates the kappas (on tag level + whole decision)
		 */
		
		fleissKappas1=iaaHelper.interAnnotatorAgreementDebateStance(annotatorToSentenceToDecisions,annotators,fleissKappas1);
//
		for(String target: targets_Set1){
			fleissKappas1=iaaHelper.interAnnotatorAgreementTarget_Set1(annotatorToSentenceToDecisions,annotators,target,fleissKappas1);
		}
		
		for(String target: targets_Set2){
			fleissKappas2=iaaHelper.interAnnotatorAgreementTarget_Set2(annotatorToSentenceToDecisions,annotators,target,fleissKappas2);
		}
		
		for(String insultTag: insultTags){
			fleissKappasInsult=iaaHelper.interAnnotatorAgreementInsult(annotatorToSentenceToDecisions,annotators,insultTag,fleissKappasInsult);
		}
		
		for(String reference: nonTextualReference){
			fleissNonTextReference=iaaHelper.interAnnotatorAgreementReference(annotatorToSentenceToDecisions,annotators,reference,fleissNonTextReference);
		}
		
		/**
		 * TODO: whole decision
		 */
		
		/**
		 * this block prints the results to console
		 */
		
		for(String target: fleissKappas1.keySet()){
			System.out.println(target+"\t"+fleissKappas1.get(target));
		}
		System.out.println("--------");
		
		for(String target: fleissKappas2.keySet()){
			System.out.println(target+"\t"+fleissKappas2.get(target));
		}
		System.out.println("----------");
		
		for(String tag: fleissKappasInsult.keySet()){
			System.out.println(tag+"\t"+fleissKappasInsult.get(tag));
		}
		System.out.println("----------");
		for(String tag: fleissNonTextReference.keySet()){
			System.out.println(tag+"\t"+fleissNonTextReference.get(tag));
		}
	}







	private static void inspect(Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions) {
		for(String docId: annotatorToSentenceToDecisions.keySet()){
			System.out.println(docId);
			for(int sentence: annotatorToSentenceToDecisions.get(docId).keySet()){
				System.out.println(sentence);
				for(AnnotatorDecision decisison: annotatorToSentenceToDecisions.get(docId).get(sentence)){
					System.out.println(decisison.getAnnotator()+ " "+decisison.getSentenceId());
					if(decisison.getStance() != null){
						System.out.println("\t"+decisison.getStance().getPolarity()+" on "+decisison.getStance().getCoveredText());
						System.out.println("\t"+decisison.getText());
					}
				}
			}
		}
		
	}


	



	private static String getSentenceId(List<AnnotatorDecision> list) {
		List<String>resultList=new ArrayList<>();
		for(AnnotatorDecision decision:list){
			resultList.add(String.valueOf(decision.getSentenceId()+" "+decision.getAnnotator()));
		}
		return StringUtils.join(resultList," ");
	}




	/**
	 * extracts the zipped files from the webanno structure
	 * 
	 * @param folder
	 */
	private static void extract(File folder) {

		for (File xmiFolder : folder.listFiles()) {
			if (xmiFolder.isDirectory()) {
				for (File annotation : xmiFolder.listFiles()) {
					// System.out.println(annotation.getName());
					// System.out.println(annotation.getName().substring(3));
					File newAnnoFolder = new File(folder.getParent() + "/annotation_unzipped/"
							+ xmiFolder.getName().substring(0, xmiFolder.getName().length() - 8));
					newAnnoFolder.mkdir();
					if (annotation.getName().substring(annotation.getName().length() - 3).equals("zip")) {
						System.out.println("unzip " + annotation.getName());
						unzip(annotation, newAnnoFolder);
					}
				}
			}
		}
		System.out.println("unzipping done");
	}
	
	/**
	 * consolidates all annotations in a datastructure that enables to
	 * Calculates interrater agreement writes the results for all subdescisison
	 * (each target) into a csv file in the path
	 */
	private static Map<String,Map<Integer, List<AnnotatorDecision>>> annotatorToSentences(String path) throws Exception {
		System.out.println(path);
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, path, XmiReader.PARAM_PATTERNS, "**/*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");

		Map<String,Map<Integer, List<AnnotatorDecision>>> result= new HashMap<String,Map<Integer, List<AnnotatorDecision>>>();
		for (JCas jcas : new JCasIterable(reader)) {
			DocumentMetaData documentMetaData= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
			int sentenceCount=0;
			for(Sentence sentence:JCasUtil.select(jcas, Sentence.class)){
//				System.out.println(documentMetaData.getDocumentId()+":"+sentenceCount +" text "+sentence.getCoveredText());
				AnnotatorDecision decisison= new AnnotatorDecision(documentMetaData.getDocumentId(), sentenceCount, documentMetaData.getDocumentTitle(),sentence.getCoveredText());
				
				if(JCasUtil.selectCovered(Debate_Stance.class, sentence).isEmpty()){
					throw new Exception("no debate stance in "+documentMetaData.getDocumentId()+": "+sentenceCount+ "  "+sentence.getCoveredText());
				}
				for (Debate_Stance stance : JCasUtil.selectCovered(Debate_Stance.class, sentence)) {
					decisison.setStance(stance,jcas);
//					System.out.println(stance);
				}
				List<Explicit_Stance_Set1> explicitStances_Set1= JCasUtil.selectCovered(Explicit_Stance_Set1.class, sentence);
				if(!explicitStances_Set1.isEmpty()){
					decisison.setExplicitStances_Set1(explicitStances_Set1);
				}
				List<Explicit_Stance_Set2> explicitStances_Set2= JCasUtil.selectCovered(Explicit_Stance_Set2.class, sentence);
				if(!explicitStances_Set2.isEmpty()){
					decisison.setExplicitStances_set2(explicitStances_Set2);
				}
				List<Direct_Insult> insultSet= JCasUtil.selectCovered(Direct_Insult.class, sentence);
				if(!insultSet.isEmpty()){
					decisison.setInsults(insultSet);
				}
				
				List<NonTextual_Content> referenceSet= JCasUtil.selectCovered(NonTextual_Content.class, sentence);
				if(!referenceSet.isEmpty()){
					decisison.setReferences(referenceSet);
				}
				
				result= addToResult(documentMetaData.getDocumentTitle(),sentenceCount,decisison,result);
				sentenceCount++;
			}
		}
		return result;
	}

	
	/**
	 * add info to desired data structure
	 * @param documentTitle
	 * @param sentenceCount
	 * @param decisison
	 * @param result
	 * @return
	 */
	private static Map<String, Map<Integer, List<AnnotatorDecision>>> addToResult(String documentTitle,
			int sentenceCount, AnnotatorDecision decisison, Map<String, Map<Integer, List<AnnotatorDecision>>> result) {
		if(result.containsKey(documentTitle)){
			addSentence(documentTitle, sentenceCount, decisison, result);
		}else{
			result.put(documentTitle, new HashMap<Integer, List<AnnotatorDecision>>());
			addSentence(documentTitle, sentenceCount, decisison, result);
		}
		return result;
	}

/**
 * 
 * @param documentTitle
 * @param sentenceCount
 * @param decisison
 * @param result
 */
	private static void addSentence(String documentTitle, int sentenceCount, AnnotatorDecision decisison,
			Map<String, Map<Integer, List<AnnotatorDecision>>> result) {
		
		if(sentenceCount==0){
			System.err.println("$$$$ "+decisison.getSentenceId()+" "+decisison.getAnnotator()+ " "+decisison.getStance().getPolarity()+" on "+decisison.getStance().getCoveredText());
			System.err.println("$$$$ "+decisison.getText());
		}
		
		if(result.get(documentTitle).containsKey(sentenceCount)){
			result.get(documentTitle).get(sentenceCount).add(decisison);
			if(sentenceCount==0){
				for(AnnotatorDecision d: result.get(documentTitle).get(sentenceCount)){
					System.out.println("<<<"+d.getStance().getPolarity()+" "+d.getAnnotator()+" "+d.getText());
				}
			}
		}else{
			result.get(documentTitle).put(sentenceCount, new ArrayList<AnnotatorDecision>());
			result.get(documentTitle).get(sentenceCount).add(decisison);
		}
		
	}



	private static void unzip(File fileEntry, File folder) {
		String source = fileEntry.getAbsolutePath();
		String destination = folder.getAbsolutePath();
		try {
			ZipFile zipFile = new ZipFile(source);
			zipFile.extractAll(destination);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		for (File unzipped : new File(destination).listFiles()) {
			if (unzipped.getName().substring(unzipped.getName().length() - 3).equals("xmi")
					&& !unzipped.getName().contains("tweet")) {
				File newName = new File(folder.getAbsolutePath() + "/" + folder.getName() + "_" + unzipped.getName());
				unzipped.renameTo(newName);
			}
		}
	}
}
