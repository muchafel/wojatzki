package assertionRegression.io;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ReadInDeepResults {

	public static void main(String[] args) throws IOException {
		File folder= new File("/Users/michael/Desktop/org.dkpro.lab/repository");
		Map<String, Double> name2Score= new LinkedHashMap();
		
		
		for (File file:folder.listFiles()) {
//			System.out.println(file.getName());
			if(file.getName().equals(".DS_Store")) {
				System.out.println(file.getName());
				continue;
			}
			for (File resultfile:file.listFiles()) {
				if(resultfile.getName().equals("results.txt")) {
					
					for (String line : FileUtils.readLines(resultfile, "UTF-8")) {
						
						if(line.startsWith("Pearson")) {
//							System.out.println(line+" "+file);
							name2Score.put(file.getName(),Double.valueOf(line.split("=")[1]));
						}
					}
					
				}
			}
		}
		name2Score=sortByValue(name2Score);
		for(String key: name2Score.keySet()) {
			System.out.println(key+ " "+name2Score.get(key));
		}

	}
	
	private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
    }

}
