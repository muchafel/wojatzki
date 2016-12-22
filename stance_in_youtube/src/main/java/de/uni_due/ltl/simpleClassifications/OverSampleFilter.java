package de.uni_due.ltl.simpleClassifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.FeatureStore;
import org.dkpro.tc.api.features.Instance;
import org.dkpro.tc.fstore.filter.FeatureStoreFilter;

public class OverSampleFilter implements FeatureStoreFilter{

	@Override
	public void applyFilter(FeatureStore store) {
		
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
		
		// find the largest class
		int maxClassSize = 0;
		for (String outcome : outcome2instanceOffset.keySet()) {
			int classSize = outcome2instanceOffset.get(outcome).size();
			if (classSize > maxClassSize) {
				maxClassSize = classSize;
			}
		}
		
		// resample all but the smallest class to the same size as the smallest class
		// return the offsets of the instances that should be deleted
		SortedSet<Integer> offsetsToDelete = new TreeSet<>();
		for (String outcome : outcome2instanceOffset.keySet()) {
			int diff = maxClassSize-outcome2instanceOffset.get(outcome).size();
			while(diff>0){
				diff--;
				Instance randomInstance= getRandomInstance(outcome2instanceOffset.get(outcome),store);
				Instance instance= new Instance(randomInstance.getFeatures(),randomInstance.getOutcome());
				try {
					store.addInstance(instance);
				} catch (TextClassificationException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		
	}

	private Instance getRandomInstance(List<Integer> offSets, FeatureStore store) {
		Random random = new Random();
		int index=random.nextInt(offSets.size());
		return store.getInstance(offSets.get(index));
	}

	@Override
	public boolean isApplicableForTraining() {
		return true;
	}

	@Override
	public boolean isApplicableForTesting() {
		// TODO Auto-generated method stub
		return false;
	}

}
