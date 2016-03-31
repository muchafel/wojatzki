package util;

import java.util.ArrayList;
import java.util.List;

import org.dkpro.tc.api.features.FeatureStore;
import org.dkpro.tc.api.features.Instance;
import org.dkpro.tc.fstore.filter.FeatureStoreFilter;

public class NoneTrainFilter implements FeatureStoreFilter {

	@Override
	public void applyFilter(FeatureStore store) {
		List<Integer> toDelete= new ArrayList<Integer>();
		for (int i = 0; i < store.getNumberOfInstances(); i++) {
			Instance instance = store.getInstance(i);
			if (instance.getOutcome().equals("NONE")) {
				toDelete.add(i);
			}
		}
		
		int nrOfDeleted = 0;
		for (int i :toDelete) {
			store.deleteInstance(i-nrOfDeleted);
			nrOfDeleted++;
		}
	}

	@Override
	public boolean isApplicableForTesting() {
		return false;
	}

	@Override
	public boolean isApplicableForTraining() {
		return true;
	}

}
