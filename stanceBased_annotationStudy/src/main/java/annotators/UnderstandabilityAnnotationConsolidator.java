package annotators;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import consolidatedTypes.Understandability;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import webanno.custom.Irony;
import webanno.custom.UnUnderstandability;
import webanno.custom.Undersatndability;

public class UnderstandabilityAnnotationConsolidator extends JCasAnnotator_ImplBase{
	public static final String PARAM_ANNOTATIONS_PATH = "webAnnoUnderstandabilityAnnotationPath";
	@ConfigurationParameter(name = PARAM_ANNOTATIONS_PATH, mandatory = true)
	private String annotationPath;
	
	private Map<String, Map<String,String>> docToAnno;
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		//FIXME: somehow parameter handling is not working...
		String baseDir = null;
		try {
			 baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		annotationPath=baseDir + "/semevalTask6/annotationStudy/Stance_Arguments_Study_2016-04-21_0903/annotation_unzipped";
		this.docToAnno=getConsolidationMapping(annotationPath);
	}
	
	private Map<String, Map<String,String>> getConsolidationMapping(String annotationPath) throws ResourceInitializationException {
		Map<String, Map<String,String>> docToAnno = new HashMap<>();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, annotationPath, XmiReader.PARAM_PATTERNS, "**/*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");
		
		for (JCas jcas : new JCasIterable(reader)) {
			String docId = jcas.getDocumentText().split(" ")[0];

			String docAnnotator = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId().split(" ")[0];

			for (Undersatndability understandability : JCasUtil.select(jcas, Undersatndability.class)) {
				if (docToAnno.containsKey(docId)) {
					docToAnno.get(docId).put(docAnnotator, "UnUnderstandability");
				}else{
					Map<String,String> annotatorToIrony= new HashMap<String,String>();
					annotatorToIrony.put(docAnnotator, "UnUnderstandability");
					docToAnno.put(docId, annotatorToIrony);
				}
			}
		}
		return docToAnno;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String id= JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
		id=id.replace(".xml", "");
		id=id.replace("tweets", "");
		if(docToAnno.get(id)!=null){
			Map<String, String> annotatedDoc = docToAnno.get(id);
			for(String annotator: annotatedDoc.keySet()){
				createAnnotation(jcas,annotatedDoc.get(annotator), annotator);
			}
		}
	}

	private void createAnnotation(JCas jcas, String anno, String annotator) {
		Understandability annotation= new Understandability(jcas,0, jcas.getDocumentText().length());
		annotation.setAnnotator(annotator);
		annotation.addToIndexes();
	}
}
