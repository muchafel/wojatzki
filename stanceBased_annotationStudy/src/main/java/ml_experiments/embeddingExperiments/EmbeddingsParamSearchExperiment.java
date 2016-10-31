package ml_experiments.embeddingExperiments;

import java.util.ArrayList;
import java.util.Arrays;

import org.dkpro.lab.Lab;
import org.dkpro.lab.task.BatchTask;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class EmbeddingsParamSearchExperiment {
	public final static String DIM_VARIANT = "variant";
	public static final String DIM_TARGETS = "targets";
	public static final String DIM_YEARS = "years";
	public static final String DIM_WORD2VEC_DIMS = "word2vecDims";
	
	
	public static void main(String[] args) 
		throws Exception
	{
        ParameterSpace pSpace = new ParameterSpace(
                Dimension.create(DIM_VARIANT, new String[] { 
                		"Word2Vec_raw"
                		,"Word2Vec_clustered1000"
                		,"Word2Vec_clustered1000_atheism_Embeddings"
                		,"Word2Vec_raw_atheism_embeddings" 
                		}),
                Dimension.create(DIM_YEARS, new String[] {
//                		"2015",
                		"2016"
                		,"20152016" }),
                Dimension.create(DIM_WORD2VEC_DIMS, new String[] { "75", "150"}),
                Dimension.create(DIM_TARGETS, new String[] {
                		"Original_Stance",
                		"secularism",
				"Same-sex marriage", "religious_freedom", "Conservative_Movement", 
				"Freethinking", "Islam",
				"No_evidence_for_religion", 
				"USA", "Supernatural_Power_Being", 
				"Life_after_death", "Christianity"
				})
        );
        
        String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		
        ExampleBatchTask batch = new ExampleBatchTask();
        batch.setParameterSpace(pSpace);
        
        Lab.getInstance().run(batch);
	}
}
