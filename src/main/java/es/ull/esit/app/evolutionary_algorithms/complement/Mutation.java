package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class that defines the mutation operator.
 */
public abstract class Mutation {
	
  /**
   * Applies mutation to a given state with a specified mutation probability.
   * @param state [State] the state to be mutated
   * @param pm [double] the mutation probability
   * @return [State] the mutated state
   */
	public abstract State mutation (State state, double pm);

}
