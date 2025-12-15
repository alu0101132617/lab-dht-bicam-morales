package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the Particle Swarm Optimization (PSO) generator.
 * It manages:
 *  - A list of particles.
 *  - Local best per swarm (lBest).
 *  - Global best (gBest).
 */
public class ParticleSwarmOptimization extends Generator {

  /** Reference state for PSO (usually the global best). */
  private State stateReferencePSO;

  /** List of reference states visited during the search (gBest history). */
  private List<State> listStateReference = new ArrayList<>();

  /** List of particles in the swarm(s). */
  private List<Particle> listParticle = new ArrayList<>();

  /** Generator type. */
  private GeneratorType generatorType;

  /** Total number of particles (coutSwarm * countParticleBySwarm). */
  private static int countRef = 0;

  /** Index of the current particle being updated (global). */
  private static int countParticle = 0;

  /** Number of swarms. */
  private static int coutSwarm = 0;

  /** Number of particles per swarm. */
  private static int countParticleBySwarm = 0;

  /** Weight associated with this generator (for multi-generator schemes). */
  private float weight = 50.0f;

  /** Maximum inertia weight. */
  private static double wmax = 0.9;

  /** Minimum inertia weight. */
  private static double wmin = 0.2;

  /** Cognitive and social learning factors. */
  private static int learning1 = 2;
  private static int learning2 = 2;

  /** Constriction factor (computed from learning factors). */
  private static double constriction;

  /** Flag indicating whether PSO works in binary mode. */
  private static boolean binary = false;

  /** Local best per swarm (shared by particles). */
  private static State[] lBest;

  /** Global best among all swarms. */
  private static State gBest;

  /** Current iteration counter for PSO. */
  private static int countCurrentIterPSO;

  /** Local “better gender” statistics. */
  private int[] listCountBetterGenderPSO = new int[10];

  /** Local gender statistics. */
  private int[] listCountGender = new int[10];

  /** Trace of weight values over time. */
  private float[] listTrace = new float[1200000];

