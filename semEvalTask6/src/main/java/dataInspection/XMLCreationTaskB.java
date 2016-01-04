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

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import util.Tweet;

public class XMLCreationTaskB {

	public static void main(String[] args) throws FileNotFoundException, IOException, JAXBException {
		List<String> topics= new ArrayList<String>();
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		File file= new File("/Users/michael/ArgumentMiningCoprora/semEval2016/downloaded_Donald_Trump5.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    boolean firstLine=true;
		    while ((line = br.readLine()) != null) {
		    	if(firstLine) firstLine=false;
		    	else if(line.split("\t")[1].equals("Not Available")){
		    		System.out.println(line.split("\t")[0]+ " not available");
		    	}
		    	else {
		    		String textWithoutSemST=line.split("\t")[1].replace("#SemST", "");
		    		Tweet tweet=new Tweet(line.split("\t")[0] ,"DonaldTrump",textWithoutSemST,"UNKNOWN");
		    		
		    	    JAXBContext context = JAXBContext.newInstance(Tweet.class);
		    	    Marshaller m = context.createMarshaller();
		    	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		    	    // Write to System.out
		    	    m.marshal(tweet, System.out);
		    	    // Write to File
		    	    m.marshal(tweet, new File(baseDir+"/semevalTask6/tweetsTaskB/"+line.split("\t")[0]+".xml"));
		    	}
		    }
		}
	}

}
