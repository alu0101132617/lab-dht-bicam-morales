package main.java.es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.List;

import main.java.es.ull.esit.app.problem.definition.State;


public abstract class FatherSelection {
	
	public abstract List<State> selection(List<State> listState, int truncation);

}
