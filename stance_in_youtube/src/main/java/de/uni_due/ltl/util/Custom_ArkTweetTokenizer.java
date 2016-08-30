package de.uni_due.ltl.util;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;

import cmu.arktweetnlp.Twokenize;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Custom_ArkTweetTokenizer extends CasAnnotator_ImplBase{

	
	private Type tokenType;
	private Type sentenceType;

    @Override
    public void typeSystemInit(TypeSystem aTypeSystem)
        throws AnalysisEngineProcessException
    {
        super.typeSystemInit(aTypeSystem);

        tokenType = aTypeSystem.getType(Token.class.getName());
        sentenceType = aTypeSystem.getType(Sentence.class.getName());
    }
	
	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		
		String text = cas.getDocumentText();
		
		int sentenceStart=0;
		int i=0;
		for(String sentence: text.split(System.lineSeparator())){
			i++;
			System.out.println("< "+sentence+ " >"+ sentenceStart+ " "+ sentence.length());
			if(i<text.split(System.lineSeparator()).length){
				AnnotationFS sentenceAnno = cas.createAnnotation(sentenceType, sentenceStart, sentenceStart+sentence.length()+ System.lineSeparator().length());
				cas.addFsToIndexes(sentenceAnno);
			}else{
				AnnotationFS sentenceAnno = cas.createAnnotation(sentenceType, sentenceStart, sentenceStart+sentence.length());
				cas.addFsToIndexes(sentenceAnno);
			}
			sentenceStart+=sentence.length()+ System.lineSeparator().length();
		}
		System.out.println("---- start sentence splitting");
		try {
			for(Sentence sentence: JCasUtil.select(cas.getJCas(), Sentence.class)){
				System.out.println(sentence.getCoveredText());
			}
		} catch (CASException e) {
			e.printStackTrace();
		}
		
        List<String> tokenize = Twokenize.tokenize(text);
        int offset = 0;
        for (String t : tokenize) {
            int start = text.indexOf(t, offset);
            int end = start + t.length();
            createTokenAnnotation(cas, start, end);
            offset = end;
        }
		
	}

	private void createTokenAnnotation(CAS cas, int start, int end) {
		AnnotationFS tokenAnno = cas.createAnnotation(tokenType, start, end);
        cas.addFsToIndexes(tokenAnno);
	}
}
