package main.java.es.ull.esit.app.local_search.acceptation_type;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.problem.definition.State;

public abstract class AcceptableCandidate {
  
	public abstract Boolean acceptCandidate(State stateCurrent, State stateCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
