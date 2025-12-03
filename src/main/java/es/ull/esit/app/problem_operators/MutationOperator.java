package es.ull.esit.app.problem_operators;

import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the mutation operator.
 */
public class MutationOperator extends Operator {

  /**
   * Generates new states by applying the mutation operator.
   * 
   * @param stateCurrent [State] Current state.
   * @param operatornumber [Integer] Number of new states to generate.
   * @return listNeigborhood [List<State>] List of new states generated.
   */
	public List<State> generatedNewState(State stateCurrent, Integer operatornumber){
		List<State> listNeigborhood = new ArrayList<>();
		for (int i = 0; i < operatornumber; i++){
			int key = Strategy.getStrategy().getProblem().getCodification().getAleatoryKey();
			Object candidate = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue(key);
			State state = (State) stateCurrent.getCopy();
			state.getCode().set(key, candidate);
			listNeigborhood.add(state);
		}
		return listNeigborhood;
	}

  /**
   * Generates random states.
   * 
   * @param operatornumber [Integer] Number of random states to generate.
   * @return listRandomStates [List<State>] List of random states generated.
   */
	@Override
	public List<State> generateRandomState(Integer operatornumber) {
    List<State> listRandomStates = new ArrayList<>();
    for (int i = 0; i < operatornumber; i++){
      State state = new State();
      for (int j = 0; j < Strategy.getStrategy().getProblem().getCodification().getVariableCount(); j++){
        Object candidate = Strategy.getStrategy().getProblem().getCodification().getVariableAleatoryValue(j);
        state.getCode().add(candidate);
      }
      listRandomStates.add(state);
    }
    return listRandomStates;
  }

}
