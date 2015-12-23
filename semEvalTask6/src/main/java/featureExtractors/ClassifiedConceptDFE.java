package featureExtractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.DocumentFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import de.tudarmstadt.ukp.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import util.SimilarityHelper;

public class ClassifiedConceptDFE extends FeatureExtractorResource_ImplBase implements DocumentFeatureExtractor {

	Map<String, Map<String, String>> conceptToId2Outcome;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		// TODO paramterisize!!!!!
		conceptToId2Outcome = readConceptid2Outcome("src/main/resources/concepts/FeministMovement");
		return true;
	}

	private Map<String, Map<String, String>> readConceptid2Outcome(String path) {
		List<File> concepts = getConcepts(path);
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		for (File concept : concepts) {
			try {
				// TODO paramterisize!!!!!
				result.put(concept.getName().replace(".txt", ""), readPrediction("src/main/resources/concepts/FeministMovement/" + concept.getName()));
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private List<File> getConcepts(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<File> files = new ArrayList<File>();
		for (File f : listOfFiles) {
			if (!f.isDirectory())
				files.add(f);
		}
		return files;
	}

	private static Map<String, String> readPrediction(String path)
			throws NumberFormatException, UnsupportedEncodingException, IOException {
		HashMap<String, String> prediction = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line = "";
		List<String> labelList = null;
		while ((line = br.readLine()) != null) {
			// this needs to happen at the beginning of the loop
			if (line.startsWith("#labels")) {
				labelList = getLabels(line);
			} else if (!line.startsWith("#")) {
				if (labelList == null) {
					br.close();
					throw new IOException("Wrong file format.");
				}
				// line might contain several '=', split at the last one
				int idxMostRightHandEqual = line.lastIndexOf("=");
				String evaluationData = line.substring(idxMostRightHandEqual + 1);
				String id = line.split("=")[0];
				String[] splittedEvaluationData = evaluationData.split(";");
				String[] predictionS = splittedEvaluationData[0].split(",");

				for (int i = 0; i < predictionS.length; i++) {
					if (predictionS[i].equals("1")) {
						// System.out.println("prediction " + id + " " +
						// labelList.get(i));
						prediction.put(id, labelList.get(i));
					}
				}
			}
		}
		br.close();

		return prediction;
	}

	public static List<String> getLabels(String line) throws UnsupportedEncodingException {
		String[] numberedClasses = line.split(" ");
		List<String> labels = new ArrayList<String>();

		// filter #labels out and collect labels
		for (int i = 1; i < numberedClasses.length; i++) {
			// split one more time and take just the part with class name
			// e.g. 1=NPg, so take just right site
			String className = numberedClasses[i].split("=")[1];
			labels.add(URLDecoder.decode(className, "UTF-8"));
		}
		return labels;
	}

	@Override
	public Set<Feature> extract(JCas jcas) throws TextClassificationException {
		Set<Feature> features= new HashSet<Feature>();
		int summedConceptPolarity=0;
		int numberOfPositiveConcepts=0;
		int numberOfNegativeConcepts=0;
		for (String concept : conceptToId2Outcome.keySet()) {
			int polarity=0;
			if (conceptContained(concept, jcas)) {
//				System.out.println(concept+ " similar to "+ jcas.getDocumentText());
//				System.out.println(conceptToId2Outcome.get(concept).get(DocumentMetaData.get(jcas).getDocumentId()));
				if (conceptToId2Outcome.get(concept).get(DocumentMetaData.get(jcas).getDocumentId())!=null && conceptToId2Outcome.get(concept).get(DocumentMetaData.get(jcas).getDocumentId()).equals("FAVOR")){
					polarity=1;
					summedConceptPolarity+=1;
					numberOfPositiveConcepts++;
				}else if(conceptToId2Outcome.get(concept).get(DocumentMetaData.get(jcas).getDocumentId())!=null && conceptToId2Outcome.get(concept).get(DocumentMetaData.get(jcas).getDocumentId()).equals("AGAINST")){
					polarity=-1;
					summedConceptPolarity+=-1;
					numberOfNegativeConcepts++;
				}
			}
			features.add(new Feature("concept_"+concept, polarity));
		}
		features.add(new Feature("summedConceptPolarity", summedConceptPolarity));
		features.add(new Feature("numberOfPositveConcepts", numberOfPositiveConcepts));
		features.add(new Feature("numberOfNegativeConcepts", numberOfNegativeConcepts));
		return features;
	}

	private boolean conceptContained(String concept, JCas jcas) {
		for (Token t : JCasUtil.select(jcas, Token.class)) {
			if (t.getPos().getPosValue().equals("NN") || t.getPos().getPosValue().equals("NNS")
					|| t.getPos().getPosValue().equals("NP") || t.getPos().getPosValue().equals("NPS")
					|| t.getPos().getPosValue().equals("NPS")) {
//				System.out.println(t.getCoveredText()+ " "+concept);
				if (t.getCoveredText().toLowerCase().equals(concept)|| SimilarityHelper.wordsAreSimilar(t.getCoveredText(), concept)) {
					return true;
				}
			}
		}
		return false;
	}

}
