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
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Hill class that implements a Hill Climbing with Restart generator.
 * 
 */
public class HillClimbingRestart extends Generator {

  /** Number of iterations between restarts. */
  private int count;

  /** Step used to advance the restart counter. */
  private int countCurrent;

  /** List of reference states stored each time a restart occurs. */
  private final List<State> restartReferences = new ArrayList<>();

  /** Helper to obtain a candidate from a neighborhood. */
  private CandidateValue candidatevalue;

  /** Acceptance type used by this generator. */
  private AcceptType typeAcceptation;

  /** Local search strategy type (e.g., NORMAL, TABU…). */
  private StrategyType strategy;

  /** Candidate comparison mode (greater/smaller). */
  private CandidateType typeCandidate;

  /** Current reference state for hill climbing. */
  private State stateReferenceHC;

  /** Generator type. */
  private GeneratorType generatorType;

  /** Weight used in strategy selection. */
  private float weight;

  /** Counters and traces for dynamic problems. */
  private final int[] listCountBetterGenderHillClimbingRestart = new int[10];
  private final int[] listCountGender = new int[10];
  private final float[] listTrace = new float[1200000];


  /**
   * Default constructor.
   * Initializes acceptance/candidate types and statistics structures.
   */
  public HillClimbingRestart() {
    super();

    // Restart parameters
    countCurrent = count;

    this.typeAcceptation = AcceptType.AcceptBest;
    this.strategy = StrategyType.NORMAL;

    // Decide candidate type based on problem type
    if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      this.typeCandidate = CandidateType.GreaterCandidate;
    } else {
      this.typeCandidate = CandidateType.SmallerCandidate;
    }

    this.candidatevalue = new CandidateValue();
    this.generatorType = GeneratorType.HillClimbing;
    this.weight = 50.0f;

    // Initialize trace and counters
    listTrace[0] = this.weight;
    listCountBetterGenderHillClimbingRestart[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the concrete one
    this.listCountBetterGender = this.listCountBetterGenderHillClimbingRestart;
  }

  /**
   * Generates a new candidate state. When the restart condition is met,
   * the current reference is stored and a new random reference state is created.
   *
   * @param operatornumber operator index used by the neighborhood generator.
   * @return the new candidate state.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    // Restart logic
    if (count == Strategy.getStrategy().getCountCurrent()) {
      // Store current reference if available
      if (stateReferenceHC != null) {
        restartReferences.add(new State(stateReferenceHC));
      }

      // New random reference state
      stateReferenceHC = Strategy.getStrategy()
          .getProblem()
          .getOperator()
          .generateRandomState(1)
          .get(0);

      Strategy.getStrategy().getProblem().evaluate(stateReferenceHC);
      count += countCurrent;
    }

    // Generate neighborhood around current reference
    List<State> neighborhood = Strategy.getStrategy()
        .getProblem()
        .getOperator()
        .generatedNewState(stateReferenceHC, operatornumber);

    // Select candidate from neighborhood
    return candidatevalue.stateCandidate(
        stateReferenceHC,
        typeCandidate,
        strategy,
        operatornumber,
        neighborhood
    );
  }

  /**
   * Updates the reference state if the candidate is accepted
   * according to the acceptance policy.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
    Boolean accept = candidate.acceptCandidate(stateReferenceHC, stateCandidate);
    if (Boolean.TRUE.equals(accept)) {
      stateReferenceHC = stateCandidate;
    }
  }

  /**
   * Returns a copy of all reference states handled by this generator:
   * the current reference (if any) plus all stored references from restarts.
   *
   * @return [List<State>] list of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    List<State> referenceList = new ArrayList<>();
    if (stateReferenceHC != null) {
      referenceList.add(stateReferenceHC);
    }
    referenceList.addAll(restartReferences);
    return referenceList;
  }

  @Override
  public State getReference() {
    return stateReferenceHC;
  }

  /**
   * Sets the current reference state.
   * 
   * @param stateRef [State] New reference state.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceHC = stateRef;
  }

  /**
   * Sets the initial reference state for hill climbing.
   * 
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceHC = stateInitialRef;
  }

  /**
   * Gets the generator type.
   * 
   * @return [GeneratorType] The type of the generator.
   */
  public GeneratorType getGeneratorType() {
    return generatorType;
  }

