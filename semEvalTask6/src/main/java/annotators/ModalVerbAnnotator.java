package annotators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import types.ModalVerb;

public class ModalVerbAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_MODALVERBS_FILE_PATH = "modalVerbsFilePath";
	
	@ConfigurationParameter(name = PARAM_MODALVERBS_FILE_PATH, mandatory = true)
	private String taskFilePath;

	private List<String> modalVerbs;

	@Override
	public void initialize(org.apache.uima.UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			modalVerbs = init(taskFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

	private List<String> init(String path) throws FileNotFoundException, IOException {
		List<String> modals= new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while (( line= br.readLine()) != null) {
				modals.add(line);
			}
		}
		return modals;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if(modalVerbs.contains(t.getCoveredText())){
				ModalVerb anno = new ModalVerb(jcas);
				anno.setBegin(t.getBegin());
				anno.setEnd(t.getEnd());
				anno.setIsModalVerb(true);
				anno.addToIndexes();
			}
		}
	}

}
