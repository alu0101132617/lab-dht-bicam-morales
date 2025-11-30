/**
 * @(#) CandidateValue.java
 */

package main.java.es.ull.esit.app.local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import main.java.es.ull.esit.app.problem.definition.State;

import main.java.es.ull.esit.app.local_search.complement.StrategyType;
import main.java.es.ull.esit.app.local_search.complement.TabuSolutions;
import main.java.es.ull.esit.app.metaheurictics.strategy.Strategy;

//import ceis.grial.problem.Problem;
import main.java.es.ull.esit.app.factory_interface.IFFactoryCandidate;
import main.java.es.ull.esit.app.factory_method.FactoryCandidate;

public class CandidateValue {

	@SuppressWarnings("unused")
	private StrategyType strategy;

	private IFFactoryCandidate ifFactory;

	@SuppressWarnings("unused")
	private CandidateType typecand;

	private TabuSolutions tabusolution;

	private SearchCandidate searchcandidate;

	public CandidateValue(){}

	public CandidateValue(StrategyType strategy, IFFactoryCandidate ifFactory, CandidateType typecand, 
			TabuSolutions tabusolution, SearchCandidate searchcandidate) { //, Strategy executegenerator
		super();
		this.strategy = strategy;
		this.ifFactory = ifFactory;
		this.typecand = typecand;
		this.tabusolution = tabusolution;
		this.searchcandidate = searchcandidate;
	}

	public SearchCandidate newSearchCandidate(CandidateType typecandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifFactory = new FactoryCandidate();
		searchcandidate = ifFactory.createSearchCandidate(typecandidate);
		return searchcandidate;
	}

	public State stateCandidate(State stateCurrent, CandidateType typeCandidate, StrategyType strategy, Integer operatornumber, List<State> neighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		//Problem problem = ExecuteGenerator.getExecuteGenerator().getProblem();
		State stateCandidate;
		List<State> auxList = new ArrayList<State>();
		for (int i = 0; i < neighborhood.size(); i++) {
			auxList.add(neighborhood.get(i));
		}
		this.tabusolution = new TabuSolutions();
		if (strategy.equals(StrategyType.TABU)) {
			try {
				auxList = this.tabusolution.filterNeighborhood(auxList);
			}
			catch (Exception e) {
				Strategy strategys = Strategy.getStrategy();
				if(strategys.getProblem()!=null){
					neighborhood = strategys.getProblem().getOperator().generatedNewState(neighborhood.get(0), operatornumber);
				}
				return stateCandidate(stateCurrent, typeCandidate, strategy, operatornumber, neighborhood);
			}
		}
		SearchCandidate searchCand = newSearchCandidate(typeCandidate);
		stateCandidate = searchCand.stateSearch(auxList);
		return stateCandidate;
	}

	public TabuSolutions getTabusolution() {
		return tabusolution;
	}

	public void setTabusolution(TabuSolutions tabusolution) {
		this.tabusolution = tabusolution;
	}
}
