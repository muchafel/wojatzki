package assertionRegression.io;

import static org.dkpro.tc.core.util.ReportUtils.getDiscriminatorValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dkpro.lab.reporting.BatchReportBase;
import org.dkpro.lab.reporting.FlexTable;
import org.dkpro.lab.reporting.ReportBase;
import org.dkpro.lab.storage.StorageService;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.lab.storage.impl.PropertiesAdapter;
import org.dkpro.lab.task.Task;
import org.dkpro.lab.task.TaskContextMetadata;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.util.ReportUtils;
import org.dkpro.tc.core.util.TcFlexTable;
import org.dkpro.tc.evaluation.Id2Outcome;
import org.dkpro.tc.ml.report.TcTaskTypeUtil;
import org.dkpro.tc.util.EvaluationReportUtil;

public class FoldReport extends BatchReportBase implements Constants {
	boolean softEvaluation = true;
	boolean individualLabelMeasures = false;

	@Override
	public void execute() throws Exception {

		StorageService store = getContext().getStorageService();

		TcFlexTable<String> table = TcFlexTable.forClass(String.class);

		for (TaskContextMetadata subcontext : getSubtasks()) {
			if (!TcTaskTypeUtil.isCrossValidationTask(store, subcontext.getId())) {
				continue;
			}
			Map<String, String> discriminatorsMap = ReportUtils.getDiscriminatorsForContext(store, subcontext.getId(),
					Constants.DISCRIMINATORS_KEY_TEMP);

			File fileToEvaluate = store.locateKey(subcontext.getId(),
					Constants.TEST_TASK_OUTPUT_KEY + "/" + Constants.SERIALIZED_ID_OUTCOME_KEY);

			Map<String, String> resultMap = EvaluationReportUtil.getResultsHarmonizedId2Outcome(fileToEvaluate,
					softEvaluation, individualLabelMeasures);
			
			for(String key: resultMap.keySet()) {
				System.out.println(key+ " "+resultMap.get(key));
			}

			Map<String, String> values = new HashMap<String, String>();
			values.putAll(discriminatorsMap);
			values.putAll(resultMap);

			table.addRow(subcontext.getLabel(), values);
		}
	}

}
