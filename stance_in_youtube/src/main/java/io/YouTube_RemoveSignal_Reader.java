package io;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.uni_due.ltl.simpleClassifications.FunctionalPartsAnnotator;
import de.uni_due.ltl.simpleClassifications.SentimentCommentAnnotator;
import de.uni_due.ltl.util.Custom_ArkTweetTokenizer;

public class YouTube_RemoveSignal_Reader extends JCasResourceCollectionReader_ImplBase{

	public static final String PARAM_EXPLICIT_TARGET_SIGNAL_TO_REMOVE = "Signal_of_ExplicitTarget_toRemove";
    @ConfigurationParameter(name = PARAM_EXPLICIT_TARGET_SIGNAL_TO_REMOVE, mandatory = true)
	protected String toRemoveTarget;
    
    public static final String PARAM_TARGET_LABEL = "TargetLabelOriginal";
    @ConfigurationParameter(name = PARAM_TARGET_LABEL, mandatory = true)
	protected String targetLabel;
 
    public static final String PARAM_TARGET_SET = "TargetSetOriginal";
    @ConfigurationParameter(name = PARAM_TARGET_SET, mandatory = true)
	protected String targetSet;
    
    public static final String PARAM_BINCAS_LOCATION = "LocationOfOriginalBincases";
    @ConfigurationParameter(name = PARAM_BINCAS_LOCATION, mandatory = true)
	protected String binCasLocation;
	
//    private JCas currentOriginalJcas; 
    private Iterator<JCas> jcasIter;
    private AnalysisEngine engine;
    private int tcId = 0;
    private Map<String,String> instance2Outcome;
    
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		engine= getPreprocessingEngine();
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(YouTubeReader.class, YouTubeReader.PARAM_SOURCE_LOCATION, binCasLocation, YouTubeReader.PARAM_LANGUAGE,
				"en", YouTubeReader.PARAM_PATTERNS, "*.bin", YouTubeReader.PARAM_TARGET_LABEL,targetLabel, YouTubeReader.PARAM_TARGET_SET,targetSet);
		jcasIter= new JCasIterable(reader).iterator();
		instance2Outcome= new HashMap<>();
		for (JCas jcas : new JCasIterable(reader)) {
			DocumentMetaData origMetaData= JCasUtil.selectSingle(jcas, DocumentMetaData.class);
			int i =0;
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				String outcome=JCasUtil.selectCovered(jcas, curated.Debate_Stance.class,sentence).get(0).getPolarity();
				instance2Outcome.put(origMetaData.getDocumentTitle()+"_"+String.valueOf(i), outcome);
				i++;
			}
//			System.out.println(origMetaData.getDocumentTitle()+" "+JCasUtil.select(jcas, Sentence.class).size());
		}
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		if(jcasIter.hasNext()){
//			System.out.println("next currentJcas "+DocumentMetaData.get(currentOriginalJcas).getDocumentId());
			return true;
		}
		return false;
	}
	@Override
	public void getNext(JCas aJCas) throws IOException, CollectionException {
		JCas currentOriginalJcas=jcasIter.next();
		DocumentMetaData origMetaData= DocumentMetaData.get(currentOriginalJcas);
//		System.out.println("in getNext: "+origMetaData.getDocumentId()+" "+ JCasUtil.select(currentOriginalJcas, Sentence.class).size());
		DocumentMetaData newMetaData = copyMetaData(aJCas,origMetaData);
		aJCas.setDocumentLanguage("en");
		String textWithoutExplicitSignal=null;
		try {
			textWithoutExplicitSignal= getTextWithoutSignal(currentOriginalJcas);
		} catch (Exception e) {
			throw new IOException(e);
		}
		aJCas.setDocumentText(textWithoutExplicitSignal);
		try {
			engine.process(aJCas);
		} catch (AnalysisEngineProcessException e) {
			throw new IOException(e);
		}
		int i=0;
		for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
			TextClassificationTarget unit = new TextClassificationTarget(aJCas, sentence.getBegin(), sentence.getEnd());
			unit.setId(tcId++);
			TextClassificationOutcome outcome = new TextClassificationOutcome(aJCas, sentence.getBegin(),sentence.getEnd());
			try {
				outcome.setOutcome(getTextClassificationOutcome(newMetaData.getDocumentTitle()+"_"+String.valueOf(i)));
				i++;
			} catch (Exception e) {
				throw new IOException(e);
			}
			addUnitAndOutComeToIndex(unit, outcome);
		}
	}

	private AnalysisEngine getPreprocessingEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(
					createEngineDescription(Custom_ArkTweetTokenizer.class)
					,createEngineDescription(FunctionalPartsAnnotator.class)
					,createEngineDescription(SentimentCommentAnnotator.class)
					));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}

	private String getTextWithoutSignal(JCas currentOriginalJcas) throws Exception {
		String oldText= currentOriginalJcas.getDocumentText();
		String newText="";
		List<Annotation> annosToRemove= getAnnosToRemove(currentOriginalJcas);
		int lowerbound = 0;
		int upperbound=0;
		if(annosToRemove.isEmpty()){
			newText=oldText;
		}else{
			for(Annotation anno : annosToRemove){
				upperbound =anno.getBegin();
				newText+=oldText.substring(lowerbound, upperbound)+" X ";
//				newText+=oldText.substring(lowerbound, upperbound);
				lowerbound= anno.getEnd();
				upperbound= anno.getEnd();
			}
			newText+=oldText.substring(upperbound, oldText.length());
		}
		return newText;
	}

	private List<Annotation> getAnnosToRemove(JCas currentOriginalJcas) throws Exception {
		List<Annotation> tormv= null;
		if(targetSet.equals("1")){
			tormv=getAnnosToRemoveSet1(currentOriginalJcas);
		}else if(targetSet.equals("2")){
			tormv=getAnnosToRemoveSet2(currentOriginalJcas);
		}else{
			throw new Exception("Lable set for explicit stances "+targetSet+ " not known!");
		}
		return tormv;
	}

	private List<Annotation> getAnnosToRemoveSet2(JCas currentOriginalJcas) throws Exception {
		List<Annotation> toRmv= new ArrayList<>();
		for(curated.Explicit_Stance_Set2 subTarget: JCasUtil.select(currentOriginalJcas, curated.Explicit_Stance_Set2.class)){
			if(toRemoveTarget.equals(subTarget.getTarget()) && !subTarget.getPolarity().equals("NONE")){
				toRmv.add(subTarget);
			}
		}
		return toRmv;
	}

	private List<Annotation> getAnnosToRemoveSet1(JCas currentOriginalJcas) {
		List<Annotation> toRmv= new ArrayList<>();
		for(curated.Explicit_Stance_Set1 subTarget: JCasUtil.select(currentOriginalJcas, curated.Explicit_Stance_Set1.class)){
			if(toRemoveTarget.equals(subTarget.getTarget()) && !subTarget.getPolarity().equals("NONE")){
				toRmv.add(subTarget);
			}
		}
		return toRmv;
	}

	private DocumentMetaData copyMetaData(JCas aJCas, DocumentMetaData origMetaData) {
		DocumentMetaData md= new DocumentMetaData(aJCas);
		md.setDocumentId(origMetaData.getDocumentId());
		md.setDocumentTitle(origMetaData.getDocumentTitle());
		md.addToIndexes();
		return md;
	}
	
	/**
	 * this method is capsuled so that the inheriting readers can apply filtering here
	 * @param unit
	 * @param outcome
	 */
	protected void addUnitAndOutComeToIndex(TextClassificationTarget unit, TextClassificationOutcome outcome) {
        unit.addToIndexes();
        outcome.addToIndexes();
	}


	private String getTextClassificationOutcome(String key) throws Exception {
		if(instance2Outcome.containsKey(key)){
			return instance2Outcome.get(key);
		}
		else{
			throw new Exception(key+" not in map!");
		}
	}
}
