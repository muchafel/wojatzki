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
import curatedTypes.CuratedMainTarget;
import curatedTypes.CuratedSubTarget;
import curatedTypes.CuratedUnderstandability;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import io.StanceReader;
import io.StanceReader_AddsOriginal;
import types.StanceAnnotation;

public class xmiToTXT {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, baseDir+"/youtubeStance/corpus/xmi", XmiReader.PARAM_PATTERNS, "*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");
		File f = new File("youtubeCorpus.txt"); 
		
		
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
