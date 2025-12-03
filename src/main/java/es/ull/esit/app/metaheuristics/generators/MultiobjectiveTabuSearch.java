package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.factory_interface.IFFactoryAcceptCandidate;
import es.ull.esit.app.factory_method.FactoryAcceptCandidate;
import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.local_search.complement.TabuSolutions;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;

/**
 * Generator that implements a multi-objective Tabu Search algorithm.
 */
public class MultiobjectiveTabuSearch extends Generator {

  /** Utility used to select a candidate from a neighbourhood. */
  private CandidateValue candidateValue;

  /** Acceptance rule for multi-objective optimisation with Tabu. */
  private AcceptType typeAcceptation;

  /** Strategy used when selecting candidates (TABU). */
  private StrategyType strategy;

  /** Candidate selection rule (e.g. random candidate among non-tabu). */
  private CandidateType typeCandidate;

  /** Current reference state of the algorithm. */
  private State stateReferenceTS;

  /** Generator type identifier. */
  private GeneratorType typeGenerator;

  /** List of reference states visited by the algorithm. */
  private List<State> listStateReference = new ArrayList<>();

  /** Weight associated with this generator in the multi-generator framework. */
  private float weight;

  /** Local counter of “better gender” for statistics. */
  private int[] listCountBetterGenderMultiObjTS = new int[10];

  /** Local counter of genders for statistics. */
  private int[] listCountGender = new int[10];

  /** Trace of weights during the execution. */
  private float[] listTrace = new float[1200000];

  /**
   * Default constructor. It initialises the internal configuration and
   * statistics arrays for the multiobjective Tabu Search.
   */
  public MultiobjectiveTabuSearch() {
    super();
    this.typeAcceptation = AcceptType.AcceptNotDominatedTabu;
    this.strategy = StrategyType.TABU;
    this.typeCandidate = CandidateType.RandomCandidate;
    this.candidateValue = new CandidateValue();
    this.typeGenerator = GeneratorType.MultiobjectiveTabuSearch;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderMultiObjTS[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderMultiObjTS;
  }

  /**
   * Returns the current reference state of the Tabu Search.
   *
   * @return [State] Current reference state.
   */
  public State getStateReferenceTS() {
    return stateReferenceTS;
  }

  /**
   * Sets the current reference state of the Tabu Search.
   *
   * @param stateReferenceTS [State] New reference state.
   */
  public void setStateReferenceTS(State stateReferenceTS) {
    this.stateReferenceTS = stateReferenceTS;
  }

  /**
   * Returns the internal generator type.
   *
   * @return [GeneratorType] Generator type.
   */
  public GeneratorType getTypeGenerator() {
    return typeGenerator;
  }

  /**
   * Sets the internal generator type.
   *
   * @param typeGenerator [GeneratorType] Generator type.
   */
  public void setTypeGenerator(GeneratorType typeGenerator) {
    this.typeGenerator = typeGenerator;
  }

  /**
   * Generates a new candidate state from the current reference using the
   * neighbourhood defined by the given operator. Tabu logic is handled in
   * the acceptance rule (AcceptNotDominatedTabu).
   *
   * @param operatornumber [Integer] Operator index.
   * @return [State] Generated candidate state.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    List<State> neighbourhood = new ArrayList<>();

    Strategy strategyInstance = Strategy.getStrategy();
    if (strategyInstance != null) {
      Problem problem = strategyInstance.getProblem();
      if (problem != null && problem.getOperator() != null && stateReferenceTS != null) {
        // Neighbourhood of the reference state
        neighbourhood = problem.getOperator().generatedNewState(stateReferenceTS, operatornumber);
      }
    }

    // A random candidate (according to typeCandidate/strategy) among non-tabu neighbours
    return candidateValue.stateCandidate(
        stateReferenceTS,
        typeCandidate,
        strategy,
        operatornumber,
        neighbourhood
    );
  }

  /**
   * Updates the reference solution according to the multiobjective Tabu
   * acceptance rule and maintains the tabu list.
   *
   * @param stateCandidate         [State] Candidate state.
   * @param countIterationsCurrent [Integer] Current iteration (unused here).
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    // Defensive: nothing to do if there is no reference or candidate
    if (stateReferenceTS == null || stateCandidate == null) {
      return;
    }

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);

    boolean accept = candidate.acceptCandidate(stateReferenceTS, stateCandidate);

    if (accept) {
      stateReferenceTS = stateCandidate;

      // If TABU strategy is active, update the tabu list
      if (strategy.equals(StrategyType.TABU)) {
        updateTabuList(stateCandidate);
      }
    }

    // Track reference states for statistics/audit
    getReferenceList();
  }

  /**
   * Updates the global tabu list with the given accepted candidate.
   *
   * @param stateCandidate [State] Accepted state.
   */
  private void updateTabuList(State stateCandidate) {
    if (stateCandidate == null) {
      return;
    }

    List<State> tabu = TabuSolutions.listTabu;
    int max = TabuSolutions.maxelements;

    if (tabu.size() < max) {
      if (!tabu.contains(stateCandidate)) {
        tabu.add(stateCandidate);
      }
    } else {
      // Remove oldest element and then try to add if not already present
      if (!tabu.isEmpty()) {
        tabu.remove(0);
      }
      if (!tabu.contains(stateCandidate)) {
        tabu.add(stateCandidate);
      }
    }
  }

  /**
   * Returns the generator type (required by abstract base class).
   *
   * @return [GeneratorType] Generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.typeGenerator;
  }

  /**
   * Returns the list of reference states visited so far. Each call appends
   * the current reference state.
   *
   * @return [List<State]] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceTS != null) {
      listStateReference.add(stateReferenceTS);
    }
    return new ArrayList<>(listStateReference);
  }

  /**
   * Returns the current reference state.
   *
   * @return [State] Current reference state.
   */
  @Override
  public State getReference() {
    return stateReferenceTS;
  }

  /**
   * Sets the initial reference state.
   *
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceTS = stateInitialRef;
  }

  /**
   * Sets the current reference state.
   *
   * @param stateRef [State] New reference state.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceTS = stateRef;
  }

  /**
   * Multiobjective Tabu Search does not maintain an explicit son list.
   *
   * @return [List<State]] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Sets the candidate selection rule.
   *
   * @param typeCandidate [CandidateType] New candidate rule.
   */
  public void setTypeCandidate(CandidateType typeCandidate) {
    this.typeCandidate = typeCandidate;
  }

  /**
   * Decides if the candidate should be considered an improvement for
   * statistics purposes. For Tabu Search, we consider any different
   * state as a potential update.
   *
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] true if considered an improvement, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateReferenceTS == null || stateCandidate == null) {
      return false;
    }
    return !stateReferenceTS.equals(stateCandidate);
  }

  /**
   * Returns the weight of this generator.
   *
   * @return [float] Weight of the generator.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight of this generator.
   *
   * @param weight [float] New weight.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Returns the internal statistics array of “better gender” counts.
   *
   * @return [int[]] Counts of better genders.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderMultiObjTS;
  }

  /**
   * Returns the internal statistics array of gender counts.
   *
   * @return [int[]] Counts of genders.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Returns the trace of weight changes.
   *
   * @return [float[]] Trace values.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }
}
