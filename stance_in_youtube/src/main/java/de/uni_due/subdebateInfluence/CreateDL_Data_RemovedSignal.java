package de.uni_due.subdebateInfluence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.uni_due.ltl.util.TargetSets;
import io.YouTubeReader;
import io.YouTube_RemoveSignal_Reader;
import preprocessing.CommentText;

public class CreateDL_Data_RemovedSignal {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		System.out.println("DKPRO_HOME: " + baseDir);
		for (String explicitTarget : TargetSets.targets_Set1) {
			createDLData(explicitTarget,"1",baseDir+"/youtubeStance/corpus_curated/bin_preprocessed/");
		}
		for (String explicitTarget : TargetSets.targets_Set2) {
			createDLData(explicitTarget,"2",baseDir+"/youtubeStance/corpus_curated/bin_preprocessed/");
		}
	}

	private static void createDLData(String explicitTargetToRemove, String set, String path) throws ResourceInitializationException, IOException {
		
		CollectionReaderDescription reader1 = CollectionReaderFactory.createReaderDescription(YouTube_RemoveSignal_Reader.class, YouTube_RemoveSignal_Reader.PARAM_BINCAS_LOCATION, path, YouTube_RemoveSignal_Reader.PARAM_LANGUAGE,
				"en", YouTube_RemoveSignal_Reader.PARAM_PATTERNS, "*.bin", YouTube_RemoveSignal_Reader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTube_RemoveSignal_Reader.PARAM_TARGET_SET,set, YouTube_RemoveSignal_Reader.PARAM_EXPLICIT_TARGET_SIGNAL_TO_REMOVE,explicitTargetToRemove);

		CollectionReaderDescription reader2 = CollectionReaderFactory.createReaderDescription(YouTube_RemoveSignal_Reader.class, YouTube_RemoveSignal_Reader.PARAM_BINCAS_LOCATION, path, YouTube_RemoveSignal_Reader.PARAM_LANGUAGE,
				"en", YouTube_RemoveSignal_Reader.PARAM_PATTERNS, "*.bin", YouTube_RemoveSignal_Reader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTube_RemoveSignal_Reader.PARAM_TARGET_SET,set, YouTube_RemoveSignal_Reader.PARAM_EXPLICIT_TARGET_SIGNAL_TO_REMOVE,explicitTargetToRemove);

		
		for (JCas jcas : new JCasIterable(reader1)) {
			DocumentMetaData metaData = DocumentMetaData.get(jcas);
			String id = metaData.getDocumentId();
			String folderid=strip(id);
			System.out.println(id);
			File folder= new File("src/main/resources/dl_data_signalRemoved/"+explicitTargetToRemove+"/"+folderid);
			int i=0;
			int sentences=0;
			for (JCas jcas_inner : new JCasIterable(reader2)) {
				DocumentMetaData metaData_inner = DocumentMetaData.get(jcas_inner);
				String id_inner = metaData_inner.getDocumentId();
				String trainOrTest="";
				if(id_inner.equals(id)){
					trainOrTest="test";
				}else{
					trainOrTest="train";
				}
				for (TextClassificationOutcome outcome : JCasUtil.select(jcas_inner, TextClassificationOutcome.class)) {
					if(outcome.getOutcome().equals("NONE")){
						FileUtils.write(new File(folder+"/"+trainOrTest+"/none.txt"),String.valueOf(i)+"_"+ String.valueOf(sentences)+"\t" +getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next())+"\n",true);
					}
					if(outcome.getOutcome().equals("FAVOR")){
						FileUtils.write(new File(folder+"/"+trainOrTest+"/favor.txt"),String.valueOf(i)+"_"+ String.valueOf(sentences)+"\t"+ getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next())+"\n",true);
					}
					if(outcome.getOutcome().equals("AGAINST")){
						FileUtils.write(new File(folder+"/"+trainOrTest+"/against.txt"), String.valueOf(i)+"_"+ String.valueOf(sentences)+"\t"+getWhitespaceSpearatedTokens(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next())+"\n",true);
					}
					sentences++;
				}
				i++;
			}
			System.out.println(sentences);
			System.out.println(i);
			System.out.println();
		}
	}
	private static String strip(String id) {
		String strippedId= id.split("=")[1];
		strippedId= strippedId.substring(0, strippedId.length()-14);
		return strippedId;
	}

	private static CharSequence getWhitespaceSpearatedTokens(CommentText commentText) {
		List<String>tokenTexts=new ArrayList<>();
		for(Token t:JCasUtil.selectCovered(Token.class,commentText)){
			tokenTexts.add(t.getCoveredText());
		}
		return StringUtils.join(tokenTexts, " ");
	}

}
