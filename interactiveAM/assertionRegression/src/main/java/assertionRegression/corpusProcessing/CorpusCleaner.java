package assertionRegression.corpusProcessing;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetTokenizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class CorpusCleaner {
	
	protected AnalysisEngine engine;
	
	public static void main(String[] args) throws IOException, UIMAException {
		CorpusCleaner cleaner= new CorpusCleaner();
		cleaner.clean("/Users/michael/Desktop/statuses");
	}

	public CorpusCleaner(){
		this.engine=getTokenizerEngine();
	}
	
	private void clean(String path) throws IOException, UIMAException {
		File folder= new File(path);
		for(File file: folder.listFiles()){
			if(file.getName().endsWith(".txt")){
				File writeTo=new File(folder, file.getName().substring(0, file.getName().length()-4)+"_cleaned.txt");
				for(String line: FileUtils.readLines(file, "UTF-8")){
					
//					System.out.println(line);
					String[] parts=line.split("\t");
					if(!line.isEmpty() && parts.length>2){
						//if line != retweet
						if(parts[2].equals("false")){
							String tokenized=getTokens(parts[1]);
							FileUtils.write(writeTo, tokenized+"\n","UTF-8",true);
						}
					}
				}
			}
		}
		
	}

	private String getTokens(String text) throws UIMAException {
		JCas jcas = JCasFactory.createText(text, "en");
		engine.process(jcas);
		List<String> tokens= JCasUtil.toText(JCasUtil.select( jcas, Token.class));
		String tokenString = StringUtils.join(tokens," ");
		return tokenString;
	}
	
	protected AnalysisEngine getTokenizerEngine() {
		AggregateBuilder builder = new AggregateBuilder();
		AnalysisEngine engine = null;
		try {
			builder.add(createEngineDescription(createEngineDescription(ArktweetTokenizer.class)));
			engine = builder.createAggregate();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return engine;
	}
}
