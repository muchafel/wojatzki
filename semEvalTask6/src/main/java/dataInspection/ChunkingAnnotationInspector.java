package dataInspection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import types.HashTagStancePolarity;
import types.WordStancePolarity;

public class ChunkingAnnotationInspector  extends JCasAnnotator_ImplBase{
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		
		for(Chunk chunk: JCasUtil.select(jcas, Chunk.class)){
			System.out.println(chunk.getCoveredText()+ " "+ chunk.getChunkValue());
		}
	}
}
