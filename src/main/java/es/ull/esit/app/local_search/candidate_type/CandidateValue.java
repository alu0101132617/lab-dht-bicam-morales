package es.ull.esit.app.local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.problem.definition.State;

import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.local_search.complement.TabuSolutions;
import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.factory_interface.IFFactoryCandidate;
import es.ull.esit.app.factory_method.FactoryCandidate;

/**
 * Class that represents the candidate value in local search algorithms.
 */
public class CandidateValue {

  /**  Strategy type used in the local search */
	@SuppressWarnings("unused")
	private StrategyType strategy;

  /** Factory interface for creating search candidates */
	private IFFactoryCandidate ifFactory;

  /** Type of candidate used in the local search */
	@SuppressWarnings("unused")
	private CandidateType typecand;

  /** Tabu solutions used in the local search */
	private TabuSolutions tabusolution;

  /** Search candidate used in the local search */
	private SearchCandidate searchcandidate;

  /** Constructor for CandidateValue */
	public CandidateValue(){}

  /**
   * Constructor for CandidateValue with parameters.
   * @param strategy [StrategyType] Type of strategy used.
   * @param ifFactory [IFFactoryCandidate] Factory interface for creating candidates.
   * @param typecand [CandidateType] Type of candidate.
   * @param tabusolution [TabuSolutions] Tabu solutions.
   * @param searchcandidate [SearchCandidate] Search candidate.
   */
	public CandidateValue(StrategyType strategy, IFFactoryCandidate ifFactory, CandidateType typecand, 
			TabuSolutions tabusolution, SearchCandidate searchcandidate) { //, Strategy executegenerator
		super();
		this.strategy = strategy;
		this.ifFactory = ifFactory;
		this.typecand = typecand;
		this.tabusolution = tabusolution;
		this.searchcandidate = searchcandidate;
	}

  /**
   * Creates a new SearchCandidate based on the provided CandidateType.
   * @param typecandidate [CandidateType] The type of candidate to create.
   * @return [SearchCandidate] The created search candidate.
   * @throws IllegalArgumentException If the argument is invalid.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to the class is illegal.
   * @throws InvocationTargetException If the method invocation fails.
   * @throws NoSuchMethodException If the method does not exist.
   */
	public SearchCandidate newSearchCandidate(CandidateType typecandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifFactory = new FactoryCandidate();
		searchcandidate = ifFactory.createSearchCandidate(typecandidate);
		return searchcandidate;
	}

  /**
   * Finds a candidate state based on the current state, candidate type, strategy, operator number, and neighborhood.
   * @param stateCurrent [State] The current state.
   * @param typeCandidate [CandidateType] The type of candidate.
   * @param strategy [StrategyType] The strategy type.
   * @param operatornumber [Integer] The operator number.
   * @param neighborhood [List<State>] The neighborhood of states.
   * @return [State] The candidate state found.
   * @throws IllegalArgumentException If the argument is invalid.
   * @throws SecurityException If a segurity violation occurs.
   * @throws ClassNotFoundException If the class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to the class is illegal.
   * @throws InvocationTargetException If the method invocation fails.
   * @throws NoSuchMethodException If the method does not exist.
   */
	public State stateCandidate(State stateCurrent, CandidateType typeCandidate, StrategyType strategy, Integer operatornumber, List<State> neighborhood) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		State stateCandidate;
		List<State> auxList = new ArrayList<>();
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

  /**
   * Gets the tabu solutions.
   * @return [TabuSolutions] The tabu solutions.
   */
	public TabuSolutions getTabusolution() {
		return tabusolution;
	}

  /**
   * Sets the tabu solutions.
   * @param tabusolution [TabuSolutions] The tabu solutions to set.
   */
	public void setTabusolution(TabuSolutions tabusolution) {
		this.tabusolution = tabusolution;
	}
}
