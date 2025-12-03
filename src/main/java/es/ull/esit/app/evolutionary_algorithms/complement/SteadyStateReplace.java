package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class that implements the steady state replacement method.
 */
public class SteadyStateReplace extends Replace {

  /**
   * Replaces a state in the population using the steady state replacement method.
   * @param stateCandidate [State] The candidate state to be considered for replacement.
   * @param listState [List<State>] The current population of states.
   * @return [List<State>] The updated population of states after replacement.
   */
	@Override
	public List<State> replace(State stateCandidate, List<State> listState) {
		ProblemType type = Strategy.getStrategy().getProblem().getTypeProblem();
		State stateREP = type.equals(ProblemType.MAXIMIZAR) ? minValue(listState) : maxValue(listState);
		
		double candidateEval = stateCandidate.getEvaluation().get(0);
		double repEval = stateREP.getEvaluation().get(0);
		boolean shouldReplace = type.equals(ProblemType.MAXIMIZAR) ? (candidateEval >= repEval) : (candidateEval <= repEval);
		
		if (shouldReplace) {
			int index = listState.indexOf(stateREP);
			if (index >= 0) {
				listState.set(index, stateCandidate);
			}
		}
		return listState;
	}
	
  /**
   * Finds the state with the minimum evaluation value in a list of states.
   * @param listState [List<State>] The list of states to search.
   * @return [State] The state with the minimum evaluation value.
   */
	public State minValue (List<State> listState){
		State value = listState.get(0);
		double min = listState.get(0).getEvaluation().get(0);
		for (int i = 1; i < listState.size(); i++) {
			if(listState.get(i).getEvaluation().get(0) < min){
				min = listState.get(i).getEvaluation().get(0);
				value = listState.get(i);
			}
		}
		return value;
	}
  
  /**
   * Finds the state with the maximum evaluation value in a list of states.
   * @param listState [List<State>] The list of states to search.
   * @return [State] The state with the maximum evaluation value.
   */
	public State maxValue (List<State> listState){
		State value = listState.get(0);
		double max = listState.get(0).getEvaluation().get(0);
		for (int i = 1; i < listState.size(); i++) {
			if(listState.get(i).getEvaluation().get(0) > max){
				max = listState.get(i).getEvaluation().get(0);
				value = listState.get(i);
			}
		}
		return value;
	}
}
