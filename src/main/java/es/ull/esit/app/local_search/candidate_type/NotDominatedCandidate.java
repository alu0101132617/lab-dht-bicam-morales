package es.ull.esit.app.local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import es.ull.esit.app.local_search.acceptation_type.Dominance;
import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that represents a candidate not dominated in local search algorithms.
 */
public class NotDominatedCandidate extends SearchCandidate {

  /**
   * Creates a new State from the list of neighborhood states that is not dominated by any other.
   * @param listNeighborhood [List<State>] List of neighborhood states.
   * @return [State] State not dominated by any other.
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
		State state = new State();
		State stateA = listNeighborhood.get(0);
		boolean stop = false;
		if(listNeighborhood.size() == 1){
			state = stateA;
		}
		else {
			Strategy.getStrategy().getProblem().evaluate(stateA);
			State stateB;
			Dominance dominance = new Dominance();
			for (int i = 1; i < listNeighborhood.size(); i++) {
				while(!stop){
					stateB = listNeighborhood.get(i);
					Strategy.getStrategy().getProblem().evaluate(stateB);
					if(dominance.dominance(stateB, stateA)){
						stateA = stateB;
					}else{
						stop = true;
						state = stateA;
					}
				}
			}
		}
		return state;
	}

}
