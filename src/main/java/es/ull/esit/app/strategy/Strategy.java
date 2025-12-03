package es.ull.esit.app.strategy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.logging.Level;


import es.ull.esit.app.factory_interface.IFFactoryGenerator;
import es.ull.esit.app.factory_method.FactoryGenerator;

import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

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

/**
 * Singleton class that manages the execution strategy of metaheuristic algorithms.
 */
public class Strategy {

  private static final Logger LOGGER = Logger.getLogger(Strategy.class.getName());

  /** Singleton instance of the Strategy class */
	private static Strategy strategy = null;
  /** Best state found during the execution */
	private State bestState;
  /** Problem instance associated with the strategy */
	private Problem problem;
  /** Map of generators used in the strategy */
	private SortedMap<GeneratorType, Generator> mapGenerators;
  /** Stop execution criteria */
	private StopExecute stopexecute;
  /** Update parameter strategy */
	private UpdateParameter updateparameter;
  /** Factory for creating generators */
	private IFFactoryGenerator ifFactoryGenerator;
  /** Current iteration count */
	private int countCurrent;
  /** Maximum iteration count */
	private int countMax;
  /** Generator used in the strategy */
	public Generator generator;
  /** Threshold value for the strategy */
	public double threshold;

  /** List of all generated states */
	private List<State> listStates;
  /** List of best states found in each iteration */
	private List<State> listBest;
  /** Dominance criteria */
	public Dominance notDominated; 

	/** Flags for saving various data during execution */
	private boolean saveListStates;                    /** Flag to save the list of generated states */
	private boolean saveListBestStates;                /** Flag to save the list of best states found in each iteration */
	private boolean saveFreneParetoMonoObjetivo;       /** Flag to save the list of non-dominated solutions from an execution */
	private boolean calculateTime;                     /** Flag to calculate the execution time of an algorithm */
	
	/** Variables for tracking execution time */
	long initialTime;
	long finalTime;
	
  /** Array to store offline performance metrics */
	private float[] listOfflineError = new float[100];
  /** Variables for managing iteration changes */
	private int countPeriodChange = 0; 
  /** Variable to control the current period */
	private int periodo; 


  /** Private constructor for singleton pattern */
	private Strategy(){
		super();
	}

  /** Method to get the singleton instance of the Strategy class */
	public static synchronized Strategy getStrategy() {
		if (strategy == null) {
			strategy = new Strategy();
		}
		return strategy;
	}

