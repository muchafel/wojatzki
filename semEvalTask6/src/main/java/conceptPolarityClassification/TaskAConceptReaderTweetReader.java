package conceptPolarityClassification;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase.Resource;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.dkpro.tc.api.io.TCReaderSingleLabel;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import io.TaskATweetReader_base;
import types.OriginalResource;
import types.StanceAnnotation;
import util.PreprocessingPipeline;
import util.SimilarityHelper;

public class TaskAConceptReaderTweetReader extends TaskATweetReader_base {

	public static final String PARAM_CONCEPT = "concept";
	@ConfigurationParameter(name = PARAM_CONCEPT, mandatory = true)
	protected String concept;

	/**
	 * resource
	 */
	public static final String PARAM_MEMORIZE_RESOURCE = "Resource";
	@ConfigurationParameter(name = PARAM_MEMORIZE_RESOURCE, mandatory = false, defaultValue = "false")
	protected boolean memorizeResource;

	Resource res;
	private AnalysisEngine engine;
	
	@Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);
        AggregateBuilder aggregateBuilder= new AggregateBuilder();
        aggregateBuilder.add(PreprocessingPipeline.getPreprocessingBreakTwokenizerTweetAnnos());
        this.engine=aggregateBuilder.createAggregate();
    }
	
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		if(getResourceIterator().hasNext()){
			Resource next = getResourceIterator().next(); 
			if (conceptContained(next)) {
				res = next;
				return true;
			}else{
				return hasNext();
			}
		}else return false;
		
	}

	private boolean conceptContained(Resource next) {
		JCas jcas = null;
		
		try {
			jcas = JCasFactory.createJCas();
			initCas(jcas, next);
			jcas= readCas(jcas, next);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
		
		try {
			engine.process(jcas);
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
		//just use FAVOR and AGAINST
		if(JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance().equals("NONE")) return false;
		
		//check if one noun equals the concept
		for(Token t: JCasUtil.select(jcas, Token.class)){
			if(t.getPos().getPosValue().equals("NN")||t.getPos().getPosValue().equals("NNS")||t.getPos().getPosValue().equals("NP")||t.getPos().getPosValue().equals("NPS")||t.getPos().getPosValue().equals("NPS")){
				if(t.getCoveredText().equals(concept)||SimilarityHelper.wordsAreSimilar(t.getCoveredText(),concept)){
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		initCas(jcas, res);
		jcas= readCas(jcas, res);
		
//		System.out.println(jcas.getDocumentText()+ " "+JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getStance());
		if (memorizeResource) {
			OriginalResource origin = new OriginalResource(jcas);
			origin.setLocation(res.getResource().getFile().getAbsolutePath());
			origin.setFileName(res.getPath());
			// System.out.println(res.getPath());
			// System.out.println(res.getResource().getFile().getAbsolutePath());
			origin.addToIndexes();
		}

		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
		outcome.setOutcome(getTextClassificationOutcome(jcas));
		// System.out.println(getTextClassificationOutcome(jcas));
		outcome.addToIndexes();
	}
}
