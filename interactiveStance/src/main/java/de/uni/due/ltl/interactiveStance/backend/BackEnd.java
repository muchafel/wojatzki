package de.uni.due.ltl.interactiveStance.backend;

import de.uni.due.ltl.interactiveStance.client.ConfigView;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.uima.UIMAException;
import org.dkpro.tc.api.exception.TextClassificationException;

import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzerBase;
import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer_distributionDerived;
import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer_fixedThresholds;
import de.uni.due.ltl.interactiveStance.analyzer.CollocationNgramAnalyzer_optimized;
import de.uni.due.ltl.interactiveStance.analyzer.TargetSearcher;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.io.EvaluationScenario;
import de.uni.due.ltl.interactiveStance.io.EvaluationDataSet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackEnd {

	private static long idCounter;
	private HashMap<String, ExplicitTarget> availableTargets = new HashMap<>();
	private HashMap<String, ExplicitTarget> selectedFavorTargets = new HashMap<>();
	private HashMap<String, ExplicitTarget> selectedAgainstTargets = new HashMap<>();

	// Create dummy data by randomly combining first and last names
	static String[] targets = { "Dummy I", "Dummy II" };

	private static BackEnd instance;
	private static StanceDB db;
	private static TargetSearcher searcher;
	private static EvaluationScenario evaluationScenario;
	private static CollocationNgramAnalyzerBase analyzer;

	public static BackEnd loadData() {
		/**
		 * DB logic here
		 */
		if (instance == null) {

			final BackEnd backend = new BackEnd();

			//set up scenario
			try {
				evaluationScenario = new EvaluationScenario(ConfigView.getScenario(), ConfigView.getExperimentMode());
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
//			backend.selectTestSave();

			
			//set up db
			/**
			 * TODO credentials
			 * TODO exception handling
			 */
			try {
				db = new StanceDB("iStance", "eschedu","jdbc:mysql://localhost/interactiveArgumentMining");
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			//Set up Searcher
			searcher = new TargetSearcher();
			try {
				searcher.SetUp(db,100);
				for(ExplicitTarget target:searcher.search(ConfigView.getScenario(),true)){
					backend.save(target);
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			//Set up analyzer
			try {
				analyzer= selectAnalyzer(db,evaluationScenario);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			instance = backend;
		}

		return instance;
	}

	private static CollocationNgramAnalyzerBase selectAnalyzer(StanceDB db2,
			EvaluationScenario evaluationScenario2) throws Exception {
		CollocationNgramAnalyzerBase analyzer = null;
		
		if(evaluationScenario.getMode().equals("Fixed Threshold")){
			analyzer = new CollocationNgramAnalyzer_fixedThresholds(db,evaluationScenario,75);
		}else if(evaluationScenario.getMode().equals("Optmized Threshold")){
			analyzer= new CollocationNgramAnalyzer_optimized(db,evaluationScenario);
		}else if(evaluationScenario.getMode().equals("Distributional Threshold")){
			analyzer= new CollocationNgramAnalyzer_distributionDerived(db,evaluationScenario,0.95);
		}else{
			throw new Exception(evaluationScenario.getMode()+" is not a valid analyzer");
		}
		
		return analyzer;
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
		EvaluationResult result = null;
		//TODO: proper exception handling
		try {
			result=analyzer.analyze(this.selectedFavorTargets, selectedAgainstTargets, 1, true);
		} catch ( NumberFormatException | UIMAException | SQLException | TextClassificationException e) {
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
			evaluationScenario= new EvaluationScenario(ConfigView.getScenario(), ConfigView.getExperimentMode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	/**
//	 * returns the loaded train data, object can be used to get descriptive stats
//	 * @param target
//	 * @return
//	 */
//	public synchronized EvaluationDataSet getTrainData () {
//		return evaluationScenario.getTrainData();
//	}
//	
//	/**
//	 * returns the loaded test data, object can be used to get descriptive stats
//	 * @param target
//	 * @return
//	 */
//	public synchronized EvaluationDataSet getTestData () {
//		return evaluationScenario.getTestData();
//	}
	
	
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

	public Map<String, Double> getAblation(boolean evaluateFavor) {
		TargetAblationTest ablation= new TargetAblationTest(selectedFavorTargets,selectedAgainstTargets,analyzer, true);
		try {
			return ablation.ablationTest(evaluateFavor);
		} catch (NumberFormatException | UIMAException | SQLException | TextClassificationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized EvaluationScenario getEvaluationScenario() {
		return evaluationScenario;
	}

	public synchronized void setEvaluationScenario(EvaluationScenario evaluationScenario) {
		BackEnd.evaluationScenario = evaluationScenario;
	}
}
