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
 * Generator that implements the Multi-case Simulated Annealing (MC-SA) algorithm.
 */
public class MultiCaseSimulatedAnnealing extends Generator {

  /** Utility class used to select a candidate from a neighbourhood. */
  private CandidateValue candidateValue;

  /** Acceptance type used by the algorithm. */
  private AcceptType typeAcceptation;

  /** Strategy type used to choose candidates. */
  private StrategyType strategy;

  /** Candidate selection rule (e.g. random, best, etc.). */
  private CandidateType typeCandidate;

  /** Current reference state of the algorithm. */
  private State stateReferenceSA;

  /** Cooling factor (α) for simulated annealing. */
  private static double alpha;

  /** Initial temperature. */
  private static double tinitial;

  /** Final temperature (may be used externally by the strategy). */
  private static double tfinal;

  /** Iteration count at which the temperature must be updated. */
  private int countIterationsT;

  /** Type of generator. */
  private GeneratorType typeGenerator;

  /** List of reference states visited by the algorithm. */
  private List<State> listStateReference = new ArrayList<>();

  /** Weight associated with this generator in the multi-generator framework. */
  private float weight;

  /** Local counter of better genders for statistics (size 10). */
  private int[] listCountBetterGenderMultiCaseSA = new int[10];

  /** Local counter of genders for statistics (size 10). */
  private int[] listCountGender = new int[10];

  /** Trace of weights during the execution (for statistics). */
  private float[] listTrace = new float[1200000];

  /**
   * Gets the type of generator.
   *
   * @return [GeneratorType] The type of generator.
   */
  public GeneratorType getTypeGenerator() {
    return typeGenerator;
  }

  /**
   * Sets the type of generator.
   *
   * @param typeGenerator [GeneratorType] The type of generator.
   */
  public void setTypeGenerator(GeneratorType typeGenerator) {
    this.typeGenerator = typeGenerator;
  }

  /**
   * Default constructor. It initialises the internal configuration for
   * Multi-case Simulated Annealing and basic statistics arrays.
   */
  public MultiCaseSimulatedAnnealing() {
    super();
    this.typeAcceptation = AcceptType.AcceptMulticase;
    this.strategy = StrategyType.NORMAL;
    this.typeCandidate = CandidateType.RandomCandidate;
    this.candidateValue = new CandidateValue();
    this.typeGenerator = GeneratorType.MultiCaseSimulatedAnnealing;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderMultiCaseSA[0] = 0;
    listCountGender[0] = 0;

    this.listCountBetterGender = this.listCountBetterGenderMultiCaseSA;
  }

  /**
   * Generates a new candidate state using the current reference state and the
   * neighbourhood defined by the given operator.
   *
   * @param operatornumber [Integer] the operator index.
   * @return [State] the generated candidate state.
   * @throws IllegalArgumentException  if an illegal argument is provided.
   * @throws SecurityException         if a security violation occurs.
   * @throws ClassNotFoundException    if a required class cannot be found.
   * @throws InstantiationException    if a class cannot be instantiated.
   * @throws IllegalAccessException    if there is an illegal access.
   * @throws InvocationTargetException if a reflective invocation fails.
   * @throws NoSuchMethodException     if a required method cannot be found.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    List<State> neighbourhood = new ArrayList<>();
    Problem problem = Strategy.getStrategy().getProblem();

    if (problem != null && problem.getOperator() != null && stateReferenceSA != null) {
      neighbourhood = problem.getOperator().generatedNewState(stateReferenceSA, operatornumber);
    }

    return candidateValue.stateCandidate(
        stateReferenceSA,
        typeCandidate,
        strategy,
        operatornumber,
        neighbourhood
    );
  }

  /**
   * Returns the current reference state.
   *
   * @return [State] The current reference state.
   */
  @Override
  public State getReference() {
    return stateReferenceSA;
  }

