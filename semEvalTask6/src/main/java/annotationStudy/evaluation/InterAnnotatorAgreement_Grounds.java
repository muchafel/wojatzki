package annotationStudy.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.generic.Select;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.CohenKappaAgreement;
import org.dkpro.statistics.agreement.coding.DiceAgreement;
import org.dkpro.statistics.agreement.coding.FleissKappaAgreement;
import org.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import io.TaskATweetReader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import webanno.custom.Central_Target;
import webanno.custom.Ground_Attitudes;
import webanno.custom.Stance;

public class InterAnnotatorAgreement_Grounds {

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
//		extract(new File(baseDir+"/semevalTask6/prestudy_annotation/Stance_Arguments_Prestudy_2016-03-23_1639/annotation"));
//		extract(new File(baseDir+"/semevalTask6/prestudy_annotation/Stance_Arguments_Prestudy_2016-04-07_1426/annotation"));
		
//		interAnnotatorAgreement(baseDir+ "/semevalTask6/prestudy_annotation/Stance_Arguments_Prestudy_2016-03-23_1639/annotation_unzipped");
//		interAnnotatorAgreement(baseDir+ "/semevalTask6/prestudy_annotation/Stance_Arguments_Prestudy_2016-03-31_1510/annotation_unzipped");
		interAnnotatorAgreement(baseDir+ "/semevalTask6/prestudy_annotation/Stance_Arguments_Prestudy_2016-04-07_1426/annotation_unzipped");
	}

	private static void interAnnotatorAgreement(String path) throws Exception {
		System.out.println(path);
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, path, XmiReader.PARAM_PATTERNS, "**/*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");
		
		int allAnnotationCount=0;
		FrequencyDistribution<String> all = new FrequencyDistribution<String>();
		FrequencyDistribution<String> allAnnos = new FrequencyDistribution<String>();
		
		Map<String, AnnotatedDocument> docToAnno = new HashMap<>();
		List<JCas> allDocs = new ArrayList<>();

		String currentId = "";
		for (JCas jcas : new JCasIterable(reader)) {
			allAnnotationCount++;
			System.out.println(
					JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId() + " " + jcas.getDocumentText());
			allDocs.add(jcas);
			List<StanceContainer> stances = new ArrayList<>();
			String docId = jcas.getDocumentText().split(" ")[0];

			String docAnnotator = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId().split(" ")[0];

			//handle Stance annos
			for (Stance stance : JCasUtil.select(jcas, Stance.class)) {
				all.inc(stance.getStance_Polarity());
				allAnnos.inc(stance.getStance_Polarity()+" ("+stance.getStance_Target()+")");
				stances.add(new StanceContainer(stance));
			}
			
			//handle Central_Target annos
			for (Ground_Attitudes stance : JCasUtil.select(jcas, Ground_Attitudes.class)) {
				String mappedTarget=stance.getAttitudes();
				all.inc(mappedTarget);
				
				//handle missing values
				if(stance.getPolarity()==null){
					allAnnos.inc(mappedTarget+" (NONE)");
				}else{
					allAnnos.inc(mappedTarget+" ("+stance.getPolarity()+")");
				}
				stances.add(new StanceContainer(stance,mappedTarget));
			}

			//assemble map from found annotations
			if (docToAnno.containsKey(docId)) {
				docToAnno.get(docId).getAnnotatorToAnnotations().put(docAnnotator, stances);
			} else {
				Map<String, List<StanceContainer>> annotatorToAnnotations = new HashMap<String, List<StanceContainer>>();
				annotatorToAnnotations.put(docAnnotator, stances);
				docToAnno.put(docId, new AnnotatedDocument(docId, annotatorToAnnotations));
			}
			for (StanceContainer stance : stances) {
				System.out.println("stance target: " + stance.getTarget() + " " + stance.getPolarity());
			}
//			if (currentId.equals(docId)) {
//				System.out.println(docToAnno.get(currentId).getAnnotatorToAnnotations());
//			} else {
//				currentId = docId;
//			}
		}
