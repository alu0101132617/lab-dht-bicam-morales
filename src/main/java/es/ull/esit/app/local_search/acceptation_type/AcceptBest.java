/**
 * @(#) AcceptBest.java
 */

package main.java.es.ull.esit.app.local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.metaheurictics.strategy.Strategy;
import main.java.es.ull.esit.app.problem.definition.Problem;
import main.java.es.ull.esit.app.problem.definition.State;
import main.java.es.ull.esit.app.problem.definition.Problem.ProblemType;

public class AcceptBest extends AcceptableCandidate {

	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Boolean accept = null;
		Problem problem = Strategy.getStrategy().getProblem();
		if(problem.getTypeProblem().equals(ProblemType.Maximizar)) {
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
