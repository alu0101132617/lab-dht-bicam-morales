package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


import es.ull.esit.app.factory_method.FactoryGenerator;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Generator that implements a multi-generator framework.
 */
public class MultiGenerator extends Generator {

  /** Logger for the MultiGenerator class. */
  private static final Logger LOGGER = Logger.getLogger(MultiGenerator.class.getName());
  
  /** Generator type identifier. */
  private GeneratorType generatorType;
  /** List of generators managed by the multi-generator. */
  private static Generator[] listGenerators = new Generator[GeneratorType.values().length];
  /** List of states generated for the Bin Packing Problem. */
  private static List<State> listGeneratedPP = new ArrayList<>();
  /** Currently active generator within the multi-generator framework. */
  private static Generator activeGenerator;
  /** List of reference states visited by the multi-generator. */
  private static List<State> listStateReference = new ArrayList<>();

  /** Random number generator for selecting generators. */
  private static SecureRandom random = new SecureRandom();

  /**
   * Gets the list of reference states.
   * 
   * @return [List<State>] List of reference states.
   */
  public static List<State> getListStateReference() {
    return listStateReference;
  }

  /**
   * Sets the list of reference states.
   * 
   * @param listStateReference [List<State>] List of reference states to set.
   */
  public static void setListStateReference(List<State> listStateReference) {
    MultiGenerator.listStateReference = listStateReference;
  }

  /**
   * Sets the generator type.
   * 
   * @param generatortype [GeneratorType] Generator type to set.
   */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /**
   * Default constructor. It initializes the multi-generator framework.
   */
  public MultiGenerator() {
    super();
    this.generatorType = GeneratorType.MultiGenerator;
  }

  /**
   * Destroys the multi-generator framework by clearing its state.
   */
  public static void destroyMultiGenerator() {
    listGeneratedPP.clear();
    listStateReference.clear();
    activeGenerator = null;
    listGenerators = null;
  }

