package annotators;

import java.util.Collection;
import java.util.HashSet;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import lexicons.StanceLexicon;
import types.FunctionalPartAnnotation;
import types.StanceAnnotation;

public abstract class PolarityAnnotator_base extends JCasAnnotator_ImplBase {

	public static final String PARAM_USE_FUCNTIONAL_PARTITION = "useFunctionalPartition";
	
	@ConfigurationParameter(name = PARAM_USE_FUCNTIONAL_PARTITION, mandatory = false, defaultValue="false")
	protected boolean useFunctionalPartition;
	
	protected StanceLexicon atheismStances;
	protected StanceLexicon abortionStances;
	protected StanceLexicon hillaryStances;
	protected StanceLexicon feministStances;
	protected StanceLexicon climateStances;
	
	
	protected Collection<Token> getFunctionalTokens(JCas jcas, String function) {
		Collection<Token> tokens= new HashSet<Token>();
		for(FunctionalPartAnnotation part: JCasUtil.select(jcas, FunctionalPartAnnotation.class)){
			if(part.getFunction().equals(function)){
				tokens.addAll(JCasUtil.selectCovered(Token.class,part));
			}
		}
		return tokens;
	}



	protected StanceLexicon chooseLexicon(StanceAnnotation stanceAnno) throws Throwable {
		switch (stanceAnno.getTarget()) {
		case "Atheism":
			return this.atheismStances;
		case "Legalization of Abortion":
			return this.abortionStances;
		case "Hillary Clinton":
			return this.hillaryStances;
		case "Feminist Movement":
			return this.feministStances;
		case "Climate Change is a Real Concern":
			return this.climateStances;
		default:
			throw(new Throwable("no lexicon specified for "+stanceAnno.getTarget()));
		}
	}
}
