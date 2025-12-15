package es.ull.esit.app.local_search.candidate_type;

import java.util.List;
import java.security.SecureRandom;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements a random candidate selection strategy for local search.
 */
public class RandomCandidate extends SearchCandidate {

  private SecureRandom random = new SecureRandom();
  /**
   * Selects a random state from the provided list of neighboring states.
   * @param listNeighborhood List of neighboring states.
   * @return A randomly selected state from the list.
   */
	@Override
	public State stateSearch(List<State> listNeighborhood) {
		int pos = random.nextInt(listNeighborhood.size());
		return listNeighborhood.get(pos);
	}
}
