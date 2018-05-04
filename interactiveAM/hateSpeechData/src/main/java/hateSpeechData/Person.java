package hateSpeechData;

import java.util.Map;

public class Person {
	
	public Person(double age, double gender, double edu, double profession, int genderMeasures, String mothercode) {
		this.age = age;
		this.gender = gender;
		this.edu = edu;
		this.profession = profession;
		this.genderMeasures = genderMeasures;
		this.mothercode = mothercode;
	}
	
	public Person() {
		
	}
	
	private Map<String, Double> varToJudgments;
	private double age;
	private BWSResult bwsRes;
	private double gender;
	private double edu;
	private double profession;
	private double genderMeasures;
	private String mothercode;
	
	public double getAge() {
		return age;
	}
	public void setAge(double age) {
		this.age = age;
	}
	public double getGender() {
		return gender;
	}
	public void setGender(double gender) {
		this.gender = gender;
	}
	public double getEdu() {
		return edu;
	}
	public void setEdu(double edu) {
		this.edu = edu;
	}
	public double getProfession() {
		return profession;
	}
	public void setProfession(double profession) {
		this.profession = profession;
	}
	public double getGenderMeasures() {
		return genderMeasures;
	}
	public void setGenderMeasures(double genderMeasures) {
		this.genderMeasures = genderMeasures;
	}
	public String getMothercode() {
		return mothercode;
	}
	public void setMothercode(String mothercode) {
		this.mothercode = mothercode;
	}
	
	
	public String print() {
		
		StringBuilder sb= new StringBuilder();
		sb.append("mothercode: "+mothercode+ " \n");
		sb.append("profession: "+profession+"\n");	
		sb.append("gender: "+gender+ " \n");
		sb.append("edu: "+edu+ " \n");
		sb.append("gendermsrs: "+genderMeasures);
		
		
		return sb.toString();
		
	}


	public BWSResult getBwsRes() {
		return bwsRes;
	}

	public void setBWSRes(BWSResult bwsRes) {
		this.bwsRes = bwsRes;
	}



	public Map<String, Double> getVarToJudgments() {
		return varToJudgments;
	}

	public void setVarToJudgments(Map<String, Double> varToJudgments) {
		this.varToJudgments = varToJudgments;
	}

}
