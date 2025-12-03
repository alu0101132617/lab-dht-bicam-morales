/**
 * @(#) AcceptNoBadU.java
 */

package es.ull.esit.app.local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class that implements the acceptation type "Accept Not Bad with Utility"
 */
public class AcceptNotBadU extends AcceptableCandidate{

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
			Double result = stateCurrent.getEvaluation().get(0) - stateCandidate.getEvaluation().get(0);
			if (result < Strategy.getStrategy().getThreshold())
				accept = true;
			else
				accept = false;
		} else {
			Double resultMin = stateCurrent.getEvaluation().get(0) - stateCandidate.getEvaluation().get(0);
			if (resultMin > Strategy.getStrategy().getThreshold())
				accept = true;
			else
				accept = false;
		}
		return accept;
	}
}
