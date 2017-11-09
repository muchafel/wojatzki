package assertionRegression.corpusProcessing;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CorpusCleaner {
	public static void main(String[] args) throws IOException {
		CorpusCleaner cleaner= new CorpusCleaner();
		cleaner.clean("/Users/michael/Desktop/statuses");
	}

	private void clean(String path) throws IOException {
		File folder= new File(path);
		for(File file: folder.listFiles()){
			if(file.getName().endsWith(".txt")){
				File writeTo=new File(folder, file.getName().substring(0, file.getName().length()-4)+"_cleaned.txt");
				for(String line: FileUtils.readLines(file, "UTF-8")){
					
//					System.out.println(line);
					String[] parts=line.split("\t");
					if(!line.isEmpty() && parts.length>2){
						//if line != retweet
						if(parts[2].equals("false")){
							FileUtils.write(writeTo, parts[1]+"\n","UTF-8",true);
						}
					}
				}
			}
		}
		
	}
}
