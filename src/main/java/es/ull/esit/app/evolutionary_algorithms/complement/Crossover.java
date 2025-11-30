package main.java.es.ull.esit.app.evolutionary_algorithms.complement;

import main.java.es.ull.esit.app.problem.definition.State;

public abstract class Crossover {
	
	public abstract State crossover(State father1, State father2, double PC);
}
