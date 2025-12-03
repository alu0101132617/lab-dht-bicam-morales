package es.ull.esit.app.evolutionary_algorithms.complement;


import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that implements the one-point mutation operator.
 */
public class OnePointMutation extends Mutation {

  /**
   * Applies the one-point mutation operation on a given state with a specified mutation probability.
   * @param state [State] the state to be mutated.
   * @param pm [double] the mutation probability.
   * @return [State] the mutated state.
   */
	@Override
	public State mutation(State state, double pm) {
		double probM = ThreadLocalRandom.current().nextDouble();
		if(pm >= probM)
		{
			int index =  Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
			Object value = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue(index);
			state.getCode().set(index, value);
		}
		return state;
	}
}
