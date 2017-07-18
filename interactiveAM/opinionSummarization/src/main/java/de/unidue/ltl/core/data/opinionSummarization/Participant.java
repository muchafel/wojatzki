package de.unidue.ltl.core.data.opinionSummarization;

public class Participant {
	
	private int id;
	private String gender;
	private int age;
	private String profession;
	
	public Participant(int id, String gender, int age, String profession) {
		this.id = id;
		this.gender = gender;
		this.age = age;
		this.profession = profession;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String print(){
		StringBuilder sb= new StringBuilder();
		sb.append(id+"\t");
		sb.append(gender+"\t");
		sb.append(String.valueOf(age)+"\t");
		sb.append(profession+"\t");
		return sb.toString();
	}
	
}
