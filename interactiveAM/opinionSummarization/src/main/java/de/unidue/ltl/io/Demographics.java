package de.unidue.ltl.io;

public class Demographics {
	private int id;
	private int age;
	private String gender;
	private String affiliation;
	private String eductaion;
	private String profession;
	private String familyStauts;
	private String religion;
	private String race;
	private String ties2Overseas;
	private String usCitizen;
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAffiliation() {
		return affiliation;
	}
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	public String getEductaion() {
		return eductaion;
	}
	public void setEductaion(String eductaion) {
		this.eductaion = eductaion;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getFamilyStauts() {
		return familyStauts;
	}
	public void setFamilyStauts(String familyStauts) {
		this.familyStauts = familyStauts;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getTies2Overseas() {
		return ties2Overseas;
	}
	public void setTies2Overseas(String ties2Overseas) {
		this.ties2Overseas = ties2Overseas;
	}
	public String getUsCitizen() {
		return usCitizen;
	}
	public void setUsCitizen(String usCitizen) {
		this.usCitizen = usCitizen;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String prettyPrint(){
		StringBuilder sb= new StringBuilder();
		sb.append(this.getId()+"\t");
		sb.append(this.getAge()+"\t");
		sb.append(this.getGender()+"\t");
		sb.append(this.getAffiliation()+"\t");
		sb.append(this.getEductaion()+"\t");
		sb.append(this.getFamilyStauts()+"\t");
		sb.append(this.getProfession()+"\t");
		sb.append(this.getRace()+"\t");
		sb.append(this.getReligion()+"\t");
		sb.append(this.getTies2Overseas()+"\t");
		sb.append(this.getUsCitizen());
		
		return sb.toString();
		
	}
}
