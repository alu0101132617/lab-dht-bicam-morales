package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
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
import es.ull.esit.app.problem.definition.Problem;

/**
 * Generator class that implements a multi–objective Hill Climbing with restart.
 */
public class MultiobjectiveHillClimbingRestart extends Generator {

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
  private int[] listCountBetterGenderMultiObjHC = new int[10];

  /** Local counter of genders for statistics. */
  private int[] listCountGender = new int[10];

  /** Trace of weights during the execution. */
  private float[] listTrace = new float[1200000];

  /** List of visited states, used to avoid revisiting neighbours. */
  private List<State> visitedStates = new ArrayList<>();

  /** Neighbourhood size used in the restart logic. */
  private static int sizeNeighbors;

  /**
   * Default constructor. It initialises the internal configuration and
   * statistics arrays for the multiobjective Hill Climbing with restart.
   * 
   */
  public MultiobjectiveHillClimbingRestart() {
    super();
    this.typeAcceptation = AcceptType.AcceptNotDominated;
    this.strategy = StrategyType.NORMAL;
    this.typeCandidate = CandidateType.NotDominatedCandidate;
    this.candidateValue = new CandidateValue();
    this.generatorType = GeneratorType.MultiobjectiveHillClimbingRestart;

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderMultiObjHC[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderMultiObjHC;
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
   * rule and restart logic.
   *
   * @param stateCandidate        [State] Candidate state.
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

    Strategy strategyInstance = Strategy.getStrategy();
    if (strategyInstance == null || strategyInstance.getProblem() == null) {
      return;
    }

    Problem problem = strategyInstance.getProblem();
    if (problem.getOperator() == null) {
      return;
    }

    List<State> refList = strategyInstance.getListRefPoblacFinal();

    // Add the first solution to the list of non–dominated solutions if empty
    if (refList.isEmpty()) {
      refList.add(stateReferenceHC.copy());
    }

    IFFactoryAcceptCandidate ifacceptCandidate = new FactoryAcceptCandidate();
    AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);

    // Last non–dominated state
    State lastState = refList.get(refList.size() - 1);

    // Neighbourhood of current reference
    List<State> neighbourhood =
        problem.getOperator().generatedNewState(stateReferenceHC, sizeNeighbors);

    boolean accept = candidate.acceptCandidate(lastState, stateCandidate.copy());

    if (accept) {
      // Directly accept the candidate
      stateReferenceHC = stateCandidate.copy();
      visitedStates = new ArrayList<>();
      
    } else {
      // Try to find a not–visited neighbour that can be accepted
      boolean stop = false;
      int i = 0;

      while (i < neighbourhood.size() && !stop) {
        State neighbour = neighbourhood.get(i);
        if (!containsState(neighbour)) {
          stateCandidate = neighbour;
          problem.evaluate(stateCandidate);
          visitedStates.add(stateCandidate);
          accept = candidate.acceptCandidate(lastState, stateCandidate.copy());
          stop = true;
        }
        i++;
      }

      // If all neighbours were visited, generate random states until a new one is found
      while (!stop) {
        State randomState =
            problem.getOperator().generateRandomState(1).get(0);
        if (!containsState(randomState)) {
          stateCandidate = randomState;
          problem.evaluate(stateCandidate);
          stop = true;
          accept = candidate.acceptCandidate(lastState, stateCandidate.copy());
        }
      }

      if (accept) {
        stateReferenceHC = stateCandidate.copy();
        visitedStates = new ArrayList<>();
      }
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
   * Multiobjective Hill Climbing with restart does not maintain an explicit son list.
   *
   * @return [List<State]] Empty list.
   */
  @Override
  public List<State> getSonList() {
    return new ArrayList<>();
  }

  /**
   * Checks if the given state has already been visited.
   *
   * @param state [State] State to check.
   * @return [boolean] true if it has been visited, false otherwise.
   */
  private boolean containsState(State state) {
    if (state == null) {
      return false;
    }
    for (Iterator<State> iter = visitedStates.iterator(); iter.hasNext();) {
      State element = iter.next();
      if (element != null && element.comparator(state)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Decides if the candidate should be considered an improvement for statistics
   * purposes. For multiobjective HC, we simply require the candidate to be
   * different from the current reference.
   *
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] true if considered an improvement, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateReferenceHC == null || stateCandidate == null) {
      return false;
    }
    // Basic heuristic: any different state can be considered an update.
    return !stateReferenceHC.equals(stateCandidate);
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
    return this.listCountBetterGenderMultiObjHC;
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

  /**
   * Returns the neighbourhood size used in the restart logic.
   *
   * @return [int] Neighbourhood size.
   */
  public static int getSizeNeighbors() {
    return sizeNeighbors;
  }

  /**
   * Sets the neighbourhood size used in the restart logic.
   *
   * @param sizeNeighbors [int] Neighbourhood size.
   */
  public static void setSizeNeighbors(int sizeNeighbors) {
    MultiobjectiveHillClimbingRestart.sizeNeighbors = sizeNeighbors;
  }
}
