package util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import annotators.FunctionalPartsAnnotator;
import annotators.HashTagStancePolarityAnnotator;
import annotators.LexiconBasedSentimentAnnotator;
import annotators.MergedArktweetTokenizer;
import annotators.ModalVerbAnnotator;
import annotators.NegationAnnotator;
import annotators.TokenStancePolarityAnnotator;
import annotators.TwitterSpecificAnnotator;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.berkeleyparser.BerkeleyParser;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpDependencyParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import edu.stanford.nlp.pipeline.SentimentAnnotator;

public class PreprocessingPipeline {

	/**
	 * 1. run ark-tools tokenizer
	 * 3. open NLP POS tagging
	 * 4. lemmas (Stanford)
	 * 5. annotate syntax tress using stanford parser
	 * 6. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingTwokenizer() throws ResourceInitializationException {
		return createEngineDescription(
//				createEngineDescription(BreakIteratorSegmenter.class),
//				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(ArktweetTokenizer.class),
//				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
//				createEngineDescription(StanfordParser.class,StanfordParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(BerkeleyParser.class, BerkeleyParser.PARAM_PRINT_TAGSET,true),
//				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	/**
	 * 1. break iterator segmenter
	 * 3. open NLP POS tagging
	 * 4. lemmas (Stanford)
	 * 5. annotate syntax tress using stanford parser
	 * 6. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingBreakIteratorSegmenter() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(BerkeleyParser.class, BerkeleyParser.PARAM_PRINT_TAGSET,true),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. open NLP POS tagging
	 * 4. lemmas (Stanford)
	 * 5. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingBreakTwokenizer() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. POS tagging
	 * 4. lemmas (Stanford)
	 * 5. clear nlp dependencies
	 * 6. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingDependencies() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. open NLP POS tagging
	 * 4. lemmas (Stanford)
	 * 5. annotate syntax tress using stanford parser
	 * 6. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingTree() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(StanfordParser.class,StanfordParser.PARAM_PRINT_TAGSET, true, StanfordParser.PARAM_WRITE_PENN_TREE, true),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. open NLP POS tagging
	 * 4. lemmas (Stanford)
	 * 5. NER
	 * 6. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingNER() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(StanfordNamedEntityRecognizer.class),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 5. lemmas (Stanford)
	 * 6. annotate modal verbs
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingBreakTwokenizerTweetAnnos() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 6. lemmas (Stanford)
	 * 7. ClearNlpDependencyParser
	 * 8. NegationAnnotator 
	 * 6. ModalVerbAnnotator
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingBreakTwokenizerTweetAnnosNegation() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(NegationAnnotator.class, NegationAnnotator.PARAM_NEGATIONWORDS_FILE_PATH,"src/main/resources/lists/listOfNegationWords.txt"),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run arg tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 6. lemmas (Stanford)
	 * 7. ClearNlpDependencyParser
	 * 8. NegationAnnotator 
	 * 9. TokenStancePolarityAnnotator
	 * 10. ModalVerbAnnotator
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingTokenStanceAnno() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(NegationAnnotator.class, NegationAnnotator.PARAM_NEGATIONWORDS_FILE_PATH,"src/main/resources/lists/listOfNegationWords.txt"),
				createEngineDescription(TokenStancePolarityAnnotator.class,TokenStancePolarityAnnotator.PARAM_ABORTIONSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_ATHEISMSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_CLIMATESTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_FEMINISTSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_HILLARYSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/stanceLexicon.txt"),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run ark tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 6. lemmas (Stanford)
	 * 7. ClearNlpDependencyParser
	 * 8. NegationAnnotator 
	 * 9. HTStancePolarityAnnotator
	 * 10. ModalVerbAnnotator
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingHTStanceAnno() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(NegationAnnotator.class, NegationAnnotator.PARAM_NEGATIONWORDS_FILE_PATH,"src/main/resources/lists/listOfNegationWords.txt"),
				createEngineDescription(HashTagStancePolarityAnnotator.class,HashTagStancePolarityAnnotator.PARAM_ABORTION_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_ATHEISM_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_CLIMATE_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_FEMINIST_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_HILLARY_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/hashtag_stanceLexicon.txt"),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run ark tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 6. lemmas (Stanford)
	 * 7. ClearNlpDependencyParser
	 * 8. NegationAnnotator 
	 * 9. FunctionalPartsAnnotator
	 * 10. TokenStancePolarityAnnotator
	 * 11. HashTagStancePolarityAnnotator
	 * 12. ModalVerbAnnotator
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingFunctionalStanceAnno() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class,OpenNlpPosTagger.PARAM_PRINT_TAGSET, true),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(NegationAnnotator.class, NegationAnnotator.PARAM_NEGATIONWORDS_FILE_PATH,"src/main/resources/lists/listOfNegationWords.txt"),
				createEngineDescription(FunctionalPartsAnnotator.class),
				createEngineDescription(TokenStancePolarityAnnotator.class,
						TokenStancePolarityAnnotator.PARAM_USE_FUCNTIONAL_PARTITION,true,
						TokenStancePolarityAnnotator.PARAM_ABORTIONSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_ATHEISMSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_CLIMATESTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_FEMINISTSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_HILLARYSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/stanceLexicon.txt"),
				createEngineDescription(HashTagStancePolarityAnnotator.class,
						HashTagStancePolarityAnnotator.PARAM_USE_FUCNTIONAL_PARTITION,true,
						HashTagStancePolarityAnnotator.PARAM_ABORTION_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_ATHEISM_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_CLIMATE_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_FEMINIST_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_HILLARY_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/hashtag_stanceLexicon.txt"),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run ark tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 6. lemmas (Stanford)
	 * 7. OpenNlpChunker
	 * 8. ClearNlpDependencyParser
	 * 9. NegationAnnotator 
	 * 10. FunctionalPartsAnnotator
	 * 11. TokenStancePolarityAnnotator
	 * 12. HashTagStancePolarityAnnotator
	 * 13. ModalVerbAnnotator
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingChunkingFunctionalStanceAnno() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class,OpenNlpPosTagger.PARAM_PRINT_TAGSET, true),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(OpenNlpChunker.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(NegationAnnotator.class, NegationAnnotator.PARAM_NEGATIONWORDS_FILE_PATH,"src/main/resources/lists/listOfNegationWords.txt"),
				createEngineDescription(FunctionalPartsAnnotator.class),
				createEngineDescription(TokenStancePolarityAnnotator.class,
						TokenStancePolarityAnnotator.PARAM_USE_FUCNTIONAL_PARTITION,true,
						TokenStancePolarityAnnotator.PARAM_ABORTIONSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_ATHEISMSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_CLIMATESTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_FEMINISTSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_HILLARYSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/stanceLexicon.txt"),
				createEngineDescription(HashTagStancePolarityAnnotator.class,
						HashTagStancePolarityAnnotator.PARAM_USE_FUCNTIONAL_PARTITION,true,
						HashTagStancePolarityAnnotator.PARAM_ABORTION_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_ATHEISM_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_CLIMATE_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_FEMINIST_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_HILLARY_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/hashtag_stanceLexicon.txt"),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	
	/**
	 * 1. use the break iterator to create sentence annos
	 * 2. run ark tweet tagger and annotate tokens but keep sentence annos
	 * 3. Ark-tools pos tagging
	 * 4. write hashtags and [at]s to TwitterSpecificAnno - User or hashtag
	 * 		then remove pos-tagging
	 * 5. open NLP POS tagging
	 * 6. lemmas (Stanford)
	 * 7. OpenNlpChunker
	 * 8. ClearNlpDependencyParser
	 * 9. SentimentAnnotator
	 * 10. NegationAnnotator 
	 * 11. FunctionalPartsAnnotator
	 * 12. TokenStancePolarityAnnotator
	 * 13. HashTagStancePolarityAnnotator
	 * 14. ModalVerbAnnotator
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingSentimentFunctionalStanceAnno() throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default") ,
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class,OpenNlpPosTagger.PARAM_PRINT_TAGSET, true),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(OpenNlpChunker.class),
				createEngineDescription(ClearNlpDependencyParser.class, ClearNlpDependencyParser.PARAM_PRINT_TAGSET, true),
				createEngineDescription(LexiconBasedSentimentAnnotator.class),
				createEngineDescription(NegationAnnotator.class, NegationAnnotator.PARAM_NEGATIONWORDS_FILE_PATH,"src/main/resources/lists/listOfNegationWords.txt"),
				createEngineDescription(FunctionalPartsAnnotator.class),
				createEngineDescription(TokenStancePolarityAnnotator.class,
						TokenStancePolarityAnnotator.PARAM_USE_FUCNTIONAL_PARTITION,true,
						TokenStancePolarityAnnotator.PARAM_ABORTIONSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_ATHEISMSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_CLIMATESTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_FEMINISTSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/stanceLexicon.txt",
						TokenStancePolarityAnnotator.PARAM_HILLARYSTANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/stanceLexicon.txt"),
				createEngineDescription(HashTagStancePolarityAnnotator.class,
						HashTagStancePolarityAnnotator.PARAM_USE_FUCNTIONAL_PARTITION,true,
						HashTagStancePolarityAnnotator.PARAM_ABORTION_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Legalization of Abortion/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_ATHEISM_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Atheism/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_CLIMATE_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Climate Change is a Real Concern/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_FEMINIST_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Feminist Movement/hashtag_stanceLexicon.txt",
						HashTagStancePolarityAnnotator.PARAM_HILLARY_HASHTAG_STANCES_FILE_PATH,"src/main/resources/lists/stanceLexicons/Hillary Clinton/hashtag_stanceLexicon.txt"),
				createEngineDescription(ModalVerbAnnotator.class, ModalVerbAnnotator.PARAM_MODALVERBS_FILE_PATH,"src/main/resources/lists/listOfModalVerbs.txt")
				);
	}
	
	
}
