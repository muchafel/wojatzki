package de.uni_due.ltl.util.iaa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import java_cup.symbol;
import java_cup.symbol_set;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import webanno.custom.Debate_Stance;
import webanno.custom.Explicit_Stance_Set1;
import webanno.custom.Explicit_Stance_Set2;

public class InterAnnotatorAgreement {

	static boolean doUnzipping=false;
	static String annotationFolder="/youtubeStance/annotation/Stance_Youtube_Prestudy2_2016-09-15_1302/";
	static ArrayList<String> annotators = new ArrayList<String>(Arrays.asList("NiklasMeyer","BenjaminGansert", "MarvinHoltermann"));
	static ArrayList<String> targets_Set1 = new ArrayList<String>(Arrays.asList(
			"Bodies of people sentenced to death should be used to repay society (e.g. medical experiments, organ donation)",	
//			"Death Penalty (Debate)",
//			"Death Penalty for especially heinous crimes (murder, mass murder, rape, child molestation etc.)",	
			"Death Penalty should be done by gunshot",	
//			"Death Penalty should be done by hypoxia",	
//			"Death Penalty should be done by the electric chair",	
			"Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",	
//			"If Death Penalty is allowed, abortion should be legal, too.",	
//			"If Death Penalty is allowed, euthanasia should be allowed",	
//			"If one is against death penalty, one has to be against all state use of lethal force (e.g. military)",	
//			"If studies were to show that the Death Penalty is deterrend, it would be immoral to oppose it",	
			"In certain cases, capital punishment shouldn’t have to be humane but more harsh",
//			"Life-long prison should be replaced by Death Penalty",	
			"The level of certainty that is necessary for Death Penalty is unachievable"
//			,
//			"There is currently no human form of Death Penalty",	
//			"Witnessing Death Penalty can have a negative impact on humans psyche (e.g. for the executioner)"
			));
	
	static ArrayList<String> targets_Set2 = new ArrayList<String>(Arrays.asList(
//			"Death Penalty (Debate)",
//			"Execution helps alleviate the overcrowding of prisons.",	
//			"Execution prevents the accused from committing further crimes.",	
			"It helps the victims’ families achieve closure.",	
			"State-sanctioned killing is wrong (state has not the right).",	
			"The death penalty can produce irreversible miscarriages of justice.",	
			"The death penalty deters crime.",	
			"The death penalty is a financial burden on the state.",	
			"The death penalty should apply as punishment for first-degree murder; an eye for an eye.",	
			"Wrongful convictions are irreversible."
			));
	
	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		if(doUnzipping){
			extract(new File(baseDir+annotationFolder+"annotation"));
		}
		
		Map<String,Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions = annotatorToSentences(baseDir+annotationFolder+"annotation_unzipped");
		System.out.println(annotatorToSentenceToDecisions);
		inspect(annotatorToSentenceToDecisions);

		interAnnotatorAgreementDebateStance(annotatorToSentenceToDecisions,annotators);
//
		for(String target: targets_Set1){
			interAnnotatorAgreementTarget_Set1(annotatorToSentenceToDecisions,annotators,target);
		}
		for(String target: targets_Set2){
			interAnnotatorAgreementTarget_Set2(annotatorToSentenceToDecisions,annotators,target);
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



	private static void interAnnotatorAgreementTarget_Set1(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators2, String target) throws Exception {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}
		
		//for each document
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				if(annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()!=annotators.size()){
					System.err.println("skip sentence because not enough annos");
				}else{
//					System.out.println(sentenceId+ " "+ annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size());
					if(annotators.size()==2){
						study.addItem(getAnnotatorExplicitStance_1(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
					else if(annotators.size()==3){
						System.out.println(sentenceId+" "+getAnnotatorExplicitStance_1(annotators.get(0),
								annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
								+ " "
								+ getAnnotatorExplicitStance_1(annotators.get(1),
										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
								+ " " + getAnnotatorExplicitStance_1(annotators.get(1),
										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target));
						study.addItem(getAnnotatorExplicitStance_1(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
				}
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);

		System.out.println("**TARGET**"+ target);
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		
	}
	
	private static void interAnnotatorAgreementTarget_Set2(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators2, String target) throws Exception {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}
		
		//for each document
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				if(annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()!=annotators.size()){
					System.err.println("skip sentence because not enough annos");
				}else{
//					System.out.println(sentenceId+ " "+ annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size());
					if(annotators.size()==2){
						study.addItem(getAnnotatorExplicitStance_2(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
					else if(annotators.size()==3){
						System.out.println(sentenceId+" "+getAnnotatorExplicitStance_2(annotators.get(0),
								annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
								+ " "
								+ getAnnotatorExplicitStance_2(annotators.get(1),
										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
								+ " " + getAnnotatorExplicitStance_2(annotators.get(1),
										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target));
						study.addItem(getAnnotatorExplicitStance_2(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
				}
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);

		System.out.println("**TARGET**"+ target);
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		
	}




	private static String getAnnotatorExplicitStance_1(String annotatorName, List<AnnotatorDecision> decisisons, String target) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getExplicitStances_Set1() == null || decision.getExplicitStances_Set1().isEmpty()){
					return "NONE";
				}
				for(Explicit_Stance_Container stance:decision.getExplicitStances_Set1()){
					if(stance.getTarget().equals(target)){
						return stance.getPolarity();
					}
				}
			}
		}
		return "NONE";
	}

	
	private static String getAnnotatorExplicitStance_2(String annotatorName, List<AnnotatorDecision> decisisons, String target) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getExplicitStances_Set2() == null || decision.getExplicitStances_Set2().isEmpty()){
					return "NONE";
				}
				for(Explicit_Stance_Container stance:decision.getExplicitStances_Set2()){
					if(stance.getTarget().equals(target)){
						return stance.getPolarity();
					}
				}
			}
		}
		return "NONE";
	}
	


	private static void interAnnotatorAgreementDebateStance(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions, ArrayList<String> annotators) throws Exception {
		
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}
		
		//for each document
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				
				if(annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()!=annotators.size()){
					System.err.println("skip sentence because not enough annos"+annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()+" != "+annotators.size());
				}else{
//					System.out.println(sentenceId+ " "+ annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size());
					if(annotators.size()==2){
						study.addItem(getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
					}
					else if(annotators.size()==3){
						System.out.println(documentId+" sentence id: "+sentenceId+" "+ getSentenceId(annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
						study.addItem(getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getAnnotatorDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
					}
				}
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
		System.out.println("DEBATE STANCE");
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
	}


	


	private static String getSentenceId(List<AnnotatorDecision> list) {
		List<String>resultList=new ArrayList<>();
		for(AnnotatorDecision decision:list){
			resultList.add(String.valueOf(decision.getSentenceId()+" "+decision.getAnnotator()));
		}
		return StringUtils.join(resultList," ");
	}



	private static String getAnnotatorDebateStance(String annotatorName, List<AnnotatorDecision> decisisons) {
		for(AnnotatorDecision decision: decisisons){
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getStance() == null){
					System.err.println("WTF");
					return "NONE";
				}
				return decision.getStance().getPolarity();
			}
		}
		System.err.println("no matching annotator"+ annotatorName);
		return "NONE";
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
					throw new Exception("no debate stance in "+documentMetaData.getDocumentId()+":"+sentenceCount+ "annotator "+documentMetaData.getDocumentId());
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
