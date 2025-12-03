package es.ull.esit.app.local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class defining the interface for candidate selection strategies in local search.
 */
public abstract class SearchCandidate {
  
  /**
   * Method to select a state from the list of neighboring states. 
   * @param listNeighborhood [List<State>] List of neighboring states.
   * @return [State] Selected state from the list.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an object cannot be instantiated.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
	public abstract State stateSearch(List<State> listNeighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
