package de.uni_due.ltl.featureExtractors.commentNgrams;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.dkpro.tc.core.Constants.NGRAM_GLUE;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.toText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.meta.MetaCollector;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.features.ngram.LuceneNGram;
import org.dkpro.tc.features.ngram.base.NGramFeatureExtractorBase;
import org.dkpro.tc.features.ngram.meta.LuceneBasedMetaCollector;
import org.dkpro.tc.features.ngram.util.NGramUtils;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathFactory;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import edu.stanford.nlp.pipeline.Annotation;
import preprocessing.CommentText;

public class CommentNGramMetaCollector extends MetaCollector {

	public final static String LUCENE_DIR = "lucene";

	public static final String LUCENE_ID_FIELD = "id";

	public static final String PARAM_TARGET_LOCATION = ComponentParameters.PARAM_TARGET_LOCATION;
	@ConfigurationParameter(name = PARAM_TARGET_LOCATION, mandatory = true)
	private File luceneDir;

	// this is a static singleton as different Lucene-based meta collectors will
	// use the same writer
	protected static IndexWriter indexWriter = null;

	private String currentDocumentId;
	private Document currentDocument;

	private FieldType fieldType;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_MIN_N, mandatory = true, defaultValue = "1")
	private int ngramMinN;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_MAX_N, mandatory = true, defaultValue = "3")
	private int ngramMaxN;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_STOPWORDS_FILE, mandatory = false)
	private String ngramStopwordsFile;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_FILTER_PARTIAL_STOPWORD_MATCHES, mandatory = true, defaultValue = "false")
	private boolean filterPartialStopwordMatches;

	@ConfigurationParameter(name = NGramFeatureExtractorBase.PARAM_NGRAM_LOWER_CASE, mandatory = false, defaultValue = "true")
	private String stringNgramLowerCase;

	boolean ngramLowerCase = true;

	private Set<String> stopwords;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44, null);

		if (indexWriter == null) {
			try {
				indexWriter = new IndexWriter(FSDirectory.open(luceneDir), config);
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}

		currentDocumentId = null;
		currentDocument = null;

		fieldType = new FieldType();
		fieldType.setIndexed(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		fieldType.setStored(true);
		fieldType.setOmitNorms(true);
		fieldType.setTokenized(false);
		fieldType.freeze();

		ngramLowerCase = Boolean.valueOf(stringNgramLowerCase);

		try {
			stopwords = FeatureUtil.getStopwords(ngramStopwordsFile, ngramLowerCase);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	protected FrequencyDistribution<String> getNgramsFD(JCas jcas) throws TextClassificationException {

		TextClassificationTarget target = new TextClassificationTarget(jcas, 0, jcas.getDocumentText().length());
		FrequencyDistribution<String> fd = null;
		fd = getDocumentNgrams(jcas, target, ngramLowerCase, filterPartialStopwordMatches, ngramMinN, ngramMaxN,
				stopwords, Token.class);

		return fd;
	}

	protected String getFieldName() {
		return LuceneNGram.LUCENE_NGRAM_FIELD + "_CommentNgrams";
	}

	public FrequencyDistribution<String> getDocumentNgrams(JCas jcas, TextClassificationTarget target,
			boolean lowerCaseNGrams, boolean filterPartialMatches, int minN, int maxN, Set<String> stopwords,
			Class<Token> annotationClass) throws TextClassificationException {
		FrequencyDistribution<String> documentNgrams = new FrequencyDistribution<String>();
		for (CommentText s : selectCovered(jcas, CommentText.class, target)) {
			List<String> strings = valuesToText(jcas, s, annotationClass.getName());
			for (List<String> ngram : new NGramStringListIterable(strings, minN, maxN)) {
				if (lowerCaseNGrams) {
					ngram = lower(ngram);
				}

				String ngramString = StringUtils.join(ngram, NGRAM_GLUE);
				documentNgrams.inc(ngramString);
			}
		}
		return documentNgrams;
	}

	private List<String> lower(List<String> ngram) {
		List<String> newNgram = new ArrayList<String>();
		for (String token : ngram) {
			newNgram.add(token.toLowerCase());
		}
		return newNgram;
	}

	private <T extends Annotation> List<String> valuesToText(JCas jcas, CommentText s, String annotationClassName)
			throws TextClassificationException {
		List<String> texts = new ArrayList<String>();

		try {
			for (Entry<AnnotationFS, String> entry : FeaturePathFactory.select(jcas.getCas(), annotationClassName)) {
				if (entry.getKey().getBegin() >= s.getBegin() && entry.getKey().getEnd() <= s.getEnd()) {
					texts.add(entry.getValue());
				}
			}
		} catch (FeaturePathException e) {
			throw new TextClassificationException(e);
		}
		return texts;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		initializeDocument(jcas);
		FrequencyDistribution<String> documentNGrams;
		try {
			documentNGrams = getNgramsFD(jcas);
		} catch (TextClassificationException e) {
			throw new AnalysisEngineProcessException(e);
		}

		for (String ngram : documentNGrams.getKeys()) {
			// As a result of discussion, we add a field for each ngram per doc,
			// not just each ngram type per doc.
			for (int i = 0; i < documentNGrams.getCount(ngram); i++) {
				addField(jcas, getFieldName(), ngram);
			}
		}

		try {
			writeToIndex();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected void addField(JCas jcas, String fieldName, String value) throws AnalysisEngineProcessException {
		if (currentDocument == null) {
			throw new AnalysisEngineProcessException(new Throwable("Document not initialized. "
					+ "Probably a lucene-based meta collector that calls addField() before initializeDocument()"));
		}

		Field field = new Field(fieldName, value, fieldType);
		currentDocument.add(field);
	}

	protected void writeToIndex() throws IOException {
		if (currentDocument == null) {
			throw new IOException("Lucene document not initialized. Fatal error.");
		}
		indexWriter.addDocument(currentDocument);
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		if (indexWriter != null) {
			try {
				indexWriter.commit();
				indexWriter.close();
				indexWriter = null;
			} catch (AlreadyClosedException e) {
				// ignore, as multiple meta collectors write in the same index
				// and will all try to close the index
			} catch (CorruptIndexException e) {
				throw new AnalysisEngineProcessException(e);
			} catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}

	}

	protected String getDocumentId(JCas jcas) {
		return DocumentMetaData.get(jcas).getDocumentId();
	}

	protected void initializeDocument(JCas jcas) {
		if (currentDocument == null || !currentDocumentId.equals(getDocumentId(jcas))) {
			currentDocumentId = getDocumentId(jcas);
			if (currentDocumentId == null) {
				throw new IllegalArgumentException(
						"Document has no id. id: " + DocumentMetaData.get(jcas).getDocumentId() + ", title: "
								+ DocumentMetaData.get(jcas).getDocumentTitle() + ", uri: "
								+ DocumentMetaData.get(jcas).getDocumentUri());
			}
			currentDocument = new Document();
			currentDocument.add(new StringField(LUCENE_ID_FIELD, currentDocumentId, Field.Store.YES));
		}
	}

}
