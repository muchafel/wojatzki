package util.languageModellingUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * makes ARPA compatible with Orens Thesaurus (needs special ARPA)
 * 
 * @author michael
 *
 */
public class ThesaurusLMGeneration_Oren {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(
				new FileReader("/Users/michael/Downloads/cmusphinx-5.0-en-us.lm"))) {
			String line = null;
			boolean noBackoff=false;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("\\")||line.startsWith("n")||line.isEmpty()){
					writeLexicon(line);
					if(line.equals("\\3-grams:")){
						System.out.println("no backoff");
						noBackoff=true;
					}
				}else{
					String[] pieces= line.split(" ");
					String newLine="";
					for(int i=0; i<pieces.length; i++){
						if(i==0){
							newLine+=pieces[i];
						}else{
							if(!noBackoff){
								char concat=' ';
								if(i==1||i==pieces.length-1)concat='\t';
								String toAdd=concat + pieces[i];
//								System.out.println(toAdd);
								newLine+=toAdd;
								newLine.concat(toAdd);
							}else{
								char concat=' ';
								if(i==1)concat='\t';
								String toAdd=concat + pieces[i];
//								System.out.println(toAdd);
								newLine+=toAdd;
								newLine.concat(toAdd);
							}
							
						}
					}
//					System.out.println(newLine);
//					if(noBackoff)System.out.println(newLine);
					writeLexicon(newLine);
				}
			}
		}
	}

	private static void writeLexicon(String toWrite) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("/Users/michael/Downloads/cmusphinx-5.0-en-us_adapted.lm", true)))) {
			out.println(toWrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
