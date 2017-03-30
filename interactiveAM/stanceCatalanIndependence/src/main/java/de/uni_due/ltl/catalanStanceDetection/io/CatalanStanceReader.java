package de.uni_due.ltl.catalanStanceDetection.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;

public class CatalanStanceReader extends JCasResourceCollectionReader_ImplBase{
	 public static final String PARAM_SOURCE_ENCODING = ComponentParameters.PARAM_SOURCE_ENCODING;
	    @ConfigurationParameter(name = PARAM_SOURCE_ENCODING, mandatory = true, defaultValue = "UTF-8")
	    protected String encoding;

	    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
		protected String language;

	    public static final String PARAM_UNESCAPE_HTML = "PARAM_UNESCAPE_HTML";
	    @ConfigurationParameter(name = PARAM_UNESCAPE_HTML, mandatory = false, defaultValue = "true")
	    private boolean unescapeHtml;
	    
	    public static final String PARAM_UNESCAPE_JAVA = "PARAM_UNESCAPE_JAVA";
	    @ConfigurationParameter(name = PARAM_UNESCAPE_JAVA, mandatory = false, defaultValue = "true")
	    private boolean unescapeJava;
	    
	    public static final String PARAM_LABEL_FILE = "PARAM_LABEL_FILE";
	    @ConfigurationParameter(name = PARAM_LABEL_FILE, mandatory = true)
	    private String labelFilePath;

	    public static final String ENCODING_AUTO = "auto";

	    private BufferedReader br;

	    private List<BufferedReader> bfs = new ArrayList<BufferedReader>();
	    private int currentReader = 0;

	    private List<String> lines= new ArrayList<>();
	    protected Iterator<String> linesIt;
	    
	    protected int instanceId = 1;
	    protected int unitId = 1;

	    private String nextLine = null;
	    
	    private Map<String,String> id2Label;
	    

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			this.id2Label = getId2LabelMapping(labelFilePath);
		} catch (IOException e1) {
			throw new ResourceInitializationException(e1);
		}

		for (Resource r : getResources()) {
			try {
				for (String line : FileUtils.readLines(r.getResource().getFile())) {
					lines.add(line);
				}
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}
		linesIt= lines.iterator();

	}

	private Map<String, String> getId2LabelMapping(String labelFilePath) throws IOException {
		Map<String, String> mapping = new HashMap<String, String>();
		for (String line : FileUtils.readLines(new File(labelFilePath), encoding)) {
			String[] compounds = line.split(":::");
			mapping.put(compounds[0], compounds[1]);
		}
		return mapping;
	}

	public void getNext(JCas jcas) throws IOException, CollectionException {

		DocumentMetaData md = new DocumentMetaData(jcas);
		md.setDocumentTitle("");
		md.setDocumentId("" + (instanceId++));
		md.setLanguage(language);
		md.addToIndexes();

		String documentText = linesIt.next();

		documentText = checkUnescapeHtml(documentText);
		documentText = checkUnescapeJava(documentText);
		String[] compounds = documentText.split(":::");
		String docId = compounds[0];
		documentText = compounds[1];

		// documentText.replace("#27S", "");
		jcas.setDocumentText(documentText);

		Sentence sentence = new Sentence(jcas, 0, jcas.getDocumentText().length());
		TextClassificationTarget unit = new TextClassificationTarget(jcas, sentence.getBegin(), sentence.getEnd());
		unit.setId(unitId++);
		sentence.addToIndexes();

		TextClassificationOutcome outcome= getTextClassificationOutcome(jcas, sentence, docId);
		
		unit.addToIndexes();
		outcome.addToIndexes();

	}

	protected TextClassificationOutcome getTextClassificationOutcome(JCas jcas, Sentence sentence, String docId) throws IOException {
		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas, sentence.getBegin(), sentence.getEnd());
		try {
			outcome.setOutcome(id2Label.get(docId));
		} catch (Exception e) {
			throw new IOException(e);
		}
		return outcome;
	}


	protected String checkUnescapeJava(String documentText) {
		String backup = documentText;
		if (unescapeJava) {
			try {
				documentText = StringEscapeUtils.unescapeJava(documentText);
			} catch (NestableRuntimeException e) {
				documentText = backup;
			}
		}
		return documentText;
	}

	protected String checkUnescapeHtml(String documentText) {
		if (unescapeHtml) {
			documentText = StringEscapeUtils.unescapeHtml(documentText);
		}
		return documentText;
	}

	public boolean hasNext() throws IOException, CollectionException {

		return linesIt.hasNext();

	}

	public Progress[] getProgress() {
		return null;
	}
}
