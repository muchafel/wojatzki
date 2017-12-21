package assertionRegression.corpusProcessing;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;

import cmu.arktweetnlp.Twokenize;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NonSentenceArktweetTokenizer  extends CasAnnotator_ImplBase {

	    private Type tokenType;

	    @Override
	    public void typeSystemInit(TypeSystem aTypeSystem)
	        throws AnalysisEngineProcessException
	    {
	        super.typeSystemInit(aTypeSystem);

	        tokenType = aTypeSystem.getType(Token.class.getName());
	    }


	    private void createTokenAnnotation(CAS cas, int start, int end)
	    {
	        AnnotationFS tokenAnno = cas.createAnnotation(tokenType, start, end);
	        cas.addFsToIndexes(tokenAnno);

	    }

		@Override
		public void process(CAS cas) throws AnalysisEngineProcessException {
			 String text = cas.getDocumentText();

		        List<String> tokenize = Twokenize.tokenize(text);
		        int offset = 0;
		        for (String t : tokenize) {
		            int start = text.indexOf(t, offset);
		            int end = start + t.length();
		            createTokenAnnotation(cas, start, end);
		            offset = end;
		        }
			
		}


}