//		inspectAgreement(docToAnno,new ArrayList<String>(Arrays.asList("TobiasHorsmann","michael_the_annotator")),"Abortion");
//		ArrayList<String> annotators = new ArrayList<String>(Arrays.asList("DominikLawatsch", "NiklasMeyer"));
//		ArrayList<String> annotators = new ArrayList<String>(Arrays.asList("DominikLawatsch", "NiklasMeyer","michael_the_annotator"));
		ArrayList<String> annotators = new ArrayList<String>(Arrays.asList("TobiasHorsmann","michael_the_annotator"));
		
		for(String target:all.getKeys()){
			System.out.println("*** Statistics for "+target+ " ***");
			System.out.println("used "+all.getCount(target)+ " times");
			long favor=allAnnos.getCount(target+ " (FAVOR)");
			long against=allAnnos.getCount(target+ " (AGAINST)");
			long none=allAnnos.getCount(target+ " (NONE)");
			none+=allAnnotationCount-(favor+against+none);
			System.out.println("class distribution");
			System.out.println("FAVOR: "+favor+" AGAINST: "+against+" NONE: "+none);
			System.out.println(target+" :"+favor+":"+against+":"+none);
			
			//FIXME real handling!!! 
			//skip calculation if we do not have at least two categories (as NONE is the default this should get almost all cases)
			if(favor<1  && against<1){
				System.err.println("there are not at least two different categories for "+target);
				continue;
			}
			calculateInterAnnotatorAgreement(docToAnno,annotators,target,target+" :"+favor+":"+against+":"+none,path);
		}
	}

	private static String getMappedTarget(String target) {
//		if(target.equals("killing unborn")||target.equals("Abortion is murder") || target.equals("killing unborn children")||target.equals("Abortion is Killing")){
//			return "Abortion is murder";
//		}
		if(target.equals("God")||target.equals("religious bullshit")){
			return "God";
		}
		if(target.equals("Killing is valuable business")||target.equals("business / making money with unborn child")){
			return "abortion business";
		}
		if(target.equals("human rights")||target.equals("self determination")||target.equals("One has the right to choose over the own body")||target.equals("woman right of self determination")){
			return "choice";
		}
		if(target.equals("Abortion is reproductive healthcare")||target.equals("healthcare system")){
			return "healthcare";
		}
		return target;
	}

	/**
	 * compute different kappa statistics for a given target for a given list of annotators
	 * FIXME: At the moment we can only handle 2 or 3 annotators. should be generalized to n!
	 * @param docToAnno
	 * @param annotators 
	 * @param toPrint 
	 * @param path 
	 * @param all
	 * @throws Exception 
	 */
	private static void calculateInterAnnotatorAgreement(Map<String, AnnotatedDocument> docToAnno, ArrayList<String> annotators, String target, String toPrint, String path) throws Exception {
		
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if(annotators.size()>3){
			throw new Exception("unhandled number of annotators");
		}

		for (String docId : docToAnno.keySet()) {
//			System.out.println(docId);
			Map<String, List<StanceContainer>> annotatedDoc = docToAnno.get(docId).getAnnotatorToAnnotations();
			if(annotators.size()==2){
				study.addItem(getCoding(annotatedDoc.get(annotators.get(0)), target),
						getCoding(annotatedDoc.get(annotators.get(1)), target));
//				System.out.println(getCoding(annotatedDoc.get("DominikLawatsch"), target) + " "
//						+ getCoding(annotatedDoc.get("NiklasMeyer"), "Abortion"));
			}else{
				study.addItem(getCoding(annotatedDoc.get(annotators.get(0)), target),
						getCoding(annotatedDoc.get(annotators.get(1)), target),getCoding(annotatedDoc.get(annotators.get(2)), target));
			}
			
		}

		System.out.println(study.getCategoryCount()+ " "+study.getCategories());
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
//		CohenKappaAgreement cohenKappaAgreement= new CohenKappaAgreement(study);
		
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
//		System.out.println("COHENKAPPA " + cohenKappaAgreement.calculateAgreement());
		writeToFile(toPrint,pa.calculateAgreement(),fleissKappa.calculateAgreement(),path);

	}

	private static void writeToFile(String toPrint, double percentageAgreement, double fleissKappa, String path) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+"/agreement.csv",true), "UTF-8"));
		try {
			out.write(toPrint+";"+String.valueOf(percentageAgreement)+";"+fleissKappa+""+System.lineSeparator());
		} finally {
			out.close();
		}
		
	}

	/**
	 * quantitative and qualitative inspection based on Number of (mis)matches 
	 * FIXME: cannot handle more than two annotators
	 * 
	 * @param docToAnno
	 * @param annotators 
	 */
	private static void inspectAgreement(Map<String, AnnotatedDocument> docToAnno, ArrayList<String> annotators, String majorTarget) {
		int totalDiscrepancy = 0;
		StanceAnnotationComparator comparator = new StanceAnnotationComparator();
		for (String docId : docToAnno.keySet()) {
			System.out.println(docId);
			Map<String, List<StanceContainer>> annotatedDoc = docToAnno.get(docId).getAnnotatorToAnnotations();
			int discrepancy = comparator.compare(annotatedDoc.get(annotators.get(0)), annotatedDoc.get(annotators.get(1)));
			totalDiscrepancy += discrepancy;
			System.out.println(getCoding(annotatedDoc.get(annotators.get(0)), majorTarget) + " "
					+ getCoding(annotatedDoc.get(annotators.get(1)), majorTarget));
		}
		System.out.println("total dicrepancy " + totalDiscrepancy);
	}

	/**
	 * returns the value that has been annotated according to a given target and
	 * a given annotator returns TARGET (NONE) if there has been no annotation
	 * to this target
	 * 
	 * @param stances
	 * @param target
	 * @return
	 */
	private static String getCoding(List<StanceContainer> stances, String target) {
		for (StanceContainer stance : stances) {
			if (stance.getTarget().equals(target)) {
				return stance.getTarget() + " (" + stance.getPolarity() + ")";
			}
		}
		return target + " (NONE)";
	}

	/**
	 * extracts the zipped files from the webanno structure
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

	private static void unzip(File fileEntry, File folder) {
		String source = fileEntry.getAbsolutePath();
		String destination = folder.getAbsolutePath();
		try {
			ZipFile zipFile = new ZipFile(source);
			zipFile.extractAll(destination);
			;
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
