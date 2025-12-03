package es.ull.esit.app.problem.extension;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class for solution methods.
 */
public abstract class SolutionMethod {

  /**
   * Method to evaluate a state.
   * 
   * @param state [State] State to be evaluated.
   */
	public abstract void evaluationState(State state);
}
