package de.unidue.ltl.core.data.opinionSummarization;

import java.util.Random;

public class CanabisParticipant extends Participant {

	private Random random;
	private boolean sportiv;
	private boolean smoker;
	private boolean smokeShopOwner;
	private boolean conservativeOrLiberal;
	
	public CanabisParticipant(int id, String gender, int age, String profession, Random random) {
		super(id, gender, age, profession);
		this.sportiv=random.nextBoolean();
		this.smoker=random.nextBoolean();
		this.smokeShopOwner=random.nextBoolean();
		this.conservativeOrLiberal=random.nextBoolean();
		this.random=random;
	}
	
	
	@Override
	public String print() {
		StringBuilder sb= new StringBuilder(super.print());
		sb.append(sportiv+ "\t");
		sb.append(smoker+ "\t");
		sb.append(smokeShopOwner+ "\t");
		sb.append(conservativeOrLiberal);
		return sb.toString();
	}


	public boolean isSportiv() {
		return sportiv;
	}


	public boolean isSmoker() {
		return smoker;
	}


	public boolean isSmokeShopOwner() {
		return smokeShopOwner;
	}


	public boolean isConservativeOrLiberal() {
		return conservativeOrLiberal;
	}

}
