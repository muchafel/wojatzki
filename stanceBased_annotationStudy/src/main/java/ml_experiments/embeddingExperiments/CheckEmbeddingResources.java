package ml_experiments.embeddingExperiments;

import java.io.File;

import org.dkpro.lab.engine.TaskContext;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.lab.task.Discriminator;
import org.dkpro.lab.task.impl.ExecutableTaskBase;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class CheckEmbeddingResources extends ExecutableTaskBase{

	
	@Discriminator
	private String variant;
	
	@Discriminator
	private String targets;
	
	@Discriminator
	private String years;
	
	@Discriminator
	private String word2vecDims;
	
	@Override
	public void execute(TaskContext context) throws Exception {
		targets= getTargetName(targets);
		if(variant.equals("Word2Vec_raw")){
			System.out.println("check "+DkproContext.getContext().getWorkspace().getAbsolutePath()+"/"+years+"/"+word2vecDims+"/"+targets+years+".txt_word2Vec_"+word2vecDims+".txt");
			File resource = new File(DkproContext.getContext().getWorkspace().getAbsolutePath()+"/"+years+"/"+word2vecDims+"/"+targets+years+".txt_word2Vec_"+word2vecDims+".txt");
			if(!resource.exists()){
				throw new Exception(resource.getAbsolutePath()+" does not exist :(");
			}
		} else if(variant.equals("Word2Vec_clustered1000")){
			System.out.println("check "+DkproContext.getContext().getWorkspace().getAbsolutePath()+"/"+years+"/"+word2vecDims+"/cluster_1000/"+targets+years+".txt_word2Vec_"+word2vecDims+".txt_1000.cluster");
			File resource = new File(DkproContext.getContext().getWorkspace().getAbsolutePath()+"/"+years+"/"+word2vecDims+"/cluster_1000/"+targets+years+".txt_word2Vec_"+word2vecDims+".txt_1000.cluster");
			if(!resource.exists()){
				throw new Exception(resource.getAbsolutePath()+" does not exist :(");
			}
		}
	}
	
	private static String getTargetName(String target) {
		switch (target) {
        case "ATHEISM":
            return "atheism";
        case "Original_Stance":
        	 return "atheism";
        case "Same-sex marriage":
        	return "same_sex_marriage";
        case "religious_freedom":
        	return "religious_freedom";
        case "Conservative_Movement":
        	return "conservative_movement";
        case "Freethinking":
        	return "freethinking";
        case "Islam":
        	return "islam";
        case "No_evidence_for_religion":
        	return "no_evidence_for_religion";
        case "USA":
        	return "usa";
        case "Supernatural_Power_Being":
        	return "supernatural_being";
        case "Life_after_death":
        	return "lifeafterdeath";
        case "Christianity":
        	return "christianity";
        case "secularism":
        	return "secularism";
        default:
            throw new IllegalArgumentException("Invalid target "+target);
		}
	}
	
}
