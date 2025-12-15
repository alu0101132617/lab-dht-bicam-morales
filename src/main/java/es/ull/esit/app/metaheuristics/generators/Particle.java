package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that represents a particle in Particle Swarm Optimization (PSO).
 * Each particle has:
 *  - A current state (position).
 *  - A personal best state (pBest).
 *  - A velocity vector.
 */
public class Particle extends Generator {

  /** Personal best state of the particle (pBest). */
  private State statePBest;

  /** Current state (position) of the particle. */
  private State stateActual;

  /** Velocity vector of the particle. */
  private List<Object> velocity;

  /** Generator type associated with this particle (PSO). */
  private GeneratorType generatorType = GeneratorType.ParticleSwarmOptimization;

  /** History of personal best states. */
  private final List<State> referenceList = new ArrayList<>();

  /** Weight associated with this particle (for statistics / multi-generator frameworks). */
  private float weight;

  /** Local counters for “better gender” statistics. */
  private final int[] listCountBetterGenderParticle = new int[10];

  /** Local counters for gender statistics. */
  private final int[] listCountGender = new int[10];

  /** Trace of weight values. */
  private final float[] listTrace = new float[1200000];

  /** Random number generator for internal use. */
  private SecureRandom random = new SecureRandom();

  /**
   * Default constructor.
   * Creates empty states and an empty velocity vector,
   * and initialises statistic arrays.
   */
  public Particle() {
    super();
    this.stateActual = new State();
    this.statePBest = new State();
    this.velocity = new ArrayList<>();

    this.weight = 50.0f;
    listTrace[0] = this.weight;
    listCountBetterGenderParticle[0] = 0;
    listCountGender[0] = 0;

    // Link inherited statistics array to the local one
    this.listCountBetterGender = this.listCountBetterGenderParticle;
  }

  /**
   * Full constructor.
   *
   * @param statePBest  [State] Personal best state.
   * @param stateActual [State] Current state.
   * @param velocity    [List<Object>] Velocity vector.
   */
  public Particle(State statePBest, State stateActual, List<Object> velocity) {
    this();
    this.statePBest = statePBest;
    this.stateActual = stateActual;
    if (velocity != null) {
      this.velocity = new ArrayList<>(velocity);
    }
  }

  /** 
   * Returns a defensive copy of the velocity vector. 
   * 
   * @return [List<Object>] A new list containing the velocity elements.
   */
  public List<Object> getVelocity() {
    return new ArrayList<>(velocity);
  }

  /** Sets the velocity vector. 
   * 
   * @param velocity The new velocity vector.
  */
  public void setVelocity(List<Object> velocity) {
    if (velocity == null) {
      this.velocity = new ArrayList<>();
    } else {
      this.velocity = new ArrayList<>(velocity);
    }
  }

  /** Returns the personal best state. 
   * 
   * @return [State] The personal best state.
  */
  public State getStatePBest() {
    return statePBest;
  }

  /** Sets the personal best state. 
   * 
   * @param statePBest [State] The new personal best state.
  */
  public void setStatePBest(State statePBest) {
    this.statePBest = statePBest;
  }

  /** Returns the current state. 
   * 
   * @return [State] The current state.
  */
  public State getStateActual() {
    return stateActual;
  }

  /** Sets the current state. 
   * 
   * @param stateActual [State] The new current state.
  */
  public void setStateActual(State stateActual) {
    this.stateActual = stateActual;
  }

