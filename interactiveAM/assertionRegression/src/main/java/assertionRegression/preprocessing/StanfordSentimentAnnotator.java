package assertionRegression.preprocessing;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.sentiment.type.StanfordSentimentAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.ejml.simple.SimpleMatrix;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class StanfordSentimentAnnotator extends JCasAnnotator_ImplBase{

	private StanfordCoreNLP pipeline;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Collection<Sentence> sentences=JCasUtil.select(jcas,Sentence.class);
//		Collection<Sentence> dkproSentences = JCasUtil.select(jcas, Sentence.class);

		if (sentences.isEmpty()) {
			throw new AnalysisEngineProcessException(new IllegalArgumentException("No sentences in jcas"));
		}

		for (Sentence comment : sentences) {
			String commentText = comment.getCoveredText();
			Annotation annotation = pipeline.process(commentText);

			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				SimpleMatrix sentimentCoefficients = RNNCoreAnnotations.getPredictions(tree);
				int predictedClass = RNNCoreAnnotations.getPredictedClass(tree);
				
				double veryNegative = sentimentCoefficients.get(0);
				double negative = sentimentCoefficients.get(1);
				double neutral = sentimentCoefficients.get(2);
				double positive = sentimentCoefficients.get(3);
				double veryPositive = sentimentCoefficients.get(4);
				
				StanfordSentimentAnnotation sentimentAnnotation = new StanfordSentimentAnnotation(jcas);
				sentimentAnnotation.setBegin(comment.getBegin());
				sentimentAnnotation.setEnd(comment.getEnd());
				sentimentAnnotation.setVeryNegative(veryNegative);
				sentimentAnnotation.setNegative(negative);
				sentimentAnnotation.setNeutral(neutral);
				sentimentAnnotation.setPositive(positive);
				sentimentAnnotation.setVeryPositive(veryPositive);
				sentimentAnnotation.addToIndexes();
			}
		}
		
	}

}
