package io;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;


public class TaskATweetReader_None_Stance extends TaskATweetReader {
	
	@Override
	protected String getContent(Element rootElement, String path) throws JaxenException {
		if(path.equals("descendant::stance")){
        	final XPath xp = new Dom4jXPath(path);
    	    for (Object element : xp.selectNodes(rootElement)) {
    	        if (element instanceof Element) {
    	        	String elementText=((Element) element).getText();
    	        	if(elementText.equals("FAVOR")||elementText.equals("AGAINST"))return"STANCE";
    	            return ((Element) element).getText();
    	        }
    	    }
        }else{
        	final XPath xp = new Dom4jXPath(path);
    	    for (Object element : xp.selectNodes(rootElement)) {
    	        if (element instanceof Element) {
    	            return ((Element) element).getText();
    	        }
    	    }
        }
		return null;
	}
}
