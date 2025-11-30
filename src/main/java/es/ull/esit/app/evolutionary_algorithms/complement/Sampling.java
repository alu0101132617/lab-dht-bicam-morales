package main.java.es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;

import main.java.es.ull.esit.app.problem.definition.State;


public abstract class Sampling {
	public abstract List<State> sampling (List<State> fathers, int countInd);
}
