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

public class SubdebateVocab_CHEATED extends FeatureExtractorResource_ImplBase implements FeatureExtractor{

	public static final String PARAM_VOCAB_TARGET = "SubdebateVocab_Target";
	@ConfigurationParameter(name = PARAM_VOCAB_TARGET, mandatory = true)
	private String target;
	
	private List<String> targetVocab;
	
	private Map<String, List<String>> target2vocab = new HashMap<String, List<String>>(){
        {
            put("Death Penalty should be done by gunshot", new ArrayList<String>(Arrays.asList("squad", "firing", "bullet")));
            put("Death Penalty (Debate)", new ArrayList<String>(Arrays.asList("death", "penalty", "sentence","pro","against","contra","favor","yes","no")));
            put("Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)", new ArrayList<String>(Arrays.asList("rape", "murder", "murderer","murderers","childeren","child","raped","murdered","rapists","killers")));
            put("Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",new ArrayList<String>(Arrays.asList("right","appeal","appeals", "straight", "innocent","immediately","years","money","row","guilty")));	
            put("If Death Penalty is allowed, abortion should be legal, too.",	new ArrayList<String>(Arrays.asList("abortion","fetus","baby","trimester")));
            put("In certain cases, capital punishment shouldn’t have to be humane but more harsh",new ArrayList<String>(Arrays.asList("innocent","penalty","humane","torture","mercy")));
            put("Life-long prison should be replaced by Death Penalty",	new ArrayList<String>(Arrays.asList("without","parole","punishment","life-long", "prison", "replace")));
            put("The level of certainty that is necessary for Death Penalty is unachievable",new ArrayList<String>(Arrays.asList("evidence","innocent","doubt", "justice", "system")));
        }
    };
	
//	private Map<String, List<String>> target2vocab = new HashMap<String, List<String>>(){
//        {
//            put("Death Penalty should be done by gunshot", new ArrayList<String>(Arrays.asList("saves","yea","squads","uncivilized")));
//            put("Death Penalty (Debate)", new ArrayList<String>(Arrays.asList("hypocritical","discipline","ban","traffickers","strict","hotels﻿","saddam","oppose","corruption","admire","beating","r","increasing","area","ide","legalized","weapon","uncivilized","vermin","foolproof","█▀█","gangster","█▀▀","destroy","italy","bout","█","racial","singapore's","lawyer","italian")));
//            put("Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)", new ArrayList<String>(Arrays.asList("raping","executioners","molesters","treated","uncivilized","vermin","wrist","gallows","letting","basis","terrorists","racial","wild")));
//            put("Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",new ArrayList<String>(Arrays.asList("morton","saves","140","yrs","form","timely","dispute","manner","followed","wild")));	
//            put("If Death Penalty is allowed, abortion should be legal, too.",	new ArrayList<String>(Arrays.asList("abortion","fetus","baby","trimester")));
//            put("In certain cases, capital punishment shouldn’t have to be humane but more harsh",new ArrayList<String>(Arrays.asList("starve","cheap","ide")));
//            put("Life-long prison should be replaced by Death Penalty",	new ArrayList<String>(Arrays.asList("psychological","rehabilitate","shock","/","soldier's","cage","basis","harming")));
//            put("The level of certainty that is necessary for Death Penalty is unachievable",new ArrayList<String>(Arrays.asList("accuracy","morton","bar","fix","poorly","1000","forensics","area","fucker","screwed","issue","flawed","cameras","names","racial","accurate","represented","merit","dispute")));
//        }
//    };
//	private Map<String, List<String>> target2vocab = new HashMap<String, List<String>>(){
//        {
//            put("Death Penalty should be done by gunshot", new ArrayList<String>(Arrays.asList("dragging","molestation","president","soldier","aim","excutions","cockroach(imagine","worry","torso","midnight","precious","asphyxiation","10years","bought","nazi","neck","ww2","oppinion","mattet","degree","detour","saves","william","moving","efficient","goddamn","squads","hole","instant","recieve","ira","beloved","round","rope","cock-sucker","bury","uncivilized","tortured","reforms","50/50","ammo","joyce","dig","yea","follows","dimes","spot","voice","reformed","p.s.","syrian","evasion","transitioning","promoting","deprives","book","tons","psychopaths","montana","leaders","chopping","napolitano")));
//            put("Death Penalty (Debate)", new ArrayList<String>(Arrays.asList("5/100","tight","unprovoked","spree","capsule","brainer","obongo","destroy","thrown","audacity","brutal","psycho","shiites","o-o","saddam","ethnic","█░░█▀▄","god﻿","diseases","hislops","█░░░▀█░░█░░░░▄░░░░▄░░░░░▀███▀░░░░░░░█░░░","10years","leftists﻿","........","forget","hotels﻿","secret","degree","parts","disasters","quality","contnue","congo","lesson","motherf","█▀█░░█","seditious","█▀▀","traffickers","red","cutting","bull","amongst","hislop","mosques","minor","█░░█░█","states﻿","prey","innocent(meaning","undermine","weapon","pillaging","rampaged","wars","crucial","inability","deprives","swayed","rule","lengthy","!!!!!!!!!","adding","religios","it﻿","outset","famous","filthy","tape","tthem","molestation","penalty﻿","admire","ne","░█░░█","░▀▄▄▀░░░░░▀▀▄▄▄░░░░░░░▄▄▄▀░▀▄▄▄▄▄▀▀░░█░░","glorified","pov","stops","innocently","foolproof","examiner","luv","bout","iranians","█░░░█░▄▄▄░░░░░░░░░░░░░░░░░░░░░▀▀░░░█░░░░","ago","above","justifiable","kill/murder","800","could've","n","░█▄░░░░░░░░░░░░▀▀▀▀▀▀▀░░░░░░░░░░░░░░█░░░","outcome﻿","awesome","rich","mode","▀▀▀░░░▀░▀░","█░░▀▀░","grey","kuwaitis","!!!!!!!!","undeniable","lie","planned","hot","250,000","singapore's","demanding","democracy","wherever","follows","vermin","ok﻿","suicides","secured","evasion","coherent","concentrating","punishment﻿","▀█▀░█","deny","inadverdantly","▄▀▀░█░░░░▀█▄▀▄▀██████░▀█▄▀▄▀████▀░░░░░░░","e","area","commenting","█░░░░█░░▀▄░░░░░░▄░░░░░░░░░█░░░░░░░░█▀▄░░","past","go﻿","blair","subjugate","lawyer","d","global","rampage","basest","potential","▀","instincts","daughters","thi","responce","italian","sickos","░▀░","asphyxiation","followers","accusations","everyone's","twitter","killling","f","incarcerated","▀▀▀","safeguards","h","░▀░░█﻿","slaving","countryside","oppose","iraq","kurds","harsher","excursion","shootings","cult","uncivilized","england","░░▄███▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█▄▄▄░░░░░░░","tortured","strict","miscarriages","hypocritical","retrospect","▀█▀","░░░░░▄▄▄▄█▀▀▀░░░░░░░░░░░░▀▀██░░░░░░░░░░░","█░█░░█","lifelong","█","shes","physical","biggest","systems","twisted","subtle","wealthy","g","ide","ruined","slaughtered","met","opposed","revoke","░░▀█▄░░░░░░░░░░░░░░░░░░░░░░░░░▄▀▀░░░▀█░░","increasing","heaving","saved","pussys","▀░▀","liberals﻿","classes","drawn","sponsored","increasingly","switzerland","governments","tampered","inhuman","█▀█","ritualistic","enslave","░█░","r","wheres","open","compel","sends","subvert","documentary","precious","racial","neck","opposes","tyrant","--","overpopulated","district","christain","expensive﻿","withheld","fishy","rely","impression","sotn","beloved","beating","▀░▀░░▀","█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀█","ppunishment","pictures","@deathforcrimes","legalized","calm","░░█░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▄██░░░░","ever-present","000","█░░░░█░▄░█","hurts","man's","obligation","outlaw","abolished﻿","reintroduced","█░░░░▀▄▀▄▀","today's","attempted","gangster","ban","psychopaths","discipline","basicly","normal","almighty","diserve","hussein","corruption","attorney","italy","░░░░░░░░░░░░▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄░░░░░░░░░░░░░","█░░░█░░░░░░▀█▄█▄███▀░░░░▀▀▀▀▀▀▀░▀▀▄░░░░░","forensic","named","150,000")));
//            put("Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)", new ArrayList<String>(Arrays.asList("spree","building","o-o","touched","0:54","exceptional","opinion﻿","basketball","granted","scream","innocent.so","persons","guarding",">","eliminated","william","contnue","dicks","a**wipes","crashes","slaps","motherf","jimmy","seditious","+little","his/her","rapes","standards","red","supposition","perpertrating","amongst","mosques","severe","comparing","minor","below","grip﻿","peole","undermine","prey","charleston","60","yaaaaaay","deprives","swayed","lengthy","chopping","omgukillkenny2","offending","3rd","filthy","tape","advocates","asap","wrist","slices","gargle","a+","ne","luv","connecticut","1:00","!!!!!........","definatly","999999999","pedos","tune﻿","theatre","unlimited","accomodation","pieces","n","torturing","birmingham","sister","rich","mode","ira","courtyard","shelter","evaluation","deed","lie","executioners","cases﻿","woul","wherever","vermin","treated","}","situations","35k","coherent","sufficient","dept","improvement","horseshit","newborn","games","richard","harm/kill","twisting","advertising","petit","blair","subjugate","global","agony","rampage","daughters","complied","responce","instability","systematic","sickos","asphyxiation","followers","staggering","brutally","reminding","ww2","uncommon","exodus","guildford","!?","slaving","21.12","harsher","fry'd","singing","uncivilized","selling","audience","cult","tortured","available","joyce","socio/psyhcopaths","lifelong","terminated","accuse","(5","sheer","occasional","syrian","slap","whoever","ruined","letting","shoul","incarcerate","ye","heaving","escorted","willful","downstairs","naa","rage","increasingly","soldier","terrorists","vehicles","mins","enslave","molesters","atrocities","subvert","precious","abolishing","racial","shaming","nazi","raping","mcdonald's","--","gallows","staying","40k","beloved","surrender","fukkers","390839038298398423","basis","re","victimized","terrorism﻿","overcrowd","obligation","reintroduced","conception","pregnant","wild","romans","app","psychopaths","hearsay","numpty","captured","irrational","diserve","maths","tends","fee","motor")));
//            put("Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",new ArrayList<String>(Arrays.asList("dragging","judge","building","excutions","media","institute","figured","owed","midnight","bought","reminding","granted","scare","detour","wiser","atrociously","moving","rude","dispute","abort","dying","cycle","round","buried","wise","form","cock-sucker","selling","ongoing","darn","standards","supposition","county","manner","terminated","comparing","(5","lying","speedy","60","mindset","5.56","michael","express","leaders","heck","smoke","wont","paid","concrete","3rd","downstairs","napolitano","alter","45","president","stoop","receiving","aim","timely","45yrs","yrs","sack","torso","lane","following","express-lane","reports","unlimited","morton","saves","mr.","ardent","toward","40k","earlier","expanded","williamson","evolved","swift","smoker","mcviegh","stints","ammo","believer","counsel","dimes","west","downright","news","}","wild","wot","35k","140","tons","hearsay","inclined","horseshit","racking","followed","richard","maths","decides","prosecutor")));	
//            put("If Death Penalty is allowed, abortion should be legal, too.",	new ArrayList<String>(Arrays.asList("clinton","anti-abortion","infant","dominated","exterminated","unborn","generation","v","republicans","hypocracy","extremist","womb","bo","delivered","security","ideas","weird","tubes","violated","society's","abort","policies","holy","roe","hillary","knowingly","cycle","wade","vehemently")));
//            put("In certain cases, capital punishment shouldn’t have to be humane but more harsh",new ArrayList<String>(Arrays.asList("dream","treatment","lesser","loving","mins","treblinka","watch﻿","agony","psycho","loses","worry","did'nt","execution's","scream","boiling","implement","pros","b4","cheap","goddamn","cut-and-dried","ness","petty","fry'd","rope","microwave","cons","wreak","testing","guard","grieving","mere","bull","regarding","golifying","selfishness","shift","inexpensive","random","thief","comiting","pos","ide","nasty","methods","becuse","lifestyle","acid","packed","suddenly","slower","asshole's","sustain","lightly","constitution","piece","auchwitz-birkenau","thug","starve","havoc","convinced","escaping/being","naa")));
//            put("Life-long prison should be replaced by Death Penalty",	new ArrayList<String>(Arrays.asList("measure","betting","injected","soldier's","rehab","preferred","confined","talks","eager","automatically","stuck","god﻿","instability","eh","ppl","diseases","fitting","abusive","staggering","violence","basketball","whoo","acquire","sits","vincent","addresses","disasters","dicks","muscly","he'll","occurs","chose","somewhat","handle","excursion","england","lifetime","rapes","buddies","pockets","various","eight","benevolent","socio/psyhcopaths","forced","rehabilitate","principles","anybody's","electrocuted","sheer","occasional","selected","six","yaaaaaay","wars","rallies","combat","either(if","tack","fifty","propose","shock","easier","face-to-face","willful","parroled﻿","advocates","dependence","rage","69","sin't","???!!!","slices","gargle","register","terrorist","innocently","throat","butt","oftentimes","indignity","patel's","harming","recipes","sudden","floggers","/","we'll","molested","!!!!!........","confine","999999999","figures","boredom","relevant","agreed","hangers","justifiable","mans","800","overpopulated","pieces","amusing","negotiate","suggestion","middle","dozen","courtyard","shelter","390839038298398423","among","bottom","evaluation","missuri","supporters","literal","basis","000","regardless","cage","outlaw","psychological","immeasurably","plotted","teenagers","inflicted","tattoed","sway","reoffend","bashers","kidnapped","james","pregnant","suicides","elderly","admits","um","disputed","torture(prison","superman","instances","trophy","parents","irrational","homes","dignifying","self-defence","newborn","?????","games","fans","`it","rips","that'd","norwegian","21","past","brainwash")));
//            put("The level of certainty that is necessary for Death Penalty is unachievable",new ArrayList<String>(Arrays.asList("5/100","together","<--","-'shot","shooter","preferred","brutal","oddly","netflix","ethnic","figured","ppl","owed","police/government","cops","mention","addresses","wiser","quality","dispute","lesson","culture","undisputable","buried","walk","wise","trump","supposition","there've","cutting","eyewitnesses","county","ian's","shadow","http://www.pnas.org/content/111/20/7230.full","innocent(meaning","60","parole(unless","crucial","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!","swayed","significantly","constitutes","hair","tack","propose","flies","strictest","nest","classified","paid","precedent","erroneous","referring","newsflash","fix","terribly","screwed","innocently","throat","examiner","merit","sudden","floggers","sack","outright","parasites","instance)","justifiable","mans","morton","popular","could've","plain","accurate","interest","widely","dragged","dozen","battle","grey","expanded","williamson","undeniable","officers/juries","bar","achieve","fucked","requires","downright","}","restrict","ironclad","superman","cruise","coherent","deny","inadverdantly","phrase","forensics","inclined","e","sub-human","area","islop","ect","rips","past","corrupted","convicted﻿","hatred","betting","d","infrastructure","reprehensible","excutions","institute","life's","eager","race","sentiment","monetary","accusations","everyone's","convicts","actuly","corporate","contaminated","death-friendly","reminding","acquire","raised","f","compare","detour","vincent","prisoner","h","nonetheless","rude","rare","wrongful","front","1000","consistent","names","freed","fewer","shootings","gender","stakes","elite","tired","film","exonerates","gov't","pockets","miscarriages","lifelong","countless","forced","people's","fan","(5","destroys","lying","questions","principles","selected","infallible","detectives","wealthy","g","fucker","michael","eligible","sympathises","apparatus","acceptable","fate","heck","boys","valuable","training","face-to-face","saved","confessions","poorly","concrete","drawn","sponsored","permanently","stacked","tampered","rat's","???!!!","indorse","register","wheres","open","compel","sends","zero","documentary","roulette","agreed","racial","russian","beings","hangers","tyrant","express-lane","hicks","--","district","mr.","sadly","stemming","crossed","negotiate","expensive﻿","withheld","rely","cameras","represented","joker","supporters","issue","000","justly","man's","flawed","outlaw","sway","shared","irreversible","more﻿","bashers","95%","james","permitted","worl","disputed","moronic","participate","specific","rating","hearsay","notice","homes","racking","fbi","accuracy","attorney","maths","themself","minority","prosecutor","tom")));
//        }
//    };
	
//	private Map<String, List<String>> target2vocab = new HashMap<String, List<String>>(){
//        {
//        put("Death Penalty should be done by gunshot", new ArrayList<String>(Arrays.asList("be","don't","use","lethal","pay","put","shot","head","squad","someone","money","saves","are","squads","get","firing","shoot","bullet","uncivilized","injection","bullets")));
//        put("Death Penalty (Debate)", new ArrayList<String>(Arrays.asList("murder","be","death","penalty","bring","is","believe","am","criminals","should","justice","crime","state","support")));
//        put("Death Penalty for heinous crimes (murder, mass murder, rape, child molestation etc.)", new ArrayList<String>(Arrays.asList("think","rapists","be","murderers","killers","crimes","penalty","kill","killed","people","rape","world","shows","raped","murdered","children","keep","should","family","says","child")));
//        put("Death Penalty should be enforced more quickly e.g. by minimizing the number of appeals",new ArrayList<String>(Arrays.asList("execution","dispute","be","death","don't","appeal","do","years","put","trial","head","appeals","guilty","money","would","keep","should","row","time","day","did")));	
//        put("If Death Penalty is allowed, abortion should be legal, too.",	new ArrayList<String>(Arrays.asList("give","abortion","alive","democrats","murderer","go","killing","is","delivered","believe","right","cycle","guilty","dominated","abort","keep","have","baby","know","wing","anti-abortion")));
//        put("In certain cases, capital punishment shouldn’t have to be humane but more harsh",new ArrayList<String>(Arrays.asList("think","quick","humane","year","like","wife","fuckers","harsh","many","cheap","starve","bit","killed","expensive","torture","executions","would","someone","shit","return","mercy")));
//        put("Life-long prison should be replaced by Death Penalty",	new ArrayList<String>(Arrays.asList("rest","be","alive","will","killing","least","is","mental","being","serve","kill","parole","life","years","food","punishment","guys","lock","decades","family","prison")));
//        put("The level of certainty that is necessary for Death Penalty is unachievable",new ArrayList<String>(Arrays.asList("be","cases","death","evidence","will","innocent","doubt","is","say","proven","killed","people","system","guilty","are","were","should","justice","crime","row","did")));
////  
//        }
//    };
    
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		this.targetVocab=target2vocab.get(target);
		return true;
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
