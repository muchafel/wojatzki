package dataInspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import io.TaskATweetReader;
import types.OriginalResource;
import types.StanceAnnotation;

public class WronglyClassifiedInspection {

	public static void main(String[] args) throws IOException, ResourceInitializationException {
		ArrayList<String> tweetsAtheism = new ArrayList<String>(
			    Arrays.asList("tweets158.xml","tweets244.xml", "tweets467.xml", "tweets298.xml", "tweets342.xml"));
		ArrayList<String> tweetsHillary = new ArrayList<String>(
			    Arrays.asList("tweets1820.xml","tweets1691.xml", "tweets2067.xml", "tweets2239.xml", "tweets1785.xml"));
		
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskA",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

		for (JCas jcas : new JCasIterable(reader)) {
			if (tweetsHillary.contains(JCasUtil.select(jcas, OriginalResource.class).iterator().next().getFileName()))
				System.out.println(jcas.getDocumentText() + " "
						+ JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome());
		}
	}
}
