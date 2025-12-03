package es.ull.esit.app.problem.extension;

import java.util.ArrayList;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.ObjetiveFunction;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class that implements the pure multi-objective solution method.
 */
public class MultiObjetivoPuro extends SolutionMethod {

  /**
   * Constructor.
   * 
   * @param name [String] Name of the solution method.
   */
	@Override
	public void evaluationState(State state) {
		double tempEval = -1;
		ArrayList<Double> evaluation = new ArrayList<>(Strategy.getStrategy().getProblem().getFunction().size());
		for (int i = 0; i < Strategy.getStrategy().getProblem().getFunction().size(); i++)
		{
			ObjetiveFunction objfunction = Strategy.getStrategy().getProblem().getFunction().get(i);
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)){
				if(objfunction.getTypeProblem().equals(ProblemType.MAXIMIZAR))
				{
					tempEval = objfunction.evaluation(state);
				}
				else{
					tempEval = 1-objfunction.evaluation(state);
				}
			}
			else{
				if(objfunction.getTypeProblem().equals(ProblemType.MAXIMIZAR))
				{
					tempEval = 1-objfunction.evaluation(state);
				}
				else{
					tempEval = objfunction.evaluation(state);
				}
			}
			evaluation.add(tempEval);
		}
		state.setEvaluation(evaluation);
	}

}
