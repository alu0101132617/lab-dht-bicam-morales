/**
 * @(#) FactoryCandidate.java
 */
package main.java.es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.local_search.candidate_type.CandidateType;
import main.java.es.ull.esit.app.local_search.candidate_type.SearchCandidate;

import main.java.es.ull.esit.app.factory_interface.IFFactoryCandidate;



public class FactoryCandidate implements IFFactoryCandidate{
	private SearchCandidate searchcandidate;
	
	public SearchCandidate createSearchCandidate(CandidateType typeCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "local_search.candidate_type." + typeCandidate.toString();
		searchcandidate = (SearchCandidate) FactoryLoader.getInstance(className);
		return searchcandidate;
	}
}
