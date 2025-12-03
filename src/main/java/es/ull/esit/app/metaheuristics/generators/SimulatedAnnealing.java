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

/**
 * Class that implements a Simulated Annealing generator.
 */
public class SimulatedAnnealing extends Generator {

  /** Utility used to select a candidate from the neighbourhood. */
  private CandidateValue candidateValue;

  /** Acceptance rule for Simulated Annealing. */
  private AcceptType typeAcceptation;

  /** Strategy used when selecting candidates. */
  private StrategyType strategy;

  /** Candidate selection rule (e.g. random neighbour). */
  private CandidateType typeCandidate;

  /** Current reference state for Simulated Annealing. */
  private State stateReferenceSA;

  /** Cooling factor (T_{k+1} = alpha * T_k). */
  private static Double alpha;

  /** Initial temperature. */
  private static Double tinitial;

  /** Final temperature (possibly used externally as stopping condition). */
  private static Double tfinal;

  /* Iteration index (global) at which the next temperature update must be performed. */
  private static int countIterationsT;

  /* Stores the repetition period of the temperature (how many iterations before decreasing temperature again). */
  private int countRept;

  /** Generator type identifier. */
  private GeneratorType typeGenerator;

  /** List of reference states visited by the algorithm. */
  private List<State> listStateReference = new ArrayList<>();

  /** Weight associated with this generator in a multi–generator framework. */
  private float weight;

  /** Local “better gender” statistics array. */
  private int[] listCountBetterGenderSA = new int[10];

  /** Local gender statistics array. */
  private int[] listCountGender = new int[10];

  /** Trace of weight values during the execution. */
  private float[] listTrace = new float[1200000];

  /**
   * Default constructor.
   *
   */
  public SimulatedAnnealing() {
    super();

    this.typeAcceptation = AcceptType.AcceptNotBadT;
    this.strategy = StrategyType.NORMAL;
    this.typeCandidate = CandidateType.RandomCandidate;
    this.candidateValue = new CandidateValue();
    this.typeGenerator = GeneratorType.SimulatedAnnealing;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderSA[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderSA;
  }

  /**
   * Generates a new candidate state from the current reference using the
   * neighbourhood defined by the operator.
   *
   * @param operatornumber [Integer] Operator index.
   * @return [State] Generated candidate state or {@code null} if strategy/problem is not available.
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
      // Not enough context to generate a neighbour
      return null;
    }

    List<State> neighbourhood = strategyInstance
        .getProblem()
        .getOperator()
        .generatedNewState(stateReferenceSA, operatornumber);

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
   * @return [State] Current reference state.
   */
  @Override
  public State getReference() {
    return stateReferenceSA;
  }

  /**
   * Sets the current reference state.
   *
   * @param stateRef [State] New reference state.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceSA = stateRef;
  }

  /**
   * Sets the initial reference state for Simulated Annealing.
   *
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceSA = stateInitialRef;
  }

  /**
   * Updates the reference state according to the Simulated Annealing
   * acceptance criteria and cooling schedule.
   *
   * @param stateCandidate          [State] Candidate state.
   * @param countIterationsCurrent  [Integer] Current global iteration count.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    // Defensive checks: if we do not have a reference or candidate, nothing to do
    if (stateReferenceSA == null || stateCandidate == null) {
      return;
    }

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);

    boolean accept = candidate.acceptCandidate(stateReferenceSA, stateCandidate);
    if (accept) {
      stateReferenceSA = stateCandidate;
    }

    // Cooling schedule:
    // If we reach the iteration in which temperature must be updated,
    // reduce T and set the next update at current + countRept.
    if (countIterationsCurrent != null
        && countIterationsT > 0
        && alpha != null
        && tinitial != null) {

      // First time we enter, store the period
      if (countRept == 0) {
        countRept = countIterationsT;
      }

      if (countIterationsCurrent.equals(countIterationsT)) {
        tinitial = tinitial * alpha;
        countIterationsT = countIterationsT + countRept;
      }
    }
  }

  /**
   * Returns the type of this generator.
   *
   * @return [GeneratorType] Generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.typeGenerator;
  }

  /**
   * Returns the generator type (explicit getter).
   *
   * @return [GeneratorType] Generator type.
   */
  public GeneratorType getTypeGenerator() {
    return typeGenerator;
  }

  /**
   * Sets the generator type (mainly for testing or configuration).
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
   * @return [List<State]] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceSA != null) {
      listStateReference.add(stateReferenceSA);
    }
    return new ArrayList<>(listStateReference);
  }

  /**
   * Simulated Annealing does not explicitly maintain a son list.
   * We return an empty list to avoid null handling.
   *
   * @return [List<State]] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * For Simulated Annealing, we do not use an additional “award” criterion
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
   * Returns the weight associated with this generator.
   *
   * @return [float] Weight value.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight associated with this generator.
   *
   * @param weight [float] New weight value.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Returns the internal statistics array of “better gender” counts.
   *
   * @return [int[]] Better gender counts.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderSA;
  }

  /**
   * Returns the internal statistics array of gender counts.
   *
   * @return [int[]] Gender counts.
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

  /**
   * Gets the cooling factor.
   * 
   * @return [Double] Cooling factor.
   */
  public static Double getAlpha() {
    return alpha;
  }

  /**
   * Sets the cooling factor.
   * 
   * @param aAlpha [Double] New cooling factor.
   */  
  public static void setAlpha(Double aAlpha) {
    alpha = aAlpha;
  }


  /**
   * Gets the initial temperature.
   * 
   * @return [Double] Initial temperature.
   */
  public static Double getTinitial() {
    return tinitial;
  }

  /**
   * Sets the initial temperature.
   * 
   * @param aTinitial [Double] New initial temperature.
   */
  public static void setTinitial(Double aTinitial) {
    tinitial = aTinitial;
  }

  /**
   * Gets the final temperature.
   * 
   * @return [Double] Final temperature.
   */
  public static Double getTfinal() {
    return tfinal;
  }

  /**
   * Sets the final temperature.
   * 
   * @param aTfinal [Double] New final temperature.
   */
  public static void setTfinal(Double aTfinal) {
    tfinal = aTfinal;
  }

  /**
   * Gets the iteration index for the next temperature update.
   * 
   * @return [int] Iteration index.
   */
  public static int getCountIterationsT() {
    return countIterationsT;
  }

  /**
   * Sets the iteration index for the next temperature update.
   * 
   * @param aCountIterationsT [int] New iteration index.
   */
  public static void setCountIterationsT(int aCountIterationsT) {
    countIterationsT = aCountIterationsT;
  }

}
