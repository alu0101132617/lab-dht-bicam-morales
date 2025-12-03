package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;
import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class that defines the distribution operator.
 */
public abstract class Distribution {
  /**
   * Applies the distribution operation to a list of father states.
   * @param fathers [List<State>] the list of father states.
   * @return [List<Probability>] the list of probabilities resulting from the distribution.
   */
	public abstract List<Probability> distribution(List<State> fathers);

}
