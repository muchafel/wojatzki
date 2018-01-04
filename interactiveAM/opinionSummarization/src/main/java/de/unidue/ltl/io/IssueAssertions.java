package de.unidue.ltl.io;

import java.util.List;
import java.util.Set;

public class IssueAssertions {
	List<String> assertion_list;
	Set<String> assertion_set;
	
	Set<String> setOfVariables;

	public List<String> getAssertion_list() {
		return assertion_list;
	}

	public void setAssertion_list(List<String> assertion_list) {
		this.assertion_list = assertion_list;
	}

	public Set<String> getAssertion_set() {
		return assertion_set;
	}

	public void setAssertion_set(Set<String> assertion_set) {
		this.assertion_set = assertion_set;
	}

	public Set<String> getSetOfVariables() {
		return setOfVariables;
	}

	public IssueAssertions(List<String> assertion_list, Set<String> assertion_set, Set<String> setOfVariables) {
		this.assertion_list = assertion_list;
		this.assertion_set = assertion_set;
		this.setOfVariables = setOfVariables;
	}

	public void setSetOfVariables(Set<String> setOfVariables) {
		this.setOfVariables = setOfVariables;
	}
	
}
