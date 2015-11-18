package dataInspection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class PreprocessingDependencyInspection extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		System.out.println(jcas.getDocumentText());
		Collection<Dependency> dependencies = JCasUtil.select(jcas, Dependency.class);
		for (Dependency dep : dependencies) {
			System.out.println(dep.getDependencyType() + " dependent: " + dep.getDependent().getCoveredText()
					+ " governor: " + dep.getCoveredText());
		}
		printSubjectChain(dependencies);
	}
	
	
	private void printSubjectChain(Collection<Dependency> dependencies) {
		for (Dependency dep : dependencies) {
			if (dep.getDependencyType().equals("nsubj")) {
				List<Dependency> relatedDependencies = getDependienciesWithSharedGovernor(dep.getGovernor(),
						dependencies);
				List<Dependency> modifyingDependencies = getDependienciesWithGovernorAsDependent(dep.getGovernor(),
						dependencies);
//				for(Dependency d: modifyingDependencies)System.out.println(d.getDependencyType()+" "+d.getGovernor().getCoveredText());
				printPairs(dep, relatedDependencies);
			}
		}
	}

	private List<Dependency> getDependienciesWithSharedGovernor(Token governor, Collection<Dependency> dependencies) {
		List<Dependency> relatedDependencies = new ArrayList<Dependency>();
		for (Dependency dep : dependencies) {
			if (dep.getGovernor() == governor) {
				relatedDependencies.add(dep);
			}
		}
		return relatedDependencies;
	}


	private List<Dependency> getDependienciesWithGovernorAsDependent(Token governor,
			Collection<Dependency> dependencies) {
		List<Dependency> relatedDependencies = new ArrayList<Dependency>();
		for (Dependency dep : dependencies) {
			if (dep.getDependent() == governor) {
				relatedDependencies.add(dep);
			}
		}
		return relatedDependencies;
	}

	private void printPairs(Dependency dep, List<Dependency> relatedDependency) {
		for (Dependency relatedDep : relatedDependency) {
//			System.out.println(relatedDep.getDependencyType());
			if (relatedDep.getDependencyType().equals("dobj") || relatedDep.getDependencyType().equals("pobj")||relatedDep.getDependencyType().equals("xcomp")||relatedDep.getDependencyType().equals("acomp"))
				System.out.println(dep.getDependent().getCoveredText() + " " + dep.getGovernor().getCoveredText() + " "
						+ relatedDep.getDependent().getCoveredText());
		}
	}

}
