package de.uni.due.ltl.interactiveStance.experimentLogging;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;

public class ExperimentLogging {

	private String username;
	private File loggingFile;
	
	public ExperimentLogging(String username) {
		this.username= username;
		System.out.println("welcome "+username+ " ("+currentDate()+")");	   
		String baseDir = null;
		try {
			baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.loggingFile= new File(baseDir+"/interactiveStance/results/"+username+"_"+currentDate()+".txt");
	}

	
	
	private String currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss_SSS");
		Date date = new Date();		
		return dateFormat.format(date);
	}



	/**
	 * TODO write to DB?
	 * @param loggingingEvent
	 */
	public void persist(LoggingEvent loggingingEvent) {
		System.out.println(currentDate()+"\t"+loggingingEvent.eventToString());
		try {
			FileUtils.write(loggingFile, loggingingEvent.eventToString()+"\n", "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
}
