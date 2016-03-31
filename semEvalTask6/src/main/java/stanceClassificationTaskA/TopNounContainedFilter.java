package stanceClassificationTaskA;

import org.dkpro.tc.api.features.FeatureStore;
import org.dkpro.tc.api.features.Instance;
import org.dkpro.tc.fstore.filter.FeatureStoreFilter;

public class TopNounContainedFilter implements FeatureStoreFilter {

	@Override
	public void applyFilter(FeatureStore store) {
		for (int i = 0; i < store.getNumberOfInstances(); i++) {
			Instance instance = store.getInstance(i);
//			instance
		}

	}

	@Override
	public boolean isApplicableForTraining() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicableForTesting() {
		// TODO Auto-generated method stub
		return false;
	}

}
