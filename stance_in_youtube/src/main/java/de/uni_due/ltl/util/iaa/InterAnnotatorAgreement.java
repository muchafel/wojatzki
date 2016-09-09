package de.uni_due.ltl.util.iaa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import webanno.custom.Debate_Stance_Set1;
import webanno.custom.Debate_Stance_Set2;
import webanno.custom.ExplicitStance;

public class InterAnnotatorAgreement {

	static boolean doUnzipping=false;
	static ArrayList<String> annotators = new ArrayList<String>(Arrays.asList("NiklasMeyer","BenjaminGansert", "MarvinHoltermann"));
	static ArrayList<String> targets_Set1 = new ArrayList<String>(Arrays.asList(
			"Compulsory vaccines are a financial relief on the health system", 
//			"Duty to protect the child", 
			"Vaccines have severe side effects", 
			"parental right to decide about vaccinations for a child", 
			"state’s duty to protect its community" 
			));
	
	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		if(doUnzipping){
			extract(new File(baseDir+"/youtubeStance/annotation/Stance_Youtube_Prestudy_2016-09-08_1516/annotation"));
		}
		
		Map<String,Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions = annotatorToSentences(baseDir+"/youtubeStance/annotation/Stance_Youtube_Prestudy_2016-09-08_1516/annotation_unzipped");
		System.out.println(annotatorToSentenceToDecisions);
//		inspect(annotatorToSentenceToDecisions);

		interAnnotatorAgreementDebateStance(annotatorToSentenceToDecisions,annotators);

		for(String target: targets_Set1){
			interAnnotatorAgreementTarget(annotatorToSentenceToDecisions,annotators,target);
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
						System.out.println(decisison.getStance()+ " "+decisison.getStance().getCoveredText());
					}
				}
			}
		}
		
	}



	private static void interAnnotatorAgreementTarget(
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
						System.out.println(getAnnotatorExplicitStance_1(annotators.get(0),
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



	private static String getAnnotatorExplicitStance_1(String annotatorName, List<AnnotatorDecision> decisisons, String target) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getExplicitStances() == null || decision.getExplicitStances().isEmpty()){
					return "NONE";
				}
				for(ExplicitStance stance:decision.getExplicitStances()){
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
						System.out.println(getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
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


	


	private static String getAnnotatorDebateStance(String annotatorName, List<AnnotatorDecision> decisisons) {
		for(AnnotatorDecision decision: decisisons){
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getStance() == null){
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

				AnnotatorDecision decisison= new AnnotatorDecision(documentMetaData.getDocumentId(), sentenceCount, documentMetaData.getDocumentTitle());
				
				if(JCasUtil.selectCovered(Debate_Stance_Set1.class, sentence).isEmpty()){
					throw new Exception("no debate stance in "+documentMetaData.getDocumentId()+":"+sentenceCount+ "annotator "+documentMetaData.getDocumentId());
				}
				
				for (Debate_Stance_Set1 stance : JCasUtil.selectCovered(Debate_Stance_Set1.class, sentence)) {
					decisison.setStance(stance);
					System.out.println(stance);
				}
				List<ExplicitStance> explicitStances= JCasUtil.selectCovered(ExplicitStance.class, sentence);
				if(!explicitStances.isEmpty()){
					decisison.setExplicitStances(explicitStances);
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
		if(result.get(documentTitle).containsKey(sentenceCount)){
			result.get(documentTitle).get(sentenceCount).add(decisison);
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
