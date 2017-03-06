package de.uni.due.ltl.interactiveStance.backend;

import org.apache.commons.beanutils.BeanUtils;

import com.vaadin.v7.event.ItemClickEvent;

import de.uni.due.ltl.interactiveStance.db.StanceDB;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackEnd {

	private static long idCounter;
	private HashMap<Long, ExplicitStanceModel> availableTargets = new HashMap<>();
	private HashMap<Long, ExplicitStanceModel> selectedTargets = new HashMap<>();

	// Create dummy data by randomly combining first and last names
	static String[] targets = { "Atheism is a cure", "People With Low IQ Scores Should Be Sterilized.",
			"Hillary is bad", "Atheism ist all bad", "I hate Trump", "the bible is true",
			"Creatures of the lord are fine", "Jesus is our savious", "Hang em by the neck", "DP for Heinous crimes",
			"It is time for sugar", "Abortion is SIN", "Everyone has the right to choose", "Ban DP", "I hate gafs",
			"Harry Potter is the best movie" };

	private static BackEnd instance;
	private static StanceDB db;

	public static BackEnd loadData() {
		/**
		 * DB logic here
		 */
		if (instance == null) {

			final BackEnd contactService = new BackEnd();

			Random r = new Random(200);
			for (int i = 0; i < 100; i++) {
				ExplicitStanceModel model = new ExplicitStanceModel(idCounter++, targets[r.nextInt(targets.length)],
						r.nextInt(50), r.nextInt(40), null);
				contactService.save(model);
			}
			/**
			 * TODO credentials
			 * TODO exception handling
			 */
			try {
				db= new StanceDB("", "");
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			instance = contactService;
		}

		return instance;
	}

	public synchronized List<ExplicitStanceModel> getAllAvailableTargets(String stringFilter) {
		ArrayList<ExplicitStanceModel> arrayList = new ArrayList<ExplicitStanceModel>();
		for (ExplicitStanceModel model : availableTargets.values()) {
			boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
					|| model.getTargetName().toLowerCase().contains(stringFilter.toLowerCase());
			if (passesFilter) {
				arrayList.add(model);
			}
		}
		return arrayList;
	}

	public synchronized long count() {
		return availableTargets.size();
	}

	public synchronized void delete(ExplicitStanceModel value) {
		availableTargets.remove(value.getId());
	}

	public synchronized void save(ExplicitStanceModel entry) {
		availableTargets.put(entry.getId(), entry);
	}

	public synchronized void selectTarget(ExplicitStanceModel model) {
		selectedTargets.put(model.getId(), model);
		availableTargets.remove(model.getId());
	}

	public synchronized void deselectTarget(ExplicitStanceModel model) {
		availableTargets.put(model.getId(), model);
		selectedTargets.remove(model.getId());
	}

	public synchronized List<ExplicitStanceModel> getAllSelectedTargets() {
		ArrayList<ExplicitStanceModel> arrayList = new ArrayList<ExplicitStanceModel>();
		for (ExplicitStanceModel model : selectedTargets.values()) {
			arrayList.add(model);
		}
		return arrayList;
	}

	public synchronized String printSelectedTargets() {
		StringBuilder sb= new StringBuilder();
		for(Long key:selectedTargets.keySet()){
			sb.append(selectedTargets.get(key).getTargetName()+""+System.lineSeparator());
		}
		return sb.toString();
	}

	public synchronized EvaluationResult analyse() {
		EvaluationResult result= new EvaluationResult();
		//TODO: proper exception handling
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}


}
