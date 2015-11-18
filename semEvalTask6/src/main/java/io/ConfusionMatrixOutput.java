package io;

import java.io.File;
import java.util.Properties;

import de.tudarmstadt.ukp.dkpro.lab.reporting.ReportBase;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.tc.weka.task.WekaTestTask;
import weka.core.SerializationHelper;

public class ConfusionMatrixOutput extends ReportBase {

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
		System.out.println("F(weighted): "+eval.weightedFMeasure());
	}

}
