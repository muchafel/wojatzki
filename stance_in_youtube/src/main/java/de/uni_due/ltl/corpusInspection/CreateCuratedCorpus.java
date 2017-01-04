package de.uni_due.ltl.corpusInspection;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.nd4j.linalg.api.ops.Op;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasWriter;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.uni_due.ltl.simpleClassifications.CuratedStancesAnnotator;
import de.uni_due.ltl.simpleClassifications.FunctionalPartsAnnotator;
import de.uni_due.ltl.simpleClassifications.SentimentCommentAnnotator;
import de.uni_due.ltl.util.Custom_ArkTweetTokenizer;
import io.RemoveSentenceAnnotations;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class CreateCuratedCorpus {
	public static void main(String[] args) throws UIMAException {
//		extract(new File("/Users/michael/Dropbox/explicit targets PHASE II/curated2_deathPenalty_data/curation"));
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(XmiReader.class,
				XmiReader.PARAM_SOURCE_LOCATION,
				"/Users/michael/Dropbox/explicit targets PHASE II/curated2_deathPenalty_data/annotation_unzipped/xmis",
				XmiReader.PARAM_PATTERNS, "*.xmi", XmiReader.PARAM_LANGUAGE, "en");
		AnalysisEngine writerEngine= getWriterEngine("/Users/michael/Dropbox/explicit targets PHASE II/curated2_deathPenalty_data/annotation_unzipped/finalCorpus/");
		for (JCas jcas : new JCasIterable(reader)) {
			DocumentMetaData metaData= JCasUtil.select(jcas, DocumentMetaData.class).iterator().next();
//			System.out.println(JCasUtil.select(jcas, DocumentMetaData.class).size());
//			System.out.println(metaData.getDocumentTitle());
			String title= metaData.getDocumentTitle();
			metaData.removeFromIndexes();
			DocumentMetaData meta = new DocumentMetaData(jcas);
			meta.setDocumentTitle(title);
			meta.setDocumentId(title);
			meta.addToIndexes();
			jcas.setDocumentLanguage("en");
//			System.out.println(JCasUtil.select(jcas, DocumentMetaData.class).iterator().next().getDocumentId());
			writerEngine.process(jcas);
		}
	}
	/**
	 * extracts the zipped files from the webanno structure
	 * 
	 * @param folder
	 */
	private static void extract(File folder) {

		for (File xmiFolder : folder.listFiles()) {
			if (xmiFolder.isDirectory()) {
				for (File annotation : xmiFolder.listFiles()) {
					System.out.println(annotation.getName());
					System.out.println(annotation.getName().substring(3));
					File newAnnoFolder = new File(folder.getParent() + "/annotation_unzipped/"
							+ xmiFolder.getName().substring(0, xmiFolder.getName().length() - 8));
					newAnnoFolder.mkdir();
					if (annotation.getName().substring(annotation.getName().length() - 3).equals("zip")) {
						System.out.println("unzip " + annotation.getName());
						unzip(annotation, newAnnoFolder);
					}
				}
			}
		}
		System.out.println("unzipping done");
	}
	private static void unzip(File fileEntry, File folder) {
		String source = fileEntry.getAbsolutePath();
		String destination = folder.getAbsolutePath();
		try {
			ZipFile zipFile = new ZipFile(source);
			zipFile.extractAll(destination);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		for (File unzipped : new File(destination).listFiles()) {
			if (unzipped.getName().substring(unzipped.getName().length() - 3).equals("xmi")
					&& !unzipped.getName().contains("tweet")) {
				File newName = new File(folder.getAbsolutePath() + "/" + folder.getName() + "_" + unzipped.getName());
				unzipped.renameTo(newName);
			}
		}
	}
	private static AnalysisEngine getWriterEngine(String writeTo) {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
//					createEngineDescription(Custom_ArkTweetTokenizer.class),
					createEngineDescription(FunctionalPartsAnnotator.class),
					createEngineDescription(SentimentCommentAnnotator.class),
//					createEngineDescription(RemoveSentenceAnnotations.class),
//					createEngineDescription(OpenNlpSegmenter.class,OpenNlpSegmenter.PARAM_WRITE_TOKEN,false,OpenNlpSegmenter.PARAM_WRITE_SENTENCE,true),
					createEngineDescription(CuratedStancesAnnotator.class),
					createEngineDescription(BinaryCasWriter.class,BinaryCasWriter.PARAM_TARGET_LOCATION, writeTo+"/bin/")
					,createEngineDescription(XmiWriter.class,XmiWriter.PARAM_TARGET_LOCATION,writeTo+"/xmis/", XmiWriter.PARAM_OVERWRITE, true)

						)
					);
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
}
