package de.uni_due.ltl.simpleClassifications;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import preprocessing.CommentText;
import preprocessing.CommentType;
import preprocessing.Users;

public class FunctionalPartsAnnotator extends JCasAnnotator_ImplBase{

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for(TextClassificationTarget unit: JCasUtil.select(jcas, TextClassificationTarget.class)){
			List<Token> tokens=JCasUtil.selectCovered(jcas, Token.class,unit);
			
			//set comment type (comment vs. reply)
			boolean isComment=isComment(tokens);
			CommentType typeAnno= new CommentType(jcas,unit.getBegin(),unit.getEnd());
			typeAnno.setCommentNotReply(isComment);
			typeAnno.addToIndexes();
			
			//set author and referee
			String author=getAuthor(tokens);
			// the one who is referred
			String referee = getReferee(tokens);
			Users usersAnno= new Users(jcas,unit.getBegin(),unit.getEnd());
			usersAnno.setAuthor(author);
			usersAnno.setReferee(referee);
			usersAnno.addToIndexes();
			
			CommentText textAnno= new CommentText(jcas);
			int commentTextStart= getCommentTextBegin(tokens,referee);
			textAnno.setBegin(commentTextStart);
			textAnno.setEnd(unit.getEnd());
			textAnno.addToIndexes();
		}
	}
	private int getCommentTextBegin(List<Token> tokens, String referee) {
//		System.out.println(referee);
//		System.out.println(getList(tokens));
		if(referee.equals("None")){
			return tokens.get(2).getBegin();
		}
		if(tokens.size()>3){
			return tokens.get(3).getBegin();
		}
		else{
			return tokens.get(2).getEnd();
		}
	}

	/**
	 * if the text starts with a --> it is a reply
	 * @param tokens
	 * @return
	 */
	private boolean isComment(List<Token> tokens) {
		if(tokens.get(0).getCoveredText().equals("-->")){
			return false;
		}
		return true;
	}

	private String getReferee(List<Token> tokens) {
		String authorCand=tokens.get(2).getCoveredText();
		if(authorCand.startsWith("+User_")){
			return authorCand.replace("+", "");
		}
		if(authorCand.startsWith("User_")){
			return authorCand;
		}
		return "None";
	}

	private String getAuthor(List<Token> tokens) {
		String authorCand=tokens.get(1).getCoveredText();
		if(authorCand.startsWith("User_")){
			return authorCand;
		}
		return "Unknown";
	}
	
	private static List<String> getList(List<Token> selectCovered) {
		List<String> result= new ArrayList<>();
		for(Token token: selectCovered){
			result.add(token.getCoveredText());
		}
		return result;
	}
}
