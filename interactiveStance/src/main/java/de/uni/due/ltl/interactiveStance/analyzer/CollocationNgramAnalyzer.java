package de.uni.due.ltl.interactiveStance.analyzer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.expression.spel.support.ReflectionHelper.ArgsMatchKind;

import cmu.arktweetnlp.Twokenize;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.DataPoint;
import de.uni.due.ltl.interactiveStance.db.StanceDB;
import de.uni.due.ltl.interactiveStance.util.CollocationMeasureHelper;

public class CollocationNgramAnalyzer {

	private StanceDB db;
	
	public CollocationNgramAnalyzer(StanceDB db) {
		this.db= db;
	}

	
	/**
	 * TODO: check whether we can get rid of all the casting from int to String... ID is a INT!
	 * 
	 * @param selectedTargets
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	public void analyze(HashMap<String, ExplicitTarget> selectedTargets) throws NumberFormatException, SQLException {
		StanceLexicon lexicon = createStanceLexicon(selectedTargets);
		
		
		
		
		
	}

	/**
	 * TODO: Currently uses Twokenizer directly. Use UIMA?
	 * @param selectedTargets
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	private StanceLexicon createStanceLexicon(HashMap<String, ExplicitTarget> selectedTargets) throws NumberFormatException, SQLException {
		FrequencyDistribution<String> favor = new FrequencyDistribution<String>();
		FrequencyDistribution<String> against = new FrequencyDistribution<String>();
		
		for(String id: selectedTargets.keySet()){
			DataPoint point= db.getDataPointById(Integer.valueOf(id));
			if(point.getLabel().equals("FAVOR")){
				favor.incAll(Twokenize.tokenize(point.getText()));
			}else{
				against.incAll(Twokenize.tokenize(point.getText()));
			}
		}
		return createLexiconFromDistributions(favor,against);
	}


	private static StanceLexicon createLexiconFromDistributions(FrequencyDistribution<String> favour,
			FrequencyDistribution<String> against) {
		Map<String, Float> lexcicon = new TreeMap<String, Float>();
		Set<String> candidates = new HashSet<String>();
		//add all cands. dublicates will be removed because map stores just unique entries
		candidates.addAll(favour.getKeys());
		candidates.addAll(against.getKeys());
		
		CollocationMeasureHelper helper = new CollocationMeasureHelper(favour, against);
		
		for (String word : candidates) {
			lexcicon.put(word, helper.getDiffOfGMeans(word));
		}

		return new StanceLexicon(lexcicon);
	}


	

}
