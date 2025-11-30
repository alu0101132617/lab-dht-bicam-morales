package main.java.es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;

import main.java.es.ull.esit.app.problem.definition.State;


public abstract class Distribution {
	public abstract List<Probability> distribution(List<State> fathers);

}
