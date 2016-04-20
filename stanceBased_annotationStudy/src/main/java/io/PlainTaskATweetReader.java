package io;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase.Resource;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import types.OriginalResource;
import types.StanceAnnotation;

public class PlainTaskATweetReader extends JCasResourceCollectionReader_ImplBase{
	/**
     * Language
     */
    public static final String PARAM_LANGUAGE = "Language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true,defaultValue = "en")
	protected String language;
    
    /**
     * resource
     */
    public static final String PARAM_MEMORIZE_RESOURCE = "Resource";
    @ConfigurationParameter(name = PARAM_MEMORIZE_RESOURCE, mandatory = false,defaultValue = "false")
	protected boolean memorizeResource;

    
    protected Resource res;
	
    protected JCas readCas(JCas jcas, Resource res) throws CollectionException {
		Element root;
		String tweetText = "";
		String tweetId="";

		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new BufferedInputStream(res.getInputStream()));
			root = document.getRootElement();
		} catch (DocumentException e) {
			throw new CollectionException(e);
		} catch (IOException e) {
			throw new CollectionException(e);
		}
		try {
			XPath rootPath = new Dom4jXPath("//tweet");
			Element rootElement = (Element) rootPath.selectSingleNode(root);
			tweetText = getContent(rootElement, "descendant::text");
			tweetId = getContent(rootElement, "descendant::id");
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		jcas.setDocumentText(tweetId+"  "+tweetText);
		jcas.setDocumentLanguage(language);

		return jcas;
	}

	protected String getContent(Element rootElement, String path) throws JaxenException {

		final XPath xp = new Dom4jXPath(path);
		for (Object element : xp.selectNodes(rootElement)) {
			if (element instanceof Element) {
				return ((Element) element).getText();
			}
		}
		return null;
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		
		res = nextFile();
		initCas(jcas, res);
	    jcas= readCas(jcas, res); 
//	   
//	    if(memorizeResource){
//	    	OriginalResource origin= new OriginalResource(jcas);
//	    	origin.setLocation(res.getResource().getFile().getAbsolutePath());
//	    	origin.setFileName(res.getPath());
//	    	origin.addToIndexes();
//	    }
	}
	
}
