package es.ull.esit.app.local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class representing an acceptable candidate strategy for candidate states. 
 */
public abstract class AcceptableCandidate {
  /**
   * Abstract method to determine if a candidate state is acceptable.
   * @param stateCurrent [State] Current state.
   * @param stateCandidate [State] Candidate state to evaluate.
   * @return [Boolean] True if the candidate state is acceptable, false otherwise.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a specified class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a specified method cannot be found.
   */
	public abstract Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
