package de.uni_due.ltl.util.iaa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.FleissKappaAgreement;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;

public class IAAHelper {

	public Map<String, Double> interAnnotatorAgreementInsult(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators, String insultTag, Map<String, Double> fleissKappasInsult) {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		int count = 0;
		// for each document
		for (String documentId : annotatorToSentenceToDecisions.keySet()) {
			// foreachSentence
			for (int sentenceId : annotatorToSentenceToDecisions.get(documentId).keySet()) {
				count+=countFavor(getAnnotatorInsult(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),insultTag),
						getAnnotatorInsult(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),insultTag),
						getAnnotatorInsult(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),insultTag));
				study.addItem(getAnnotatorInsult(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),insultTag),
						getAnnotatorInsult(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),insultTag),
						getAnnotatorInsult(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),insultTag));
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);

		System.out.println("**INSULT**" + insultTag + " ( COUNT: " + count + ")");
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		fleissKappasInsult.put(insultTag, fleissKappa.calculateAgreement());
		return fleissKappasInsult;
	}
	
	
	public Map<String, Double> interAnnotatorAgreementReference(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators, String referenceTag, Map<String, Double> fleissKappaReference) {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		int count = 0;
		// for each document
		for (String documentId : annotatorToSentenceToDecisions.keySet()) {
			// foreachSentence
			for (int sentenceId : annotatorToSentenceToDecisions.get(documentId).keySet()) {
				count+=countFavor(getAnnotatorReference(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),referenceTag),
						getAnnotatorReference(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),referenceTag),
						getAnnotatorReference(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),referenceTag));
				study.addItem(getAnnotatorReference(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),referenceTag),
						getAnnotatorReference(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),referenceTag),
						getAnnotatorReference(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),referenceTag));
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);

		System.out.println("**REFERENCE**" + referenceTag + " (COUNT: " + count + ")");
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		fleissKappaReference.put(referenceTag, fleissKappa.calculateAgreement());
		return fleissKappaReference;
	}
	
	public Map<String, Double> interAnnotatorAgreementTarget_Set1(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators, String target,Map<String, Double> fleissKappas) throws Exception {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}
		
		int favor=0;
		int against=0;
		
		//for each document
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				if(annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()!=annotators.size()){
					System.err.println("skip sentence because not enough annos");
				}else{
//					System.out.println(sentenceId+ " "+ annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size());
					if(annotators.size()==2){
						study.addItem(getAnnotatorExplicitStance_1(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
					else if(annotators.size()==3){
						
						favor+=countFavor(getAnnotatorExplicitStance_1(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
//						
						against+=countAgainst(getAnnotatorExplicitStance_1(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
						
//						System.out.println(sentenceId+" "+getAnnotatorExplicitStance_1(annotators.get(0),
//								annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
//								+ " "
//								+ getAnnotatorExplicitStance_1(annotators.get(1),
//										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
//								+ " " + getAnnotatorExplicitStance_1(annotators.get(1),
//										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target));
						study.addItem(getAnnotatorExplicitStance_1(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_1(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
				}
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);

		System.out.println("**TARGET** "+ target +" ( FAVOR: "+favor+ " ; AGAINST: "+against+")");
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		fleissKappas.put(target, fleissKappa.calculateAgreement());
		return fleissKappas;
	}
	
	public  Map<String,Double> interAnnotatorAgreementTarget_Set2(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators, String target, Map<String,Double> fleissKappas) throws Exception {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}
		int favor=0;
		int against=0;
		//for each document
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				if(annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()!=annotators.size()){
					System.err.println("skip sentence because not enough annos");
				}else{
//					System.out.println(sentenceId+ " "+ annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size());
					if(annotators.size()==2){
						study.addItem(getAnnotatorExplicitStance_2(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
					else if(annotators.size()==3){
						
						favor+=countFavor(getAnnotatorExplicitStance_2(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
						
						against+=countAgainst(getAnnotatorExplicitStance_2(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
						
//						System.out.println(sentenceId+" "+getAnnotatorExplicitStance_2(annotators.get(0),
//								annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
//								+ " "
//								+ getAnnotatorExplicitStance_2(annotators.get(1),
//										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target)
//								+ " " + getAnnotatorExplicitStance_2(annotators.get(1),
//										annotatorToSentenceToDecisions.get(documentId).get(sentenceId), target));
						study.addItem(getAnnotatorExplicitStance_2(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target),
								getAnnotatorExplicitStance_2(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId),target));
					}
				}
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);

		System.out.println("**TARGET** "+ target +" ( FAVOR: "+favor+ " ; AGAINST: "+against+")");
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		fleissKappas.put(target, fleissKappa.calculateAgreement());
		return fleissKappas;
	}
	
	private String getAnnotatorExplicitStance_1(String annotatorName, List<AnnotatorDecision> decisisons, String target) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getExplicitStances_Set1() == null || decision.getExplicitStances_Set1().isEmpty()){
					return "NONE";
				}
				for(Explicit_Stance_Container stance:decision.getExplicitStances_Set1()){
					if(stance.getTarget().equals(target)){
						if(stance.getPolarity()==null){
							System.err.println(annotatorName+ " null stance for "+target);
							return "NONE";
						}
						return "Debate";
//						return stance.getPolarity();
					}
				}
			}
		}
		return "NONE";
	}

	
	private String getAnnotatorExplicitStance_2(String annotatorName, List<AnnotatorDecision> decisisons, String target) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getExplicitStances_Set2() == null || decision.getExplicitStances_Set2().isEmpty()){
					return "NONE";
				}
				for(Explicit_Stance_Container stance:decision.getExplicitStances_Set2()){
					if(stance.getTarget().equals(target)){
						return "Debate";
//						return stance.getPolarity();
					}
				}
			}
		}
		return "NONE";
	}
	
	private String getAnnotatorInsult(String annotatorName, List<AnnotatorDecision> decisisons, String target) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getInsults() == null || decision.getInsults().isEmpty()){
					return "NONE";
				}
				for(InsultContainer insult:decision.getInsults()){
					if(insult.getTag().equals(target)){
						return "FAVOR";
					}
				}
			}
		}
		return "NONE";
	}
	
	private String getAnnotatorReference(String annotatorName, List<AnnotatorDecision> decisisons, String tag) {
		for(AnnotatorDecision decision: decisisons){
//			System.out.println(decision.getAnnotator());
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getReferences() == null || decision.getReferences().isEmpty()){
					return "NONE";
				}
				for(ReferenceContainer reference:decision.getReferences()){
					//if source is not set but there is an annotation we assume that this refers to foreign source (default)
					if(reference.getSource()==null){
						if(tag.equals("Video")){
							return "NONE";
						}else{
							return "FAVOR";
						}
					}
					if(reference.getSource().equals(tag)){
						return "FAVOR";
					}
				}
			}
		}
		return "NONE";
	}
	
	public Map<String, Double> interAnnotatorAgreementDebateStance(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions, ArrayList<String> annotators, Map<String, Double> fleissKappas) throws Exception {
		
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		if (annotators.size() > 3) {
			throw new Exception("unhandled number of annotators");
		}
		
		//for each document
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				
				if(annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()!=annotators.size()){
					System.err.println("skip sentence because not enough annos"+annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size()+" != "+annotators.size());
				}else{
//					System.out.println(sentenceId+ " "+ annotatorToSentenceToDecisions.get(documentId).get(sentenceId).size());
					if(annotators.size()==2){
						study.addItem(getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
					}
					else if(annotators.size()==3){
//						System.out.println(documentId+" sentence id: "+sentenceId+" "+ getSentenceId(annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId))+" "+getAnnotatorDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
						study.addItem(getAnnotatorDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getAnnotatorDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getAnnotatorDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
					}
				}
			}
		}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
		System.out.println("DEBATE STANCE");
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		fleissKappas.put("Debate_Stance", fleissKappa.calculateAgreement());
		return fleissKappas;
	}
	
	private String getAnnotatorDebateStance(String annotatorName, List<AnnotatorDecision> decisisons) {
		for(AnnotatorDecision decision: decisisons){
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getStance() == null){
					System.err.println("WTF");
					return "NONE";
				}
				return decision.getStance().getPolarity();
			}
		}
		System.err.println("no matching annotator"+ annotatorName);
		return "NONE";
	}
	
	
	private static int countAgainst(String annotatorExplicitStance_2, String annotatorExplicitStance_22,
			String annotatorExplicitStance_23) {
		int count=0;
		if (annotatorExplicitStance_2.equals("AGAINST"))count++;
		if (annotatorExplicitStance_22.equals("AGAINST"))count++;
		if (annotatorExplicitStance_23.equals("AGAINST"))count++;
		return count;
	}


	private static int countFavor(String annotatorExplicitStance_2, String annotatorExplicitStance_22,
			String annotatorExplicitStance_23) {
		int count=0;
		if (annotatorExplicitStance_2.equals("FAVOR"))count++;
		if (annotatorExplicitStance_22.equals("FAVOR"))count++;
		if (annotatorExplicitStance_23.equals("FAVOR"))count++;
		return count;
	}


	public Map<String, Double> iaaDebateStanceInferredFromContext(
			Map<String, Map<Integer, List<AnnotatorDecision>>> annotatorToSentenceToDecisions,
			ArrayList<String> annotators, Map<String, Double> fleissKappas) {
		CodingAnnotationStudy study = new CodingAnnotationStudy(annotators.size());
		int count = 0;
		for(String documentId: annotatorToSentenceToDecisions.keySet()){
			//foreachSentence
			for(int sentenceId:annotatorToSentenceToDecisions.get(documentId).keySet()){
				
				count+=countFavor(getExplicitNoneDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
						getExplicitNoneDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
						getExplicitNoneDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
									study.addItem(getExplicitNoneDebateStance(annotators.get(0),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getExplicitNoneDebateStance(annotators.get(1),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)),
								getExplicitNoneDebateStance(annotators.get(2),annotatorToSentenceToDecisions.get(documentId).get(sentenceId)));
				}
			}
		PercentageAgreement pa = new PercentageAgreement(study);
		FleissKappaAgreement fleissKappa = new FleissKappaAgreement(study);
		System.out.println("DEBATE STANCE (Inferred) count:"+count);
		System.out.println("PERCENTAGE AGREEMENT " + pa.calculateAgreement());
		System.out.println("FLEISSKAPPA " + fleissKappa.calculateAgreement());
		fleissKappas.put("Debate_Stance(Inferred)", fleissKappa.calculateAgreement());
		return fleissKappas;
	}
	private String getExplicitNoneDebateStance(String annotatorName, List<AnnotatorDecision> decisisons) {
		for(AnnotatorDecision decision: decisisons){
			if(decision.getAnnotator().equals(annotatorName)){
				if(decision.getExplicitStances_Set1() == null || decision.getExplicitStances_Set1().isEmpty()){
					return "NONE";
				}
				for(Explicit_Stance_Container container:decision.getExplicitStances_Set1()){
					if(container.getTarget().equals("Death Penalty (Debate)")){
						if(container.getPolarity().equals("NONE")){
							return "FAVOR";
						}else{
							return "NONE";
						}
					}
				}
			}
		}
		return "NONE";
	}
	
}
