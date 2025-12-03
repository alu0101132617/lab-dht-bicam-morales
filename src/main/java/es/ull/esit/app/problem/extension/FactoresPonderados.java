package es.ull.esit.app.problem.extension;

import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class that implements the weighted factors solution method.
 */
public class FactoresPonderados extends SolutionMethod {

  /**
   * Constructor for the FactoresPonderados class.
   * 
   * @param typeSolutionMethod [TypeSolutionMethod] Type of solution method.
   */
  @Override
  public void evaluationState(State state) {
    double eval = 0;
    double tempWeight = 0;
    List<Double> evaluation = new ArrayList<>(Strategy.getStrategy().getProblem().getFunction().size());

    for (int i = 0; i < Strategy.getStrategy().getProblem().getFunction().size(); i++) {

      if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
        if (Strategy.getStrategy().getProblem().getFunction().get(i).getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
          tempWeight = Strategy.getStrategy().getProblem().getFunction().get(i).evaluation(state);
          tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
        } else {
          tempWeight = 1 - Strategy.getStrategy().getProblem().getFunction().get(i).evaluation(state);
          tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
        }
      } else {
        if (Strategy.getStrategy().getProblem().getFunction().get(i).getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
          tempWeight = 1 - Strategy.getStrategy().getProblem().getFunction().get(i).evaluation(state);
          tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
        } else {
          tempWeight = Strategy.getStrategy().getProblem().getFunction().get(i).evaluation(state);
          tempWeight = tempWeight * Strategy.getStrategy().getProblem().getFunction().get(i).getWeight();
        }
      }
      eval += tempWeight;
    }
    evaluation.add(evaluation.size(), eval);
    state.setEvaluation(evaluation);

  }

}
