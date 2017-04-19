package io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.sentiment.type.StanfordSentimentAnnotation;
import io.xmlTypes.XMLComment;
import io.xmlTypes.XMLCommentSet;
import io.xmlTypes.XMLCorpus;
import io.xmlTypes.XMLSubDebate;
import io.xmlTypes.XMLSubdebateCollection;
import io.xmlTypes.XMLVideo;
import preprocessing.CommentText;
import preprocessing.Users;

public class CreateXMLCorpus {
public static void main(String[] args) throws ResourceInitializationException, AnalysisEngineProcessException, JAXBException {
		
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, "/Users/michael/DKPRO_HOME/youtubeStance/corpus_curated/bin_preprocessed", YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,"DEATH PENALTY", YouTubeReader.PARAM_TARGET_SET,"1");
		
		createXMLCorpus(reader);
	
	}

	private static void createXMLCorpus(CollectionReaderDescription reader) throws AnalysisEngineProcessException, JAXBException {
		XMLCorpus corpus= new XMLCorpus();
		List<XMLVideo> videos= new ArrayList<>();
		corpus.setVideos(videos);
		for (JCas jcas : new JCasIterable(reader)) {
			XMLVideo video= new XMLVideo();
			videos.add(video);
			System.out.println(JCasUtil.select(jcas,DocumentMetaData.class).iterator().next().getDocumentTitle());
			video.setURL(JCasUtil.select(jcas,DocumentMetaData.class).iterator().next().getDocumentTitle());
			XMLCommentSet set= new XMLCommentSet();
			video.setComments(set);
			List<XMLComment> commentList= new ArrayList<>();
			set.setComments(commentList);
			for(TextClassificationOutcome outcome: JCasUtil.select(jcas, TextClassificationOutcome.class)){
				XMLComment comment= new XMLComment();
				commentList.add(comment);
				comment.setAuthor(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor());
				comment.setReplyTo(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
				comment.setStance(outcome.getOutcome());
				comment.setText(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getCoveredText());
				XMLSubdebateCollection sset= new XMLSubdebateCollection();
				comment.setSubdebateCollection(sset);
				List<XMLSubDebate> subList= new ArrayList<>();
				sset.setSub_debates(subList);
				int textStart=JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getBegin();
				System.out.println(JCasUtil.selectCovered(CommentText.class,outcome).iterator().next().getCoveredText());
				System.out.println(JCasUtil.selectCovered(Users.class,outcome).iterator().next().getAuthor()+" --> "+JCasUtil.selectCovered(Users.class,outcome).iterator().next().getReferee());
				for(curated.Explicit_Stance_Set1 explicitTarget1: JCasUtil.selectCovered(curated.Explicit_Stance_Set1.class, outcome)){
					if(!explicitTarget1.getPolarity().equals("NONE")){
						XMLSubDebate subdebate = new XMLSubDebate();
						subdebate.setStance(explicitTarget1.getPolarity());
						subdebate.setSub_debate_set("reddit");
						subdebate.setSub_debate_target(explicitTarget1.getTarget());
						subdebate.setBegin(explicitTarget1.getBegin()-textStart);
						subdebate.setEnd(explicitTarget1.getEnd()-textStart);
						subList.add(subdebate);
						System.out.println(explicitTarget1.getTarget()+" "+explicitTarget1.getPolarity()); 
					}
				}
				for(curated.Explicit_Stance_Set2 explicitTarget2: JCasUtil.selectCovered(curated.Explicit_Stance_Set2.class, outcome)){
					if(!explicitTarget2.getPolarity().equals("NONE")){
						XMLSubDebate subdebate = new XMLSubDebate();
						subdebate.setStance(explicitTarget2.getPolarity());
						subdebate.setSub_debate_set("idebate");
						subdebate.setSub_debate_target(explicitTarget2.getTarget());
						subdebate.setBegin(explicitTarget2.getBegin()-textStart);
						subdebate.setEnd(explicitTarget2.getEnd()-textStart);
						subList.add(subdebate);
						
						System.out.println(explicitTarget2.getTarget()+" "+explicitTarget2.getPolarity()); 
						System.out.println("\t"+outcome.getBegin());
						System.out.println("\t"+textStart);
						System.out.println("\t"+explicitTarget2.getCoveredText());
						System.out.println("\t"+explicitTarget2.getBegin());
						System.out.println("\t"+explicitTarget2.getEnd());
						System.out.println("\t"+(explicitTarget2.getBegin()-textStart));
						System.out.println("\t"+(explicitTarget2.getEnd()-textStart));
					}
				}
			}
		}
		
		JAXBContext context = JAXBContext.newInstance(XMLCorpus.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(corpus, new File("src/main/resources/sub_stance_death_penalty_corpus.xml"));
	}
}
