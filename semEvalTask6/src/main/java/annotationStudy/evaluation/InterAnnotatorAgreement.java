package annotationStudy.evaluation;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.taskdefs.Mkdir;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class InterAnnotatorAgreement {

	public static void main(String[] args) throws IOException {
		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
//		extract(new File(baseDir+"/semevalTask6/prestudy_annotation/Stance_Arguments_Prestudy_2016-03-23_1639/annotation"));
		
	}

	private static void extract(File folder) {
		
		for  (File xmiFolder : folder.listFiles()) {
	        if (xmiFolder.isDirectory()) {
	        	for(File annotation: xmiFolder.listFiles()){
//	        		System.out.println(annotation.getName());
//	        		System.out.println(annotation.getName().substring(3));
	        		File newAnnoFolder=new File(folder.getParent()+"/annotation_unzipped/"+xmiFolder.getName().substring(0, xmiFolder.getName().length()-8));
	        		newAnnoFolder.mkdir();
	        		if(annotation.getName().substring(annotation.getName().length()-3).equals("zip")){
	        			System.out.println("unzip "+annotation.getName());
	        			unzip(annotation,newAnnoFolder);
	        		}
	        	}
	        } 
	    }
	}

	private static void unzip(File fileEntry,File folder) {
		String source = fileEntry.getAbsolutePath();
		String destination = folder.getAbsolutePath();   
		    try {
		        ZipFile zipFile = new ZipFile(source);
		        zipFile.extractAll(destination);;
		    } catch (ZipException e) {
		        e.printStackTrace();
		    }
		    for  (File unzipped : new File(destination).listFiles()) {
		    	if(unzipped.getName().substring(unzipped.getName().length()-3).equals("xmi")&&!unzipped.getName().contains("tweet")){
		    		File newName=new File(folder.getAbsolutePath()+"/"+folder.getName()+"_"+unzipped.getName());
		    		unzipped.renameTo(newName);
		    	}
		    }
	}

}
