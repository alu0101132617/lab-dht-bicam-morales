/**
 * @(#) SearchCandidate.java
 */

package main.java.es.ull.esit.app.local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import main.java.es.ull.esit.app.problem.definition.State;


public abstract class SearchCandidate {
	
	public abstract State stateSearch(List<State> listNeighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
