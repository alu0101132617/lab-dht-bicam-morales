package es.ull.esit.app.metaheurictics.strategy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ull.esit.app.factory_interface.IFFactoryGenerator;
import es.ull.esit.app.factory_method.FactoryGenerator;
import es.ull.esit.app.local_search.acceptation_type.Dominance;
import es.ull.esit.app.local_search.complement.StopExecute;
import es.ull.esit.app.local_search.complement.UpdateParameter;
import es.ull.esit.app.metaheuristics.generators.DistributionEstimationAlgorithm;
import es.ull.esit.app.metaheuristics.generators.EvolutionStrategies;
import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheuristics.generators.GeneticAlgorithm;
import es.ull.esit.app.metaheuristics.generators.MultiGenerator;
import es.ull.esit.app.metaheuristics.generators.ParticleSwarmOptimization;
import es.ull.esit.app.metaheuristics.generators.RandomSearch;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Singleton class that manages the execution strategy of metaheuristic
 * algorithms.
 */
public class Strategy {

  /** Singleton instance of the Strategy class */
  private static Strategy strategy = null;
  /** Best state found during the execution */
  private State bestState;
  /** Problem instance associated with the strategy */
  private Problem problem;
  /** Map of generator types to their corresponding generator instances */
  private SortedMap<GeneratorType, Generator> mapGenerators;
  /** Instance of StopExecute to manage stopping criteria */
  private StopExecute stopexecute;
  /** Instance of UpdateParameter to manage parameter updates */
  private UpdateParameter updateparameter;
  /** Instance of IFFactoryGenerator for factory generation */
  private IFFactoryGenerator ifFactoryGenerator;
  /** Current iteration count */
  private int countCurrent;
  /** Maximum iteration count */
  private int countMax;
  /** Current generator used in the strategy */
  private Generator generator;
  /** Threshold value for the strategy */
  private double threshold;
  /** Lists to store states in each iteration */
  private List<State> listStates;
  /** List of best states found in each iteration */
  private List<State> listBest;
  /** Dominance criteria for multi-objective optimization */
  private final Dominance notDominated = new Dominance();
  /** Flags for saving states and calculating time */
  private boolean saveListStates = false;
  /** Save list of generated states */
  private boolean saveListBestStates = false;
  /** Save list of best states found */
  private boolean saveFreneParetoMonoObjetivo = false;
  /** Save list of non-dominated solutions */
  private boolean calculateTime = false;
  /** List of reference non-dominated population */
  private List<State> listRefPoblacFinal;

  /** Logger for logging information */
  private final Logger logger = Logger.getLogger(getClass().getName());

  /** Time tracking variables */
  long initialTime;
  long finalTime;
  long timeExecute;

  /** Array to store the offline performance metric */
  private final float[] listOfflineError = new float[100];
  /** Count of iterations before a change */
  private int countPeriodChange = 0;

  /** Period counter to control the saving period */
  private int periodo;

  /** Private constructor to enforce singleton pattern */
  private Strategy() {
    super();
  }

  /**
   * Gets the singleton instance of the Strategy class.
   * @return [Strategy] The singleton instance.
   */
  public static synchronized Strategy getStrategy() {
    if (strategy == null) {
      strategy = new Strategy();
    }
    return strategy;
  }

  /**
   * Executes the strategy with the specified parameters.
   * @param countmaxIterations [int] Maximum number of iterations.
   * @param countIterationsChange [int] Number of iterations before a change occurs.
   * @param operatornumber [int] Number of operators to be used.
   * @param generatorType [GeneratorType] Type of generator to be used.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class is not found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method is not found.
   */
  public void executeStrategy(int countmaxIterations, int countIterationsChange, int operatornumber,
                              GeneratorType generatorType)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    if (calculateTime) {
      initialTime = System.currentTimeMillis();
    }
    this.countMax = countmaxIterations;

    // Estado inicial
    Generator randomInitial = new RandomSearch();
    State initialState = randomInitial.generate(operatornumber);
    problem.evaluate(initialState);
    initialState.setTypeGenerator(generatorType);
    getProblem().setState(initialState);

