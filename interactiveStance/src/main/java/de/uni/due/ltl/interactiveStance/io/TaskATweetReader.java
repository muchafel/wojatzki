package de.uni.due.ltl.interactiveStance.io;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
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
import de.uni.due.ltl.interactiveStance.types.OriginalResource;
import de.uni.due.ltl.interactiveStance.types.StanceAnnotation;

import org.dkpro.tc.api.io.TCReaderSingleLabel;
import org.dkpro.tc.api.type.TextClassificationOutcome;

public class TaskATweetReader extends TaskATweetReader_base{
	
    /**
     * resource
     */
    public static final String PARAM_MEMORIZE_RESOURCE = "Resource";
    @ConfigurationParameter(name = PARAM_MEMORIZE_RESOURCE, mandatory = false,defaultValue = "false")
	protected boolean memorizeResource;

    
    protected Resource res;
    
	@Override
	public String getTextClassificationOutcome(JCas jcas) throws CollectionException {
		String outcome="";
		for (StanceAnnotation anno : JCasUtil.select(jcas, StanceAnnotation.class)) {
			outcome=anno.getStance();
		}
		return outcome;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		res = nextFile();
		initCas(jcas, res);
	    jcas= readCas(jcas, res); 
	   
	    if(memorizeResource){
	    	OriginalResource origin= new OriginalResource(jcas);
	    	origin.setLocation(res.getResource().getFile().getAbsolutePath());
	    	origin.setFileName(res.getPath());
	    	origin.addToIndexes();
	    }
	    
	    TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
	    outcome.setOutcome(getTextClassificationOutcome(jcas));
	    outcome.addToIndexes();
	}

}
