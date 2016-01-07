package assembly;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Progress;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import types.StanceAnnotation;

public class UnclassifiedTweetReader extends JCasResourceCollectionReader_ImplBase {


	/**
	 * Set this as the language of the produced documents.
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name=PARAM_LANGUAGE, mandatory=false)
	private String language;

	private Resource res;
	
	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		res = nextFile();
		initCas(jcas, res);
		jcas= readCas(jcas, res); 
		
	}

	private JCas readCas(JCas jcas, Resource res2) throws CollectionException {
		Element root;
		String tweetText = "";
		String tweetTarget = "";
		String tweetId = "";

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
			tweetTarget = getContent(rootElement, "descendant::target");
			tweetId = getContent(rootElement, "descendant::id");
			// System.out.println(tweetText+" "+tweetTarget+ " "+ tweetStance);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		jcas.setDocumentText(tweetText);
		jcas.setDocumentLanguage(language);

		StanceAnnotation anno = new StanceAnnotation(jcas);
		anno.setTarget(tweetTarget);
		anno.setOriginalId(tweetId);
		anno.addToIndexes();
		return jcas;
	}
	
	private String getContent(Element rootElement, String path) throws JaxenException {

		final XPath xp = new Dom4jXPath(path);
		for (Object element : xp.selectNodes(rootElement)) {
			if (element instanceof Element) {
				// Element node = (Element) element;
				return ((Element) element).getText();
			}
		}
		return null;
	}

}
