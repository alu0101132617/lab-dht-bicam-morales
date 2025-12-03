package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class that defines the crossover operator.
 */
public abstract class Crossover {
	
  /**
   * Applies the crossover operation between two parent states with a given crossover probability.
   * @param father1 [State] the first parent state.
   * @param father2 [State] the second parent state.
   * @param pc [double] the crossover probability.
   * @return [State] the resulting offspring state after crossover.
   */
	public abstract State crossover(State father1, State father2, double pc);
}