  /**
   * Updates the velocity and position of the particle according to PSO
   * equations and returns the updated current state.
   *
   * @param operatornumber [Integer] Not used in PSO.
   * @return [State] Updated current state of the particle.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    List<Object> actualVelocity = updateVelocity();
    List<Object> newCode = updateCode(actualVelocity);

    this.velocity = new ArrayList<>(actualVelocity);
    if (this.stateActual != null) {
      this.stateActual.setCode(newCode);
    }
    return this.stateActual;
  }

  /**
   * Updates the velocity vector of the particle using PSO equations.
   *
   * @return [List<Object>] Updated velocity vector.
   */
  private List<Object> updateVelocity() {

    Strategy strategy = Strategy.getStrategy();
    if (strategy == null || strategy.getProblem() == null
        || strategy.getProblem().getState() == null) {
      // Not enough information to update velocity
      return new ArrayList<>(velocity);
    }

    int codeSize = strategy.getProblem().getState().getCode().size();
    if (codeSize == 0) {
      return new ArrayList<>(velocity);
    }

    // Inertia weight
    double w = ParticleSwarmOptimization.getWmax()
        - ((ParticleSwarmOptimization.getWmax() - ParticleSwarmOptimization.getWmin())
          / strategy.getCountMax())
          * ParticleSwarmOptimization.getCountCurrentIterPSO();

    double rand1 = random.nextDouble();
    double rand2 = random.nextDouble();

    // Learning factors (cognitive + social)
    int learning = ParticleSwarmOptimization.getLearning1()
        + ParticleSwarmOptimization.getLearning2();

    // Constriction factor (computed here, stored in PSO)
    double constrictionValue = 2.0 /
        Math.abs(2.0 - learning - Math.sqrt((learning * learning) - 4.0 * learning));
    ParticleSwarmOptimization.setConstriction(constrictionValue);

    List<Object> actualVelocity = new ArrayList<>();

    // Ensure velocity vector has the correct size
    if (velocity == null) {
      velocity = new ArrayList<>();
    }
    if (velocity.isEmpty()) {
      for (int i = 0; i < codeSize; i++) {
        velocity.add(0.0);
      }
    }

    for (int i = 0; i < codeSize; i++) {
      double inertia = w * ((Double) velocity.get(i));

      // Swarm index of this particle
      int swarm = 0;
      if (ParticleSwarmOptimization.getCountParticleBySwarm() != 0) {
        swarm = ParticleSwarmOptimization.getCountParticle()
            / ParticleSwarmOptimization.getCountParticleBySwarm();
      }

      double cognitive;
      double social;

      if (ParticleSwarmOptimization.isBinary()) {
        int xPBest = (Integer) statePBest.getCode().get(i);
        int xActual = (Integer) stateActual.getCode().get(i);
        int xLBest = (Integer) (ParticleSwarmOptimization.getLBest()[swarm])
            .getCode().get(i);

        cognitive = ParticleSwarmOptimization.getLearning1() * rand1 * (xPBest - xActual);
        social = ParticleSwarmOptimization.getLearning2() * rand2 * (xLBest - xActual);
      } else {
        double xPBest = (Double) statePBest.getCode().get(i);
        double xActual = (Double) stateActual.getCode().get(i);
        double xLBest = (Double) (ParticleSwarmOptimization.getLBest()[swarm])
            .getCode().get(i);

        cognitive = ParticleSwarmOptimization.getLearning1() * rand1 * (xPBest - xActual);
        social = ParticleSwarmOptimization.getLearning2() * rand2 * (xLBest - xActual);
      }

      double newVel =
          ParticleSwarmOptimization.getConstriction() * (inertia + cognitive + social);
      actualVelocity.add(newVel);
    }

    return actualVelocity;
  }

  /**
   * Computes the new position (code) of the particle based on the updated velocity.
   *
   * @param actualVelocity [List<Object>] Updated velocity vector.
   * @return [List<Object>] New position (code) of the particle.
   */
  private List<Object> updateCode(List<Object> actualVelocity) {
    List<Object> newCode = new ArrayList<>();
    List<Object> binaryCode = new ArrayList<>();

    if (stateActual == null || stateActual.getCode() == null) {
      return newCode;
    }

    int size = stateActual.getCode().size();

    // Continuous case
    if (!ParticleSwarmOptimization.isBinary()) {
      for (int i = 0; i < size; i++) {
        double x = (Double) stateActual.getCode().get(i);
        double v = (Double) actualVelocity.get(i);
        newCode.add(x + v);
      }
      return newCode;
    }

    // Binary case
    for (int i = 0; i < size; i++) {
      double rand = random.nextDouble();
      double v = (Double) actualVelocity.get(i);
      double s = 1.0 / (1.0 + 1.72 * v);
      if (rand < s) {
        binaryCode.add(1);
      } else {
        binaryCode.add(0);
      }
    }
    return binaryCode;
  }