  /**
   * Default constructor.
   * Initialises the PSO generator with particles from RandomSearch reference states
   * or reuses an existing particle list from the strategy map.
   * 
   * @throws IllegalArgumentException If an error occurs during initialisation.
   * @throws SecurityException If an error occurs during initialisation.
   * @throws ClassNotFoundException If an error occurs during initialisation.
   * @throws InstantiationException If an error occurs during initialisation.
   * @throws IllegalAccessException If an error occurs during initialisation.
   * @throws InvocationTargetException If an error occurs during initialisation.
   * @throws NoSuchMethodException If an error occurs during initialisation.
   */
  public ParticleSwarmOptimization() {
    super();

    // Total number of particles = number of swarms * particles per swarm
    countRef = coutSwarm * countParticleBySwarm;

    // Build particle list from reference states (or reuse existing list)
    this.listParticle = getListStateRef();

    this.generatorType = GeneratorType.ParticleSwarmOptimization;
    this.weight = 50.0f;

    // Statistics initialisation
    listTrace[0] = this.weight;
    listCountBetterGenderPSO[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderPSO;

    // Initialise lBest and gBest only if there are particles and swarms
    if (!listParticle.isEmpty() && coutSwarm > 0) {
      lBest = new State[coutSwarm];
      countCurrentIterPSO++;
      inicialiceLBest();
      gBest = gBestInicial();
      stateReferencePSO = gBest;
    } else {
      lBest = new State[0];
      gBest = null;
      stateReferencePSO = null;
    }

    // Reset current particle counter
    countParticle = 0;
  }

  /**
   * Moves one particle using its internal PSO update and returns its new state.
   *
   * @param operatornumber [Integer] Not used in PSO.
   * @return [State] The new state of the current particle after movement.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    if (listParticle.isEmpty() || countRef == 0) {
      return null;
    }

    if (countParticle >= countRef) {
      countParticle = 0;
    }

    Particle currentParticle = listParticle.get(countParticle);
    currentParticle.generate(1);
    return currentParticle.getStateActual();
  }

  /**
   * Initialises the local best (lBest) for each swarm from the personal
   * best of the particles.
   * 
   */
  public void inicialiceLBest() {
    Strategy strategy = Strategy.getStrategy();
    Problem problem = (strategy == null) ? null : strategy.getProblem();
    ProblemType type = (problem == null) ? null : problem.getTypeProblem();

    if (listParticle.isEmpty() || coutSwarm <= 0 || countParticleBySwarm <= 0) {
      return;
    }

    boolean isMax = (type == null || type.equals(ProblemType.MAXIMIZAR));
    int localCount = 0;

    for (int j = 0; j < coutSwarm; j++) {
      State reference = listParticle.get(localCount).getStatePBest();
      int end = Math.min(localCount + countParticleBySwarm, listParticle.size());

      for (int i = localCount; i < end; i++) {
        State pBest = listParticle.get(i).getStatePBest();
        double current = pBest.getEvaluation().get(0);
        double best = reference.getEvaluation().get(0);
        if ((isMax && current > best) || (!isMax && current < best)) {
          reference = pBest;
        }
      }

      lBest[j] = reference;
      localCount = end;
    }
  }

  /**
   * Returns the current reference state.
   * For PSO, this is the global best if available, otherwise the internal reference.
   * 
   * @return [State] The current reference state.
   */
  @Override
  public State getReference() {
    return (gBest != null) ? gBest : stateReferencePSO;
  }

  /**
   * Builds the internal list of particles from the reference list of
   * {@link RandomSearch}, or reuses an existing particle list from a previous
   * PSO instance in the strategy map.
   *
   * @return [List<Particle>] List of particles.
   */
  private List<Particle> getListStateRef() {
    boolean found = false;
    Strategy strategy = Strategy.getStrategy();

    if (strategy == null) {
      return this.setListParticle(new ArrayList<>());
    }

    List<String> key = strategy.getListKey();
    int count = 0;

    if (RandomSearch.getListStateReference().isEmpty()) {
      return this.setListParticle(new ArrayList<>());
    }

    while (!found && strategy.getMapGenerators().size() > count) {
      // Search the PSO generator in the map
      if (key.get(count).equals(GeneratorType.ParticleSwarmOptimization.toString())) {
        GeneratorType keyGenerator =
            GeneratorType.valueOf(String.valueOf(key.get(count)));
        ParticleSwarmOptimization generator =
            (ParticleSwarmOptimization) strategy.getMapGenerators().get(keyGenerator);

        if (generator.getListParticle().isEmpty()) {
          // First time: convert RandomSearch states to particles
          for (int j = 0; j < RandomSearch.getListStateReference().size(); j++) {
            if (getListParticle().size() >= countRef && countRef > 0) {
              break;
            }
            ArrayList<Object> velocity = new ArrayList<>();
            State baseState = RandomSearch.getListStateReference().get(j);

            State stateAct = (State) baseState.getCopy();
            stateAct.setCode(new ArrayList<>(baseState.getCode()));
            stateAct.setEvaluation(baseState.getEvaluation());

            State statePBest = (State) baseState.getCopy();
            statePBest.setCode(new ArrayList<>(baseState.getCode()));
            statePBest.setEvaluation(baseState.getEvaluation());

            Particle particle = new Particle(statePBest, stateAct, velocity);
            getListParticle().add(particle);
          }
        } else {
          // Reuse existing particle list
          setListParticle(generator.getListStateReference());
        }
        found = true;
      }
      count++;
    }
    return getListParticle();
  }

  /** Gets the internal PSO reference state. 
   * 
   * @return [State] The internal PSO reference state.
  */
  public State getStateReferencePSO() {
    return stateReferencePSO;
  }

  /** Sets the internal PSO reference state. 
   * 
   * @param stateReferencePSO [State] The new internal PSO reference state.
  */
  public void setStateReferencePSO(State stateReferencePSO) {
    this.stateReferencePSO = stateReferencePSO;
  }

  /** Returns the particle list (for tests / external inspection). 
   * 
   * @return [List<Particle>] The particle list.
  */
  public List<Particle> getListStateReference() {
    return this.getListParticle();
  }

  /** Sets the internal list of reference states (gBest history). 
   * 
   * @param listStateReference [List<State>] The new list of reference states.
  */
  public void setListStateReference(List<State> listStateReference) {
    if (listStateReference == null) {
      this.listStateReference = new ArrayList<>();
    } else {
      this.listStateReference = listStateReference;
    }
  }

  /** Returns the particle list. 
   * 
   * @return [List<Particle>] The particle list.
  */
  public List<Particle> getListParticle() {
    return listParticle;
  }

  /**
   * Sets the particle list.
   *
   * @param listParticle [List<Particle>] The new particle list.
   * @return [List<Particle>] The same list, for chaining.
   */
  public List<Particle> setListParticle(List<Particle> listParticle) {
    if (listParticle == null) {
      this.listParticle = new ArrayList<>();
    } else {
      this.listParticle = listParticle;
    }
    return this.listParticle;
  }

  /** Gets the generator type. 
   * 
   * @return [GeneratorType] The generator type.
  */
  public GeneratorType getGeneratorType() {
    return generatorType;
  }

  /** Sets the generator type. 
   * 
   * @param generatorType [GeneratorType] The new generator type.
  */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /** Helper getters for static fields used by Particle. 
   * 
   * @return [int] The total number of particles.
  */
  public static int getCountParticle() {
    return countParticle;
  }

  /**
   * Returns the number of swarms.
   * @return [int] The number of swarms.
   */
  public static int getCountParticleBySwarm() {
    return countParticleBySwarm;
  }

  /**
   * Returns the local best array (lBest).
   * @return [State[]] The local best array.
   */
  public static State[] getLBest() {
    return lBest;
  }

  /**
   * Updates lBest and gBest based on the current particle personal best and
   * the problem type (maximisation/minimisation).
   * 
   * @param stateCandidate [State] The candidate state (not used directly here).
   * @param countIterationsCurrent [Integer] The current iteration count.
   * @throws IllegalArgumentException If an error occurs during update.
   * @throws SecurityException If an error occurs during update.
   * @throws ClassNotFoundException If an error occurs during update.
   * @throws InstantiationException If an error occurs during update.
   * @throws IllegalAccessException If an error occurs during update.
   * @throws InvocationTargetException If an error occurs during update.
   * @throws NoSuchMethodException If an error occurs during update.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    if (listParticle.isEmpty() || countParticleBySwarm == 0 || coutSwarm == 0) {
      return;
    }

    Strategy strategy = Strategy.getStrategy();
    Problem problem = (strategy == null) ? null : strategy.getProblem();
    ProblemType type = (problem == null) ? null : problem.getTypeProblem();

    Particle particle = listParticle.get(countParticle);
    int swarm = countParticle / countParticleBySwarm;

    if (lBest == null || lBest.length == 0 || swarm >= lBest.length) {
      return;
    }

    if (type == null || type.equals(ProblemType.MAXIMIZAR)) {
      // Maximisation: update lBest if particle's personal best is better
      if (lBest[swarm].getEvaluation().get(0)
          < particle.getStatePBest().getEvaluation().get(0)) {
        lBest[swarm] = particle.getStatePBest();
        if (!getReferenceList().isEmpty()
            && lBest[swarm].getEvaluation().get(0)
               > getReferenceList().get(getReferenceList().size() - 1)
                   .getEvaluation().get(0)) {
          gBest = new State();
          gBest.setCode(new ArrayList<>(lBest[swarm].getCode()));
          gBest.setEvaluation(lBest[swarm].getEvaluation());
          gBest.setTypeGenerator(lBest[swarm].getTypeGenerator());
          stateReferencePSO = gBest;
        }
      }
    } else {
      // Minimisation: first let the particle update its personal best
      particle.updateReference(stateCandidate, countIterationsCurrent);
      if (lBest[swarm].getEvaluation().get(0)
          > particle.getStatePBest().getEvaluation().get(0)) {
        lBest[swarm] = particle.getStatePBest();
        if (!getReferenceList().isEmpty()
            && lBest[swarm].getEvaluation().get(0)
               < getReferenceList().get(getReferenceList().size() - 1)
                   .getEvaluation().get(0)) {
          gBest = new State();
          gBest.setCode(new ArrayList<>(lBest[swarm].getCode()));
          gBest.setEvaluation(lBest[swarm].getEvaluation());
          gBest.setTypeGenerator(lBest[swarm].getTypeGenerator());
          stateReferencePSO = gBest;
        }
      }
    }

    if (gBest != null) {
      listStateReference.add(gBest);
    }

    // Advance to next particle and increase PSO iteration counter
    countParticle++;
    countCurrentIterPSO++;
  }

  /**
   * Computes the initial global best state (gBest) from lBest array.
   * 
   * @return [State] The initial gBest state.
   */
  public State gBestInicial() {
    if (lBest == null || lBest.length == 0) {
      return null;
    }

    Strategy strategy = Strategy.getStrategy();
    Problem problem = (strategy == null) ? null : strategy.getProblem();
    ProblemType type = (problem == null) ? null : problem.getTypeProblem();

    State stateBest = lBest[0];
    for (int i = 1; i < lBest.length; i++) {
      if (type == null || type.equals(ProblemType.MAXIMIZAR)) {
        if (lBest[i].getEvaluation().get(0)
            > stateBest.getEvaluation().get(0)) {
          stateBest = lBest[i];
        }
      } else {
        if (lBest[i].getEvaluation().get(0)
            < stateBest.getEvaluation().get(0)) {
          stateBest = lBest[i];
        }
      }
    }
    return stateBest;
  }

  /**
   * Sets the initial reference state. For PSO we may use it as an initial gBest.
   * 
   * @param stateInitialRef [State] The initial reference state.
   * 
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferencePSO = stateInitialRef;
    if (gBest == null) {
      gBest = stateInitialRef;
    }
  }

  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Returns the list of reference states (gBest history).
   * 
   * @return [List<State>] The list of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    return new ArrayList<>(this.listStateReference);
  }

  /**
   * PSO does not keep a separate list of sons; we return current particle
   * positions as “sons”.
   * 
   * @return [List<State>] The list of current particle states.
   */
  @Override
  public List<State> getSonList() {
    List<State> sons = new ArrayList<>();
    for (Particle p : listParticle) {
      if (p.getStateActual() != null) {
        sons.add(p.getStateActual());
      }
    }
    return sons;
  }

  /**
   * Returns true if the candidate is better than the current gBest
   * (according to the problem type).
   * 
   * @param stateCandidate [State] The candidate state to compare.
   * @return [boolean] True if candidate is better than gBest.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (stateCandidate == null
        || stateCandidate.getEvaluation() == null
        || stateCandidate.getEvaluation().isEmpty()) {
      return false;
    }

    Strategy strategy = Strategy.getStrategy();
    Problem problem = (strategy == null) ? null : strategy.getProblem();
    ProblemType type = (problem == null) ? null : problem.getTypeProblem();

    double evalCand = stateCandidate.getEvaluation().get(0);
    double evalRef = (gBest != null && gBest.getEvaluation() != null
        && !gBest.getEvaluation().isEmpty())
        ? gBest.getEvaluation().get(0)
        : evalCand;

    if (type == null || type.equals(ProblemType.MAXIMIZAR)) {
      return evalCand > evalRef;
    } else {
      return evalCand < evalRef;
    }
  }

  /** Sets the weight of this generator. 
   * 
   * @param weight [float] The new weight.
  */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /** Gets the weight of this generator. 
   * 
   * @return [float] The current weight.
  */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /** Returns the “better gender” statistics array. 
   * 
   * @return [int[]] The array of better gender counters.
  */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderPSO;
  }

