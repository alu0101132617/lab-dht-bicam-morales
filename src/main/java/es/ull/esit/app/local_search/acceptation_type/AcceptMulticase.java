package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheuristics.generators.*;
import es.ull.esit.app.metaheurictics.strategy.*;

import java.util.List;
import java.security.SecureRandom;

import es.ull.esit.app.problem.definition.State;

/**
 * Class representing an accept-multicase strategy for candidate states.
 */
public class AcceptMulticase extends AcceptableCandidate {

  /** Random number generator for acceptance probability calculations. */
  private SecureRandom rdm = new SecureRandom();

  /**
   * Determines if a candidate state is acceptable based on multicase criteria.
   * 
   * @param stateCurrent   [State] Current state.
   * @param stateCandidate [State] Candidate state to evaluate.
   * @return [Boolean] True if the candidate state is acceptable, false otherwise.
   */
  @Override
  public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {

    Boolean accept = false;
    List<State> list = Strategy.getStrategy().getListRefPoblacFinal();

    if (list.isEmpty()) {
      list.add(stateCurrent.copy());
    }

    double tInitial = MultiCaseSimulatedAnnealing.getTinitial();
    Dominance dominance = new Dominance();

    double pAccept = calculateAcceptanceProbability(stateCurrent, stateCandidate, list, dominance, tInitial);

    // Generar un número aleatorio y decidir aceptación
    if (rdm.nextFloat() < pAccept) {
      // Verificando que la solución candidata domina a alguna de las soluciones
      accept = dominance.listDominance(stateCandidate, list);
    }

    return accept;
  }

  /**
   * Calculates the acceptance probability for a candidate state.
   * 
   * @param stateCurrent   [State] Current state.
   * @param stateCandidate [State] Candidate state.
   * @param list           [List<State>] List of reference states.
   * @param dominance      [Dominance] Dominance checker.
   * @param tInitial       [double] Initial temperature.
   * @return [double] Acceptance probability
   */
  private double calculateAcceptanceProbability(State stateCurrent,
      State stateCandidate,
      List<State> list,
      Dominance dominance,
      double tInitial) {

    // ¿La candidata domina a la actual?
    boolean candidateDominatesCurrent = dominance.dominance(stateCandidate, stateCurrent);
    if (candidateDominatesCurrent) {
      return 1.0;
    }

    // En caso contrario, se aplican las demás reglas
    int dominatedCount = dominanceCounter(stateCandidate, list);
    if (dominatedCount > 0) {
      return 1.0;
    }

    int candidateRank = dominanceRank(stateCandidate, list);
    int currentRank = dominanceRank(stateCurrent, list);

    if (candidateRank == 0 || candidateRank < currentRank) {
      return 1.0;
    }

    if (candidateRank == currentRank) {
      return calculateEvaluationAcceptance(stateCurrent, stateCandidate, tInitial);
    }

    // candidateRank > currentRank
    if (currentRank != 0) {
      float value = (float) candidateRank / (float) currentRank;
      return Math.exp(-(value + 1) / tInitial);
    }

    // Caso fallback
    return calculateEvaluationAcceptance(stateCurrent, stateCandidate, tInitial);
  }

  /**
   * Calculates acceptance probability based on evaluations.
   * 
   * @param stateCurrent   [State] Current state.
   * @param stateCandidate [State] Candidate state.
   * @param tInitial       [double] Initial temperature.
   * @return [double] Evaluation-based acceptance probability.
   */
  private double calculateEvaluationAcceptance(State stateCurrent,
      State stateCandidate,
      double tInitial) {
    List<Double> evaluations = stateCurrent.getEvaluation();
    double total = 0;
    for (int i = 0; i < evaluations.size() - 1; i++) {
      Double evalA = evaluations.get(i);
      Double evalB = stateCandidate.getEvaluation().get(i);
      if (evalA != 0 && evalB != 0) {
        total += (evalA - evalB) / ((evalA + evalB) / 2);
      }
    }
    return Math.exp(-(1 - total) / tInitial);
  }

  /**
   * Calculates how many solutions in the list are dominated by the candidate.
   * 
   * @param stateCandidate [State] Candidate state.
   * @param list           [List<State>] List of states to compare against.
   * @return [int] The count of states dominated by the candidate state.
   */
  private int dominanceCounter(State stateCandidate, List<State> list) {
    int counter = 0;
    for (State solution : list) {
      Dominance dominance = new Dominance();
      if (dominance.dominance(stateCandidate, solution)) {
        counter++;
      }
    }
    return counter;
  }

  /**
   * Calculates how many solutions in the list dominate the candidate.
   * 
   * @param stateCandidate [State] Candidate state.
   * @param list           [List<State>] List of states to compare against.
   * @return [int] The dominance rank of the candidate state.
   */
  private int dominanceRank(State stateCandidate, List<State> list) {
    int rank = 0;
    for (State solution : list) {
      Dominance dominance = new Dominance();
      if (dominance.dominance(solution, stateCandidate)) {
        rank++;
      }
    }
    return rank;
  }

}