    if (saveListStates) {
      listStates = new ArrayList<>();
      listStates.add(initialState);
    }
    if (saveListBestStates) {
      listBest = new ArrayList<>();
      listBest.add(initialState);
    }

    generator = newGenerator(generatorType);
    generator.setInitialReference(initialState);
    bestState = initialState;
    countCurrent = 0;
    listRefPoblacFinal = new ArrayList<>();
    MultiGenerator multiGenerator = null;
    countPeriodChange = countIterationsChange;
    int countChange = countIterationsChange;
    int countPeriodo = countIterationsChange / 10;

    if (generatorType.equals(GeneratorType.MultiGenerator)) {
      initializeGenerators();
      MultiGenerator.initializeGenerators();
      MultiGenerator.getListGeneratedPP().clear();
      multiGenerator = ((MultiGenerator) generator).copy();
    } else {
      initialize();
    }
    update(countCurrent);

    float sumMax = 0;
    int countOff = 0;

    while (countCurrent < countmaxIterations) {
      if (countCurrent == countChange) {
        // Cambio detectado
        calculateOffLinePerformance(sumMax, countOff);
        countOff++;
        sumMax = 0;
        updateRef(generatorType);
        countChange = countChange + countPeriodChange;

        State stateCandidate;
        if (generatorType.equals(GeneratorType.MultiGenerator)) {

          if (multiGenerator == null) {
            throw new IllegalStateException("multiGenerator no ha sido inicializado correctamente.");
          }

          if (countPeriodo == countCurrent) {
            updateCountGender();
            countPeriodo = countPeriodo + countPeriodChange / 10;
            periodo = 0;
            MultiGenerator.getActiveGenerator().countBetterGender = 0;
          }

          updateWeight();
          stateCandidate = multiGenerator.generate(operatornumber);

          if (stateCandidate == null) {
            throw new IllegalStateException("El MultiGenerator devolvió un estado nulo.");
          }
          problem.evaluate(stateCandidate);
          stateCandidate.setEvaluation(stateCandidate.getEvaluation());
          stateCandidate.setNumber(countCurrent);
          stateCandidate.setTypeGenerator(generatorType);
          multiGenerator.updateReference(stateCandidate, countCurrent);
        } else {
          stateCandidate = generator.generate(operatornumber);
          problem.evaluate(stateCandidate);
          stateCandidate.setEvaluation(stateCandidate.getEvaluation());
          stateCandidate.setNumber(countCurrent);
          stateCandidate.setTypeGenerator(generatorType);
          generator.updateReference(stateCandidate, countCurrent);
          if (saveListStates) {
            listStates.add(stateCandidate);
          }
        }

        updateBestState(stateCandidate);

        if (saveListBestStates) {
          listBest.add(bestState);
        }
        sumMax = (float) (sumMax + bestState.getEvaluation().get(0));

      } else {
        // No hay cambio
        State stateCandidate;
        if (generatorType.equals(GeneratorType.MultiGenerator)) {
          if (countPeriodo == countCurrent) {
            updateCountGender();
            countPeriodo = countPeriodo + countPeriodChange / 10;
            periodo++;
            MultiGenerator.getActiveGenerator().countBetterGender = 0;
          }
          stateCandidate = multiGenerator.generate(operatornumber);
          problem.evaluate(stateCandidate);
          stateCandidate.setEvaluation(stateCandidate.getEvaluation());
          stateCandidate.setNumber(countCurrent);
          stateCandidate.setTypeGenerator(generatorType);
          multiGenerator.updateReference(stateCandidate, countCurrent);
        } else {
          stateCandidate = generator.generate(operatornumber);
          problem.evaluate(stateCandidate);
          stateCandidate.setEvaluation(stateCandidate.getEvaluation());
          stateCandidate.setNumber(countCurrent);
          stateCandidate.setTypeGenerator(generatorType);
          generator.updateReference(stateCandidate, countCurrent);
          if (saveListStates) {
            listStates.add(stateCandidate);
          }
          if (saveFreneParetoMonoObjetivo) {
            notDominated.listDominance(stateCandidate, listRefPoblacFinal);
          }
        }

        countCurrent = UpdateParameter.updateParameter(countCurrent);
        updateBestState(stateCandidate);

        if (saveListBestStates) {
          listBest.add(bestState);
        }
        sumMax = (float) (sumMax + bestState.getEvaluation().get(0));
      }
    }