  /**
   * Initializes the list of generators used in the multi-generator framework.
   * 
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a specified class cannot be found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a specified method cannot be found.
   */
  public static void initializeListGenerator()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    listGenerators = new Generator[4];
    Generator generator1 = new HillClimbing();
    Generator generator2 = new EvolutionStrategies();
    Generator generator3 = new LimitThreshold();
    Generator generator4 = new GeneticAlgorithm();
    listGenerators[0] = generator1;
    listGenerators[1] = generator2;
    listGenerators[2] = generator3;
    listGenerators[3] = generator4;
  }

  /**
   * Initializes the multi-generator framework.
   * 
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a specified class cannot be found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a specified method cannot be found.
   */
  public static void initializeGenerators() throws IllegalArgumentException, SecurityException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    initializeListGenerator();
    State stateREF = new State(Strategy.getStrategy().getProblem().getState());
    listStateReference.add(stateREF);
    for (int i = 0; i < listGenerators.length; i++) {
      if ((listGenerators[i].getType().equals(GeneratorType.HillClimbing))
          || (listGenerators[i].getType().equals(GeneratorType.RandomSearch))
          || (listGenerators[i].getType().equals(GeneratorType.TabuSearch))
          || (listGenerators[i].getType().equals(GeneratorType.SimulatedAnnealing)
              || (listGenerators[i].getType().equals(GeneratorType.LimitThreshold)))) {
        listGenerators[i].setInitialReference(stateREF);
      }
    }
    createInstanceGeneratorsBPP();
    Strategy.getStrategy().setListStates(MultiGenerator.getListGeneratedPP());

    FactoryGenerator ifFactoryGeneratorEE = new FactoryGenerator();
    Generator generatorEE = ifFactoryGeneratorEE.createGenerator(GeneratorType.EvolutionStrategies);

    FactoryGenerator ifFactoryGeneratorGA = new FactoryGenerator();
    Generator generatorGA = ifFactoryGeneratorGA.createGenerator(GeneratorType.GeneticAlgorithm);

    FactoryGenerator ifFactoryGeneratorEDA = new FactoryGenerator();
    Generator generatorEDA = ifFactoryGeneratorEDA.createGenerator(GeneratorType.DistributionEstimationAlgorithm);

    for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
      if (MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.EvolutionStrategies)) {
        MultiGenerator.getListGenerators()[i] = generatorEE;
      }
      if (MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.GeneticAlgorithm)) {
        MultiGenerator.getListGenerators()[i] = generatorGA;
      }
      if (MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.DistributionEstimationAlgorithm)) {
        MultiGenerator.getListGenerators()[i] = generatorEDA;
      }
    }
  }

  /**
   * Creates instances of candidate states for the Bin Packing Problem.
   * 
   */
  public static void createInstanceGeneratorsBPP() {
    Generator generator = new RandomSearch();
    int j = 0;
    while (j < EvolutionStrategies.getCountRef()) {
      State stateCandidate;
      try {
        stateCandidate = generator.generate(1);
        Strategy.getStrategy().getProblem().evaluate(stateCandidate);
        stateCandidate.setNumber(j);
        stateCandidate.setTypeGenerator(generator.getType());
        listGeneratedPP.add(stateCandidate);
      } catch (Exception e) {
         LOGGER.log(Level.SEVERE, "Error generating state candidate", e);
      }
      j++;
    }
  }

  /**
   * Gets the list of generated states for the Bin Packing Problem.
   * 
   * @return [List<State>] List of generated states.
   */
  public static List<State> getListGeneratedPP() {
    return listGeneratedPP;
  }

  /**
   * Gets the list of generators used in the multi-generator framework.
   * 
   * @return [Generator[]] List of generators.
   */
  public static Generator[] getListGenerators() {
    return listGenerators;
  }

  /**
   * Sets the list of generators used in the multi-generator framework.
   * 
   * @param listGenerators [Generator[]] List of generators to set.
   */
  public static void setListGenerators(Generator[] listGenerators) {
    MultiGenerator.listGenerators = listGenerators;
  }

  /**
   * Gets the currently active generator within the multi-generator framework.
   * 
   * @return [Generator] Currently active generator.
   */
  public static Generator getActiveGenerator() {
    return activeGenerator;
  }

  /**
   * Sets the currently active generator within the multi-generator framework.
   * 
   * @param activeGenerator [Generator] Generator to set as active.
   */
  public static void setActiveGenerator(Generator activeGenerator) {
    MultiGenerator.activeGenerator = activeGenerator;
  }

  /**
   * Sets the list of generated states for the Bin Packing Problem.
   * 
   * @param listGeneratedPP [List<State>] List of generated states to set.
   */
  public static void setListGeneratedPP(List<State> listGeneratedPP) {
    MultiGenerator.listGeneratedPP = listGeneratedPP;
  }

  /**
   * Generates a new state using the currently active generator.
   * 
   * @param operatornumber [Integer] Operator number (not used in this
   *                       implementation).
   * @return [State] Generated state.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException,
      ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException,
      NoSuchMethodException {
    Strategy.getStrategy().setGenerator(roulette());
    activeGenerator = Strategy.getStrategy().getGenerator();
    activeGenerator.countGender++;
    return Strategy.getStrategy().getGenerator().generate(1);

  }

  /**
   * Gets the current reference state.
   * 
   * @return [State] Current reference state.
   */
  @Override
  public State getReference() {
    // Return the most recent reference state, or the current problem state if none
    // exists.
    if (!listStateReference.isEmpty()) {
      return listStateReference.get(listStateReference.size() - 1);
    }
    return Strategy.getStrategy().getProblem().getState();
  }

  /**
   * Gets the list of reference states.
   * 
   * @return [List<State>] List of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    // Return a copy of the reference list to avoid exposing internal state.
    return new ArrayList<>(listStateReference);
  }

  /**
   * Gets the list of son states (not used in this implementation).
   * 
   * @return [List<State>] Empty list of son states.
   */
  @Override
  public List<State> getSonList() {
    // Return an empty list instead of null.
    return new ArrayList<>();
  }

  /**
   * Gets the generator type.
   * 
   * @return [GeneratorType] Generator type.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Sets the initial reference state.
   * 
   * @param stateInitialRef [State] Initial reference state to set.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    // Initialize the reference list with the provided initial reference state.
    if (stateInitialRef != null) {
      listStateReference.add(stateInitialRef);
    }
  }

  /**
   * Updates the reference state based on the candidate state and current
   * iteration count.
   * 
   * @param stateCandidate         [State] Candidate state.
   * @param countIterationsCurrent [Integer] Current iteration count.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a specified class cannot be found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a specified method cannot be found.
   */
  @Override
  public void updateReference(State stateCandidate,
      Integer countIterationsCurrent) throws IllegalArgumentException,
      SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException,
      NoSuchMethodException {
    updateWeight(stateCandidate);
    tournament(stateCandidate, countIterationsCurrent);
  }

  /**
   * Updates the weight of the active generator based on the candidate state.
   * 
   * @param stateCandidate [State] Candidate state.
   */
  public void updateWeight(State stateCandidate) {
    boolean search = searchState(stateCandidate);// premio por calidad.
    if (!search)
      updateAwardImp();
    else
      updateAwardSC();
  }

  /**
   * Searches for a state based on the problem type and candidate state.
   * 
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] True if the candidate state is better than the best state,
   *         false otherwise.
   */
  public boolean searchState(State stateCandidate) {
    if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      if (stateCandidate.getEvaluation().get(0) > Strategy.getStrategy().getBestState().getEvaluation().get(0)) {
        if (stateCandidate.getEvaluation().get(0) > Strategy.getStrategy().getBestState().getEvaluation().get(0))
          activeGenerator.countBetterGender++;
        return true;
      } else
        return false;
    } else {
      if (stateCandidate.getEvaluation().get(0) < Strategy.getStrategy().getBestState().getEvaluation().get(0)) {
        if (stateCandidate.getEvaluation().get(0) < Strategy.getStrategy().getBestState().getEvaluation().get(0))
          activeGenerator.countBetterGender++;
        return true;
      } else
        return false;
    }

  }

  /**
   * Gets the weight of the active generator.
   * 
   * @return [float] Weight of the active generator.
   */
  @Override
  public float getWeight() {
    return activeGenerator != null ? activeGenerator.getWeight() : 0f;
  }

  /**
   * Selects a generator using roulette wheel selection based on their weights.
   * 
   * @return [Generator] Selected generator.
   */
  public Generator roulette() {
    float totalWeight = 0;
    for (int i = 0; i < listGenerators.length; i++) {
      totalWeight = listGenerators[i].getWeight() + totalWeight;
    }
    List<Float> listProb = new ArrayList<>();
    for (int i = 0; i < listGenerators.length; i++) {
      float probF = listGenerators[i].getWeight() / totalWeight;
      listProb.add(probF);
    }
    List<LimitRoulette> listLimit = new ArrayList<>();
    float limitHigh = 0;
    float limitLow = 0;
    for (int i = 0; i < listProb.size(); i++) {
      LimitRoulette limitRoulette = new LimitRoulette();
      limitHigh = listProb.get(i) + limitHigh;
      limitRoulette.setLimitHigh(limitHigh);
      limitRoulette.setLimitLow(limitLow);
      limitLow = limitHigh;
      limitRoulette.setGenerator(listGenerators[i]);
      listLimit.add(limitRoulette);
    }
    float numbAleatory = (float) (random.nextDouble() * 1.0);
    boolean find = false;
    int i = 0;
    while ((!find) && (i < listLimit.size())) {
      if ((listLimit.get(i).getLimitLow() <= numbAleatory) && (numbAleatory <= listLimit.get(i).getLimitHigh())) {
        find = true;
      } else
        i++;
    }
    if (find) {
      return listLimit.get(i).getGenerator();
    } else
      return listLimit.get(listLimit.size() - 1).getGenerator();
  }

  /**
   * Awards an update to the reference list if the candidate state improves over
   * the current best.
   * 
   * @param stateCandidate [State] Candidate state.
   * @return [boolean] True if the candidate state improved over the current best,
   *         false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    // Update the reference list if the candidate improves over the current best.
    boolean improved = searchState(stateCandidate);
    if (improved) {
      listStateReference.add(new State(stateCandidate));
    }
    return improved;
  }

  /**
   * Updates the weight of the active generator when a significant improvement is
   * found.
   */
  @SuppressWarnings("static-access")
  public void updateAwardSC() {
    float weightLast = activeGenerator.getWeight();
    float weightUpdate = (float) (weightLast * (1 - 0.1) + 10);
    activeGenerator.setWeight(weightUpdate);
    for (int i = 0; i < listGenerators.length; i++) {
      if (listGenerators[i].equals(activeGenerator))
        activeGenerator.getTrace()[Strategy.getStrategy().getCountCurrent()] = weightUpdate;
      else {
        if (!listGenerators[i].getType().equals(generatorType.MultiGenerator)) {
          float trace = listGenerators[i].getWeight();
          listGenerators[i].getTrace()[Strategy.getStrategy().getCountCurrent()] = trace;
        }
      }
    }
  }

  /**
   * Updates the weight of the active generator when no significant improvement is
   * found.
   * 
   */
  @SuppressWarnings("static-access")
  public void updateAwardImp() {
    float weightLast = activeGenerator.getWeight();
    float weightUpdate = (float) (weightLast * (1 - 0.1));
    activeGenerator.setWeight(weightUpdate);
    for (int i = 0; i < listGenerators.length; i++) {
      if (listGenerators[i].equals(activeGenerator))
        activeGenerator.getTrace()[Strategy.getStrategy().getCountCurrent()] = weightUpdate;
      else {
        if (!listGenerators[i].getType().equals(generatorType.MultiGenerator)) {
          float trace = listGenerators[i].getWeight();
          listGenerators[i].getTrace()[Strategy.getStrategy().getCountCurrent()] = trace;
        }
      }
    }
  }

  /**
   * Sets the weight of the active generator.
   * 
   * @param weight [float] Weight to set.
   */
  @Override
  public void setWeight(float weight) {
    if (activeGenerator != null) {
      activeGenerator.setWeight(weight);
    }
  }

  /**
   * Gets the trace of weight changes for the active generator.
   * 
   * @return [float[]] Trace of weight changes.
   */
  @Override
  public float[] getTrace() {
    return activeGenerator != null && activeGenerator.getTrace() != null
        ? activeGenerator.getTrace()
        : new float[0];
  }

  /**
   * Conducts a tournament among generators to update their references based on
   * the candidate state.
   * 
   * @param stateCandidate         [State] Candidate state.
   * @param countIterationsCurrent [Integer] Current iteration count.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If a specified class cannot be found.
   * @throws InstantiationException    If an instantiation error occurs.
   * @throws IllegalAccessException    If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException     If a specified method cannot be found.
   */
  @SuppressWarnings("static-access")
  public void tournament(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    State stateTem = new State(stateCandidate);
    for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
      if (!listGenerators[i].getType().equals(generatorType.MultiGenerator))
        MultiGenerator.getListGenerators()[i].updateReference(stateTem, countIterationsCurrent);
    }
  }

  /**
   * Copy constructor.
   * 
   * @param other [MultiGenerator] MultiGenerator instance to copy.
   */
  public MultiGenerator(MultiGenerator other) {
    super();
    this.generatorType = other.generatorType;
  }

  /**
   * Creates a copy of the current MultiGenerator instance.
   * 
   * @return [MultiGenerator] Copy of the current instance.
   */
  public MultiGenerator copy() {
    return new MultiGenerator(this);
  }

  /**
   * Gets the list of counts of better genders for each generator.
   * 
   * @return [int[]] List of counts of better genders.
   */
  @Override
  public int[] getListCountBetterGender() {
    if (listGenerators == null) {
      return new int[0];
    }
    int[] result = new int[listGenerators.length];
    for (int i = 0; i < listGenerators.length; i++) {
      result[i] = listGenerators[i] != null ? listGenerators[i].countBetterGender : 0;
    }
    return result;
  }

  /**
   * Gets the list of counts of genders for each generator.
   * 
   * @return [int[]] List of counts of genders.
   */
  @Override
  public int[] getListCountGender() {
    if (listGenerators == null) {
      return new int[0];
    }
    int[] result = new int[listGenerators.length];
    for (int i = 0; i < listGenerators.length; i++) {
      result[i] = listGenerators[i] != null ? listGenerators[i].countGender : 0;
    }
    return result;
  }

}
