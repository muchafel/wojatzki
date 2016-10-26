package util;

import java.io.File;
import java.io.IOException;

import org.apache.bcel.generic.Select;
import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import consolidatedTypes.Irony;
import consolidatedTypes.Understandability;
import curatedTypes.CuratedIrony;
import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import curatedTypes.CuratedUnderstandability;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import io.StanceReader;
import io.StanceReader_AddsOriginal;
import types.StanceAnnotation;

public class xmiToTXT {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
//		String loc=baseDir + "/semevalTask6/annotationStudy/curatedTweets/Atheism/all"+"_wo_irony_understandability";
		String loc="/Users/michael/DKPRO_HOME/semevalTask6/annotationStudy/originalDebateStanceLabels/bin";
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(StanceReader.class,StanceReader.PARAM_SOURCE_LOCATION, loc, StanceReader.PARAM_LANGUAGE,"en", StanceReader.PARAM_PATTERNS, "*.bin", StanceReader.PARAM_TARGET_LABEL,
				"ATHEISM");
		File f = new File("corpus_comparison.txt"); 
		
		
		for (JCas jcas : new JCasIterable(reader)) {
			String toPrint=jcas.getDocumentText() ;
			CuratedMainTarget mainT=JCasUtil.selectSingle(jcas,CuratedMainTarget.class);
			toPrint+="\t"+mainT.getTarget().toLowerCase()+":"+mainT.getPolarity().toLowerCase();
			StanceAnnotation origAnno=JCasUtil.selectSingle(jcas, StanceAnnotation.class);
			toPrint+="\t"+origAnno.getTarget().toLowerCase()+":"+origAnno.getStance();
			for(CuratedSubTarget subTarget: JCasUtil.select(jcas, CuratedSubTarget.class)){
				toPrint+="\t"+subTarget.getTarget().toLowerCase()+ ":"+subTarget.getPolarity().toLowerCase();
			}
//			if(!JCasUtil.select(jcas, CuratedIrony.class).isEmpty()){
//				toPrint+="\t"+"irony:true";
//			}else{
//				toPrint+="\t"+"irony:false";
//			}
//			if(!JCasUtil.select(jcas, CuratedUnderstandability.class).isEmpty()){
//				toPrint+="\t"+"understandable:true";
//			}else{
//				toPrint+="\t"+"understandable:false";
//			}
			toPrint+="\n";
			System.out.println(toPrint);
			FileUtils.writeStringToFile(f, toPrint,"UTF-8", true); 
		}
		
	}

}
