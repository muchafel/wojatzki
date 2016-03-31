package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.fstore.simple.DenseFeatureStore;
import types.StanceAnnotation;

public class StanceResultWriter extends JCasAnnotator_ImplBase {

	public static final String PARAM_RESULT_OUTPUT_TARGET = "ResultOutPutTarget";
	@ConfigurationParameter(name = PARAM_RESULT_OUTPUT_TARGET, mandatory = true)
	protected String resultTarget;

	File result;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		result = new File("src/main/resources/results/taskA/" + resultTarget + "_test_predicted.txt");
		try {
			result.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		StanceAnnotation stanceAnno = JCasUtil.selectSingle(jcas, StanceAnnotation.class);
		TextClassificationOutcome outcome = JCasUtil.selectSingle(jcas, TextClassificationOutcome.class);
		String resultString = stanceAnno.getOriginalId() + "\t" + stanceAnno.getTarget() + "\t"+jcas.getDocumentText()+"\t" + outcome.getOutcome();
		try (PrintWriter pw = new PrintWriter(new PrintWriter(new FileOutputStream(result, true)))) {
			pw.write(resultString + "" + System.lineSeparator());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
