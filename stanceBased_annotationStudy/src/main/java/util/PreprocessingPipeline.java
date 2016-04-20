package util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import annotators.FunctionalPartsAnnotator;
import annotators.MergedArktweetTokenizer;
import annotators.TwitterSpecificAnnotator;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class PreprocessingPipeline {



	/**
	 * 1. use the break iterator to create sentence annos 2. run ark tweet
	 * tagger and annotate tokens but keep sentence annos 3. Ark-tools pos
	 * tagging 4. write hashtags and [at]s to TwitterSpecificAnno - User or
	 * hashtag then remove pos-tagging 5. open NLP POS tagging 6. lemmas
	 * (Stanford) 7. OpenNlpChunker 8. ClearNlpDependencyParser 9.
	 * NegationAnnotator 10. FunctionalPartsAnnotator 11.
	 * TokenStancePolarityAnnotator 12. HashTagStancePolarityAnnotator 13.
	 * ModalVerbAnnotator
	 * 
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription getPreprocessingChunkingFunctionalStanceAnno()
			throws ResourceInitializationException {
		return createEngineDescription(
				createEngineDescription(BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false),
				createEngineDescription(MergedArktweetTokenizer.class),
				createEngineDescription(ArktweetPosTagger.class, ArktweetPosTagger.PARAM_VARIANT, "default"),
				createEngineDescription(TwitterSpecificAnnotator.class),
				createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_PRINT_TAGSET, false),
				createEngineDescription(StanfordLemmatizer.class),
				createEngineDescription(OpenNlpChunker.class,OpenNlpChunker.PARAM_PRINT_TAGSET,true),
				createEngineDescription(FunctionalPartsAnnotator.class));
	}
}
