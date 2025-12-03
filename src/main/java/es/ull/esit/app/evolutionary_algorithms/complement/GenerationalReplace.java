package es.ull.esit.app.evolutionary_algorithms.complement;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the generational replacement strategy.
 * 
 */
public class GenerationalReplace extends Replace {

  /**
   * Replaces the worst state in the population with the candidate state.
   * @param stateCandidate [State] the candidate state to be added to the population.
   * @param listState [List<State>] the current population of states.
   * @return [List<State>] the updated population of states after replacement.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	@Override
	public List<State> replace(State stateCandidate, List<State> listState) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		listState.remove(0);
		listState.add(stateCandidate);
		
		return listState;
	}
}
