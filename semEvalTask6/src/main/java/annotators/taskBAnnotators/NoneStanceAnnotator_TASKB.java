package annotators.taskBAnnotators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.fstore.simple.DenseFeatureStore;
import de.tudarmstadt.ukp.dkpro.tc.ml.modelpersist.ModelPersistUtil;
import de.tudarmstadt.ukp.dkpro.tc.ml.uima.TcAnnotatorUtil;
import types.TaskBStanceAnnotation;

public class NoneStanceAnnotator_TASKB extends JCasAnnotator_ImplBase {

	public static final String PARAM_TOPI_NOUNS = "topINouns_File";
	@ConfigurationParameter(name = PARAM_TOPI_NOUNS, mandatory = true)
	protected File topINounsFile;
	
	public static final String PARAM_FREQUENCY_CUT_OFF = "frqCutOff_StanceVs_None";
	public static final String PARAM_STANCE_TARGET = "target";
	
	@ConfigurationParameter(name = PARAM_STANCE_TARGET, mandatory = true)
	protected String target;

	@ConfigurationParameter(name = PARAM_FREQUENCY_CUT_OFF, mandatory = false, defaultValue="60")
	private int cutOff;
	
	private List<String> topiNouns;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			topiNouns = getTop60Nouns(topINounsFile,cutOff);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		boolean stance=false;
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if (topiNouns.contains(t.getCoveredText().toLowerCase())) {
				stance=true;
				break;
			}
		}
		TaskBStanceAnnotation annotation= new TaskBStanceAnnotation(jcas);
		annotation.setTarget(target);
		if(stance){
			annotation.setStance("STANCE");
		}else{
			annotation.setStance("NONE");
		}
		annotation.setBegin(0);
		annotation.setEnd(jcas.getDocumentText().length());
		annotation.addToIndexes();
	}
	
	private  List<String> getTop60Nouns(File target, int cutOff) throws FileNotFoundException, IOException {
		List<String> top60Nouns = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(target.getAbsolutePath()))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				for (String noun : line.split(","))
					top60Nouns.add(noun.replace(" ", ""));
			}
		}
		return top60Nouns.subList(0, cutOff);
	}

}