  /**
   * Sets the current reference state.
   *
   * @param stateRef [State] The new reference state.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceSA = stateRef;
  }

  /**
   * Sets the initial reference state.
   *
   * @param stateInitialRef [State] The initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceSA = stateInitialRef;
  }

  /**
   * Updates the reference state according to the Multi-case SA acceptance rule
   * and updates the temperature when required.
   *
   * @param stateCandidate [State] Candidate state.
   * @param countIterationsCurrent [Integer] Current iteration count.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a required class cannot be found.
   * @throws InstantiationException If a class cannot be instantiated.
   * @throws IllegalAccessException If there is an illegal access.
   * @throws InvocationTargetException If a reflective invocation fails.
   * @throws NoSuchMethodException If a required method cannot be found.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    // Save the current period for temperature updates
    int countRept = countIterationsT;

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
    boolean accept = candidate.acceptCandidate(stateReferenceSA, stateCandidate);

    if (accept) {
      // Defensivo: copy sólo si no es null
      stateReferenceSA = (stateCandidate == null) ? null : stateCandidate.copy();
    }

    if (countIterationsCurrent != null && countIterationsCurrent.equals(countIterationsT)) {
      // Classical geometric cooling schedule
      if (getTinitial() != 0.0 && alpha != 0.0) {
       setTinitial(getTinitial() * alpha);
      }
      // Next temperature update will happen after another 'countRept' iterations
      countIterationsT = countIterationsT + countRept;
    }

    // Track the reference state for statistics/audit
    getReferenceList();
  }

  /**
   * Gets the generator type.
   *
   * @return [GeneratorType] The generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.typeGenerator;
  }

  /**
   * Returns the list of reference states visited so far. Each call stores a copy
   * of the current reference state in the internal list and then returns it.
   *
   * @return [List<State]] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceSA != null) {
      listStateReference.add(stateReferenceSA.copy());
    }
    return new ArrayList<>(listStateReference);
  }

  /**
   * Multi-case Simulated Annealing does not maintain an explicit son list.
   *
   * @return [List<State]] An empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Decides if the given candidate should be considered as an improvement over
   * the current reference state when updating statistics.
   *
   *
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] true if the candidate should be considered an improvement, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateReferenceSA == null || stateCandidate == null) {
      return false;
    }
    if (stateReferenceSA.getEvaluation() == null
        || stateCandidate.getEvaluation() == null
        || stateReferenceSA.getEvaluation().isEmpty()
        || stateCandidate.getEvaluation().isEmpty()) {
      return false;
    }

    double refEval = stateReferenceSA.getEvaluation().get(0);
    double candEval = stateCandidate.getEvaluation().get(0);

    Problem problem = Strategy.getStrategy().getProblem();
    if (problem == null || problem.getTypeProblem() == null) {
      // Fallback: assume maximisation
      return candEval > refEval;
    }

    if (problem.getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      return candEval > refEval;
    } else {
      return candEval < refEval;
    }
  }

  /**
   * Gets the weight of this generator.
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
   * Returns the internal array with the number of times this generator has
   * produced a better gender (for dynamic problems).
   *
   * @return [int[]] Counts of better genders.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderMultiCaseSA;
  }

  /**
   * Returns the internal array with the number of times this generator has
   * produced genders (for dynamic problems).
   *
   * @return [int[]] Counts of genders.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Returns the trace of weight changes over time.
   *
   * @return [float[]] Trace values.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }

  /**
   * Gets the final temperature used in the simulated annealing process.
   *
   * @return [double] The final temperature.
   */
  public static double tfinal() {
    return tfinal;
  }


  /**
   * Sets the final temperature used in the simulated annealing process.
   * 
   * @param finalTemperature [double] The final temperature to set.
   */
  public static void setTfinal(double finalTemperature) {
    tfinal = finalTemperature;
  }

  /**
   * Gets the initial temperature used in the simulated annealing process.
   *
   * @return [double] The initial temperature.
   */  
  public static double getTinitial() {
    return tinitial;  
  }

  /**
   * Sets the initial temperature used in the simulated annealing process.
   * 
   * @param initialTemperature [double] The initial temperature to set.
   */
  public static void setTinitial(double initialTemperature) {
    tinitial = initialTemperature;
  }

  
   

}
