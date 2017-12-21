package assertionRegression.corpusProcessing;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class LineBreaker extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		int start=0;
		for(String part: aJCas.getDocumentText().split("\n")){
			int end=start+part.length();
//			System.out.println(start+ " "+ end+ " "+part);
//			System.out.println(aJCas.getDocumentText().charAt(end));
//			System.out.println(aJCas.getDocumentText().charAt(end+1));
			Sentence sentence= new Sentence(aJCas, start, end);
			sentence.addToIndexes();
			start=end+1;
			
		}

	}

}
