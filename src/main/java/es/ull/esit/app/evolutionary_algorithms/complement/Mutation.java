package main.java.es.ull.esit.app.evolutionary_algorithms.complement;

import main.java.es.ull.esit.app.problem.definition.State;

public abstract class Mutation {
	
	public abstract State mutation (State state, double PM);

}
