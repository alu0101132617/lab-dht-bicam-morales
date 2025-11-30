/**
 * @(#) AcceptAnyone.java
 */

package main.java.es.ull.esit.app.local_search.acceptation_type;

import main.java.es.ull.esit.app.problem.definition.State;


public class AcceptAnyone extends AcceptableCandidate{

	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		Boolean accept = true;
		return accept;
	}
	
}
