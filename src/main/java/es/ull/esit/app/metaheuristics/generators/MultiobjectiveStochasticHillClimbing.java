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
 * Generator that implements a multi-objective Stochastic Hill Climbing.
 */
public class MultiobjectiveStochasticHillClimbing extends Generator {

  /** Utility used to select a candidate from a neighbourhood. */
  private CandidateValue candidateValue;

  /** Acceptance rule for multiobjective optimisation. */
  private AcceptType typeAcceptation;

  /** Strategy used when selecting candidates. */
  private StrategyType strategy;

  /** Candidate selection rule (non–dominated, etc.). */
  private CandidateType typeCandidate;

  /** Current reference state of the algorithm. */
  private State stateReferenceHC;

  /** Generator type identifier. */
  private GeneratorType generatorType;

  /** List of reference states visited by the algorithm. */
  private List<State> listStateReference = new ArrayList<>();

  /** Weight associated with this generator in the multi–generator framework. */
  private float weight;

  /** Local counter of “better gender” for statistics. */
  private int[] listCountBetterGenderMultiObjSHC = new int[10];

  /** Local counter of genders for statistics. */
  private int[] listCountGender = new int[10];

  /** Trace of weights during the execution. */
  private float[] listTrace = new float[1200000];

  /**
   * Default constructor. It initialises the internal configuration and
   * statistics arrays for the multiobjective Stochastic Hill Climbing.
   */
  public MultiobjectiveStochasticHillClimbing() {
    super();
    this.typeAcceptation = AcceptType.AcceptNotDominated;
    this.strategy = StrategyType.NORMAL;
    this.typeCandidate = CandidateType.NotDominatedCandidate;
    this.candidateValue = new CandidateValue();
    this.generatorType = GeneratorType.MultiobjectiveStochasticHillClimbing;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderMultiObjSHC[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderMultiObjSHC;
  }

  /**
   * Generates a new candidate state from the current reference using the
   * neighbourhood defined by the given operator.
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
      if (problem != null && problem.getOperator() != null && stateReferenceHC != null) {
        neighbourhood = problem.getOperator().generatedNewState(stateReferenceHC, operatornumber);
      }
    }

    return candidateValue.stateCandidate(
        stateReferenceHC,
        typeCandidate,
        strategy,
        operatornumber,
        neighbourhood
    );
  }

  /**
   * Updates the reference solution according to the multiobjective acceptance
   * rule.
   *
   * @param stateCandidate         [State] Candidate state.
   * @param countIterationsCurrent [Integer] Current iteration.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    // Defensive checks: if there is no reference or candidate, nothing to do
    if (stateReferenceHC == null || stateCandidate == null) {
      return;
    }

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);

    boolean accept = candidate.acceptCandidate(stateReferenceHC, stateCandidate);

    if (accept) {
      stateReferenceHC = stateCandidate.copy();
    }

    // Track reference solutions for statistics/audit
    getReferenceList();
  }

  /**
   * Returns the list of reference states visited so far. Each call appends a
   * copy of the current reference state.
   *
   * @return [List<State]] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceHC != null) {
      listStateReference.add(stateReferenceHC.copy());
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
   * Sets the initial reference state.
   *
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceHC = stateInitialRef;
  }

  /**
   * Returns the generator type.
   *
   * @return [GeneratorType] Generator type.
   */
  public GeneratorType getGeneratorType() {
    return generatorType;
  }

  /**
   * Sets the generator type.
   *
   * @param generatorType [GeneratorType] Generator type.
   */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /**
   * Returns the generator type (required by abstract base class).
   *
   * @return [GeneratorType] Generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Multiobjective Stochastic Hill Climbing does not maintain an explicit son list.
   *
   * @return [List<State]] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Decides if the candidate should be considered an improvement for statistics
   * purposes. Here we use a simple comparison on the first objective.
   *
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] true if considered an improvement, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateReferenceHC == null || stateCandidate == null) {
      return false;
    }
    if (stateReferenceHC.getEvaluation() == null
        || stateCandidate.getEvaluation() == null
        || stateReferenceHC.getEvaluation().isEmpty()
        || stateCandidate.getEvaluation().isEmpty()) {
      return false;
    }

    double refEval = stateReferenceHC.getEvaluation().get(0);
    double candEval = stateCandidate.getEvaluation().get(0);

    Strategy strategyInstance = Strategy.getStrategy();
    Problem problem = (strategyInstance != null) ? strategyInstance.getProblem() : null;

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
    return this.listCountBetterGenderMultiObjSHC;
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
