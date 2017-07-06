package de.uni.due.ltl.interactiveStance.experimentLogging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExperimentLogging {

	private String username;
	
	
	public ExperimentLogging(String username) {
		this.username= username;
		System.out.println("welcome "+username+ " ("+currentDate()+")");
		
	}

	
	
	private String currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		Date date = new Date();		
		return dateFormat.format(date);
	}



	public void persist(LoggingEvent loggingingEvent) {
		System.out.println(currentDate()+"\t"+loggingingEvent.eventToString());
		
	}

	
	
}
