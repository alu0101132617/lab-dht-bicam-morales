package es.ull.esit.app.evolutionary_algorithms.complement;


import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that represents a two points mutation operator.
 */
public class TowPointsMutation extends Mutation {
  
  /**
   * Applies the two points mutation to the given individual with a certain probability.
   * @param newind [State] The individual to be mutated.
   * @param pm [double] The mutation probability.
   * @return [State] The mutated individual.
   */
	@Override
	public State mutation(State newind, double pm) {
		Object key1 = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
		Object key2 = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
		Object value1 = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue((Integer) key1);
		Object value2 = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue((Integer) key2);
		newind.getCode().set((Integer) key1, (Integer)value2);
		newind.getCode().set((Integer) key2, (Integer)value1);
		return newind;
	}
}
  