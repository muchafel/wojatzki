package dataInspection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import util.Tweet;

public class XMLCreationTaskA {

	public static void main(String[] args) throws FileNotFoundException, IOException, JAXBException {
		List<String> topics= new ArrayList<String>();
		
//		File file= new File("/Users/michael/ArgumentMiningCoprora/semEval2016/semeval2016-task6-trainingdata.txt");
		File fileTest= new File("/Users/michael/ArgumentMiningCoprora/semEval2016/SemEval2016-Task6-testdata/SemEval2016-Task6-subtaskA-testdata.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(fileTest))) {
		    String line;
		    boolean firstLine=true;
		    while ((line = br.readLine()) != null) {
		    	if(firstLine) firstLine=false;
		    	else{
		    		String textWithoutSemST=line.split("\t")[2].replace("#SemST", "");
		    		Tweet tweet=new Tweet(line.split("\t")[0] ,line.split("\t")[1],textWithoutSemST,line.split("\t")[3]);
		    		
		    	    JAXBContext context = JAXBContext.newInstance(Tweet.class);
		    	    Marshaller m = context.createMarshaller();
		    	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		    	    // Write to System.out
		    	    m.marshal(tweet, System.out);
		    	    // Write to File
		    	    m.marshal(tweet, new File("/Users/michael/ArgumentMiningCoprora/semEval2016/SemEval2016-Task6-testdata/xmls/tweets/taskA/"+line.split("\t")[0]+".xml"));
//		    	    m.marshal(tweet, new File("src/main/resources/tweets"+line.split("\t")[0]+".xml"));
		    	}
		    }
		}
	}

}
