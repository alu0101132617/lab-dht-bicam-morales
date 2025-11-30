package main.java.es.ull.esit.app.problem.extension;

import java.util.ArrayList;

import main.java.es.ull.esit.app.metaheurictics.strategy.Strategy;

import main.java.es.ull.esit.app.problem.definition.State;
import main.java.es.ull.esit.app.problem.definition.Problem.ProblemType;

public class FactoresPonderados extends SolutionMethod {

	@Override
	public void evaluationState(State state) {
		// TODO Auto-generated method stub
		double eval = 0;       
		double tempWeight = 0;	
		ArrayList<Double> evaluation = new ArrayList<Double>(Strategy.getStrategy().getProblem().getFunction().size());
		
		for (int i = 0; i < Strategy.getStrategy().getProblem().getFunction().size(); i++) {

			tempWeight = 0;
			if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
				if(Strategy.getStrategy().getProblem().getFunction().get(i).getTypeProblem().equals(ProblemType.Maximizar)){
					tempWeight = Strategy.getStrategy().getProblem().getFunction().get(i).Evaluation(state);
					tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
				}
				else{
					tempWeight = 1 - Strategy.getStrategy().getProblem().getFunction().get(i).Evaluation(state);
					tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
				}
			}
			else{
				if(Strategy.getStrategy().getProblem().getFunction().get(i).getTypeProblem().equals(ProblemType.Maximizar)){
					tempWeight = 1 - Strategy.getStrategy().getProblem().getFunction().get(i).Evaluation(state);
					tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
				}
				else{
					tempWeight = Strategy.getStrategy().getProblem().getFunction().get(i).Evaluation(state);
					tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
				}
			}
			eval += tempWeight;
		}
		evaluation.add(evaluation.size(), eval);
		state.setEvaluation(evaluation);
		
	}

}
