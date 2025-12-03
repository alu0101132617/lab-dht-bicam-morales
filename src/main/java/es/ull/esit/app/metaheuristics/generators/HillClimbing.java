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
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the Hill Climbing generator.
 */
public class HillClimbing extends Generator {

  /** Candidate selection strategy. */
  private CandidateValue candidateValue;

  /** Acceptance type. */
  private AcceptType typeAcceptation;

  /** Exploration strategy (normal, etc.). */
  private StrategyType strategy;

  /** Candidate type (greater, smaller, etc.). */
  private CandidateType typeCandidate;

  /** Reference state (current solution). */
  private State stateReferenceHC;

  /** Generator type. */
  private GeneratorType generatorType;

  /** List of reference states (trace of best solutions). */
  private List<State> listStateReference = new ArrayList<>();

  /** Weight of the generator (for portfolio, etc.). */
  private float weight;

  /** Counters and traces for dynamic problems / portfolio. */
  private final int[] listCountBetterGenderHillClimbing = new int[10];
  private final int[] listCountGender = new int[10];
  private final float[] listTrace = new float[1200000];

  /**
   * Default constructor.
   * Initializes parameters following the pattern of other generators.
   */
  public HillClimbing() {
    super();
    this.typeAcceptation = AcceptType.AcceptBest;
    this.strategy = StrategyType.NORMAL;

    if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      this.typeCandidate = CandidateType.GreaterCandidate;
    } else {
      this.typeCandidate = CandidateType.SmallerCandidate;
    }

    this.candidateValue = new CandidateValue();
    this.generatorType = GeneratorType.HillClimbing;
    this.weight = 50.0f;

    // Link the specific array with the protected field of the superclass
    this.listCountBetterGender = this.listCountBetterGenderHillClimbing;

    // Initialize traces and counters
    listTrace[0] = this.weight;
    this.countBetterGender = 0;
    this.countGender = 0;
    this.listCountBetterGenderHillClimbing[0] = 0;
    this.listCountGender[0] = 0;
  }

  /**
   * Generates a new candidate state from the neighborhood of the reference state.
   *
   * @param operatornumber [Integer] The operator number to use for generating neighbors.
   * @return [State] The generated candidate state.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    List<State> neighborhood = Strategy.getStrategy()
        .getProblem()
        .getOperator()
        .generatedNewState(stateReferenceHC, operatornumber);

    return candidateValue.stateCandidate(
        stateReferenceHC,
        typeCandidate,
        strategy,
        operatornumber,
        neighborhood
    );
  }

  /**
   * Updates the reference state using the configured acceptance policy.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    IFFactoryAcceptCandidate ifAcceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifAcceptCandidate.createAcceptCandidate(typeAcceptation);

    Boolean accept = candidate.acceptCandidate(stateReferenceHC, stateCandidate);
    if (Boolean.TRUE.equals(accept)) {
      stateReferenceHC = stateCandidate;
    }
  }

  /**
   * Gets the list of reference states (best solutions found).
   * @return [List<State>] The list of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceHC != null && !listStateReference.contains(stateReferenceHC)) {
      listStateReference.add(stateReferenceHC);
    }
    return new ArrayList<>(listStateReference);
  }

  /**
   * Gets the current reference state.
   * @return [State] The current reference state.
   */
  @Override
  public State getReference() {
    return stateReferenceHC;
  }

  /**
   * Sets the reference state explicitly.
   * @param stateRef [State] The reference state to set.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceHC = stateRef;
  }

  /**
   * Sets the initial reference state.
   * @param stateInitialRef [State] The initial reference state to set.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceHC = stateInitialRef;
  }

  /**
   * Gets the specific generator type.
   * @return [GeneratorType] The specific generator type.
   */
  public GeneratorType getGeneratorType() {
    return generatorType;
  }

  /**
   * Sets the specific generator type.
   * @param generatorType [GeneratorType] The generator type to set.
   */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /**
   * Gets the type of the generator.
   * @return [GeneratorType] The type of the generator.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Gets the list of son states (not used in Hill Climbing).
   * @return [List<State>] An empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Allows changing the type of candidate used (greater, lesser, etc.).
   */
  public void setTypeCandidate(CandidateType typeCandidate) {
    this.typeCandidate = typeCandidate;
  }

  /**
   * Indicates if the candidate improves the reference, according to the problem type.
   * This is used as a "reward" in portfolio generators.
   * @param stateCandidate [State] The candidate state to evaluate.
   * @return [boolean] True if the candidate improves the reference, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateReferenceHC == null || stateCandidate == null
        || stateCandidate.getEvaluation() == null
        || stateReferenceHC.getEvaluation() == null
        || stateCandidate.getEvaluation().isEmpty()
        || stateReferenceHC.getEvaluation().isEmpty()) {
      return false;
    }

    double candidateEval = stateCandidate.getEvaluation().get(0);
    double refEval = stateReferenceHC.getEvaluation().get(0);

    if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      return candidateEval > refEval;
    } else {
      return candidateEval < refEval;
    }
  }

  /**
   * Gets the weight of the generator.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight of the generator.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Returns the array of "better gender" counters (for 10 periods).
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderHillClimbing;
  }

  /**
   * Returns the array of "gender" counters (for 10 periods).
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Gets the trace array.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }
}
