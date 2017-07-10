package de.uni.due.ltl.interactiveStance.experimentLogging;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.uni.due.ltl.interactiveStance.backend.EvaluationResult;
import de.uni.due.ltl.interactiveStance.backend.ExperimentConfiguration;

public class ExperimentLogging {

	private String username;
	private ExperimentConfiguration config;
	private File currentLoggingFile;
	private File generalLoggingFile;
	private String logIn;
	private String logOut;
	private double bestF1_micro;
	private List<String> bestTargetsFavor;
	private List<String> bestTragetsAgainst;
	
	public ExperimentLogging(String username) {
		this.username= username;
		this.bestF1_micro=0.0;
		System.out.println("welcome "+username+ " ("+currentDate()+")");	   
		String baseDir = null;
		try {
			baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.currentLoggingFile= new File(baseDir+"/interactiveStance/results/"+username+"_"+currentDate()+".txt");
		this.generalLoggingFile= new File(baseDir+"/interactiveStance/results/experimentLog.csv");
		try {
			initgeneralLogging();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	private void initgeneralLogging() throws IOException {
		if(!generalLoggingFile.exists()){
			FileUtils.write(generalLoggingFile, "user;scenario;mode;simpleMode;logIN;logOUT;logFile;bestResult_microF1;targetsForBestResult_FAVOR;targetsForBestResult_AGAINST"+"\n", "UTF-8", true);
		}
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
	public void persistEvent(LoggingEvent loggingingEvent) {
		String currentTime=currentDate();
		System.out.println(currentTime+"\t"+loggingingEvent.eventToString());
		memorizeEventSpecific(loggingingEvent,currentTime);
		try {
			FileUtils.write(currentLoggingFile, loggingingEvent.eventToString()+"\n", "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void memorizeEventSpecific(LoggingEvent loggingingEvent, String currentTime) {
		if(loggingingEvent instanceof ConfigurationEvent){
			this.logIn=currentTime;
			this.config=((ConfigurationEvent) loggingingEvent).getConfig();
		}
		else if(loggingingEvent instanceof ResultEvent){
			ResultEvent resultEvent= (ResultEvent) loggingingEvent;
			EvaluationResult result=resultEvent.getResult();
			if(result.getMicroF()>this.bestF1_micro){
				this.bestF1_micro=result.getMicroF();
				this.bestTargetsFavor=resultEvent.getFavorTargets();
				this.bestTragetsAgainst=resultEvent.getAgainstTargets();
			}
		}
		
	}



	public void persistExperiment(LoggingEvent loggingingEvent) {
		this.logOut=currentDate();
		try {
			FileUtils.write(generalLoggingFile, username+";"+config.getScenario()+";"+config.getExperimentMode()+";"+config.isSimpleMode()+";"+logIn+";"+logOut+";"+currentLoggingFile.getName()+";"+this.bestF1_micro+";"+this.bestTargetsFavor+";"+this.bestTragetsAgainst+";"+"\n", "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	
	
}