  /** Returns the gender statistics array. 
   * 
   * @return [int[]] The array of gender counters.
  */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /** Returns the trace of weight values. 
   * 
   * @return [float[]] The trace array.
  */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }

  /** Convenience getters/setters for static fields (used by Particle). 
   * 
   * @return [double] The constriction factor.
  */
  public static double getConstriction() {
    return constriction;
  }

  /** Sets the constriction factor. 
   * 
   * @param constriction [double] The new constriction factor.
  */
  public static void setConstriction(double constriction) {
    ParticleSwarmOptimization.constriction = constriction;
  }

  /** Gets the count of the current iteration in PSO. 
   * 
   * @return [int] The current iteration count.
  */
  public static int getCountCurrentIterPSO() {
    return countCurrentIterPSO;
  }

  /** Sets the count of the current iteration in PSO. 
   * 
   * @param countCurrentIterPSO [int] The new current iteration count.
  */
  public static void setCountCurrentIterPSO(int countCurrentIterPSO) {
    ParticleSwarmOptimization.countCurrentIterPSO = countCurrentIterPSO;
  }

  /** Maximum inertia weight accessors. 
   * 
   * @return [double] The maximum inertia weight.
  */
  public static double getWmax() {
    return wmax;
  }

  /**
   * Sets the maximum inertia weight.
   * @param wmax [double] The new maximum inertia weight.
   */
  public static void setWmax(double wmax) {
    ParticleSwarmOptimization.wmax = wmax;
  }

  /** Minimum inertia weight accessors. 
   * 
   * @return [double] The minimum inertia weight.
  */
  public static double getWmin() {
    return wmin;
  }

  /**
   * Sets the minimum inertia weight.
   * 
   * @param wmin [double] The new minimum inertia weight.
   */
  public static void setWmin(double wmin) {
    ParticleSwarmOptimization.wmin = wmin;
  }

  /**
   * Gets the cognitive learning factor.
   * @return [int] The cognitive learning factor.
   */
  public static int getLearning1() {
    return learning1;
  }

  /**
   * Sets the cognitive learning factor.
   * 
   * @param learning1 [int] The new cognitive learning factor.
   */
  public static void setLearning1(int learning1) {
    ParticleSwarmOptimization.learning1 = learning1;
  }

  /**
   * Gets the social learning factor.
   * 
   * @return [int] The social learning factor.
   */
  public static int getLearning2() {
    return learning2;
  }

  /**
   * Sets the social learning factor.
   * 
   * @param learning2 [int] The new social learning factor.
   */
  public static void setLearning2(int learning2) {
    ParticleSwarmOptimization.learning2 = learning2;
  }

  /** Binary mode flag (both isBinary and getBinary for convenience). 
   * 
   * @return [boolean] True if PSO is in binary mode, false otherwise.
  */
  public static boolean isBinary() {
    return binary;
  }

  /**
   * Sets the binary mode flag.
   * 
   * @param binary [boolean] The new binary mode flag.
   */
  public static void setBinary(boolean binary) {
    ParticleSwarmOptimization.binary = binary;
  }

  /** Gets the global best state (gBest). 
   * 
   * @return [State] The global best state.
  */
  public State getGBest() {
    return gBest;
  }

  /** Sets the global best state (gBest). 
   * 
   * @param gBest [State] The new global best state.
  */
  public static void setGBest(State gBest) {
    ParticleSwarmOptimization.gBest = gBest;
  }

  /**
   * Gets the total number of particles (countRef).
   * 
   * @return [int] The total number of particles.
   */
  public static int getCountRef() {
    return countRef;
  }

  /**
   * Sets the total number of particles (countRef).
   * 
   * @param countRef [int] The new total number of particles.
   */
  public static void setCountRef(int countRef) {
    ParticleSwarmOptimization.countRef = countRef;
  }
}
