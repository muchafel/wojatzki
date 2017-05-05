package de.uni.due.ltl.interactiveStance.backend;

import org.apache.lucene.queryparser.classic.ParseException;

import de.uni.due.ltl.interactiveStance.analyzer.TargetSearcher;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.io.EvaluationData;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackEnd {

	private static long idCounter;
	private HashMap<String, ExplicitTarget> availableTargets = new HashMap<>();
	private HashMap<String, ExplicitTarget> selectedFavorTargets = new HashMap<>();
	private HashMap<String, ExplicitTarget> selectedAgainstTargets = new HashMap<>();

	// Create dummy data by randomly combining first and last names
	static String[] targets = { "Atheism is a cure", "People With Low IQ Scores Should Be Sterilized.",
			"Hillary is bad", "Atheism ist all bad", "I hate Trump", "the bible is true",
			"Creatures of the lord are fine", "Jesus is our savious", "Hang em by the neck", "DP for Heinous crimes",
			"It is time for sugar", "Abortion is SIN", "Everyone has the right to choose", "Ban DP", "I hate gafs",
			"Harry Potter is the best movie" };

	private static BackEnd instance;
	private static StanceDB db;
	private static TargetSearcher searcher;
	private static EvaluationData evaluationData;

	public static BackEnd loadData() {
		/**
		 * DB logic here
		 */
		if (instance == null) {

			final BackEnd backend = new BackEnd();

			//for testing only, should be done in teh config section
			try {
				evaluationData= new EvaluationData("Atheism");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			//uncomment for testing
//			Random r = new Random(200);
//			for (int i = 0; i < 100; i++) {
//				ExplicitTarget model = new ExplicitTarget(String.valueOf(idCounter++), targets[r.nextInt(targets.length)],
//						r.nextInt(50), r.nextInt(40));
//				backend.save(model);
//			}
			// Workaround, grid drad and drop feature still in developing phase. Don't support empty grid.
			backend.selectTestSave();

			/**
			 * TODO credentials
			 * TODO exception handling
			 */
			try {
				db= new StanceDB("root", "","jdbc:mysql://localhost/interactiveArgumentMining");
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}


			searcher = new TargetSearcher();
			try {
				searcher.SetUp(db,100);
				for(ExplicitTarget target:searcher.search("atheism",true)){
					backend.save(target);
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			instance = backend;
			
		}

		return instance;
	}

	public synchronized List<ExplicitTarget> getAllAvailableTargets(String stringFilter) {
		ArrayList<ExplicitTarget> arrayList = new ArrayList<ExplicitTarget>();
		for (ExplicitTarget model : availableTargets.values()) {
			boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
					|| model.getTargetName().toLowerCase().contains(stringFilter.toLowerCase());
			if (passesFilter) {
				arrayList.add(model);
			}
		}
		return arrayList;
	}

	public synchronized List<ExplicitTarget> getFilteredTargets(HashMap<String, ExplicitTarget> targets, String stringFilter) {
		ArrayList<ExplicitTarget> arrayList = new ArrayList<ExplicitTarget>();
		for (ExplicitTarget model : targets.values()) {
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

	public synchronized void delete(ExplicitTarget value) {
		availableTargets.remove(value.getId());
	}

	public synchronized void save(ExplicitTarget entry) {
		availableTargets.put(entry.getId(), entry);
	}

	// Workaround, https://github.com/vaadin/framework/issues/9068  Yafei
	public void selectTestSave() {
		Random r = new Random(200);
		ExplicitTarget model = new ExplicitTarget(String.valueOf(idCounter++), targets[r.nextInt(targets.length)],
				r.nextInt(50), r.nextInt(40));
		selectedFavorTargets.put(model.getId(), model);
		selectedAgainstTargets.put(model.getId(), model);
	}

	public synchronized void selectFavorTarget(ExplicitTarget model) {
		selectedFavorTargets.put(model.getId(), model);
		availableTargets.remove(model.getId());
	}

	public synchronized void selectAgainstTarget(ExplicitTarget model) {
		selectedAgainstTargets.put(model.getId(), model);
		availableTargets.remove(model.getId());
	}

	public synchronized void deselectFavorTarget(ExplicitTarget model) {
		availableTargets.put(model.getId(), model);
		selectedFavorTargets.remove(model.getId());
	}

	public synchronized void deselectAgainstTarget(ExplicitTarget model) {
		availableTargets.put(model.getId(), model);
		selectedAgainstTargets.remove(model.getId());
	}

	public synchronized List<ExplicitTarget> getAllSelectedFavorTargets() {
		ArrayList<ExplicitTarget> arrayList = new ArrayList<ExplicitTarget>();
		for (ExplicitTarget model : selectedFavorTargets.values()) {
			arrayList.add(model);
		}
		return arrayList;
	}

	public synchronized List<ExplicitTarget> getAllSelectedAgainstTargets() {
		ArrayList<ExplicitTarget> arrayList = new ArrayList<ExplicitTarget>();
		for (ExplicitTarget model : selectedAgainstTargets.values()) {
			arrayList.add(model);
		}
		return arrayList;
	}

	public synchronized String printSelectedTargets() {
		StringBuilder sb = new StringBuilder();
		for(String key:selectedFavorTargets.keySet()){
			sb.append(selectedFavorTargets.get(key).getTargetName()+""+System.lineSeparator());
		}
		// add selected targets of against.
		for(String key:selectedAgainstTargets.keySet()){
			sb.append(selectedAgainstTargets.get(key).getTargetName()+""+System.lineSeparator());
		}
		return sb.toString();
	}

	public synchronized EvaluationResult analyse() {
		EvaluationResult result = new EvaluationResult();
		//TODO: proper exception handling
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * loads the selected data in target wrapper class
	 * @param target
	 */
	public synchronized void loadEvaluationData (String target) {
		try {
			evaluationData= new EvaluationData(target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * returns the loaded train data, object can be used to get descriptive stats
	 * @param target
	 * @return
	 */
	public synchronized EvaluationDataSet getTrainData (String target) {
		return evaluationData.getTrainData();
	}
	
	/**
	 * returns the loaded test data, object can be used to get descriptive stats
	 * @param target
	 * @return
	 */
	public synchronized EvaluationDataSet getTestData (String target) {
		return evaluationData.getTestData();
	}
	
	
	public synchronized boolean newSearch(String query) {
		if (searcher == null) {
			return false;
		}

		availableTargets = new HashMap<>();
		
		try {
			List<ExplicitTarget>targets = searcher.search(query,true);
			if (targets != null) {
				for(ExplicitTarget target:targets){
					this.save(target);
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
