package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import es.ull.esit.app.factory_interface.IFFactoryAcceptCandidate;
import es.ull.esit.app.factory_method.FactoryAcceptCandidate;

import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;

/**
 * Generator that implements a multi-objective Hill Climbing based on distance.
 */
public class MultiobjectiveHillClimbingDistance extends Generator {

  /** Utility used to select a candidate from a neighbourhood. */
  protected CandidateValue candidatevalue;
  /** Acceptance rule for multiobjective optimisation. */
  protected AcceptType typeAcceptation;
  /** Strategy used when selecting candidates. */
  protected StrategyType strategy;
  /** Candidate selection rule (non–dominated, etc.). */
  protected CandidateType typeCandidate;
  /** Current reference state of the algorithm. */
  protected State stateReferenceHC;
  /** Factory for acceptance of candidates. */
  protected IFFactoryAcceptCandidate ifacceptCandidate;
  /** Generator type identifier. */
  protected GeneratorType generatorType;
  /** List of reference states visited by the algorithm. */
  protected List<State> listStateReference = new ArrayList<>();
  /** Weight associated with this generator in the multi–generator framework. */
  protected float weight;
  /** Trace of weights during the execution. */
  protected List<Float> listTrace = new ArrayList<>();
  /** List of visited states to avoid cycles. */
  private List<State> visitedState = new ArrayList<>();
  /** Size of the neighborhood to be generated. */
  public static int sizeNeighbors;
  /**
   * List containing the distances of each solution in the estimated Pareto front.
   */
  public static List<Double> distanceSolution = new ArrayList<>();

  /**
   * Default constructor. It initialises the internal configuration and
   * statistics arrays for the multiobjective Hill Climbing based on distance.
   */
  public MultiobjectiveHillClimbingDistance() {
    super();
    this.typeAcceptation = AcceptType.AcceptNotDominated;
    this.strategy = StrategyType.NORMAL;
    this.typeCandidate = CandidateType.NotDominatedCandidate;
    this.candidatevalue = new CandidateValue();
    this.generatorType = GeneratorType.MultiobjectiveHillClimbingDistance;
    this.weight = 50;
    listTrace.add(weight);
  }

  /**
   * Generates a new state based on the current reference state and the specified
   * operator number.
   * 
   * @param operatornumber [Integer] The operator number to be used for generating
   *                       the new state.
   * @return [State] The generated state.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a required class is not found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If an illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a required method is not found.
   * 
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    List<State> neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceHC,
        operatornumber);
    return candidatevalue.stateCandidate(stateReferenceHC, typeCandidate, strategy, operatornumber, neighborhood);
  }

  /**
   * Updates the reference state based on the candidate state and the current
   * iteration count.
   * 
   * @param stateCandidate         [State] The candidate state to be considered
   *                               for updating the reference.
   * @param countIterationsCurrent [Integer] The current iteration count.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a required class is not found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If an illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a required method is not found.
   * 
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    initializeParetoListIfNeeded(stateCandidate);

    ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);

    List<State> refList = Strategy.getStrategy().getListRefPoblacFinal();
    State lastState = refList.get(refList.size() - 1);

    boolean accept = Boolean.TRUE.equals(candidate.acceptCandidate(lastState, stateCandidate.copy()));
    if (accept) {
      setReferenceAndResetVisited(stateCandidate);
      getReferenceList();
      return;
    }

    List<State> neighborhood = Strategy.getStrategy().getProblem().getOperator()
        .generatedNewState(stateReferenceHC, sizeNeighbors);

    if (tryFallbackFromNeighborhood(neighborhood)) {
      getReferenceList();
      return;
    }

    if (tryRandomRestart(candidate, lastState)) {
      getReferenceList();
      return;
    }

    getReferenceList();
  }

  /**
   * Initializes the Pareto front list if it is empty.
   * 
   * @param stateCandidate [State] Candidate state to consider for initialization.
   */
  private void initializeParetoListIfNeeded(State stateCandidate) {
    if (Strategy.getStrategy().getListRefPoblacFinal().isEmpty()) {
      if (stateReferenceHC == null && stateCandidate != null) {
        stateReferenceHC = stateCandidate.copy();
      }
      if (stateReferenceHC != null) {
        Strategy.getStrategy().getListRefPoblacFinal().add(stateReferenceHC.copy());
      }
      distanceSolution.add(0.0d);
    }
  }

  /**
   * Sets the reference state and resets the visited states list.
   * 
   * @param newRef [State] The new reference state to be set.
   */
  private void setReferenceAndResetVisited(State newRef) {
    stateReferenceHC = newRef.copy();
    visitedState = new ArrayList<>();
  }

