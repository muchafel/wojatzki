package annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import annotationStudy.evaluation.AnnotatedDocument;
import annotationStudy.evaluation.StanceContainer;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import webanno.custom.Stance;

public class AnnotationConsolidator extends JCasAnnotator_ImplBase{

	public static final String PARAM_ANNOTATIONS_PATH = "webAnnoAnnotationPath";
	@ConfigurationParameter(name = PARAM_ANNOTATIONS_PATH, mandatory = true)
	private String annotationPath;
	
	private Map<String, AnnotatedDocument> docToAnno;
	
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		System.out.println(annotationPath);
		this.docToAnno=getConsolidationMapping(annotationPath);
	}
	
	private Map<String, AnnotatedDocument> getConsolidationMapping(String annotationPath) throws ResourceInitializationException {
		Map<String, AnnotatedDocument> docToAnno = new HashMap<>();

		System.out.println(annotationPath);
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION, annotationPath, XmiReader.PARAM_PATTERNS, "**/*.xmi", XmiReader.PARAM_LANGUAGE,
				"en");
		
		for (JCas jcas : new JCasIterable(reader)) {
			List<StanceContainer> stances = new ArrayList<>();
//			String docId = jcas.getDocumentText().split(" ")[0];
			String docId = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();

			String docAnnotator = JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId().split(" ")[0];

			for (Stance stance : JCasUtil.select(jcas, Stance.class)) {
				stances.add(new StanceContainer(stance));
			}

			if (docToAnno.containsKey(docId)) {
				docToAnno.get(docId).getAnnotatorToAnnotations().put(docAnnotator, stances);
			} else {
				Map<String, List<StanceContainer>> annotatorToAnnotations = new HashMap<String, List<StanceContainer>>();
				annotatorToAnnotations.put(docAnnotator, stances);
				docToAnno.put(docId, new AnnotatedDocument(docId, annotatorToAnnotations));
			}
		}
		return docToAnno;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String id= JCasUtil.selectSingle(jcas, DocumentMetaData.class).getDocumentId();
		System.out.println(id);
		System.out.println(docToAnno.size());
		Map<String, List<StanceContainer>> annotatedDoc = docToAnno.get(id).getAnnotatorToAnnotations();
		for(String annotator: annotatedDoc.keySet()){
			System.out.println(annotator);
			for(StanceContainer stanceContainer: annotatedDoc.get(annotator)){
				System.out.println(stanceContainer.getTarget()+ " "+ stanceContainer.getPolarity());
			}
		}
	}

}
