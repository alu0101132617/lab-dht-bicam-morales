/**
 * @(#) AleatoryCandidate.java
 */

package main.java.es.ull.esit.app.local_search.candidate_type;

import java.util.List;

import main.java.es.ull.esit.app.problem.definition.State;

public class RandomCandidate extends SearchCandidate {

	@Override
	public State stateSearch(List<State> listNeighborhood) {
		int pos = (int)(Math.random() * (double)(listNeighborhood.size() - 1));
		State stateAleatory = listNeighborhood.get(pos);
		return stateAleatory;
	}
}