  /**
   * Attempts to find a fallback state from the neighborhood that is not in the
   * visited states.
   * 
   * @param neighborhood [List<State>] The list of neighboring states.
   * @return [boolean] True if a fallback state was found and set, false otherwise.
   */
  private boolean tryFallbackFromNeighborhood(List<State> neighborhood) {
    for (int i = 0; i < neighborhood.size(); i++) {
      if (!contains(neighborhood.get(i))) {
        State mostDistant = solutionMoreDistance(Strategy.getStrategy().getListRefPoblacFinal(), distanceSolution);
        if (mostDistant != null) {
          stateReferenceHC = mostDistant;
          visitedState.add(stateReferenceHC);
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Attempts to perform a random restart by generating random states until a
   * suitable one is found or the maximum number of attempts is reached.
   * 
   * @param candidate  [AcceptableCandidate] The candidate acceptance strategy.
   * @param lastState  [State] The last reference state.
   * @return [boolean] True if a suitable random state was found and set, false
   *         otherwise.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a required class is not found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If an illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a required method is not found.
   */
  private boolean tryRandomRestart(AcceptableCandidate candidate, State lastState)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    int attempts = 0;
    while (attempts < sizeNeighbors) {
      State randomState = Strategy.getStrategy().getProblem().getOperator().generateRandomState(1).get(0);
      if (!contains(randomState)) {
        Strategy.getStrategy().getProblem().evaluate(randomState);
        visitedState.add(randomState);
        boolean accepted = Boolean.TRUE.equals(candidate.acceptCandidate(lastState, randomState.copy()));
        if (accepted) {
          setReferenceAndResetVisited(randomState);
          return true;
        }
      }
      attempts++;
    }
    return false;
  }

  /**
   * Finds the solution with the maximum distance from the provided list.
   * 
   * @param state            [List<State>] List of states to evaluate.
   * @param distanceSolution [List<Double>] Corresponding distances of the states.
   * @return [State] The state with the maximum distance, or null if none found.
   */
  private State solutionMoreDistance(List<State> state, List<Double> distanceSolution) {
    Double max = (double) -1;
    int pos = -1;
    Double[] distance = distanceSolution.toArray(new Double[distanceSolution.size()]);
    State[] solutions = state.toArray(new State[state.size()]);
    for (int i = 0; i < distance.length; i++) {
      Double dist = distance[i];
      if (dist > max) {
        max = dist;
        pos = i;
      }
    }
    if (pos != -1)
      return solutions[pos];
    else
      return null;
  }

  /**
   * Returns the list of reference states visited so far. Each call appends a
   * copy of the current reference state.
   * 
   * @return [List<State]] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    listStateReference.add(stateReferenceHC.copy());
    return listStateReference;
  }

  /**
   * Returns the current reference state.
   * 
   * @return [State] The current reference state.
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
   * Returns the generator type.
   * 
   * @return [GeneratorType] Generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Returns the list of son states (not implemented).
   * 
   * @return [List<State>] List of son states.
   */
  @Override
  public List<State> getSonList() {
    return java.util.Collections.emptyList();
  }

  /**
   * Calculates the updated distances when a new solution is added.
   * 
   * @param solution [List<State>] List of current solutions including the new one.
   * @return [List<Double>] Updated list of distances.
   */
  public static List<Double> distanceCalculateAdd(List<State> solution) {
    State[] solutions = solution.toArray(new State[solution.size()]);
    Double distance;
    List<Double> listDist = new ArrayList<>();
    State lastSolution = solution.get(solution.size() - 1);
    for (int k = 0; k < solutions.length - 1; k++) {
      State solA = solutions[k];
      distance = solA.distance(lastSolution);
      listDist.add(distanceSolution.get(k) + distance);
    }
    distance = 0.0;
    if (solutions.length == 1) {
      return distanceSolution;

    } else {

      for (int l = 0; l < solutions.length - 1; l++) {
        State solB = solutions[l];
        distance += lastSolution.distance(solB);
      }
      listDist.add(distance);
      distanceSolution = listDist;

      return distanceSolution;
    }

  }

  /**
   * Checks if the given state is already in the visited states list.
   * 
   * @param state [State] The state to check.
   * @return [boolean] True if the state is in the visited list, false otherwise.
   */
  private boolean contains(State state) {
    boolean found = false;
    for (Iterator<State> iter = visitedState.iterator(); iter.hasNext();) {
      State element = iter.next();
      if (element.comparator(state)) {
        found = true;
      }
    }
    return found;
  }

  /**
   * Decides if the candidate should be considered an improvement for statistics
   * purposes.
   * 
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] true if considered an improvement, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    // Minimal implementation: update internal reference and trace, return true if candidate is non-null.
    if (stateCandidate == null) {
      return false;
    }
    this.stateReferenceHC = stateCandidate.copy();
    if (listTrace.isEmpty() || listTrace.get(listTrace.size() - 1) != weight) {
      listTrace.add(weight);
    }
    return true;
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
    listTrace.add(weight);
  }

  /**
   * Returns an array of counts of better gender updates (not implemented).
   * 
   * @return [int[]] Array of counts.
   */
  @Override
  public int[] getListCountBetterGender() {
    return new int[0];
  }

  /**
   * Returns an array of counts of gender updates (not implemented).
   * 
   * @return [int[]] Array of counts.
   */
  @Override
  public int[] getListCountGender() {
    return new int[0];
  }

  /**
   * Returns the trace of weights during execution.
   * 
   * @return [float[]] Array of weights trace.
   */
  @Override
  public float[] getTrace() {
    float[] out = new float[listTrace.size()];
    for (int i = 0; i < listTrace.size(); i++) {
      out[i] = listTrace.get(i);
    }
    return out;
  }
}
