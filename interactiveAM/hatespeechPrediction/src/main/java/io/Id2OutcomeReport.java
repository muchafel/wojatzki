package io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dkpro.lab.reporting.BatchReportBase;
import org.dkpro.lab.storage.StorageService;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.lab.task.TaskContextMetadata;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.task.TcTaskTypeUtil;
import org.dkpro.tc.io.libsvm.LibsvmDataFormatWriter;
import org.dkpro.tc.ml.report.TcBatchReportBase;


public class Id2OutcomeReport extends TcBatchReportBase implements Constants {


	@Override
	public void execute() throws Exception {
		List<String> result = new ArrayList<>();
		StorageService store = getContext().getStorageService();

		for (TaskContextMetadata subcontext : store.getContexts()) {
			if (TcTaskTypeUtil.isCrossValidationTask(store, subcontext.getId())) {
				File attributes = store.locateKey(subcontext.getId(), "ATTRIBUTES.txt");
				List<String> foldersOfSingleRuns = getFoldersOfSingleRuns(attributes);
				for (String file : foldersOfSingleRuns) {
//					System.out.println(file);
					File predFile = store.locateKey(file, "id2outcome.txt");
					result = addAllPredictions(FileUtils.readLines(predFile, "utf-8"), result);
				}
				break;
			}
		}
		for (TaskContextMetadata subcontext : getSubtasks()) {
			if (subcontext.getId().startsWith("ExperimentCrossValidation")||subcontext.getId().startsWith("DeepLearningExperimentCrossValidation")) {
				File predFile = store.locateKey(subcontext.getId(), "id2outcome.txt");
				FileUtils.writeLines(predFile, result);
			}
		}

	}
    
    
    private List<String> addAllPredictions(List<String> readLines, List<String> existingPredictions) {
		for(String pred: readLines) {
			if(pred.startsWith("#"))continue;
			existingPredictions.add(pred);
		}
		return existingPredictions;
	}


	private List<String> getFoldersOfSingleRuns(File attributesTXT) throws Exception{
        List<String> readLines = FileUtils.readLines(attributesTXT);
        int idx = 0;
        for (String line : readLines) {
        		if (line.startsWith("Subtask")) {
        			break; 
        		} 
        		idx++;
        	}
        String line = readLines.get(idx);
        int start = line.indexOf("[") + 1;
        int end = line.indexOf("]");
        String subTasks = line.substring(start, end);

        String[] tasks = subTasks.split(",");

        List<String> results = new ArrayList<>();

        for (String task : tasks) {
            if (TcTaskTypeUtil.isMachineLearningAdapterTask(getContext().getStorageService(),
            		task.trim())) {
                results.add(task.trim());
            }
        }

        return results;
    }
    
    

private Map<String, String> getMapping(boolean isUnit) throws IOException {
		
		File f;
		if (isUnit) {
			f = new File(getContext().getFolder(TEST_TASK_INPUT_KEY_TEST_DATA, AccessMode.READONLY),
					LibsvmDataFormatWriter.INDEX2INSTANCEID);
		} else {
			f = new File(getContext().getFolder(TEST_TASK_INPUT_KEY_TEST_DATA, AccessMode.READONLY),
					Constants.FILENAME_DOCUMENT_META_DATA_LOG);
		}
		
		Map<String, String> m = new HashMap<>();

		int idx=0;
		for (String l : FileUtils.readLines(f, "utf-8")) {
			if (l.startsWith("#")) {
				continue;
			}
			if (l.trim().isEmpty()) {
				continue;
			}
			String[] split = l.split("\t");

//			if (isUnit) {
				m.put(idx + "", split[0]);
				idx++;
//			} else {
//				m.put(split[0], split[1]);
//			}

		}
		return m;
	}

    private File getId2OutcomeFileLocation()
    {
        File evaluationFolder = getContext().getFolder("", AccessMode.READWRITE);
        return new File(evaluationFolder, ID_OUTCOME_KEY);
    }

	private List<String> readPredictions() throws IOException {

		List<String> result = new ArrayList<>();
		for (TaskContextMetadata subcontext : getSubtasks()) {
			System.out.println(subcontext.getLabel()+ " "+subcontext.getId()+ " "+TcTaskTypeUtil.isMachineLearningAdapterTask(getContext().getStorageService(), subcontext.getId()));
			if(TcTaskTypeUtil.isMachineLearningAdapterTask(getContext().getStorageService(), subcontext.getId())){
				System.out.println(subcontext.getLabel()+ " "+subcontext.getId());
				File predFolder = getContext().getFolder(subcontext.getId(), AccessMode.READWRITE);
				System.out.println(predFolder.getAbsolutePath());
				String predFileName = "id2outcome.txt";
				System.out.println(predFileName);
				result.addAll(FileUtils.readLines(new File(predFolder, predFileName), "utf-8"));
			}

		}
		System.out.println("done");
		return result;
	}

    private String buildHeader()
        throws UnsupportedEncodingException
    {
        StringBuilder header = new StringBuilder();
        header.append("ID=PREDICTION;GOLDSTANDARD;THRESHOLD" + "\n" + "labels" + " ");
        return header.toString();
    }


}
