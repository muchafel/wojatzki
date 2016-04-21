package annotators;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.bcel.generic.Select;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import consolidatedTypes.Irony;
import consolidatedTypes.MainTarget;
import consolidatedTypes.SubTarget;
import consolidatedTypes.Understandability;
import curatedTypes.CuratedIrony;
import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import curatedTypes.CuratedUnderstandability;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class AnnotationCurator extends JCasAnnotator_ImplBase {

	private ArrayList<String> subTargets = new ArrayList<String>(Arrays.asList("secularism", "Same-sex marriage",
			"religious_freedom", "Conservative_Movement", "Freethinking", "Islam", "No_evidence_for_religion", "USA",
			"Supernatural_Power_Being", "Life_after_death", "Christianity"));

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		//curate main target
		FrequencyDistribution<String> fdAtheism = new FrequencyDistribution<>();
		for (MainTarget mainTarget : JCasUtil.select(jcas, MainTarget.class)) {
			// System.out.println(mainTarget.getAnnotator()+ ": "+
			// mainTarget.getTarget()+ " "+mainTarget.getPolarity());
			fdAtheism.inc(mainTarget.getPolarity());
		}
//		System.out.println("    ATHEISM " + JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId() + " "
//				+ fdAtheism.getSampleWithMaxFreq() + "(" + fdAtheism.getN() + ")");
		createCuratedMainAnno(fdAtheism,jcas);

		//curate subTargets
		for (String subtarget : subTargets) {
			createCuratedAnno(subtarget, jcas);
		}

		//curate Irony
		FrequencyDistribution<String> fdIrony = new FrequencyDistribution<>();
		for (Irony irony : JCasUtil.select(jcas, Irony.class)) {
			fdIrony.inc("Irony");
		}
		createCuratedIronyAnno(fdIrony,jcas);
		
		//curate Understandability
		FrequencyDistribution<String> fdUnderstandability = new FrequencyDistribution<>();
		for (Understandability irony : JCasUtil.select(jcas, Understandability.class)) {
			fdUnderstandability.inc("Understandability");
		}
		createCuratedUnderstandabilityAnno(fdUnderstandability,jcas);

	}

	private void createCuratedUnderstandabilityAnno(FrequencyDistribution<String> fdUnderstandability, JCas jcas) {
		if (fdUnderstandability.getSampleWithMaxFreq() != null && fdUnderstandability.getN() > 1) {
			CuratedUnderstandability annotation= new CuratedUnderstandability(jcas,0, jcas.getDocumentText().length());
			annotation.setUnderstandability("UNDERSTANDABILITY");
			annotation.addToIndexes();
		}
	}

	private void createCuratedIronyAnno(FrequencyDistribution<String> fdIrony, JCas jcas) {
		if (fdIrony.getSampleWithMaxFreq() != null && fdIrony.getN() > 1) {
			CuratedIrony annotation= new CuratedIrony(jcas,0, jcas.getDocumentText().length());
			annotation.setIrony("IRONY");
			annotation.addToIndexes();
		}
	}

	private void createCuratedMainAnno(FrequencyDistribution<String> fdAtheism, JCas jcas) {
		CuratedMainTarget annotation= new CuratedMainTarget(jcas,0, jcas.getDocumentText().length());
		annotation.setTarget("ATHEISM");
		annotation.setPolarity(fdAtheism.getSampleWithMaxFreq());
		annotation.addToIndexes();
		
	}

	private void createCuratedAnno(String subtarget, JCas jcas) {
		FrequencyDistribution<String> fd = new FrequencyDistribution<>();
		for (SubTarget target : JCasUtil.select(jcas, SubTarget.class)) {
			if (target.getTarget().equals(subtarget)) {
				// System.out.println(target.getAnnotator()+ ": "+
				// target.getTarget()+ " "+target.getPolarity());
				fd.inc(target.getPolarity());
			}
		}
//		if (fd.getSampleWithMaxFreq() != null && fd.getN() > 1 && !fd.getSampleWithMaxFreq().equals("NONE")) {
//			System.out.println(
//					"    " + subtarget + " " + JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId() + " "
//							+ fd.getSampleWithMaxFreq() + "(" + fd.getN() + ")");
//		}
		createCuratedSubAnno(fd,jcas,subtarget);
	}

	private void createCuratedSubAnno(FrequencyDistribution<String> fd, JCas jcas, String subtarget) {
		CuratedSubTarget annotation= new CuratedSubTarget(jcas,0, jcas.getDocumentText().length());
		annotation.setTarget(subtarget);
		if (fd.getSampleWithMaxFreq() != null && fd.getN() > 1 && !fd.getSampleWithMaxFreq().equals("NONE")) {
			annotation.setPolarity(fd.getSampleWithMaxFreq());
		}else{
			annotation.setPolarity("NONE");
		}
		
		annotation.addToIndexes();
		
	}

}
