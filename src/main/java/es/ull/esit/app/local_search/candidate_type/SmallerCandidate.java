/**
 * @(#) SmallerCandidate.java
 */

package es.ull.esit.app.local_search.candidate_type;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that represents a candidate with the smallest evaluation in local search algorithms.
 */
public class SmallerCandidate extends SearchCandidate {

  /**
   * Creates a new State from the list of neighborhood states that has the smallest evaluation.
   * @param listNeighborhood [List<State>] List of neighborhood states.
   * @return [State] State with the smallest evaluation.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class is not found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If there is an illegal access.
   * @throws InvocationTargetException If the method invocation fails.
   * @throws NoSuchMethodException If the method is not found.
   * 
   */
	@Override
	public State stateSearch(List<State> listNeighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		State stateSmaller = null;
		if(listNeighborhood.size() > 1){
			double counter = 0;
			double currentCount = listNeighborhood.get(0).getEvaluation().get(0);
			for (int i = 1; i < listNeighborhood.size(); i++) {
				counter = listNeighborhood.get(i).getEvaluation().get(0);
				if (counter < currentCount) {
					currentCount = counter;
					stateSmaller = listNeighborhood.get(i);
				}
			}
		}
		else stateSmaller = listNeighborhood.get(0);
		return stateSmaller;
	}
}