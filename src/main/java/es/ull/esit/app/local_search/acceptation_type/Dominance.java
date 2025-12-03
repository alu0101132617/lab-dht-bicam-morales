package es.ull.esit.app.local_search.acceptation_type;

import java.util.List;
import java.util.ListIterator;

import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheuristics.generators.MultiobjectiveHillClimbingDistance;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements dominance-based acceptance criteria for local search algorithms.
 */
public class Dominance {

  /**
   * Determines if solutionX dominates any of the non-dominated solutions in a
   * list.
   *
   * @param solutionX [State] The candidate solution.
   * @param list      [List<State>] The list of non-dominated solutions.
   * @return [boolean] True if solutionX was added to the list, false otherwise.
   */
  public boolean listDominance(State solutionX, List<State> list) {
    boolean dominatedByList = false;

    // Evitamos llamar muchas veces a Strategy / generator
    boolean isDistanceGenerator =
        Strategy.getStrategy().getGenerator().getType().equals(GeneratorType.MultiobjectiveHillClimbingDistance);

    ListIterator<State> iterator = list.listIterator();
    while (iterator.hasNext() && !dominatedByList) {
      State current = iterator.next();

      if (dominance(solutionX, current)) {
        // solutionX domina al elemento actual -> se elimina
        iterator.remove();

        // Si estamos en el generador MultiobjectiveHillClimbingDistance y
        // la lista se queda vacía, se recalculan distancias
        if (isDistanceGenerator && list.isEmpty()) {
          MultiobjectiveHillClimbingDistance.distanceCalculateAdd(list);
        }
      } else if (dominance(current, solutionX)) {
        // Alguna solución de la lista domina a solutionX
        dominatedByList = true;
      }
    }

    // Si la nueva solución ha sido dominada, no se añade
    if (dominatedByList) {
      return false;
    }

    // Comprobando que la solución no exista ya en la lista
    boolean found = false;
    for (State element : list) {
      if (solutionX.comparator(element)) {
        found = true;
        break;
      }
    }

    // Si la solución no existe, se añade a la lista de Pareto
    if (!found) {
      list.add(solutionX.copy());
      if (isDistanceGenerator) {
        // En este caso siempre recalculamos distancias tras la inserción
        MultiobjectiveHillClimbingDistance.distanceCalculateAdd(list);
      }
      return true;
    }

    return false;
  }

  /**
   * Determines if solutionX dominates solutionY.
   *
   * @param solutionX [State] The candidate solution.
   * @param solutionY [State] The solution to compare against.
   * @return [boolean] True if solutionX dominates solutionY, false otherwise.
   */
  public boolean dominance(State solutionX, State solutionY) {
    int countBest = 0;
    int countEquals = 0;

    ProblemType type = Strategy.getStrategy().getProblem().getTypeProblem();
    List<Double> evalX = solutionX.getEvaluation();
    List<Double> evalY = solutionY.getEvaluation();

    for (int i = 0; i < evalX.size(); i++) {
      float x = evalX.get(i).floatValue();
      float y = evalY.get(i).floatValue();

      if (isBetter(x, y, type)) {
        countBest++;
      } else if (x == y) {
        countEquals++;
      }
    }

    // Domina si es mejor en al menos un objetivo y
    // el resto son iguales (es decir, no es peor en ninguno)
    return countBest >= 1 && (countBest + countEquals == evalX.size());
  }

  /**
   * Returns true if x is better than y according to the problem type.
   */
  private boolean isBetter(float x, float y, ProblemType type) {
    if (type.equals(ProblemType.MAXIMIZAR)) {
      return x > y;
    } else {
      return x < y;
    }
  }
}
