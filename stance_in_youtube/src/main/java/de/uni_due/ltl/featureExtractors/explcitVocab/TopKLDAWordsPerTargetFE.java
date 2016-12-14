package de.uni_due.ltl.featureExtractors.explcitVocab;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class TopKLDAWordsPerTargetFE extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	public static final String PARAM_VOCAB_TARGET = "SubdebateVocab_Target";
	@ConfigurationParameter(name = PARAM_VOCAB_TARGET, mandatory = true)
	private String target;

	public static final String PARAM_TOP_K_WORDS = "lda_vocab_topK";
	@ConfigurationParameter(name = PARAM_TOP_K_WORDS, mandatory = true)
	private int topk;
	
	private List<String> targetVocab;
	
	/**
	 * mapping based on labeled LDA top 40 words
	 */
	private Map<String, List<String>> target2vocab = new HashMap<String, List<String>>(){
        {
            put("Death Penalty should be done by gunshot", new ArrayList<String>(Arrays.asList("is","be","would","are","people","death","have","do","person","someone","can","don't","think","punishment","head","has","should","will","could","cost","lethal","way","execution","being","injection","penalty","more","other","something","want","life","humane","going","society","shot","feel","less","doesn't","put","cruel","gun","innocent","die","firing","executed","time","kill","method","were","capital","killing","was","much","bullet","wouldn't","use","isn't","can't","take","does","know","point","pain","expensive","nitrogen","right","gunshot","brain","may","see","make","done","crime","been","many","gas","squad","prison","reason","matter","care","live","room","unusual","doubt","row","idea","People","body","get","system","need","used","likely","dignity","had","find","argument","part","am")));
//            put("Death Penalty (Debate)", new ArrayList<String>(Arrays.asList("death", "penalty", "sentence","pro","against","contra","favor","yes","no")));
            put("Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)", new ArrayList<String>(Arrays.asList("is","be","are","have","people","death","would","punishment","crime","should","someone","life","innocent","can","penalty","think","was","do","person","will","crimes","kill","has","don't","system","murder","more","other","prison","child","society","capital","being","were","get","point","sentence","many","children","cases","killing","justice","rape","could","been","case","does","want","commit","make","put","believe","criminals","wrong","isn't","way","evidence","argument","guilty","executed","doesn't","know","go","say","time","criminal","same","going","cost","good","convicted","years","something","sex","view","chance","problem","money","jail","killed","reason","victim","see","thing","can't","committed","less","lives","state","saying","may","had","agree","bad","right","human","did","am","need","year")));
            put("Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",new ArrayList<String>(Arrays.asList("is","be","are","have","death","would","appeals","guilty","appeal","someone","people","row","can","should","evidence","system","innocent","has","person","world","time","make","was","crime","being","case","get","process","hypothetical","penalty","been","think","do","many","justice","court","point","were","real","doesn't","did","other","could","something","argument","inmates","don't","sure","crimes","trial","will","executed","allowed","life","society","prisoner","years","guilt","state","thing","know","rights","murder","certain","situation","found","means","can't","law","need","money","proven","question","put","reason","more","lot","fair","live","saying","view","execution","take","agree","made","judge","fact","chance","allow","innocence","confess","least","first","see","work","way","new","things","problem","go")));
            put("If Death Penalty is allowed, abortion should be legal, too.",	new ArrayList<String>(Arrays.asList("is","life","be","are","human","fetus","have","death","abortion","penalty","don't","people","being","think","person","can","do","would","has","someone","potential","should","say","will","believe","woman","right","argument","point","other","doesn't","society","could","support","isn't","make","body","was","same","different","does","birth","can't","way","rights","may","kill","see","case","pro-choice","view","killing","more","cannot","own","choice","living","murder","become","time","moral","am","want","mother","survive","something","get","know","wrong","views","many","born","thing","cells","child","abortions","means","prison","reason","take","give","nothing","such","issue","morality","term","taking","consider","live","autonomy","alive","pregnancy","world","difference","agree","medical","were","much","stance","mind")));
            put("In certain cases, capital punishment shouldn’t have to be humane but more harsh",new ArrayList<String>(Arrays.asList("is","be","are","have","people","death","would","penalty","punishment","do","don't","can","someone","think","suffering","could","should","life","kill","person","way","crime","police","see","other","killing","good","doesn't","society","justice","more","torture","crimes","wrong","get","does","prison","has","point","say","military","criminal","thing","system","criminals","want","same","may","was","will","view","being","threat","innocent","case","such","reason","family","many","years","execution","make","situation","isn't","doing","pain","lives","executions","use","murder","man","shoot","something","take","done","better","been","lethal","might","did","difference","can't","going","fact","rehabilitation","revenge","said","time","agree","government","others","worse","am","things","suffer","much","deserve","inflicted","danger","cartel")));
            put("Life-long prison should be replaced by Death Penalty",	new ArrayList<String>(Arrays.asList("is","be","death","life","are","people","have","penalty","prison","innocent","would","someone","system","can","person","could","sentence","cost","will","appeals","evidence","was","killing","has","may","justice","punishment","years","should","crime","don't","murder","costs","thing","think","more","being","get","kill","take","executed","other","do","time","execution","way","reason","sentences","expensive","moral","were","guilty","society","many","certainty","doesn't","case","parole","process","might","argument","different","cases","had","day","things","convicted","chance","least","make","new","reasonable","wrong","row","say","crimes","same","found","issue","does","been","going","capital","sentenced","criminal","only","sure","can't","much","cheaper","man","human","die","give","know","change","future","committed","doubt","example")));
            put("The level of certainty that is necessary for Death Penalty is unachievable",new ArrayList<String>(Arrays.asList("is","be","death","are","life","would","punishment","people","prison","have","can","think","penalty","innocent","being","someone","don't","should","capital","criminal","killing","years","certainty","case","make","justice","point","say","do","doesn't","more","crime","was","sure","does","better","sentence","doubt","infinite","evidence","way","could","has","will","crimes","argue","different","system","were","execution","get","society","know","killed","guilty","possibility","put","same","person","reasonable","worse","row","need","issue","revenge","executed","cases","kill","something","want","reason","did","die","argument","family","agree","been","makes","many","year","appeals","live","saying","proof","least","murder","lot","guilt","like","absolute","view","other","isn't","see","moral","feel","certain","idea","cost","level")));
            		
            put("Execution prevents the accused from committing further crimes.",	new ArrayList<String>(Arrays.asList("prison","is","crimes","death","can","escape","execution","other","commit","further","be","life","jail","state","people","only","way","prisoners","prevents","total","population","penalty","longer","have","society","sentences","were","others","punishment","may","murderers","more","murder","top","are","row","criminals","certain","do","ensure","uncommon","receiving","homicide","suicide","worse","receive","Putting","dangerous","endangers","guards","must","watch","advantage","highest","security","detention","facilities","escapees","convicted","hurt","execute","Escapes","media","rare","occurrences","according","Bureau","Statistics","escaped","AWOL","prisons","Given","figure","represents","percent","impossible","sentenced","eager","awaiting","true","help","provide","closure","family","friends","will","fear","return","criminal","worry","parole","chance","able","achieve","greater","degree","woman","killer","faced","commuted")));
            put("It helps the victims’ families achieve closure.",	new ArrayList<String>(Arrays.asList("victims","families","death","penalty","is","closure","will","might","provide","have","be","knowing","can","help","family","friends","longer","fear","return","criminal","society","worry","parole","chance","escape","able","achieve","greater","degree","woman","killer","faced","commuted","sentences","life","prison","stated","were","looking","disappointed","system","Other","deemed","decision","mockery","justice","done","Many","oppose","take","comfort","guilty","party","has","been","executed","others","prefer","know","person","suffering","jail","feel","comfortable","state","killed","human","being","behalf","victim","psychiatrist","believes","witnessing","executions","fails","causes","symptoms","acute","stress","Witness","trauma","removed","experience","was","case","capital","punishment","helped","sentencing","want","Punishment","should","proportionate","crime","committed","alleged","preferences","responsibility","protect","lives")));
            put("State-sanctioned killing is wrong (state has not the right).",	new ArrayList<String>(Arrays.asList("life","state","right","be","rights","is","take","execution","are","people","crimes","heinous","psychological","death","penalty","can","criminal","prison","might","has","others","human","punishment","sentencing","should","proportionate","innocent","citizens","rate","role","acts","murder","top","instance","criminals","certain","involve","cannot","do","commit","convicted","Such","value","sentence","does","cruel","forced","associated","humane","executing","convicts","government","condoning","devaluing","process","violate","declared","Declaration","subjected","inhuman","degrading","forces","participate","taking","traumatizing","leave","permanent","scars","exercises","abrogates","intrude","takes","movement","association","property","different","forgo","devalue","affirms","taken","Certain","executioners","methods","multiple","reduce","burdens","one","become","executioner","choose","full","awareness","risks","involved","help","provide","closure","family","friends","will")));
            put("The death penalty can produce irreversible miscarriages of justice.",	new ArrayList<String>(Arrays.asList("death","are","is","penalty","can","be","justice","sentencing","verdict","likely","guilty","should","study","found","times","pervert","racial","were","victims","killed","punishment","crimes","certain","cases","process","kill","differences","get","juries","fact","biases","present","errors","jurors","will","fear","criminal","society","life","prison","system","families","deemed","been","executed","person","comfortable","being","was","capital","crime","innocent","may","execution","more","decades","murder","ways","instance","people","many","doubt","criminals","find","would","little","evidence","factors","such","social","most","do","commit","must","convicted","first-degree","sentence","problematic","die","result","jury","wrongful","reasonable","prejudicial","Juries","imperfect","increasing","stakes","couple","First","implementation","impacted","members","gender-based","biases2","impacting","victimized","groups","adding","arbitrariness")));
            put("The death penalty deters crime.",	new ArrayList<String>(Arrays.asList("death","penalty","is","be","execution","deterrent","have","effect","can","lives","rates","prison","punishment","crime","may","prospect","more","studies","are","effective","life","innocent","save","reducing","daunting","support","study","murders","States","many","criminals","less","would","deterrence","factors","such","higher","fear","Other","Many","has","suffering","jail","state","executions","case","capital","responsibility","protect","citizens","enacting","rate","violent","reasoning","simple-","play","powerful","motivating","role","convincing","potential","murderers","carry","acts","frightening","risk","change","cost-benefit","calculus","mind","murderers-to","act","worthwhile","Numerous","University","showed","single","deters","influential","looked","counties","decades","found","claim","murder","tend","fall","rise","top","ways","make","today","instance","wait","time","row","increase","short","can-","does-")));
            put("The death penalty is a financial burden on the state.",	new ArrayList<String>(Arrays.asList("death","penalty","is","are","cost","costs","punishments","be","greater","life","capital","row","appeals","other","expensive","could","process","taxpayers","financial","measures","can","criminal","prison","justice","case","punishment","should","proportionate","execution","single","ways","make","today","less","much","intensive","Further","such","high","Justice","associated","convicts","reduce","imposes","alternative","litigation","result","jury","selection","trials","long","required","cases2","presents","additional","burden","Savings","abolishing","example","estimated","sought","year","imprisoning","money","spent","benefit","system-","policing","education","crime-preventing","cost-effective","priceless","sufficient","reason","ban","Fair","independent","considerations","Shortening","changing","method","help","provide","closure","family","friends","will","longer","have","fear","return","society","worry","parole","chance","escape","able","achieve","degree","woman")));
            put("The death penalty should apply as punishment for first-degree murder; an eye for an eye.",new ArrayList<String>(Arrays.asList("is","murder","are","punishment","human","worst","death","family","be","life","justice","crime","crimes","punished","possible","second","eye","penalty","can","done","feel","being","capital","should","murderers","deterrent","less","would","other","must","deserve","severe","sanctions","first-degree","involves","intentional","slaughter","visceral","none","deadly","Such","heinous","fair","manner","Time","put","zero-sum","symmetry","simple","satisfying","instinct","deserves","Human","sacred","mechanism","place","ensures","violating","fundamental","precept","symbolizes","value","importance","placed","maintenance","sanctity","lesser","sentence","fail","duty","fairness","consistency","eye-for-an-eye","attitude","Justice","remain","petty","retributive","marks","street","community","warfare","member","justifies","revenge","attack","inconsistent","areas","law","Professor","notes","don't","burn","arsonists","houses","attempts","vindicate","committing","reprehensible","sanctioned")));
            put("Wrongful convictions are irreversible.", new ArrayList<String>(Arrays.asList("death","is","penalty","wrongful","cases","sentenced","convictions","been","should","have","be","was","innocent","are","people","receive","associated","number","lengthy","will","chance","killer","life","prison","guilty","executed","person","state","case","capital","punishment","lives","execution","more","murder","make","increase","many","doubt","appeals","evidence","prisoners","rare","impossible","sentence","does","due","process","result","example","sufficient","alarming","had","exonerated","compensate","proven","gamble","prove","justifiable","Wrongful","thorough","procedures","offer","protection","reasonable","conviction","kidnapping","rape","10-year","old","girl","released","years","lawyers","proving","paucity","confession","actual","few","legality","prejudicial","can","help","provide","closure","family","friends","longer","fear","return","criminal","society","worry","parole","escape","able","achieve","greater","degree","woman")));
            }
    };
    
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		this.targetVocab=topK(target2vocab.get(target),topk);
//		this.targetVocab=target2vocab.get(target);
		return true;
	}
	
	
	private List<String> topK(List<String> list, int topk) {
	List<String> topK= new ArrayList<>();
	int i=0;
	for(String word: list){
		if(i==topk) return topK;
		topK.add(word);
		i++;
	}
	return topK;
}


	@Override
	public Set<Feature> extract(JCas view, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		List<Token> tokens=JCasUtil.selectCovered(Token.class, unit);

		for(String word: targetVocab){
			if(contained(tokens,word)){
				featList.add(new Feature(target+"_Vocab_"+word, 1));
			}else{
				featList.add(new Feature(target+"_Vocab_"+word, 0));
			}
		}
		return featList;
	}
	private boolean contained(List<Token> tokens, String word) {
		for(Token t: tokens){
			if(t.getCoveredText().toLowerCase().equals(word))return true;
		}
		return false;
	}

}
