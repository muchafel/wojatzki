package de.uni_due.ltl.catalanStanceDetection.dl_Util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class JCASConatiner {

	private String outcome;
	private int id;
	private String text;
	
	public JCASConatiner(JCas jCas) {
		for (TextClassificationOutcome outcome : JCasUtil.select(jCas, TextClassificationOutcome.class)) {
			this.outcome=outcome.getOutcome();
		}
		this.id=Integer.valueOf(JCasUtil.selectSingle(jCas, DocumentMetaData.class).getDocumentId());
		this.text=getWhitespaceSpearatedTokens(jCas); 
	}

	private  String getWhitespaceSpearatedTokens(JCas jcas) {
		List<String> tokenTexts = new ArrayList<>();
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			tokenTexts.add(t.getCoveredText());
		}
		return StringUtils.join(tokenTexts, " ");
	}

	public String getOutcome() {
		return outcome;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}
	
	
}
