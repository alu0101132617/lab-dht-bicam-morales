package es.ull.esit.app.evolutionary_algorithms.complement;


import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class that represents the Truncation Selection method.
 * 
 */
public class TruncationSelection extends FatherSelection {
	
  /**
   * Orders the list of states from best to worst.
   * @param listState [List<State>] List of states to be ordered.
   * @return [List<State>] Ordered list of states.
   */
	public List<State> orderBetter (List<State> listState){
		State actualState = null;
		for (int i = 0; i < listState.size()- 1; i++) {
			for (int j = i+1; j < listState.size(); j++) {
				if(listState.get(i).getEvaluation().get(0) < listState.get(j).getEvaluation().get(0)){
					actualState = listState.get(i);
					listState.set(i, listState.get(j));
					listState.set(j,actualState);
				}
			}
		}
		return listState;
	}
	
  /**
   * Orders the list of states from worst to best.
   * @param listState [List<State>] List of states to be ordered.
   * @return [List<State>] Ordered list of states.
   */
	public List<State> ascOrderBetter (List<State> listState){
		State actualState = null;
		for (int i = 0; i < listState.size()- 1; i++) {
			for (int j = i+1; j < listState.size(); j++) {
				if(listState.get(i).getEvaluation().get(0) > listState.get(j).getEvaluation().get(0)){
					actualState = listState.get(i);
					listState.set(i, listState.get(j));
					listState.set(j,actualState);
				}
			}
		}
		return listState;
	}
  
  /**
   * Selects the best states from the list according to the truncation value.
   * @param listState [List<State>] List of states to select from.
   * @param truncation [int] Number of states to select.
   * @return [List<State>] Selected list of states.
   */
	@Override
	public List<State> selection(List<State> listState, int truncation) {
		List<State> auxList = new ArrayList<>();
		if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
			listState = orderBetter(listState);
		} else {
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MINIMIZAR))
				listState = ascOrderBetter(listState);
		}
		int i = 0;
		while(auxList.size()< truncation){
			auxList.add(listState.get(i));
			i++;
		}
		return auxList;
	}
}
