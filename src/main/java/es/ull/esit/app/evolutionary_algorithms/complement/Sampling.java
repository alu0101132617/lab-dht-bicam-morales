package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;

import es.ull.esit.app.problem.definition.State;

/**
 * Abstract class defining the sampling method for selecting individuals in an evolutionary algorithm.
 */
public abstract class Sampling {
  /**
   * Samples individuals from the given list of fathers.
   * @param fathers [List<State>] The list of father states (individuals) to sample from.
   * @param countInd [int] The number of individuals to sample.
   * @return [List<State>] The list of sampled individuals.
   */
	public abstract List<State> sampling (List<State> fathers, int countInd);
}
