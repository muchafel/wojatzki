package io;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.dkpro.lab.reporting.ReportBase;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.evaluation.Id2Outcome;
import org.dkpro.tc.evaluation.evaluator.EvaluatorBase;
import org.dkpro.tc.evaluation.evaluator.EvaluatorFactory;
import org.dkpro.tc.weka.task.WekaTestTask;
import weka.core.SerializationHelper;

public class ConfusionMatrixOutput extends ReportBase implements Constants{

	@SuppressWarnings("deprecation")
	@Override
	public void execute() throws Exception {
		File storage = getContext().getStorageLocation(
				WekaTestTask.TEST_TASK_OUTPUT_KEY, AccessMode.READONLY);

		Properties props = new Properties();

		File evaluationFile = new File(storage.getAbsolutePath() + "/evaluation.bin");

		weka.classifiers.Evaluation eval = (weka.classifiers.Evaluation) SerializationHelper
				.read(evaluationFile.getAbsolutePath());
		System.out.println(eval.toMatrixString());
		System.out.println("F(a): "+eval.fMeasure(0));
		System.out.println("F(b): "+eval.fMeasure(1));
//		System.out.println("F(c): "+eval.fMeasure(2));
		System.out.println("F(weighted): "+eval.weightedFMeasure());
		System.out.println("(F(a)+F(b))/2: "+(eval.fMeasure(0)+eval.fMeasure(1))/2);
	}

}
