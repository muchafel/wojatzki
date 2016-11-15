package de.uni_due.ltl.simpleClassifications;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import preprocessing.Users;

public class Remebered_UsersFE extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

	public static final String PARAM_USER_LIST = "userNamesFilePath";
	@ConfigurationParameter(name = PARAM_USER_LIST, mandatory = true)
	private File userNameFile;

	private Set<String> userNames = new HashSet<String>();
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		try {
			userNames = getNames(userNameFile);
			System.out.println(userNames);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}
	
	private Set<String> getNames(File userNameFile) throws IOException {
		Set<String> names= new HashSet<>();
		for(String line:FileUtils.readLines(userNameFile)){
			names.add(line.split("\t")[1]);
		}
		names.add("None");
		names.add("Unknown");
		return names;
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget unit) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		
		Users users=JCasUtil.selectCovered(jcas, Users.class,unit).iterator().next();
		String author=users.getAuthor();
//		String author=getAuthor(tokens);
		// the one who is referred
		String referee = users.getReferee();
//		String referee = getReferee(tokens);
		
//		System.out.println(unit.getCoveredText());
//		System.out.println(author+ " "+ referee);
		for(String name: userNames){
			if(name.equals(author)){
				featList.add(new Feature("AUTHOR_"+name,1));
			}else{
				featList.add(new Feature("AUTHOR_"+name,0));
			}
			if(name.equals(referee)){
				featList.add(new Feature("REFEREE_"+name,1));
			}else{
				featList.add(new Feature("REFEREE_"+name,0));
			}
		}
		
		return featList;
	}

//	private String getReferee(List<Token> tokens) {
//		String authorCand=tokens.get(2).getCoveredText();
//		if(authorCand.startsWith("+User_")){
//			return authorCand.replace("+", "");
//		}
//		if(authorCand.startsWith("User_")){
//			return authorCand;
//		}
//		return "None";
//	}
//
//	private String getAuthor(List<Token> tokens) {
//		String authorCand=tokens.get(1).getCoveredText();
//		if(authorCand.startsWith("User_")){
//			return authorCand;
//		}
//		return "Unknown";
//	}

}
