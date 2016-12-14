package de.uni_due.ltl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dkpro.tc.api.features.FeatureStore;
import org.dkpro.tc.api.features.Instance;
import org.dkpro.tc.fstore.filter.FeatureStoreFilter;
/**
 * filter to remove NONE outcome from training and testing
 * @author michael
 *
 */
public class None_Filter implements FeatureStoreFilter {

	@Override
	public void applyFilter(FeatureStore store) {

		// create mapping from outcomes to instance offsets in the feature store
		Map<String, List<Integer>> outcome2instanceOffset = new HashMap<String, List<Integer>>();
		for (int i = 0; i < store.getNumberOfInstances(); i++) {
			Instance instance = store.getInstance(i);
			String outcome = instance.getOutcome();
			List<Integer> offsets;
			if (outcome2instanceOffset.containsKey(outcome)) {
				offsets = outcome2instanceOffset.get(outcome);
			} else {
				offsets = new ArrayList<>();
			}
			offsets.add(i);
			outcome2instanceOffset.put(outcome, offsets);
		}


		// add all NONE offsets to the delete list
		SortedSet<Integer> offsetsToDelete = new TreeSet<>();
		for (String outcome : outcome2instanceOffset.keySet()) {
			if (outcome.equals("NONE")) {
				offsetsToDelete.addAll(outcome2instanceOffset.get(outcome));
			}
		}

		//delete NONEs
		int nrOfDeleted = 0;
		for (int offsetToDelete : offsetsToDelete) {
			store.deleteInstance(offsetToDelete - nrOfDeleted);
			nrOfDeleted++;
		}
	}


	@Override
	public boolean isApplicableForTraining() {
		return true;
	}

	@Override
	public boolean isApplicableForTesting() {
		return true;
	}
}
