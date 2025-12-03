
package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.problem.definition.State;

/**
 * Class representing an accept-anyone strategy for candidate states.
 */
public class AcceptAnyone extends AcceptableCandidate{

  /**
   * Determines if a candidate state is acceptable.
   * @param stateCurrent [State] Current state.
   * @param stateCandidate [State] Candidate state to evaluate.
   * @return [Boolean] Always returns true, indicating the candidate state is acceptable.
   */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		return true;
	}
	
}
