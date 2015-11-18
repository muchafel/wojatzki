package io;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.tc.api.io.TCReaderSingleLabel;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import types.OriginalResource;
import types.StanceAnnotation;

public class TaskATweetReader extends JCasResourceCollectionReader_ImplBase
implements TCReaderSingleLabel{
	
	
	/**
     * Language
     */
    public static final String PARAM_LANGUAGE = "Language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true,defaultValue = "de")
	protected String language;
    
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
	    
	    Element root;
	    String tweetText = "";
	    String tweetStance = "";
	    String tweetTarget = "";
	    String tweetId = "";
	    
	    try {
	        SAXReader reader = new SAXReader();
	        Document document = reader.read(new BufferedInputStream(res.getInputStream()));
	        root = document.getRootElement();
	    }
	    catch (DocumentException e) {
	        throw new CollectionException(e);
	    }
	    catch (IOException e) {
	        throw new CollectionException(e);
	    }
	    try {
			XPath rootPath = new Dom4jXPath("//tweet");
            Element rootElement = (Element) rootPath.selectSingleNode(root);
            tweetText = getContent(rootElement, "descendant::text");
            tweetStance = getContent(rootElement, "descendant::stance");
            tweetTarget = getContent(rootElement, "descendant::target");
            tweetId = getContent(rootElement, "descendant::id");
//            System.out.println(tweetText+" "+tweetTarget+ " "+ tweetStance);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
	    jcas.setDocumentText(tweetText);
	    jcas.setDocumentLanguage(language);

//	    System.out.println(jcas.getDocumentText());
	    
	    StanceAnnotation anno= new StanceAnnotation(jcas);
	    anno.setTarget(tweetTarget);
	    anno.setStance(tweetStance);
	    anno.setOriginalId(tweetId);
	    anno.addToIndexes();
	    
	    if(memorizeResource){
	    	OriginalResource origin= new OriginalResource(jcas);
	    	origin.setLocation(res.getResource().getFile().getAbsolutePath());
	    	origin.setFileName(res.getPath());
//	    	System.out.println(res.getPath());
//	    	System.out.println(res.getResource().getFile().getAbsolutePath());
	    	origin.addToIndexes();
	    }
	    
	    TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
	    outcome.setOutcome(getTextClassificationOutcome(jcas));
//	    System.out.println(getTextClassificationOutcome(jcas));
	    outcome.addToIndexes();
	}

	protected String getContent(Element rootElement, String path) throws JaxenException {
        
		final XPath xp = new Dom4jXPath(path);
	    for (Object element : xp.selectNodes(rootElement)) {
	        if (element instanceof Element) {
//	            Element node = (Element) element;
	            return ((Element) element).getText();
	        }
	    }
		return null;
	}

}
