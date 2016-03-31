package dataInspection;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.features.ngram.util.NGramUtils;
import io.TaskATweetReader;
import mulan.examples.GettingPredictionsOnUnlabeledData;
import types.OriginalResource;
import types.StanceAnnotation;
import util.CollocationMeasureHelper;

public class WordingInspection {

	public static void main(String[] args) throws IOException, ResourceInitializationException {

		String baseDir = DkproContext.getContext().getWorkspace().getAbsolutePath();
		String target = "Atheism";

		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(TaskATweetReader.class,
				TaskATweetReader.PARAM_SOURCE_LOCATION, baseDir + "/semevalTask6/tweetsTaskA",
				TaskATweetReader.PARAM_PATTERNS, "*.xml", TaskATweetReader.PARAM_LANGUAGE, "en",
				TaskATweetReader.PARAM_MEMORIZE_RESOURCE, true);

		
		FrequencyDistribution<String> favour = new FrequencyDistribution<String>();
		FrequencyDistribution<String> against = new FrequencyDistribution<String>();
		FrequencyDistribution<String> none = new FrequencyDistribution<String>();
		for (JCas jcas : new JCasIterable(reader)) {
			if (JCasUtil.select(jcas, StanceAnnotation.class).iterator().next().getTarget().equals(target)) {
				if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("FAVOR")) {
					System.out.println(jcas.getDocumentText());
					favour.incAll(Arrays.asList(jcas.getDocumentText().split(" ")));
				} else if (JCasUtil.select(jcas, TextClassificationOutcome.class).iterator().next().getOutcome()
						.equals("AGAINST")) {
					against.incAll(Arrays.asList(jcas.getDocumentText().split(" ")));
				} else {
					none.incAll(Arrays.asList(jcas.getDocumentText().split(" ")));
				}
			}
		}
		System.out.println(favour.getN());
		System.out.println(against.getN());
		System.out.println(none.getN());
		CollocationMeasureHelper helper= new CollocationMeasureHelper(favour, against);
		for (String check : favour.getMostFrequentSamples(400)) {
//			System.out.println(check+" pos g-mean: "+helper.getGMeanPositive(check)+ "|  neg g-mean: "+helper.getGMeanNegative(check));
			System.out.println(check+" normalized freq pos "+normalize(check,favour)+ "|  ormalized freq pos "+normalize(check,against));
			
			// String check= "God";
//			double inFavourCount= normalize(check,favour);
//			double againstCount= normalize(check,against);
//			double noneCount= normalize(check,none);
//			if(againstIsDoubleFavour(inFavourCount,againstCount)){
//			System.out.println(check + " favor:" + round(inFavourCount) + " against:" + round(againstCount)
//					+ "  none:" + round(noneCount));
//			}
		}
	}

	private static double normalize(String check, FrequencyDistribution<String> favour) {
		double normalized= (double)favour.getCount(check)/favour.getN();
//		normalized=normalized*100;
		return normalized;
	}

	private static boolean againstIsDoubleFavour(double favor, double against) {
		if(favor*2<against)return true;
		return false;
	}

	private static double round(double d) {
		return Double.parseDouble(new DecimalFormat("#.####").format(d));
	}
}
