package assertionRegression.similarity;

public class Participant {
	
	private int id;
	
	private String gender;
	private int age;
	private String profession;
	private String affiliation;
	private String education;
	private String familyStatus;
	private String race;
	private String religion;
	private String ties2overseas;
	private String USCitizen;
	
	public Participant(int id){
		this.id = id;
	}
	
	public Participant(int id, String gender, int age, String profession, String affiliation, String education,
			String familyStatus, String race, String religion, String ties2overseas, String uSCitizen) {
		this.id = id;
		this.gender = gender;
		this.age = age;
		this.profession = profession;
		this.affiliation = affiliation;
		this.education = education;
		this.familyStatus = familyStatus;
		this.race = race;
		this.religion = religion;
		this.ties2overseas = ties2overseas;
		this.USCitizen = uSCitizen;
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
		sb.append(affiliation+"\t");
		sb.append(education+"\t");
		sb.append(familyStatus+"\t");
		sb.append(race+"\t");
		sb.append(religion+"\t");
		sb.append(ties2overseas+"\t");
		return sb.toString();
	}
	public String getAffiliation() {
		return affiliation;
	}
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getFamilyStatus() {
		return familyStatus;
	}
	public void setFamilyStatus(String familyStatus) {
		this.familyStatus = familyStatus;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getTies2overseas() {
		return ties2overseas;
	}
	public void setTies2overseas(String ties2overseas) {
		this.ties2overseas = ties2overseas;
	}
	public String getUSCitizen() {
		return USCitizen;
	}
	public void setUSCitizen(String uSCitizen) {
		USCitizen = uSCitizen;
	}
	
	@Override
	public String toString() {
		return print();
	}
	
}
