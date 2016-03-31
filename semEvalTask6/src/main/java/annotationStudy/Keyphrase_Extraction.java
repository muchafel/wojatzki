package annotationStudy;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import io.TaskATweetReader;
import util.PreprocessingPipeline;

public class Keyphrase_Extraction {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		String target="LegalizationofAbortion";
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/targets/"+target+"/",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);
		
		FrequencyDistribution<String> fd_chunks= new FrequencyDistribution<String>();
		FrequencyDistribution<String> fd_concepts= new FrequencyDistribution<String>();
		Iterator<JCas> it= SimplePipeline.iteratePipeline(reader,PreprocessingPipeline.getPreprocessingChunkingFunctionalStanceAnno()).iterator();
		while (it.hasNext()) {
			JCas jcas = it.next();
			for(Chunk chunk: JCasUtil.select(jcas, Chunk.class)){
//				System.out.println(chunk.getCoveredText()+ " "+ chunk.getChunkValue());
				if(chunk.getChunkValue().equals("NP")){
					fd_chunks.inc(chunk.getCoveredText());
				}
			}
			
			for(Token t: JCasUtil.select(jcas, Token.class)){
//				System.out.println(chunk.getCoveredText()+ " "+ chunk.getChunkValue());
				if(t.getPos().getClass().getSimpleName().equals("NN") || t.getPos().getClass().getSimpleName().equals("NP")){
					System.out.println(t.getCoveredText()+ " "+ t.getPos().getClass().getSimpleName());
					fd_concepts.inc(t.getLemma().getValue());
				}
			}
		}
		for(String chunk : fd_chunks.getMostFrequentSamples(250)){
			System.out.println(chunk+ "\t"+fd_chunks.getCount(chunk));
		}
		System.out.println("-------------");
		for(String t : fd_concepts.getMostFrequentSamples(150)){
			System.out.println(t+ "\t"+fd_concepts.getCount(t));
		}
	}

}
