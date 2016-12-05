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
 * resamples the size of the NONE class to the SUM of the other two classes
 * @author michael
 *
 */
public class UniformClass_NONE_DistributionFilter
	implements FeatureStoreFilter
{

	@Override
	public void applyFilter(FeatureStore store) 
	{
		
		// create mapping from outcomes to instance offsets in the feature store
		Map<String, List<Integer>> outcome2instanceOffset = new HashMap<String, List<Integer>>();
		for (int i=0; i<store.getNumberOfInstances(); i++) {
			Instance instance = store.getInstance(i);
			String outcome = instance.getOutcome();
			List<Integer> offsets;
			if (outcome2instanceOffset.containsKey(outcome)) {
				offsets = outcome2instanceOffset.get(outcome);
			}
			else {
				offsets = new ArrayList<>();
			}
			offsets.add(i);
			outcome2instanceOffset.put(outcome, offsets);
		}
		
		// find the smallest class
		int sumOfMinorClasses = 0;
		for (String outcome : outcome2instanceOffset.keySet()){
			if(outcome.equals("FAVOR")){
				sumOfMinorClasses+=outcome2instanceOffset.get(outcome).size();
			}
			if(outcome.equals("AGAINST")){
				sumOfMinorClasses+=outcome2instanceOffset.get(outcome).size();
			}
		}
		
		// resample all but the smallest class to the same size as the smallest class
		// return the offsets of the instances that should be deleted
		SortedSet<Integer> offsetsToDelete = new TreeSet<>();
		for (String outcome : outcome2instanceOffset.keySet()) {
			if (outcome.equals("NONE")) {
				offsetsToDelete.addAll(resample(outcome2instanceOffset.get(outcome), sumOfMinorClasses));
			}
		}
		
		int nrOfDeleted = 0;
		for (int offsetToDelete : offsetsToDelete) {
			store.deleteInstance(offsetToDelete - nrOfDeleted);
			nrOfDeleted++;
		}
	}

	private List<Integer> resample(List<Integer> offsets, int targetSize) {
		List<Integer> shuffledOffsets = new ArrayList<>(offsets);
		Collections.shuffle(shuffledOffsets);
		return shuffledOffsets.subList(targetSize, shuffledOffsets.size());	
	}

	@Override
	public boolean isApplicableForTraining() {
		return true;
	}

	@Override
	public boolean isApplicableForTesting() {
		return false;
	}
}
