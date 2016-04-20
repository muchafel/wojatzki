package annotationStudy.evaluation;

import java.util.List;
import java.util.Map;

import webanno.custom.Stance;

public class AnnotatedDocument {

	private String id;
	private Map<String,List<StanceContainer>> annotatorToAnnotations;
	
	
	public AnnotatedDocument(String id, Map<String, List<StanceContainer>> annotatorToAnnotations) {
		this.id = id;
		this.annotatorToAnnotations = annotatorToAnnotations;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, List<StanceContainer>> getAnnotatorToAnnotations() {
		return annotatorToAnnotations;
	}
	public void setAnnotatorToAnnotations(Map<String, List<StanceContainer>> annotatorToAnnotations) {
		this.annotatorToAnnotations = annotatorToAnnotations;
	}
	
	
}