  /**
   * Executes the strategy with the specified parameters.
   * 
   * @param countmaxIterations      [int] Maximum number of iterations
   * @param countIterationsChange   [int] Number of iterations before a change occurs
   * @param operatornumber          [int] Operator number
   * @param generatorType           [GeneratorType] Type of generator to use
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a required class is not found.
   * @throws InstantiationException If an error occurs during instantiation.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an error occurs during method invocation.
   * @throws NoSuchMethodException If a required method is not found.
   */
	public void executeStrategy (int countmaxIterations, int countIterationsChange, int operatornumber, GeneratorType generatorType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(calculateTime){
			initialTime = System.currentTimeMillis();
		}
		this.countMax = countmaxIterations; 
		Generator randomInitial = new RandomSearch();
		State initialState = randomInitial.generate(operatornumber);
		problem.evaluate(initialState);
		initialState.setTypeGenerator(generatorType);
		getProblem().setState(initialState);
		if(saveListStates){
			listStates = new ArrayList<>(); 
			listStates.add(initialState);
		}
		if(saveListBestStates){
			listBest = new ArrayList<>(); 
			listBest.add(initialState);
		}
		if(saveFreneParetoMonoObjetivo){
			notDominated = new Dominance();
		}
		generator = newGenerator(generatorType);
		generator.setInitialReference(initialState);
		bestState = initialState;
		countCurrent = 0;
		List<State> listRefPoblacFinal = new ArrayList<>();
		MultiGenerator multiGenerator = null;
		countPeriodChange = countIterationsChange;
		int countChange = countIterationsChange;
		int countPeriodo = countIterationsChange / 10; 
		if(generatorType.equals(GeneratorType.MultiGenerator)){
			initializeGenerators();
			MultiGenerator.initializeGenerators();
			MultiGenerator.listGeneratedPP.clear();
			multiGenerator = ((MultiGenerator)generator).copy();
		}
		else initialize();
		update(countCurrent);
		
		float sumMax = 0; 
		int countOff = 0; 
		while (!stopexecute.stopIterations(countCurrent, countmaxIterations).booleanValue()){
			if(countCurrent == countChange){
				calculateOffLinePerformance(sumMax, countOff);
				countOff++;
				sumMax = 0;
				updateRef(generatorType);
				countChange = countChange + countPeriodChange;
				State stateCandidate = null;
				if(generatorType.equals(GeneratorType.MultiGenerator)){
					if(countPeriodo == countCurrent){
						updateCountGender();
						countPeriodo = countPeriodo + countPeriodChange / 10;
						periodo = 0;
						MultiGenerator.activeGenerator.countBetterGender = 0;
					}
					updateWeight();
					stateCandidate = multiGenerator.generate(operatornumber);
					problem.evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					multiGenerator.updateReference(stateCandidate, countCurrent);
				}
				else {
					stateCandidate = generator.generate(operatornumber);
					problem.evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					generator.updateReference(stateCandidate, countCurrent);
					if(saveListStates){
						listStates.add(stateCandidate);
					}
				}
				
				if ((getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) < stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if ((problem.getTypeProblem().equals(ProblemType.MINIMIZAR)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) > stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if(saveListBestStates){
					listBest.add(bestState);
				}
				sumMax = (float) (sumMax + bestState.getEvaluation().get(0));

			}
			else {
				State stateCandidate = null;
				if(generatorType.equals(GeneratorType.MultiGenerator)){
					if(countPeriodo == countCurrent){
						updateCountGender();
						countPeriodo = countPeriodo + countPeriodChange / 10;
						periodo++;
						MultiGenerator.activeGenerator.countBetterGender = 0;
					}
					stateCandidate = multiGenerator.generate(operatornumber);
					problem.evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					multiGenerator.updateReference(stateCandidate, countCurrent);
				}
				else {
					stateCandidate = generator.generate(operatornumber);
					problem.evaluate(stateCandidate);
					stateCandidate.setEvaluation(stateCandidate.getEvaluation());
					stateCandidate.setNumber(countCurrent);
					stateCandidate.setTypeGenerator(generatorType);
					generator.updateReference(stateCandidate, countCurrent); 
					if(saveListStates){
						listStates.add(stateCandidate);
					}
					if(saveFreneParetoMonoObjetivo){
						notDominated.listDominance(stateCandidate, listRefPoblacFinal);
					}
				}
				countCurrent = UpdateParameter.updateParameter(countCurrent);
				if ((getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) < stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if ((problem.getTypeProblem().equals(ProblemType.MINIMIZAR)) && bestState.getEvaluation().get(bestState.getEvaluation().size() - 1) > stateCandidate.getEvaluation().get(bestState.getEvaluation().size() - 1)) {
					bestState = stateCandidate;
				}
				if(saveListBestStates){
					listBest.add(bestState);
				}
				sumMax = (float) (sumMax + bestState.getEvaluation().get(0));
			}
		}

		if(calculateTime){
			finalTime = System.currentTimeMillis();
			long timeExecute = finalTime - initialTime;
      LOGGER.log(Level.INFO, "El tiempo de ejecucion: {0}", timeExecute);

		}
		if(generatorType.equals(GeneratorType.MultiGenerator)){
			listBest =  multiGenerator.getReferenceList();
			calculateOffLinePerformance(sumMax, countOff);
			if(countPeriodo == countCurrent){
				updateCountGender();
			}
		}
		else{
			listBest = generator.getReferenceList();
			calculateOffLinePerformance(sumMax, countOff);
		} 
	}
  
  /**
   * Updates the count of genders for each generator in MultiGenerator.
   * 
   */
	public void updateCountGender(){ 
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			if(!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiGenerator) ){
				MultiGenerator.getListGenerators()[i].getListCountGender()[periodo] = MultiGenerator.getListGenerators()[i].countGender + MultiGenerator.getListGenerators()[i].getListCountGender()[periodo];
				MultiGenerator.getListGenerators()[i].getListCountBetterGender()[periodo] = MultiGenerator.getListGenerators()[i].countBetterGender + MultiGenerator.getListGenerators()[i].getListCountBetterGender()[periodo];
				MultiGenerator.getListGenerators()[i].countGender = 0;
				MultiGenerator.getListGenerators()[i].countBetterGender = 0;
			}
		}
	}

  /**
   * Updates the weights of each generator in MultiGenerator.
   * 
   */
	public void updateWeight(){
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			if(!MultiGenerator.getListGenerators()[i].getType().equals(GeneratorType.MultiGenerator)){
				MultiGenerator.getListGenerators()[i].setWeight((float) 50.0);
			}
		}
	}
	