  /**
   * Sets the generator type.
   * 
   * @param generatorType [GeneratorType] The type of the generator.
   */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /**
   * Gets the generator type.
   * 
   * @return [GeneratorType] The type of the generator.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * HillClimbingRestart does not maintain a separate list of sons,
   * so an empty list is returned to avoid null handling issues.
   * 
   * @return [List<State>] An empty list of son states.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Sets the candidate comparison type.
   * 
   * @param typeCandidate [CandidateType] The candidate comparison type.
   */
  public void setTypeCandidate(CandidateType typeCandidate) {
    this.typeCandidate = typeCandidate;
  }

  /**
   * Decides whether the reference should be updated based on the evaluations
   * of the candidate and current reference, and the problem type (Maximize / Minimize).
   *
   * @param stateCandidate [State] The candidate state to evaluate.
   * @return true if the candidate should replace the reference, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateCandidate == null || stateReferenceHC == null
        || stateCandidate.getEvaluation() == null
        || stateReferenceHC.getEvaluation() == null
        || stateCandidate.getEvaluation().isEmpty()
        || stateReferenceHC.getEvaluation().isEmpty()) {

      return false;
    }

    double candidateEval = stateCandidate.getEvaluation().get(0);
    double referenceEval = stateReferenceHC.getEvaluation().get(0);

    ProblemType type = Strategy.getStrategy().getProblem().getTypeProblem();
    if (ProblemType.MAXIMIZAR.equals(type)) {
      return candidateEval > referenceEval;
    } else {
      return candidateEval < referenceEval;
    }
  }

  /**
   * Gets the weight of this generator.
   * 
   * @return [float] The weight of the generator.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight of this generator.
   * 
   * @param weight [float] The new weight of the generator.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Gets the list of better gender counts for this generator.
   * 
   * @return [int[]] The list of better gender counts.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderHillClimbingRestart;
  }

  /**
   * Gets the list of gender counts for this generator.
   * 
   * @return [int[]] The list of gender counts.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Gets the trace of this generator.
   * 
   * @return [float[]] The trace of the generator.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }

  /**
   * Static counter for restarts.
   * 
   * @return [int] The current value of the restart counter.
   */
  public int getCount() {
    return count;
  }

  /**
   * Sets the static counter for restarts.
   * @param countValue [int] The new value for the restart counter.
   */
  public void setCount(int countValue) {
    count = countValue;
  }

  /**
   * Gets the current step count for restarts.
   * 
   * @return [int] The current step count for restarts.
   */
  public int getCountCurrent() {
    return countCurrent;
  }

  /**
   * Sets the current step count for restarts.
   * 
   * @param countCurrentValue [int] The new value for the current step count.
   */
  public void setCountCurrent(int countCurrentValue) {
    countCurrent = countCurrentValue;
  }

  /**
   * Gets the count of genders.
   * @return [int] The current count of genders for this generator.
   */
  public int getCountGender() {
    return countGender;
  }

  /**
   * Sets the count of genders.
   * 
   * @param countGenderValue [int] The new count of genders for this generator.
   */
  public void setCountGender(int countGenderValue) {
    countGender = countGenderValue;
  }

  /**
   * Gets the count of better genders.
   * @return [int] The current count of better genders for this generator.
   */
  public int getCountBetterGender() {
    return countBetterGender;
  }

  /**
   * Sets the count of better genders.
   * 
   * @param countBetterGenderValue [int] The new count of better genders for this generator.
   */
  public void setCountBetterGender(int countBetterGenderValue) {
    countBetterGender = countBetterGenderValue;
  }
}

