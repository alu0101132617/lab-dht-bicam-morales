/**
 * @(#) AcceptNoBadT.java
 */

package es.ull.esit.app.local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;

import es.ull.esit.app.metaheuristics.generators.SimulatedAnnealing;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;


/**
 * Class that implements the acceptation type "Accept Not Bad with Temperature"
 */
public class AcceptNotBadT extends AcceptableCandidate{

  /** Random number generator for acceptance decisions. */
  private SecureRandom random = new SecureRandom();
  /**
   * Decides whether to accept or not a candidate state.
   * @param stateCurrent [State] Current state.
   * @param stateCandidate [State] Candidate state.
   * @return [Boolean] True if the candidate state is accepted, false otherwise.
   */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Boolean accept = null;
		Problem problem = Strategy.getStrategy().getProblem();
		if (problem.getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
			double result = (stateCandidate.getEvaluation().get(0) - stateCurrent.getEvaluation().get(0)) / SimulatedAnnealing.getTinitial();
			double probaleatory = random.nextDouble();
			double exp = Math.exp(result);
			if ((stateCandidate.getEvaluation().get(0) >= stateCurrent.getEvaluation().get(0))
					|| (probaleatory < exp))
 				accept = true;
			else
				accept = false;
		} else {
			double resultMin = (stateCandidate.getEvaluation().get(0) - stateCurrent.getEvaluation().get(0)) / SimulatedAnnealing.getTinitial();
			if ((stateCandidate.getEvaluation().get(0) <= stateCurrent.getEvaluation().get(0))
					|| (random.nextDouble() < Math.exp(resultMin)))
				accept = true;
			else
				accept = false;
		}
		return accept;
	}
}
