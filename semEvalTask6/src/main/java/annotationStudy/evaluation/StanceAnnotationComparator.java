package annotationStudy.evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import webanno.custom.Stance;

public class StanceAnnotationComparator {

	public int compare(List<StanceContainer> stanceList1, List<StanceContainer> stanceList2) {
		System.out.println("Annotator1 found " + stanceList1.size() + " targets");
		System.out.println("Annotator2 found " + stanceList2.size() + " targets");
		Set<String> shared = new HashSet<String>();
		Set<String> annotator1 = new HashSet<String>();
		Set<String> annotator2 = new HashSet<String>();

		for (StanceContainer stance1 : stanceList1) {
			String anno1=stance1.getTarget() + " (" + stance1.getPolarity() + ")";
			annotator1.add(anno1);
			for (StanceContainer stance2 : stanceList2) {
				String anno2=stance2.getTarget() + " (" + stance2.getPolarity() + ")";
				if (anno1.equals(anno2)) {
					shared.add(anno1);
				}
			}
		}

		for (StanceContainer stance2 : stanceList2) {
			annotator2.add(stance2.getTarget() + " (" + stance2.getPolarity() + ")");
		}

		//FIXME would be better to fill just non-overlapping
		annotator1.removeAll(shared);
		annotator2.removeAll(shared);
		
		System.out.println("shared " + shared);
		System.out.println("A1 only: " + annotator1);
		System.out.println("A2 only: " + annotator2);
		return annotator1.size()+annotator2.size();
	}

}
