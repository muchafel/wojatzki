package de.uni_due.ltl.catalanStanceDetection.dl_Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_component.JCasMultiplier_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.uni_due.ltl.catalanStanceDetection.io.CatalanStanceReader;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class PrepareDL_Data_Test {

	static String LANGUAGE_CODE = "es";

	public static void main(String[] args) throws IOException, UIMAException {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(CatalanStanceReader.class,
				CatalanStanceReader.PARAM_LANGUAGE, LANGUAGE_CODE, CatalanStanceReader.PARAM_SOURCE_LOCATION,
				baseDir +"/IberEval/test/test_tweets_"+LANGUAGE_CODE+".txt", CatalanStanceReader.PARAM_LABEL_FILE,
				baseDir + "/IberEval/test_truth_" + LANGUAGE_CODE + ".txt", CatalanStanceReader.PARAM_IST_TRAIN, false);

		AnalysisEngine tokenizerEngine = getTokenizerEngine(
				baseDir + "/IberEval/training_tweets_" + LANGUAGE_CODE + ".txt");

		for (JCas jcas : new JCasIterable(reader)) {
			tokenizerEngine.process(jcas);
			String text= getWhitespaceSpearatedTokens(jcas);
			DocumentMetaData md= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
			FileUtils.write(new File("src/main/resources/dl_testData/"+LANGUAGE_CODE+"/test.txt"),md.getDocumentId()+"\t"+ text+System.lineSeparator(), "UTF-8", true);
		}

	}

	

	private static void create10FoldTxtCorpus(File file, AnalysisEngine tokenizerEngine,
			CollectionReaderDescription reader) throws IOException, UIMAException {
		int numberOfInstances = 0;
		Map<Integer, JCASConatiner> id2Cas= new HashedMap();
		for (JCas jCas : new JCasIterable(reader)) {
			tokenizerEngine.process(jCas);
//			for (TextClassificationOutcome outcome : JCasUtil.select(jCas, TextClassificationOutcome.class)) {
//				System.out.println(outcome.getOutcome());
//			}
//			System.out.println(md.getDocumentId());
			JCASConatiner copiedJCas=new JCASConatiner(jCas);
			id2Cas.put(Integer.valueOf(copiedJCas.getId()), copiedJCas);
			numberOfInstances++;
		}
		System.out.println("-----");
		
		int foldSize =(int) Math.ceil((double)numberOfInstances / (double)10 );
		int lowerBound= 0;
		int upperBound= foldSize;
		for (int i = 1; i <= 10; i++) {
			System.out.println(id2Cas.keySet());
			for (int id  : id2Cas.keySet()) {
//				System.out.println(id);
				if(id>=lowerBound && id<upperBound){
					printFold(false, id2Cas.get(id), i);
				}else{
					printFold(true, id2Cas.get(id), i);
				}
			}
			lowerBound+=foldSize;
			upperBound+=foldSize;
		}
		
	}
	
	private static JCas copyJCas(JCas jCas) throws UIMAException {
		 // Create an empty CAS as a destination for a copy.
        JCas emptyJCas = JCasFactory.createJCas();
        DocumentMetaData.create(emptyJCas);
        emptyJCas.setDocumentText(jCas.getDocumentText());
        CAS emptyCas = emptyJCas.getCas();

        // Copy current CAS to the empty CAS.
        CasCopier.copyCas(jCas.getCas(), emptyCas, false);
        JCas copyJCas;
        try {
            copyJCas = emptyCas.getJCas();
        }
        catch (CASException e) {
            throw new AnalysisEngineProcessException("Exception while creating JCas", null, e);
        }
		return copyJCas;
	}


	private static void printFold(boolean isTrain, JCASConatiner jcasConatiner, int i) throws IOException {
		File folder = new File("src/main/resources/dl_data/" + LANGUAGE_CODE + "/" + String.valueOf(i) + "/");
		String trainOrTest = "";
		if (isTrain) {
			trainOrTest = "train";
		} else {
			trainOrTest = "test";
		}
//		DocumentMetaData md= JCasUtil.selectSingle(jcasConatiner, DocumentMetaData.class);
//		System.out.println(md.getDocumentId());
			if (jcasConatiner.getOutcome().equals("NEUTRAL")) {
				FileUtils.write(new File(folder + "/" + trainOrTest + "/neutral.txt"),
						String.valueOf(jcasConatiner.getId()) + "\t" + jcasConatiner.getText() + "\n", true);
			}
			if (jcasConatiner.getOutcome().equals("FAVOR")) {
				FileUtils.write(new File(folder + "/" + trainOrTest + "/favor.txt"),
						String.valueOf(jcasConatiner.getId()) + "\t" + jcasConatiner.getText() + "\n", true);
			}
			if (jcasConatiner.getOutcome().equals("AGAINST")) {
				FileUtils.write(new File(folder + "/" + trainOrTest + "/against.txt"),
						String.valueOf(jcasConatiner.getId()) + "\t" + jcasConatiner.getText() + "\n", true);
			}
}

	private static String getWhitespaceSpearatedTokens(JCas jcas) {
		List<String> tokenTexts = new ArrayList<>();
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			tokenTexts.add(t.getCoveredText());
		}
		return StringUtils.join(tokenTexts, " ");
	}

	private static AnalysisEngine getTokenizerEngine(String string) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(createEngineDescription(ArktweetTokenizer.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

}
