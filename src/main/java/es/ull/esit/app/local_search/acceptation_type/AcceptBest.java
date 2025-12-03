
package es.ull.esit.app.local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Class representing an accept-best strategy for candidate states.
 */
public class AcceptBest extends AcceptableCandidate {

  /**
   * Determines if a candidate state is acceptable based on whether it is better than or equal to the current state.
   * @param stateCurrent [State] Current state.
   * @param stateCandidate [State] Candidate state to evaluate.
   * @return [Boolean] True if the candidate state is better than or equal to the current state, false otherwise.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a specified class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a specified method cannot be found.
   * 
   */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Boolean accept = null;
		Problem problem = Strategy.getStrategy().getProblem();
		if(problem.getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
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
		}
		return accept;
	}
}
