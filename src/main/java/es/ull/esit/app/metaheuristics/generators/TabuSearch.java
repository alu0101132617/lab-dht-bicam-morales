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
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements a classic Tabu Search generator.
 *
 * It maintains:
 *  - A current reference solution.
 *  - A candidate selection rule.
 *  - A Tabu list of forbidden solutions (via {@link TabuSolutions}).
 */
public class TabuSearch extends Generator {

  /** Utility used to select a candidate from the neighbourhood. */
  private CandidateValue candidateValue;

  /** Acceptance rule for Tabu Search. */
  private AcceptType typeAcceptation;

  /** Strategy type (TABU in this case). */
  private StrategyType strategy;

  /** Candidate selection rule (e.g. best, worst, etc.). */
  private CandidateType typeCandidate;

  /** Current reference state for Tabu Search. */
  private State stateReferenceTS;

  /** Generator type identifier. */
  private GeneratorType typeGenerator;

  /** List of reference states visited during the search. */
  private List<State> listStateReference = new ArrayList<>();

  /** Weight associated with this generator (for multi–generator frameworks). */
  private float weight;

  /** Local “better gender” statistics array. */
  private int[] listCountBetterGenderTS = new int[10];

  /** Local gender statistics array. */
  private int[] listCountGender = new int[10];

  /** Trace of weight values. */
  private float[] listTrace = new float[1200000];

  /**
   * Default constructor.
   *
   * It initialises:
   *  - Acceptance: {@link AcceptType#AcceptAnyone}
   *  - Strategy: {@link StrategyType#TABU}
   *  - Candidate type: Greater/Smaller according to the problem type
   *    (MAXIMIZAR -> GreaterCandidate, else SmallerCandidate)
   */
  public TabuSearch() {
    super();

    this.typeAcceptation = AcceptType.AcceptAnyone;
    this.strategy = StrategyType.TABU;

    // Default candidate type depends on the problem type (if available)
    CandidateType defaultCandidateType = CandidateType.RandomCandidate;
    Strategy strategyInstance = Strategy.getStrategy();
    if (strategyInstance != null && strategyInstance.getProblem() != null) {
      Problem problem = strategyInstance.getProblem();
      ProblemType problemType = problem.getTypeProblem();
      if (problemType == null || problemType.equals(ProblemType.MAXIMIZAR)) {
        defaultCandidateType = CandidateType.GreaterCandidate;
      } else {
        defaultCandidateType = CandidateType.SmallerCandidate;
      }
    }
    this.typeCandidate = defaultCandidateType;

    this.candidateValue = new CandidateValue();
    this.typeGenerator = GeneratorType.TabuSearch;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderTS[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderTS;
  }

  /**
   * Generates a new candidate solution from the current reference
   * using the neighbourhood defined by the operator and
   * the configured candidate selection rule.
   *
   * @param operatornumber [Integer] Operator index.
   * @return [State] Generated candidate state, or {@code null} if context is missing.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    Strategy strategyInstance = Strategy.getStrategy();
    if (strategyInstance == null
        || strategyInstance.getProblem() == null
        || strategyInstance.getProblem().getOperator() == null) {
      // Not enough information to generate neighbours
      return null;
    }

    List<State> neighbourhood = strategyInstance
        .getProblem()
        .getOperator()
        .generatedNewState(stateReferenceTS, operatornumber);

    return candidateValue.stateCandidate(
        stateReferenceTS,
        typeCandidate,
        strategy,
        operatornumber,
        neighbourhood
    );
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
   * Sets the initial reference state for Tabu Search.
   *
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceTS = stateInitialRef;
  }

  /**
   * Sets the current reference state (explicit setter, useful in tests).
   *
   * @param stateRef [State] New reference state.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceTS = stateRef;
  }

  /**
   * Updates the reference state according to the acceptance rule and
   * maintains the Tabu list:
   *
   *  - If the candidate is accepted, the reference state is updated.
   *  - If strategy is TABU and the candidate is accepted, it is inserted
   *    into the Tabu list (unless already present), removing the oldest
   *    element when the list is full.
   *
   * @param stateCandidate         [State] Candidate state.
   * @param countIterationsCurrent [Integer] Current iteration (not used directly here).
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    if (stateReferenceTS == null || stateCandidate == null) {
      return;
    }

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);

    boolean accept = candidate.acceptCandidate(stateReferenceTS, stateCandidate);
    if (accept) {
      stateReferenceTS = stateCandidate;
    }

    // Tabu list management
    if (strategy.equals(StrategyType.TABU) && accept) {
      // If there is still room, just check if it's already Tabu
      if (TabuSolutions.getListTabu().size() < TabuSolutions.maxelements) {
        boolean found = false;
        int count = 0;

        while (!found && count < TabuSolutions.getListTabu().size()) {
          if (TabuSolutions.getListTabu().get(count).comparator(stateCandidate)) {
            found = true;
          }
          count++;
        }

        if (!found) {
          TabuSolutions.getListTabu().add(stateCandidate);
        }

      } else {
        // Remove oldest element and then insert if not present
        if (!TabuSolutions.getListTabu().isEmpty()) {
          TabuSolutions.getListTabu().remove(0);
        }

        boolean found = false;
        int count = 0;

        while (!found && count < TabuSolutions.getListTabu().size()) {
          if (TabuSolutions.getListTabu().get(count).comparator(stateCandidate)) {
            found = true;
          }
          count++;
        }

        if (!found) {
          TabuSolutions.getListTabu().add(stateCandidate);
        }
      }
    }
  }

  /**
   * Returns the generator type.
   *
   * @return [GeneratorType] Generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.typeGenerator;
  }

  /**
   * Explicit generator type getter (for tests / configuration).
   *
   * @return [GeneratorType] Generator type.
   */
  public GeneratorType getTypeGenerator() {
    return typeGenerator;
  }

  /**
   * Explicit generator type setter (for tests / configuration).
   *
   * @param typeGenerator [GeneratorType] New generator type.
   */
  public void setTypeGenerator(GeneratorType typeGenerator) {
    this.typeGenerator = typeGenerator;
  }

  /**
   * Returns the list of reference states visited so far.
   * Each call appends the current reference state if not null.
   *
   * @return [List<State>] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceTS != null) {
      listStateReference.add(stateReferenceTS);
    }
    return new ArrayList<>(listStateReference);
  }

  /**
   * Tabu Search does not explicitly maintain a son list.
   * We return an empty list to avoid null checks.
   *
   * @return [List<State>] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Sets the candidate selection rule.
   *
   * @param typeCandidate [CandidateType] New candidate selection type.
   */
  public void setTypeCandidate(CandidateType typeCandidate) {
    this.typeCandidate = typeCandidate;
  }

  /**
   * For Tabu Search we do not use an additional “award” criterion,
   * beyond the acceptance rule, so this always returns {@code false}.
   *
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] Always false.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    return false;
  }

  /**
   * Returns the weight of this generator.
   *
   * @return [float] Weight value.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight of this generator.
   *
   * @param weight [float] New weight value.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Returns the internal “better gender” statistics array.
   *
   * @return [int[]] Better gender statistics.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderTS;
  }

  /**
   * Returns the internal gender statistics array.
   *
   * @return [int[]] Gender statistics.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Returns the trace of weight values.
   *
   * @return [float[]] Weight trace.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }
}



