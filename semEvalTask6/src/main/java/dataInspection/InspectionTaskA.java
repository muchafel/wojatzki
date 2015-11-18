package dataInspection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InspectionTaskA {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<String> topics= new ArrayList<String>();
		
		File file= new File("/Users/michael/ArgumentMiningCoprora/semEval2016/semeval2016-task6-trainingdata.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    boolean firstLine=true;
		    while ((line = br.readLine()) != null) {
		    	if(firstLine) firstLine=false;
		    	else{
//			    	System.out.println("id: "+line.split("\t")[0] + " |topic: "+ line.split("\t")[1]+" |text: "+line.split("\t")[2]+" |stance: "+line.split("\t")[3]);	
			    	if(!topics.contains(line.split("\t")[1]))topics.add(line.split("\t")[1]);
		    	}
		    }
		}
		System.out.println(topics);
	}

}
