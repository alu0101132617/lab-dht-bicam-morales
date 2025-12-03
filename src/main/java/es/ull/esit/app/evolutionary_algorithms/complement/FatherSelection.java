package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;
import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class that defines the father selection operator.
 */
public abstract class FatherSelection {
	/**
   * Selects a list of father states from the given list of states using truncation selection.
   * @param listState [List<State>] the list of states to select from.
   * @param truncation [int] the truncation parameter.
   * @return [List<State>] the list of selected father states.
   */
	public abstract List<State> selection(List<State> listState, int truncation);
}
