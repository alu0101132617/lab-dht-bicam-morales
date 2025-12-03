package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class that implements the acceptation type "Accept Not Bad"
 */
public class AcceptNotBad extends AcceptableCandidate{

  /**
   * Decides whether to accept or not a candidate state.
   * @param stateCurrent [State] Current state.
   * @param stateCandidate [State] Candidate state.
   * @return [Boolean] True if the candidate state is accepted, false otherwise.
   */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		Boolean accept = null;
		Problem problem = Strategy.getStrategy().getProblem();
		for (int i = 0; i < problem.getFunction().size(); i++) {
		if (problem.getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
			if (stateCandidate.getEvaluation().get(0) >= stateCurrent.getEvaluation().get(0)) {
				accept = true;
			} else {
				accept = false;
			}
		} else {
			if (stateCandidate.getEvaluation().get(0) <= stateCurrent.getEvaluation().get(0)) {
				accept = true;
			} else {
				accept = false;
			}
		}}
		return accept;
	}
}