    if (calculateTime) {
      finalTime = System.currentTimeMillis();
      timeExecute = finalTime - initialTime;
      logger.log(Level.INFO, "El tiempo de ejecucion:  {0}", timeExecute);
    }

    if (generatorType.equals(GeneratorType.MultiGenerator)) {
      listBest = multiGenerator.getReferenceList();
      calculateOffLinePerformance(sumMax, countOff);
      if (countPeriodo == countCurrent) {
        updateCountGender();
      }
    } else {
      listBest = generator.getReferenceList();
      calculateOffLinePerformance(sumMax, countOff);
    }
  }

  /**
   * Updates the best state found based on the candidate state.
   * @param stateCandidate [State] Candidate state to compare with the best state.
   */
  private void updateBestState(State stateCandidate) {
    if ((getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR))
        && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) < stateCandidate.getEvaluation()
            .get(bestState.getEvaluation().size() - 1)) {
      bestState = stateCandidate;
    }
    if ((problem.getTypeProblem().equals(ProblemType.MINIMIZAR))
        && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) > stateCandidate.getEvaluation()
            .get(bestState.getEvaluation().size() - 1)) {
      bestState = stateCandidate;
    }
  }

  /**
   * Updates the count of improvements and usage for each generator over a given period.
   */
  public void updateCountGender() {
    for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
      if (!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiGenerator)) {
        MultiGenerator.getListGenerators()[i]
            .getListCountGender()[periodo] = MultiGenerator.getListGenerators()[i].countGender
                + MultiGenerator.getListGenerators()[i].getListCountGender()[periodo];
        MultiGenerator.getListGenerators()[i]
            .getListCountBetterGender()[periodo] = MultiGenerator.getListGenerators()[i].countBetterGender
                + MultiGenerator.getListGenerators()[i].getListCountBetterGender()[periodo];
        MultiGenerator.getListGenerators()[i].countGender = 0;
        MultiGenerator.getListGenerators()[i].countBetterGender = 0;
      }
    }
  }

  /**
   * Updates the weights of the generators in the multi-generator setup.
   */
  public void updateWeight() {
    for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
      if (!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiGenerator)) {
        MultiGenerator.getListGenerators()[i].setWeight(50.0f);
      }
    }
  }

  /**
   * Updates the generator based on the current iteration count.
   * @param countIterationsCurrent [Integer] Current iteration count.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class cannot be found.
   * @throws InstantiationException If an instance cannot be created.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
  public void update(Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    if (countIterationsCurrent.equals(GeneticAlgorithm.getCountRef() - 1)) {
      ifFactoryGenerator = new FactoryGenerator();
      Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.GeneticAlgorithm);
    }
    if (countIterationsCurrent.equals(EvolutionStrategies.getCountRef() - 1)) {
      ifFactoryGenerator = new FactoryGenerator();
      Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.EvolutionStrategies);
    }
    if (countIterationsCurrent.equals(DistributionEstimationAlgorithm.getCountRef() - 1)) {
      ifFactoryGenerator = new FactoryGenerator();
      Strategy.getStrategy().generator =
          ifFactoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
    }
    if (countIterationsCurrent.equals(ParticleSwarmOptimization.getCountRef() - 1)) {
      ifFactoryGenerator = new FactoryGenerator();
      Strategy.getStrategy().generator =
          ifFactoryGenerator.createGenerator(GeneratorType.ParticleSwarmOptimization);
    }
  }

  /**
   * Creates a new generator based on the specified generator type.
   * @param generatortype [GeneratorType] The type of generator to create.
   * @return [Generator] A new generator instance.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class cannot be found.
   * @throws InstantiationException If an instance cannot be created.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
  public Generator newGenerator(GeneratorType generatortype)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    ifFactoryGenerator = new FactoryGenerator();
    return ifFactoryGenerator.createGenerator(generatortype);
  }

  /**
   * Gets the best state found during the execution.
   * @return [State] The best state found.
   */
  public State getBestState() {
    return bestState;
  }

  /**
   * Sets the best state found during the execution.
   * @param bestState [State] The best state to set.
   */
  public void setBestState(State bestState) {
    this.bestState = bestState;
  }

  /**
   * Gets the StopExecute instance.
   * @return [StopExecute] The StopExecute instance.
   */
  public StopExecute getStopexecute() {
    return stopexecute;
  }

  /**
   * Gets the maximum count.
   * @return [int] The maximum count.
   */
  public int getCountMax() {
    return countMax;
  }

  /**
   * Sets the maximum count.
   * @param countMax [int] The maximum count to set.
   */
  public void setCountMax(int countMax) {
    this.countMax = countMax;
  }

  /**
   * Sets the StopExecute instance.
   * @param stopexecute [StopExecute] The StopExecute instance to set.
   */
  public void setStopexecute(StopExecute stopexecute) {
    this.stopexecute = stopexecute;
  }

  /**
   * Gets the UpdateParameter instance.
   * @return [UpdateParameter] The UpdateParameter instance.
   */
  public UpdateParameter getUpdateparameter() {
    return updateparameter;
  }

  /**
   * Sets the UpdateParameter instance.
   * @param updateparameter [UpdateParameter] The UpdateParameter instance to set.
   */
  public void setUpdateparameter(UpdateParameter updateparameter) {
    this.updateparameter = updateparameter;
  }

  /**
   * Gets the Problem instance.
   * @return [Problem] The Problem instance.
   */
  public Problem getProblem() {
    return problem;
  }

  /**
   * Sets the Problem instance.
   * @param problem [Problem] The Problem instance to set.
   */
  public void setProblem(Problem problem) {
    this.problem = problem;
  }

  /**
   * Gets a list of keys from the map of generators.
   * @return [List<String>] A list of generator type keys.
   */
  public List<String> getListKey() {
    ArrayList<String> listKeys = new ArrayList<>();
    String key = mapGenerators.keySet().toString();
    String returnString = key.substring(1, key.length() - 1);
    returnString = returnString + ", ";
    int countKey = mapGenerators.size();
    for (int i = 0; i < countKey; i++) {
      String r = returnString.substring(0, returnString.indexOf(','));
      returnString = returnString.substring(returnString.indexOf(',') + 2);
      listKeys.add(r);
    }
    return listKeys;
  }

  /**
   * Initializes the map of generators.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
  private void initMapGenerators()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    List<GeneratorType> listType = new ArrayList<>();
    this.mapGenerators = new TreeMap<>();
    GeneratorType[] type = GeneratorType.values();
    Collections.addAll(listType, type);
    for (int i = 0; i < listType.size(); i++) {
      Generator gen = newGenerator(listType.get(i));
      mapGenerators.put(listType.get(i), gen);
    }
  }

  /**
   * Initializes the generators.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
  public void initializeGenerators()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    initMapGenerators();
  }

  /**
   * Initializes the strategy.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
  public void initialize()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    initMapGenerators();
  }

  /**
   * Gets the current count.
   * @return [int] The current count.
   */
  public int getCountCurrent() {
    return countCurrent;
  }

  /**
   * Sets the current count.
   * @param countCurrent [int] The current count to set.
   */
  public void setCountCurrent(int countCurrent) {
    this.countCurrent = countCurrent;
  }

  /**
   * Destroys the singleton instance and clears references.
   */
  public static void destroyExecute() {
    strategy = null;
    RandomSearch.setListStateReference(null);
  }

  /**
   * Gets the threshold value. 
   * @return [double] The threshold value.
   */
  public double getThreshold() {
    return threshold;
  }
  
  /**
   * Sets the threshold value.
   * @param threshold [double] The threshold value to set.
   */
  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  /**
   * Calculates the offline performance.
   * @param sumMax [float] The sum of maximum values.
   * @param countOff [int] The count for offline performance.
   */

  public void calculateOffLinePerformance(float sumMax, int countOff) {
    float off = sumMax / countPeriodChange;
    listOfflineError[countOff] = off;
  }

  /**
   * Updates the reference state based on the generator type.
   * @param generatorType [GeneratorType] The type of generator.
   */
  public void updateRef(GeneratorType generatorType) {

    if (generatorType.equals(GeneratorType.MultiGenerator)) {
      updateRefMultiG();
      bestState = MultiGenerator.getListStateReference().get(MultiGenerator.getListStateReference().size() - 1);
    } else {
      updateRefGenerator(generator);
      bestState = generator.getReference();
    }
  }

  /**
   * Updates the reference states for all generators in the multi-generator setup.
   */
  public void updateRefMultiG() {
    for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
      updateRefGenerator(MultiGenerator.getListGenerators()[i]);
    }
  }

  /**
   * Updates the reference state for a specific generator.
   * @param generator [Generator] The generator to update.
   */
  public void updateRefGenerator(Generator generator) {
    if (generator.getType().equals(GeneratorType.HillClimbing)
        || generator.getType().equals(GeneratorType.TabuSearch)
        || generator.getType().equals(GeneratorType.RandomSearch)
        || generator.getType().equals(GeneratorType.SimulatedAnnealing)) {
      double evaluation = getProblem().getFunction().get(0).evaluation(generator.getReference());
      generator.getReference().getEvaluation().set(0, evaluation);

    }
    if (generator.getType().equals(GeneratorType.GeneticAlgorithm)
        || generator.getType().equals(GeneratorType.DistributionEstimationAlgorithm)
        || generator.getType().equals(GeneratorType.EvolutionStrategies)) {
      for (int j = 0; j < generator.getReferenceList().size(); j++) {
        double evaluation = getProblem().getFunction().get(0).evaluation(generator.getReferenceList().get(j));
        generator.getReferenceList().get(j).getEvaluation().set(0, evaluation);
      }
    }
  }

  /**
   * Gets the map of generators.
   * @return [SortedMap<GeneratorType, Generator>] The map of generators.
   */
  public SortedMap<GeneratorType, Generator> getMapGenerators() {
    return mapGenerators;
  }

  /**
   * Sets the map of generators.
   * @param mapGenerators [SortedMap<GeneratorType, Generator>] The map of generators to set.
   */
  public void setMapGenerators(SortedMap<GeneratorType, Generator> mapGenerators) {
    this.mapGenerators = mapGenerators;
  }

  /**
   * Gets the current generator.
   * @return [Generator] The current generator.
   */
  public Generator getGenerator() {
    return generator;
  }

  /**
   * Sets the current generator.
   * @param generator [Generator] The current generator to set.
   */
  public void setGenerator(Generator generator) {
    this.generator = generator;
  }

  /**
   * Gets the list of states.
   * @return [List<State>] The list of states.
   */
  public List<State> getListStates() {
    return listStates;
  }

  /**
   * Sets the list of states.
   * @param listStates [List<State>] The list of states to set.
   */
  public void setListStates(List<State> listStates) {
    this.listStates = listStates;
  }

  /**
   * Gets the list of reference non-dominated population.
   * @return [List<State>] The list of reference non-dominated population.
   */
  public List<State> getListRefPoblacFinal() {
    return listRefPoblacFinal;
  }

  /**
   * Sets the list of reference non-dominated population.
   * @param listRefPoblacFinal [List<State>] The list of reference non-dominated population to set.
   */
  public void setListRefPoblacFinal(List<State> listRefPoblacFinal) {
    this.listRefPoblacFinal = listRefPoblacFinal;
  }
}