	/**
   * Updates the strategy based on the current iteration count.
   * 
   * @param countIterationsCurrent [Integer] Current iteration count.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a required class is not found.
   * @throws InstantiationException If an error occurs during instantiation.
   * @throws IllegalAccessException If an access to a class or method is denied.
   * @throws InvocationTargetException If an error occurs during method invocation.
   * @throws NoSuchMethodException If a required method is not found.
   */
	public void update(Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {//HashMap<String, Object> map, 
		if(countIterationsCurrent.equals(GeneticAlgorithm.getCountRef() - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.GeneticAlgorithm);
		}
		if(countIterationsCurrent.equals(EvolutionStrategies.getCountRef() - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.EvolutionStrategies);
		}			
		if(countIterationsCurrent.equals(DistributionEstimationAlgorithm.getCountRef() - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
		}
		if(countIterationsCurrent.equals(ParticleSwarmOptimization.getCountRef() - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.ParticleSwarmOptimization);
		}
	}

  /**
   * Creates a new generator based on the specified type.
   * 
   * @param Generatortype [GeneratorType] Type of generator to create.
   * @return [Generator] Newly created generator instance.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a segurity violation occurs.
   * @throws ClassNotFoundException If a required class is not found.
   * @throws InstantiationException If an error occurs during instantiation.
   * @throws IllegalAccessException If an access to a class or method is denied.
   * @throws InvocationTargetException If an error occurs during method invocation.
   * @throws NoSuchMethodException If a required method is not found.
   */
	public Generator newGenerator(GeneratorType generatorType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ifFactoryGenerator = new FactoryGenerator();
		return ifFactoryGenerator.createGenerator(generatorType);
	}

  /**
   * Gets the best state found during the execution.
   * 
   * @return [State] Best state found.
   */
	public State getBestState() {
		return bestState;
	}

  /**
   * Sets the best state found during the execution.
   * 
   * @param besState [State] Best state to set.
   */
	public void setBestState(State besState) {
		this.bestState = besState;
	}

  /**  
   * Gets the stop execution criteria.
   * 
   * @return [StopExecute] Stop execution criteria.
   */
	public StopExecute getStopexecute() {
		return stopexecute;
	}

  /**
   * Gets the maximum iteration count.
   * 
   * @return [int] Maximum iteration count.
   */
	public int getCountMax() {
		return countMax;
	}

  /**
   * Sets the maximum iteration count.
   * 
   * @param countMax [int] Maximum iteration count.
   */
	public void setCountMax(int countMax) {
		this.countMax = countMax;
	}

  /**
   * Sets the stop execution criteria.
   * 
   * @param stopexecute [StopExecute] Stop execution criteria.
   */
	public void setStopexecute(StopExecute stopexecute) {
		this.stopexecute = stopexecute;
	}

  /**
   * Gets the update parameter strategy.
   * 
   * @return [UpdateParameter] Update parameter strategy.
   */
	public UpdateParameter getUpdateparameter() {
		return updateparameter;
	}

  /**
   * Sets the update parameter strategy.
   * 
   * @param updateparameter [UpdateParameter] Update parameter strategy.
   */
	public void setUpdateparameter(UpdateParameter updateparameter) {
		this.updateparameter = updateparameter;
	}

  /**
   * Gets the problem instance associated with the strategy.
   * 
   * @return [Problem] Problem instance.
   */
	public Problem getProblem() {
		return problem;
	}

  /**
   * Sets the problem instance associated with the strategy.
   * 
   * @param problem [Problem] Problem instance.
   */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
  /**
   * Gets the list of generator keys.
   * 
   * @return [ArrayList<String>] List of generator keys.
   */
	public List<String> getListKey(){
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
   * Initializes the generators used in the strategy.
   * 
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a required class is not found.
   * @throws InstantiationException If an error occurs during instantiation.
   * @throws IllegalAccessException If an access to a class or method is denied.
   * @throws InvocationTargetException If an error occurs during method invocation.
   * @throws NoSuchMethodException If a required method is not found.
   */
	public void initializeGenerators()throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<GeneratorType>	listType = new ArrayList<>();
		this.mapGenerators = new TreeMap<>();
		GeneratorType[] type = GeneratorType.values();
		
    Collections.addAll(listType, type);

		for (int i = 0; i < listType.size(); i++) {
			Generator gen = newGenerator(listType.get(i));
			mapGenerators.put(listType.get(i), gen);
		}
	}

  /**
   * Initializes the generators used in the strategy.
   * 
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class is not found.
   * @throws InstantiationException If an error occurs during instantiation.
   * @throws IllegalAccessException If an access to a class or method is denied.
   * @throws InvocationTargetException If an error occurs during method invocation.
   * @throws NoSuchMethodException If a required method is not found.
   */
	public void initialize()throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<GeneratorType>	listType = new ArrayList<>();
		this.mapGenerators = new TreeMap<>();
		GeneratorType[] type = GeneratorType.values();
    Collections.addAll(listType, type);

		for (int i = 0; i < listType.size(); i++) {
			Generator gen = newGenerator(listType.get(i));
			mapGenerators.put(listType.get(i), gen);
		}
	}

  /**
   * Gets the current iteration count.
   * 
   * @return [int] Current iteration count.
   */
	public int getCountCurrent() {
		return countCurrent;
	}

  /**
   * Sets the current iteration count.
   * 
   * @param countCurrent [int] Current iteration count.
   */
	public void setCountCurrent(int countCurrent) {
		this.countCurrent = countCurrent;
	}

  /**
   * Destroys the singleton instance of the Strategy class and resets references.
   * 
   */
	public static void destroyExecute() {
		strategy = null;
		RandomSearch.setListStateReference(null);
	}
	
  /**
   * Gets the threshold value for the strategy.
   * 
   * @return [double] Threshold value.
   */
	public double getThreshold() {
		return threshold;
	}

  /**
   * Sets the threshold value for the strategy.
   * 
   * @param threshold [double] Threshold value.
   */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
  /**
   * Calculates the offline performance metric.
   *  
   * @param sumMax   [float] Sum of maximum evaluations.
   * @param countOff [int] Count of offline evaluations.
   */
	public void calculateOffLinePerformance(float sumMax, int countOff){
		float off = sumMax / countPeriodChange;
		listOfflineError[countOff] = off;
	}
	
  /**
   * Updates the reference states based on the generator type.
   * 
   * @param generatorType [GeneratorType] Type of generator.
   */
	public void updateRef(GeneratorType generatorType){

		if(generatorType.equals(GeneratorType.MultiGenerator)){
			updateRefMultiG();
			bestState = MultiGenerator.listStateReference.get( MultiGenerator.listStateReference.size() - 1);
		}
		else{
			updateRefGenerator(generator);
			bestState = generator.getReference();
		}
	}

  /**
   * Updates the reference states for all generators in MultiGenerator.
   */
	public void updateRefMultiG() {
		for (int i = 0; i < MultiGenerator.getListGenerators().length; i++) {
			updateRefGenerator(MultiGenerator.getListGenerators()[i]);
		}
	}

  /**
   * Updates the reference state(s) for the specified generator.
   * 
   * @param generator [Generator] The generator whose reference state(s) will be updated.
   */
	public void updateRefGenerator(Generator generator) {
		if(generator.getType().equals(GeneratorType.HillClimbing) || generator.getType().equals(GeneratorType.TabuSearch) || generator.getType().equals(GeneratorType.RandomSearch) || generator.getType().equals(GeneratorType.SimulatedAnnealing)){
			double evaluation = getProblem().getFunction().get(0).evaluation(generator.getReference());
			generator.getReference().getEvaluation().set(0, evaluation);

		}
		if(generator.getType().equals(GeneratorType.GeneticAlgorithm) || generator.getType().equals(GeneratorType.DistributionEstimationAlgorithm) || generator.getType().equals(GeneratorType.EvolutionStrategies)){
			for (int j = 0; j < generator.getReferenceList().size(); j++) {
				double evaluation = getProblem().getFunction().get(0).evaluation(generator.getReferenceList().get(j));
				generator.getReferenceList().get(j).getEvaluation().set(0, evaluation);
			}
		}
	}

}
