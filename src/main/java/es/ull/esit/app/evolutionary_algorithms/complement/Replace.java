package es.ull.esit.app.evolutionary_algorithms.complement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class representing a replacement strategy in an evolutionary algorithm.
 */
public abstract class Replace {

  /**
   * Method to replace states in the population based on a candidate state.
   * @param stateCandidate [State] The candidate state to be considered for replacement.
   * @param listState [List<State>] The current list of states in the population.
   * @return [List<State>] The updated list of states after applying the replacement strategy.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	public abstract List<State> replace(State stateCandidate, List<State>listState) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
