package de.uni_due.ltl.util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import ch.qos.logback.classic.db.DBAppender;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import io.YoutubeCommentReader;

public class CommentsToXMI {

	public static void main(String[] args) throws IOException, ResourceInitializationException, UIMAException {
		CommentsToXMI pipelineTaskA= new CommentsToXMI();
//		String commentFolder= "/youtubeStance/youtube_prestudy";
//		String commentFolder= "/youtubeStance/youtube_prestudy_short";
		String commentFolder= "/youtubeStance/youtube_dp_prestudy2/raw";
//		String commentFolder= "/youtubeStance/youtube deathpenalty comments/xmis/cleaned";
//		String commentFolder= "/youtubeStance/youtube deathpenalty comments/cleaned";
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		
		pipelineTaskA.run(baseDir+commentFolder);
	}

	private void run(String folder) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				//replace emojis with alias using https://github.com/vdurmont/emoji-java
				CollectionReaderFactory.createReader(
						YoutubeCommentReader.class,
						YoutubeCommentReader.PARAM_SOURCE_LOCATION, folder, YoutubeCommentReader.PARAM_PATTERNS,
						"*.txt", YoutubeCommentReader.PARAM_LANGUAGE, "en", YoutubeCommentReader.PARAM_REPLACE_EMOJIS_WITH_ALIAS, true),
				AnalysisEngineFactory.createEngineDescription(createEngineDescription(Custom_ArkTweetTokenizer.class)),
//				AnalysisEngineFactory.createEngineDescription(createEngineDescription(ArktweetTokenizer.class)),
				AnalysisEngineFactory.createEngineDescription(createEngineDescription(XmiWriter.class,XmiWriter.PARAM_TARGET_LOCATION,folder+"/xmis/", XmiWriter.PARAM_OVERWRITE, true))
		);	
	}

}
