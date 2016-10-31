package ml_experiments.embeddingExperiments;

import org.dkpro.lab.engine.TaskContext;
import org.dkpro.lab.task.impl.DefaultBatchTask;

public class ExampleBatchTask extends DefaultBatchTask {
	@Override
    public void initialize(TaskContext aContext)
    {
        super.initialize(aContext);
        
//        PreprocessingTask preprocessingTask = new PreprocessingTask();
        
        CheckEmbeddingResources checkResourcesTask = new CheckEmbeddingResources();
        ClassificationUsingEmbeddingsTask analysisTask = new ClassificationUsingEmbeddingsTask();
//        analysisTask.addImport(preprocessingTask, PreprocessingTask.OUTPUT_FOLDER);
        
//        addTask(preprocessingTask);
        addTask(checkResourcesTask);
        addTask(analysisTask);
    }
}
