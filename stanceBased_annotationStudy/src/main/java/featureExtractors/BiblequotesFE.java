package featureExtractors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;


public class BiblequotesFE 
	extends FeatureExtractorResource_ImplBase
	implements FeatureExtractor{

	public static final String BIBLE_QUOTE = "bibleQuote";

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target1) 
			throws TextClassificationException
	{
		Set<Feature> featList = new HashSet<Feature>();
		
		//patterns
		Pattern bibleQuoteRegexPattern = Pattern.compile("[0-9]+:[0-9]+");
		
		//end of patterns
		
		boolean bibleQuote = false;
		String sentences = jcas.getDocumentText();
		
			Matcher quoteMatcher = bibleQuoteRegexPattern.matcher(jcas.getDocumentText());
		
			if (quoteMatcher.find()){
				bibleQuote=true;
			
			
		}
		featList.add(new Feature(BIBLE_QUOTE, bibleQuote));
		return featList;
	}

	

}