  /**
   * Updates the personal best (pBest) according to the problem type (maximisation or minimisation).
   * 
   * @param stateCandidate   [State] Candidate state to consider for pBest update.
   * @param countIterationsCurrent [Integer] Current iteration count (not used directly here).
   * @throws IllegalArgumentException If arguments are invalid.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an object cannot be instantiated.
   * @throws IllegalAccessException If access to a class or method is illegal.
   * @throws InvocationTargetException If a method invocation fails.
   * @throws NoSuchMethodException If a method cannot be found.
   * 
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    if (stateActual == null || statePBest == null
        || stateActual.getEvaluation() == null
        || statePBest.getEvaluation() == null
        || stateActual.getEvaluation().isEmpty()
        || statePBest.getEvaluation().isEmpty()) {
      return;
    }

    Strategy strategy = Strategy.getStrategy();
    Problem problem = (strategy == null) ? null : strategy.getProblem();
    ProblemType type = (problem == null) ? null : problem.getTypeProblem();

    double evalActual = stateActual.getEvaluation().get(0);
    double evalPBest = statePBest.getEvaluation().get(0);

    if (type == null || type.equals(ProblemType.MAXIMIZAR)) {
      // Maximisation
      if (evalActual > evalPBest) {
        statePBest.setCode(new ArrayList<>(stateActual.getCode()));
        statePBest.setEvaluation(stateActual.getEvaluation());
      }
    } else {
      // Minimisation
      double evalCandidate = (stateCandidate != null
          && stateCandidate.getEvaluation() != null
          && !stateCandidate.getEvaluation().isEmpty())
          ? stateCandidate.getEvaluation().get(0)
          : evalActual;

      if (evalCandidate < evalPBest) {
        State source = (stateCandidate != null) ? stateCandidate : stateActual;
        statePBest.setCode(new ArrayList<>(source.getCode()));
        statePBest.setEvaluation(source.getEvaluation());
      }
    }

    // Store history for statistics
    getReferenceList();
  }

  /** Returns the current personal best as reference. 
   * 
   * @return [State] The personal best state.
  */
  @Override
  public State getReference() {
    return statePBest;
  }

  /**
   * Sets the initial reference; for a particle we initialise both current
   * state and personal best with the same state.
   * 
   * @param stateInitialRef [State] The initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateActual = stateInitialRef;
    this.statePBest = stateInitialRef;
  }

  /** Returns the type of the generator. 
   * 
   * @return [GeneratorType] The generator type.
  */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Returns the list of stored personal bests.
   * Each call appends the current pBest (if not null) and returns a copy.
   * 
   * @return [List<State>] A new list containing the history of personal best states.
   * 
   */
  @Override
  public List<State> getReferenceList() {
    if (statePBest != null) {
      referenceList.add(statePBest);
    }
    return new ArrayList<>(referenceList);
  }

  /**
   * PSO does not maintain an explicit list of “sons”;
   * we return the current state as a singleton list if present.
   * 
   * @return [List<State>] A list containing the current state, or empty if null.
   */
  @Override
  public List<State> getSonList() {
    List<State> sons = new ArrayList<>();
    if (stateActual != null) {
      sons.add(stateActual);
    }
    return sons;
  }

  /**
   * Decides whether a candidate should be considered an improvement over
   * the personal best; useful for statistics.
   * 
   * @param stateCandidate [State] Candidate state to compare against pBest.
   * @return [boolean] True if candidate is better than pBest, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    if (statePBest == null || stateCandidate == null
        || statePBest.getEvaluation() == null
        || stateCandidate.getEvaluation() == null
        || statePBest.getEvaluation().isEmpty()
        || stateCandidate.getEvaluation().isEmpty()) {
      return false;
    }

    double evalRef = statePBest.getEvaluation().get(0);
    double evalCand = stateCandidate.getEvaluation().get(0);

    Strategy strategy = Strategy.getStrategy();
    Problem problem = (strategy == null) ? null : strategy.getProblem();
    ProblemType type = (problem == null) ? null : problem.getTypeProblem();

    if (type == null || type.equals(ProblemType.MAXIMIZAR)) {
      return evalCand > evalRef;
    } else {
      return evalCand < evalRef;
    }
  }

  /**
   * Sets the weight of the particle.
   * 
   * @param weight [float] The weight to set.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Returns the weight of the particle.
   * 
   * @return [float] The current weight.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Returns the trace of weight values.
   * 
   * @return [float[]] The trace array.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }

  /**
   * Returns the local counters for “better gender” statistics.
   * 
   * @return [int[]] The array of counters.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderParticle;
  }

  /**
   * Returns the local counters for gender statistics.
   * 
   * @return [int[]] The array of counters.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }
}

