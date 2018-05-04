package hateSpeechData;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class BWSResult {

	public String mothercode;
	public FrequencyDistribution<String> listBest, listWorst;
	

	public BWSResult(String stringCellValue, FrequencyDistribution<String> listBest, FrequencyDistribution<String> listWorst) {
		this.mothercode=stringCellValue;
		this.listBest = listBest;
		this.listWorst = listWorst;
	}

	public String getMothercode() {
		return mothercode;
	}

	public void setMothercode(String mothercode) {
		this.mothercode = mothercode;
	}

	public FrequencyDistribution<String> getListBest() {
		return listBest;
	}

	public void setListBest(FrequencyDistribution<String> listBest) {
		this.listBest = listBest;
	}

	public FrequencyDistribution<String> getListWorst() {
		return listWorst;
	}

	public void setListWorst(FrequencyDistribution<String> listWorst) {
		this.listWorst = listWorst;
	}

}
