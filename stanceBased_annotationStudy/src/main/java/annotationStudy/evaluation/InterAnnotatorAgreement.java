package annotationStudy.evaluation;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.FleissKappaAgreement;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;

import consolidatedTypes.MainTarget;
import consolidatedTypes.SubTarget;
import curatedTypes.CuratedIrony;
import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import curatedTypes.CuratedUnderstandability;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

public class InterAnnotatorAgreement {

	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList("secularism", "Same-sex marriage",
			"religious_freedom", "Conservative_Movement", "Freethinking", "Islam", "No_evidence_for_religion", "USA",
			"Supernatural_Power_Being", "Life_after_death", "Christianity"));

	private static boolean useSubTargets = false;

	private ArrayList<String> annotators = new ArrayList<String>(
			Arrays.asList("DominikLawatsch", "NiklasMeyer", "michael_the_annotator"));

	public static void main(String[] args) throws Exception {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		InterAnnotatorAgreement agreementCalculator = new InterAnnotatorAgreement();
		agreementCalculator.calculateIAA(baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all",
				useSubTargets);
		// agreementCalculator.calculateTotalIAA(baseDir +
		// "/semevalTask6/annotationStudy/curatedTweets/Atheism/all");
	}

	private void calculateTotalIAA(String path) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, path, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}

		for (JCas jcas : new JCasIterable(reader)) {
			// ignore irony and ununderstandability
			if (JCasUtil.select(jcas, CuratedIrony.class).isEmpty()
					&& JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()) {
				List<String> decisions = new ArrayList<>();
				for (String annotator : annotators) {
					decisions.add(getAnnotatorDecisison(jcas, annotator));
				}
				fd.incAll(decisions);
				study.addItem(decisions.get(0), decisions.get(1), decisions.get(2));
			}
		}
		// System.out.println(target);
		for (String polarity : fd.getMostFrequentSamples(fd.getKeys().size())) {
			System.out.println(polarity + " " + fd.getCount(polarity));
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
		System.out.println("pa: " + pa.calculateAgreement() + " fleiss " + fleissKappa.calculateAgreement());

	}

	private String getAnnotatorDecisison(JCas jcas, String annotator) {
		String targetString = "Atheism:" + JCasUtil.selectSingle(jcas, CuratedMainTarget.class).getPolarity();
		for (String subTarget : subTargets) {
			for (SubTarget target : JCasUtil.select(jcas, SubTarget.class)) {
				if (target.getAnnotator().equals(annotator) && target.getTarget().equals(subTarget)
						&& !target.getPolarity().equals("NONE")) {
					targetString += "_" + target.getTarget() + ":" + target.getPolarity();
				}
			}
		}
		return targetString;
	}

	private void calculateIAA(String path, boolean useSubTargets) throws Exception {

		if (useSubTargets) {
			for (String target : subTargets) {
				System.out.println(target);
				System.out.println(getAgreement(path, target));
				writeToFile(target + "\t" + getAgreement(path, target), path);
			}
		} else {
			System.out.println(getAgreementMain(path));
		}
	}

	private String getAgreementMain(String path) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, path, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}

		for (JCas jcas : new JCasIterable(reader)) {
			// ignore irony and ununderstandability
			if (JCasUtil.select(jcas, CuratedIrony.class).isEmpty()
					&& JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()) {
				List<String> decisions = new ArrayList<>();
				for (String annotator : annotators) {
					decisions.add(getAnnotatorDecisisonMain(jcas, annotator));
				}
				fd.incAll(decisions);
				study.addItem(decisions.get(0), decisions.get(1), decisions.get(2));
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
		
		String toPrint = String.valueOf(fd.getCount("ATHEISM_FAVOR")) + "\t"
				+ String.valueOf(fd.getCount("ATHEISM_AGAINST")) + "\t"
				+ String.valueOf(fd.getCount("ATHEISM_NONE"));

		return toPrint+"\t"+pa.calculateAgreement() + "\t" + fleissKappa.calculateAgreement();
	}

	private String getAnnotatorDecisisonMain(JCas jcas, String annotator) {
		for (MainTarget target : JCasUtil.select(jcas, MainTarget.class)) {
			if (target.getAnnotator().equals(annotator)) {
				System.out.println(
						"ATHEISM_" + target.getAnnotator() + " " + target.getPolarity() + " " + target.getTarget());
				return "ATHEISM_" + target.getPolarity();
			}
		}
		return "ATHEISM_NONE";
	}

	private String getAgreement(String path, String target) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(BinaryCasReader.class,
				BinaryCasReader.PARAM_SOURCE_LOCATION, path, BinaryCasReader.PARAM_PATTERNS, "*.bin",
				BinaryCasReader.PARAM_TYPE_SYSTEM_LOCATION, "typesystem.bin");

		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}

		for (JCas jcas : new JCasIterable(reader)) {
			// ignore irony and ununderstandability
			if (JCasUtil.select(jcas, CuratedIrony.class).isEmpty()
					&& JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()) {
				List<String> decisions = new ArrayList<>();
				for (String annotator : annotators) {
					decisions.add(getAnnotatorDecisison(jcas, target, annotator));
				}
				fd.incAll(decisions);
				study.addItem(decisions.get(0), decisions.get(1), decisions.get(2));
			}
		}
		// System.out.println(target);
		// for (String polarity : fd.getMostFrequentSamples(3)) {
		// System.out.println(polarity + " " + fd.getCount(polarity));
		// }
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
		// System.out.println("pa: "+pa.calculateAgreement()+ " fleiss
		// "+fleissKappa.calculateAgreement());
		String toPrint = String.valueOf(fd.getCount(target + "_FAVOR")) + "\t"
				+ String.valueOf(fd.getCount(target + "AGAINST")) + "\t"
				+ String.valueOf(fd.getCount(target + "_NONE"));
		return toPrint + "\t" + pa.calculateAgreement() + "\t" + fleissKappa.calculateAgreement();
	}

	private String getAnnotatorDecisison(JCas jcas, String target, String annotator) {
		for (SubTarget subtarget : JCasUtil.select(jcas, SubTarget.class)) {
			if (subtarget.getTarget().equals(target) && subtarget.getAnnotator().equals(annotator)) {
				// System.out.println(annotator+":
				// "+subtarget.getTarget()+"_"+subtarget.getPolarity());
				return subtarget.getTarget() + "_" + subtarget.getPolarity();
			}
		}
		return target + "_NONE";
	}

	private static void writeToFile(String toPrint, String path)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path + "/agreement.csv", true), "UTF-8"));
		try {
			out.write(toPrint + System.lineSeparator());
		} finally {
			out.close();
		}

	}
}
