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
 * Class that implements a Random Search generator.
 */
public class RandomSearch extends Generator {

  /** Utility to select a candidate from a neighbourhood. */
  private CandidateValue candidatevalue;

  /** Acceptance rule used by the generator. */
  private AcceptType typeAcceptation;

  /** Strategy type used when selecting candidates. */
  private StrategyType strategy;

  /** Candidate selection type. */
  private CandidateType typeCandidate;

  /** Current reference state for Random Search. */
  private State stateReferenceRS;

  /** Generator type. */
  private GeneratorType typeGenerator;

  /** Weight associated with this generator. */
  private float weight;

  /** Global list of reference states for population-based algorithms. */
  private static List<State> listStateReference;

 
  private int[] listCountBetterGenderRandomSearch = new int[10];
  private int[] listCountGender = new int[10];
  private float[] listTrace = new float[1200000];

  /**
   * Default constructor.
   */
  public RandomSearch() {
    super();
    this.typeAcceptation = AcceptType.AcceptBest;
    this.strategy = StrategyType.NORMAL;
    this.typeCandidate = CandidateType.RandomCandidate;
    this.candidatevalue = new CandidateValue();
    this.typeGenerator = GeneratorType.RandomSearch;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderRandomSearch[0] = 0;
    listCountGender[0] = 0;

    // Link superclass stats array to local one
    this.listCountBetterGender = this.listCountBetterGenderRandomSearch;

    listStateReference = new ArrayList<>();
  }

  /**
   * Generates a new random state using the underlying problem operator.
   *
   * @param operatornumber [Integer] Operator number to use for generation.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    List<State> neighborhood =
        Strategy.getStrategy().getProblem().getOperator().generateRandomState(operatornumber);

    State statecandidate = candidatevalue.stateCandidate(
        stateReferenceRS, typeCandidate, strategy, operatornumber, neighborhood
    );

    // Para acceder desde los algoritmos basados en poblaciones de puntos
    if (GeneticAlgorithm.getCountRef() != 0
        || EvolutionStrategies.getCountRef() != 0
        || DistributionEstimationAlgorithm.getCountRef() != 0
        || ParticleSwarmOptimization.getCountRef() != 0) {
      listStateReference.add(statecandidate);
    }

    return statecandidate;
  }

  /**
   * Gets the current reference state for Random Search.
   * 
   * @return [State] Current reference state.
   */
  @Override
  public State getReference() {
    return stateReferenceRS;
  }

  /**
   * Sets the initial reference state for Random Search.
   * 
   * @param stateInitialRef [State] Initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceRS = stateInitialRef;
  }

  /**
   * Updates the reference state based on the candidate state and acceptance criteria.
   * 
   * @param stateCandidate [State] candidate state.
   * @param countIterationsCurrent [Integer] current iteration count.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class is not found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException If a method is not found.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
    boolean accept = candidate.acceptCandidate(stateReferenceRS, stateCandidate);

    if(accept) {
      stateReferenceRS = stateCandidate;
    }
  }

  /**
   * Gets the type of generator.
   * 
   * @return [GeneratorType] Type of generator.
   */
  @Override
  public GeneratorType getType() {
    return this.typeGenerator;
  }

  /**
   * Gets the type of generator.
   * 
   * @return [GeneratorType] Type of generator.
   */
  public GeneratorType getTypeGenerator() {
    return typeGenerator;
  }

  /**
   * Sets the type of generator.
   * 
   * @param typeGenerator [GeneratorType] type of generator to set.
   */
  public void setTypeGenerator(GeneratorType typeGenerator) {
    this.typeGenerator = typeGenerator;
  }

  /**
   * Gets the list of reference states.
   * 
   * @return [List<State>] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    if (stateReferenceRS != null) {
      listStateReference.add(stateReferenceRS);
    }
    return listStateReference;
  }

  /**
   * Gets the list of son states (empty for Random Search).
   * 
   * @return [List<State>] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Awards an update to the reference state.
   * 
   * @param stateCandidate [State] candidate state.
   * @return [boolean] whether the update is awarded.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    // Random Search no tiene criterio de "premio" adicional
    return false;
  }

  /**
   * Gets the weight of the generator.
   * 
   * @return [float] weight of the generator.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight of the generator.
   * 
   * @param weight [float] weight to set.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Gets the list of better gender counts.
   * 
   * @return [int[]] List of better gender counts.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderRandomSearch;
  }

  /**
   * Gets the list of gender counts.
   * 
   * @return [int[]] List of gender counts.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Gets the trace of weights over iterations.
   * 
   * @return float[] trace of weights.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }

  /**
   * Gets the global list of reference states.
   * 
   * @return [State] list of reference states.
   */
  public static List<State> getListStateReference() {
    return listStateReference;
  }

  /**
   * Sets the global list of reference states.
   * 
   * @param listStateReference [State] list of reference states.
   */
  public static void setListStateReference(List<State> listStateReference) {
    RandomSearch.listStateReference = listStateReference;
  }
}
