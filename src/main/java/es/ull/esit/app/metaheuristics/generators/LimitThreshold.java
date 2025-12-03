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
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Limit Threshold local-search generator.
 *
 * It generates a candidate in the neighbourhood of a reference state and
 * decides whether to update that reference according to an acceptance rule.
 */
public class LimitThreshold extends Generator {

  /** Helper to select the best candidate from a neighbourhood. */
  private CandidateValue candidateValue;

  /** Acceptance rule for candidates. */
  private AcceptType typeAcceptation;

  /** Neighbourhood exploration strategy. */
  private StrategyType strategy;

  /** How to compare candidates (greater/smaller). */
  private CandidateType typeCandidate;

  /** Current reference state. */
  private State stateReferenceLT;

  /** Generator type. */
  private GeneratorType generatorType;

  /** List of reference states (can be used for logging/statistics). */
  private final List<State> listStateReference = new ArrayList<>();

  /** Current weight of this generator in multi-generator schemes. */
  private float weight;

  /** Local counters and traces (for dynamic problems/statistics). */
  private final int[] listCountBetterGenderLimitThreshold = new int[10];
  private final int[] listCountGender = new int[10];
  private final float[] listTrace = new float[1_200_000];

  /**
   * Default constructor.
   *
   * It initialises the candidate comparison mode from the problem type and
   * sets reasonable defaults for strategy, acceptance rule and weight.
   */
  public LimitThreshold() {
    super();
    this.typeAcceptation = AcceptType.AcceptNotBadU;
    this.strategy = StrategyType.NORMAL;

    Problem problem = Strategy.getStrategy().getProblem();
    if (problem.getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      this.typeCandidate = CandidateType.GreaterCandidate;
    } else {
      this.typeCandidate = CandidateType.SmallerCandidate;
    }

    this.candidateValue = new CandidateValue();
    this.generatorType = GeneratorType.LimitThreshold;
    this.weight = 50.0f;

    // Initialise trace and counters
    listTrace[0] = this.weight;
    listCountBetterGenderLimitThreshold[0] = 0;
    listCountGender[0] = 0;

    // Link inherited counter array to the specific one used here
    this.listCountBetterGender = this.listCountBetterGenderLimitThreshold;
  }

  /**
   * Generates a new candidate in the neighbourhood of the current reference
   * using the configured candidate selection strategy.
   *
   * @param operatornumber [Integer] Operator to use for generating new states.
   * @return [State] Generated candidate state.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    if (stateReferenceLT == null) {
      throw new IllegalStateException("Reference state has not been initialised");
    }

    List<State> neighbourhood = Strategy.getStrategy()
        .getProblem()
        .getOperator()
        .generatedNewState(stateReferenceLT, operatornumber);

    return candidateValue.stateCandidate(
        stateReferenceLT,
        typeCandidate,
        strategy,
        operatornumber,
        neighbourhood
    );
  }

  /**
   * Updates the reference state according to the configured acceptance rule.
   *
   * @param stateCandidate [State] Candidate state to consider for reference update.
   * @param countIterationsCurrent [Integer] Current iteration count (of the metaheuristic).
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
    Boolean accept = candidate.acceptCandidate(stateReferenceLT, stateCandidate);

    if (Boolean.TRUE.equals(accept)) {
      stateReferenceLT = stateCandidate;
    }
  }

  /**
   * Returns the current reference state.
   * 
   * @return [State] Current reference state.
   */
  @Override
  public State getReference() {
    return stateReferenceLT;
  }

  /**
   * Sets the reference state (alias for setInitialReference for tests/clients).
   * 
   * @param stateRef [State] Reference state to set.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceLT = stateRef;
  }

  /**
   * Sets the initial reference state.
   * 
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceLT = stateInitialRef;
  }

  /**
   * Returns the internal generator type.
   * 
   * @return [GeneratorType] Internal generator type.
   */
  public GeneratorType getGeneratorType() {
    return generatorType;
  }

  /**
   * Sets the internal generator type.
   * 
   * @param generatorType [GeneratorType] Internal generator type to set.
   */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /**
   * Returns the type of this generator.
   * 
   * @return [GeneratorType] Type of this generator.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Returns a copy of the reference list maintained by this generator.
   *
   * Currently this generator maintains a simple list that can be used for
   * logging; each call appends the current reference (if not null) and
   * returns a defensive copy.
   * 
   * @return [List<State>] Copy of the reference list.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceLT != null) {
      listStateReference.add(stateReferenceLT);
    }
    return new ArrayList<>(listStateReference);
  }

  /**
   * This local-search generator does not keep a separate son list.
   * 
   * @return [List<State>] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Allows changing the candidate comparison mode (greater/smaller).
   * 
   * @param typeCandidate [CandidateType] Candidate comparison mode.
   */
  public void setTypeCandidate(CandidateType typeCandidate) {
    this.typeCandidate = typeCandidate;
  }

  /**
   * Decides whether the candidate should be considered an "improvement"
   * with respect to the current reference, based purely on evaluations.
   *
   * This is independent from the acceptance rule used in updateReference.
   * 
   * @param stateCandidate [State] Candidate state to compare.
   * @return [boolean] true if the candidate is better than the reference.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateReferenceLT == null || stateCandidate == null) {
      return false;
    }
    if (stateReferenceLT.getEvaluation() == null
        || stateReferenceLT.getEvaluation().isEmpty()
        || stateCandidate.getEvaluation() == null
        || stateCandidate.getEvaluation().isEmpty()) {
      return false;
    }

    double refEval = stateReferenceLT.getEvaluation().get(0);
    double candEval = stateCandidate.getEvaluation().get(0);

    ProblemType type = Strategy.getStrategy().getProblem().getTypeProblem();
    if (type == ProblemType.MAXIMIZAR) {
      return candEval > refEval;
    } else {
      return candEval < refEval;
    }
  }

  /**
   * Returns the current generator weight.
   * 
   * @return [float] Current generator weight.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the current generator weight.
   * 
   * @param weight [float] Current generator weight to set.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Returns the "better gender" counter array for this generator.
   * 
   * @return [int[]] Better gender counter array.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderLimitThreshold;
  }

  /**
   * Returns the gender counter array for this generator.
   * 
   * @return [int[]] Gender counter array.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Returns the trace array used to store the weight evolution.
   * 
   * @return [float[]] Trace array.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }
}
