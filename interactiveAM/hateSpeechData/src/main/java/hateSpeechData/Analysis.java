package hateSpeechData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class Analysis {
	public static void main(String[] args) throws IOException {
		ConsolidateData data=new ConsolidateData();
		 
		List<Person> persons= data.getdata( "/Users/michael/Dropbox/gender_hatespeech_corpus/collected_data_cleaned/data_FrauenAD_2018-04-30_09-43.xlsx","/Users/michael/Dropbox/gender_hatespeech_corpus/collected_data_cleaned/bws_questionnaires");
		//can only be called after getData has been callled
		Set<String> assertions=data.getAssertionsToIndex().keySet();
		System.out.println(assertions);
		printData(persons,data,assertions);
	
		Map<String,Double> assertionsToAdScores= getADScores(assertions,persons);
		Map<String,Double> assertionsToBWSScores= getBWSScores(assertions,persons,45);
//		for(String assertion: assertionsToAdScores.keySet()) {
//			System.out.println(assertion.substring(1)+"\t"+assertionsToAdScores.get(assertion));
//		}
		for(String assertion: assertionsToBWSScores.keySet()) {
			System.out.println(assertion.substring(1)+"\t"+assertionsToBWSScores.get(assertion));
		}
		
		System.out.println("");
		System.out.println("\tGURLS");
		List<Person> girls=filterGender(persons,1.0);
		
		Map<String,Double> assertionsToAdScores_girls= getADScores(assertions,girls);
		Map<String,Double> assertionsToBWSScores_girls= getBWSScores(assertions,girls,24);
//		for(String assertion: assertionsToAdScores_girls.keySet()) {
//			System.out.println(assertion+"\t"+assertionsToAdScores_girls.get(assertion));
//		}
//		for(String assertion: assertionsToBWSScores_girls.keySet()) {
//			System.out.println(assertion+"\t"+assertionsToBWSScores_girls.get(assertion));
//		}
		
		System.out.println("");
		System.out.println("\tBOYS");
		List<Person> boys=filterGender(persons,2.0);
		
		Map<String,Double> assertionsToAdScores_boys= getADScores(assertions,boys);
		Map<String,Double> assertionsToBWSScores_boys= getBWSScores(assertions,boys,24);
//		for(String assertion: assertionsToBWSScores_boys.keySet()) {
//			System.out.println(assertion.substring(1)+"\t"+assertionsToBWSScores_boys.get(assertion));
//		}

//		for(String assertion: assertionsToAdScores_boys.keySet()) {
//			System.out.println(assertion+" \t"+assertionsToAdScores_boys.get(assertion));
//		}
//		correlation(assertionsToAdScores,assertionsToBWSScores, "AD(all)--BWS(all)");
//		correlation(assertionsToAdScores,assertionsToAdScores_boys, "AD(all)--AD(boys)");
//		correlation(assertionsToAdScores_girls,assertionsToAdScores_boys, "AD(girls)--AD(boys)");
//
//		
//		correlation(assertionsToBWSScores_boys,assertionsToBWSScores_girls, "BWS(girls)--BWS(boys)");
//		correlation(assertionsToBWSScores,assertionsToBWSScores_boys, "BWS(ALL)--BWS(boys)");
//		correlation(assertionsToBWSScores,assertionsToBWSScores_girls, "BWS(ALL)--BWS(girls)");
//		
//		correlation(assertionsToAdScores_boys,assertionsToBWSScores_boys, "AD(boys)--BWS(boys)");
//		correlation(assertionsToAdScores_girls,assertionsToBWSScores_girls, "AD(girls)--BWS(girls)");
//
//		
//		reliabilityAD(persons,assertions,"SHR AD(ALL)");
//		reliabilityAD(boys,assertions,"SHR AD(boys)");
//		reliabilityAD(girls,assertions,"SHR AD(girls)");
//		
//		
//		reliabilityBWS(persons,assertions,"SHR BWS(ALL)",24);
//		reliabilityBWS(boys,assertions,"SHR BWS(boys)",12);
//		reliabilityBWS(girls,assertions,"SHR BWS(girls)",12);
		
	}

	private static void printData(List<Person> persons, ConsolidateData data, Set<String> assertions) throws IOException {
		StringBuilder sb= new StringBuilder();
		sb.append("\t");
		for(String assertion: assertions) {
			sb.append(assertion+"\t");
		}
		sb.append("\n");
		for(Person p: persons) {
			sb.append(p.getMothercode()+"\t");
			for(String assertion: assertions) {
				sb.append(p.getVarToJudgments().get(assertion)+"\t");
			}
			sb.append("\n");
		}
		FileUtils.write(new File("/Users/michael/Dropbox/gender_hatespeech_corpus/collected_data_cleaned/data.tsv"), sb.toString(),"UTF-8");
		
	}

	private static void reliabilityAD(List<Person> persons, Set<String> assertions, String string) {
		double repetitions=100;
		double sum=0;
		double[] scores= new double[(int) repetitions];
		for (int i=0; i<repetitions;i++) {
			List<List<Person>> randomHalves=randomSplitHalf(persons);
			Map<String,Double> assertionsToAdScores_half1= getADScores(assertions,randomHalves.get(0));
			Map<String,Double> assertionsToAdScores_half2= getADScores(assertions,randomHalves.get(1));
			scores[i]=correlationWOPrint(assertionsToAdScores_half1,assertionsToAdScores_half2);
//			sum+=correlationWOPrint(assertionsToAdScores_half1,assertionsToAdScores_half2);
		}
		scores= zTransform(scores);
		double mean= StatUtils.mean(scores);
		System.out.println(string+" "+retransform(mean));
		
//		System.out.println(string+" "+sum/repetitions);
	}


	private static List<List<Person>> randomSplitHalf(List<Person> persons) {
		List<List<Person>> result= new ArrayList<>();
		Collections.shuffle(persons);
		List<Person> half1 = persons.subList(0, persons.size() / 2);
		List<Person> half2 = persons.subList(persons.size() / 2, persons.size());
		result.add(half1);
		result.add(half2);

		return result;
	}

	private static void reliabilityBWS(List<Person> persons, Set<String> assertions, String string, double j) {
		double repetitions=100;
		double sum=0;
		double[] scores= new double[(int) repetitions];
		for (int i=0; i<repetitions;i++) {
			List<List<Person>> randomHalves=randomSplitHalf(persons);
			Map<String,Double> assertionsToAdScores_half1= getBWSScores(assertions,randomHalves.get(0),j);
			Map<String,Double> assertionsToAdScores_half2= getBWSScores(assertions,randomHalves.get(1),j);
			scores[i]=correlationWOPrint(assertionsToAdScores_half1,assertionsToAdScores_half2);
//			sum+=correlationWOPrint(assertionsToAdScores_half1,assertionsToAdScores_half2);
		}
		scores= zTransform(scores);
		double mean= StatUtils.mean(scores);
		System.out.println(string+" "+retransform(mean));
		
	}

	private static void correlation(Map<String, Double> map1, Map<String, Double> map2, String title) {
		map1 = new TreeMap<String, Double>(map1);
		map2 = new TreeMap<String, Double>(map2);
		double[] x = ArrayUtils.toPrimitive(map1.values().toArray(new Double[map1.values().size()]));
		double[] y = ArrayUtils.toPrimitive(map2.values().toArray(new Double[map2.values().size()]));

		double pearsonCorr = new PearsonsCorrelation().correlation(y, x);
		double spearManCorr = new SpearmansCorrelation().correlation(y, x);

		System.out.println(title + " pearson: " + pearsonCorr + " spearman: " + spearManCorr);

	}
	
	private static double correlationWOPrint(Map<String, Double> map1, Map<String, Double> map2) {
		map1 = new TreeMap<String, Double>(map1);
		map2 = new TreeMap<String, Double>(map2);
		double[] x = ArrayUtils.toPrimitive(map1.values().toArray(new Double[map1.values().size()]));
		double[] y = ArrayUtils.toPrimitive(map2.values().toArray(new Double[map2.values().size()]));

		return new PearsonsCorrelation().correlation(y, x);

	}

	private static Map<String, Double> getBWSScores(Set<String> assertions, List<Person> persons, double amount) {
		Map<String,Double> result= new HashMap<>();
		for(String assertion: assertions) {
			double countBest=0;
			double countWorst=0;
			for(Person p: persons) {
//				System.out.println(p.getMothercode());
//				System.out.println(p.getBwsRes().getMothercode());
//				if(p.getBwsRes().getListBest().getCount(assertion.substring(1))>0) {
//					System.out.println(assertion);
//				}
				
				countBest+=p.getBwsRes().getListBest().getCount(assertion.substring(1));
				countWorst+=p.getBwsRes().getListWorst().getCount(assertion.substring(1));
			}
			double val= (double)countBest/amount - (double)countWorst/amount;
			result.put(assertion, val);
		}
		return sortByValue(result);
	}

	private static List<Person> filterGender(List<Person> persons, double d) {
		List<Person> ps= new ArrayList<>();
		for(Person p: persons) {
			if(p.getGender()==d) {
				ps.add(p);
			}
		}
		return ps;
	}

	private static Map<String,Double> getADScores(Set<String> assertions, List<Person> persons) {
		Map<String,Double> result= new HashMap<>();
		double sum=0.0;
		for(String assertion: assertions) {
			double countAgree=0;
			double countAll=0;
			for(Person p: persons) {
				countAll++;
				if(p.getVarToJudgments().get(assertion)==2.0) {
					countAgree++;
				}
			}
			double val= countAgree/countAll;
			sum+=val;
			result.put(assertion, val);
		}
		System.out.println("mean "+sum/assertions.size());
		return sortByValue(result);
		
	}
	
	
	private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

        List<Map.Entry<String, Double>> list=new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }



        return sortedMap;
    }
	
	public static double zTransform(double value) {
		double transformed = 0.5 * Math.log((1 + value) / (1 - value));
		return transformed;
	}

	public static double retransform(double value) {
		double retransformed = (Math.exp(2 * value) - 1) / (Math.exp(2 * value) + 1);
		return retransformed;
	}

	private static double[] zTransform(double[] scores) {
		double[] results = new double[scores.length];
		int i = 0;
		for (double score : scores) {
			results[i] = zTransform(score);
			i++;
		}
		return results;
	}
}
