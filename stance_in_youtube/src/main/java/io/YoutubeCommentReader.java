package io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import com.vdurmont.emoji.EmojiParser;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase.Resource;

public class YoutubeCommentReader extends JCasResourceCollectionReader_ImplBase{

	public static final String PARAM_REPLACE_EMOJIS_WITH_ALIAS = "maskInavlidXml";
	@ConfigurationParameter(name = PARAM_REPLACE_EMOJIS_WITH_ALIAS, mandatory = true,defaultValue = "true")
	protected boolean replaceEmojisWithAlias;
	
	/**
     * Language
     */
    public static final String PARAM_LANGUAGE = "Language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true,defaultValue = "en")
	protected String language;
	
	@Override
	public void getNext(JCas aJCas) throws IOException, CollectionException {
		Resource res = nextFile();
		initCas(aJCas, res);

		//remove line breaks
		List<String> lines =IOUtils.readLines(res.getInputStream());
		lines = removeLineBreaks(lines);
		
		//add line break for each line
		String documentText=StringUtils.join(lines, System.lineSeparator());
		
		System.out.println(documentText);
		
		if(replaceEmojisWithAlias){
			aJCas.setDocumentText(maskInvalidXML(documentText));
		}else{
			aJCas.setDocumentText(documentText);
		}
		System.out.println(aJCas.getDocumentText());
	}

	private List<String> removeLineBreaks(List<String> lines) {
		List<String> removedLineBreaks= new ArrayList<>();
		for(String line: lines){
			removedLineBreaks.add(line.replaceAll( System.lineSeparator(), ""));
		}
		return removedLineBreaks;
	}

	private String maskInvalidXML(String documentText) {
		String emo_regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";
		Matcher matcher = Pattern.compile(emo_regex).matcher(documentText);
		while (matcher.find()) {
			String emoji= matcher.group();
			String replacement = EmojiParser.parseToAliases(emoji);
			documentText = documentText.replaceAll(emoji,replacement);
		    System.out.println("replaceall "+emoji+" with  "+replacement);
		}
		return documentText;
	}

}
