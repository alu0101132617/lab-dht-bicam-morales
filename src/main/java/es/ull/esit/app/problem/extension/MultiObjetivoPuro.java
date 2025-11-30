package main.java.es.ull.esit.app.problem.extension;

import java.util.ArrayList;

import main.java.es.ull.esit.app.metaheurictics.strategy.Strategy;

import main.java.es.ull.esit.app.problem.definition.ObjetiveFunction;
import main.java.es.ull.esit.app.problem.definition.State;
import main.java.es.ull.esit.app.problem.definition.Problem.ProblemType;

public class MultiObjetivoPuro extends SolutionMethod {

	@Override
	public void evaluationState(State state) {
		// TODO Auto-generated method stub
		double tempEval = -1;
		ArrayList<Double> evaluation = new ArrayList<Double>(Strategy.getStrategy().getProblem().getFunction().size());
		for (int i = 0; i < Strategy.getStrategy().getProblem().getFunction().size(); i++)
		{
			ObjetiveFunction objfunction = (ObjetiveFunction)Strategy.getStrategy().getProblem().getFunction().get(i);
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
				if(objfunction.getTypeProblem().equals(ProblemType.Maximizar))
				{
					tempEval = objfunction.Evaluation(state);
				}
				else{
					tempEval = 1-objfunction.Evaluation(state);
				}
			}
			else{
				if(objfunction.getTypeProblem().equals(ProblemType.Maximizar))
				{
					tempEval = 1-objfunction.Evaluation(state);
				}
				else{
					tempEval = objfunction.Evaluation(state);
				}
			}
			evaluation.add(tempEval);
		}
		//evaluation.add( (double) -1);
		state.setEvaluation(evaluation);
	}

}
