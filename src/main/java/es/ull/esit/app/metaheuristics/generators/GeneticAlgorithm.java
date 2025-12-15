package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;

import es.ull.esit.app.evolutionary_algorithms.complement.Crossover;
import es.ull.esit.app.evolutionary_algorithms.complement.CrossoverType;
import es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import es.ull.esit.app.evolutionary_algorithms.complement.Mutation;
import es.ull.esit.app.evolutionary_algorithms.complement.MutationType;
import es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;
import es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;
import es.ull.esit.app.factory_interface.IFFactoryCrossover;
import es.ull.esit.app.factory_interface.IFFactoryFatherSelection;
import es.ull.esit.app.factory_interface.IFFactoryMutation;
import es.ull.esit.app.factory_interface.IFFactoryReplace;
import es.ull.esit.app.factory_method.FactoryCrossover;
import es.ull.esit.app.factory_method.FactoryFatherSelection;
import es.ull.esit.app.factory_method.FactoryMutation;
import es.ull.esit.app.factory_method.FactoryReplace;



/**
 * Genetic Algorithm generator implementation.
 */
public class GeneticAlgorithm extends Generator {

  /** Reference state for the Genetic Algorithm. */
  private State stateReferenceGA;
  /** List of states in the Genetic Algorithm. */
  private List<State> listState = new ArrayList<>();

  /** Random number generator. */
  private SecureRandom random = new SecureRandom();

  /** Static configuration parameters for the Genetic Algorithm. */
  private static MutationType mutationType;
  private static CrossoverType crossoverType;
  private static ReplaceType replaceType;
  private static SelectionType selectionType;

  /** Type of the generator. */
  private GeneratorType generatorType;
  /** Static configuration parameters for the Genetic Algorithm. */
  private static double pc;
  private static double pm;
  /** Reference count for the Genetic Algorithm. */
  private static int countRef = 0;
  private static int truncation;

  /** Weight parameter for the Genetic Algorithm. */
  private float weight;

  /** Static counters for gender statistics. */
  private int[] listCountBetterGenderGeneticAlgorithm = new int[10];
  private int[] listCountGender = new int[10];
  private float[] listTrace = new float[1200000];

  /** Constructor */
  public GeneticAlgorithm() {
    super();
    this.listState = getListStateRef();
    this.generatorType = GeneratorType.GeneticAlgorithm;
    this.weight = 50;

    // Inicializamos trazas y contadores sobre los arrays CORRECTOS
    listTrace[0] = this.weight;
    listCountBetterGenderGeneticAlgorithm[0] = 0;
    listCountGender[0] = 0;

    // Opcional pero recomendable: enlazar el campo heredado al array propio
    this.listCountBetterGender = this.listCountBetterGenderGeneticAlgorithm;
  }

  /**
   * Generates a new state using the Genetic Algorithm.
   * 
   * @param operatornumber [Integer] The operator number.
   * @return [State] The generated state.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If the class is not found.
   * @throws InstantiationException    If there is an error during the
   *                                   instantiation.
   * @throws IllegalAccessException    If there is no access to the method.
   * @throws InvocationTargetException If the method cannot be invoked.
   * @throws NoSuchMethodException     If the method does not exist.
   */
  @Override
  public State generate(Integer operatornumber)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    List<State> refList = new ArrayList<>(this.listState);
    IFFactoryFatherSelection iffatherselection = new FactoryFatherSelection();
    FatherSelection selection = iffatherselection.createSelectFather(selectionType);
    List<State> fathers = selection.selection(refList, truncation);
    int pos1 = random.nextInt(fathers.size());
    int pos2 = random.nextInt(fathers.size());

    State auxState1 = (State) Strategy.getStrategy().getProblem().getState().getCopy();
    auxState1.setCode(new ArrayList<>(fathers.get(pos1).getCode()));
    auxState1.setEvaluation(fathers.get(pos1).getEvaluation());
    auxState1.setNumber(fathers.get(pos1).getNumber());
    auxState1.setTypeGenerator(fathers.get(pos1).getTypeGenerator());

    State auxState2 = (State) Strategy.getStrategy().getProblem().getState().getCopy();
    auxState2.setCode(new ArrayList<>(fathers.get(pos2).getCode()));
    auxState2.setEvaluation(fathers.get(pos2).getEvaluation());
    auxState2.setNumber(fathers.get(pos2).getNumber());
    auxState2.setTypeGenerator(fathers.get(pos2).getTypeGenerator());

    IFFactoryCrossover iffactorycrossover = new FactoryCrossover();
    Crossover crossover = iffactorycrossover.createCrossover(crossoverType);
    auxState1 = crossover.crossover(auxState1, auxState2, pc);

    IFFactoryMutation iffactorymutation = new FactoryMutation();
    Mutation mutation = iffactorymutation.createMutation(mutationType);
    auxState1 = mutation.mutation(auxState1, pm);
    return auxState1;
  }

  /**
   * Gets the reference state for the Genetic Algorithm.
   * @return [State] The reference state.
   */
  @Override
  public State getReference() {
    stateReferenceGA = listState.get(0);
    if (Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)) {
      for (int i = 1; i < listState.size(); i++) {
        if (stateReferenceGA.getEvaluation().get(0) < listState.get(i).getEvaluation().get(0))
          stateReferenceGA = listState.get(i);
      }
    } else {
      for (int i = 1; i < listState.size(); i++) {
        if (stateReferenceGA.getEvaluation().get(0) > listState.get(i).getEvaluation().get(0))
          stateReferenceGA = listState.get(i);
      }
    }
    return stateReferenceGA;
  }

  /**
   * Sets the reference state for the Genetic Algorithm.
   * @param stateRef [State] The reference state to set.
   */
  public void setStateRef(State stateRef) {
    this.stateReferenceGA = stateRef;
  }

  /**
   * Sets the initial reference state for the Genetic Algorithm.
   * @param stateInitialRef [State] The initial reference state.
   */
  @Override
  public void setInitialReference(State stateInitialRef) {
    this.stateReferenceGA = stateInitialRef;
  }

  /**
   * Updates the reference state with a candidate state.
   * 
   * @param stateCandidate          [State] The candidate state.
   * @param countIterationsCurrent  [Integer] The current iteration count.
   * @throws IllegalArgumentException  If an illegal argument is provided.
   * @throws SecurityException         If a security violation occurs.
   * @throws ClassNotFoundException    If the class is not found.
   * @throws InstantiationException    If there is an error during the
   *                                   instantiation.
   * @throws IllegalAccessException    If there is no access to the method.
   * @throws InvocationTargetException If the method cannot be invoked.
   * @throws NoSuchMethodException     If the method does not exist.
   */
  @Override
  public void updateReference(State stateCandidate, Integer countIterationsCurrent)
      throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    IFFactoryReplace iffreplace = new FactoryReplace();
    Replace replace = iffreplace.createReplace(replaceType);
    listState = replace.replace(stateCandidate, listState);
  }

  /**
   * Gets the list of states in the Genetic Algorithm.
   * @return [List<State>] The list of states.
   */
  public List<State> getListState() {
    return listState;
  }

  /**
   * Sets the list of states in the Genetic Algorithm.
   * @param listState [List<State>] The list of states to set.
   */
  public void setListState(List<State> listState) {
    this.listState = listState;
  }

  /**
   * Gets the reference list of states from the strategy.
   * @return [List<State>] The reference list of states.
   */
  public List<State> getListStateRef() {
    Boolean found = false;
    List<String> key = Strategy.getStrategy().getListKey();
    int count = 0;
    while ((found.equals(false)) && (Strategy.getStrategy().getMapGenerators().size() > count)) {
      if (key.get(count).equals(GeneratorType.GeneticAlgorithm.toString())) {
        GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
        GeneticAlgorithm generator = (GeneticAlgorithm) Strategy.getStrategy().getMapGenerators().get(keyGenerator);
        if (generator.getListState().isEmpty()) {
          listState.addAll(RandomSearch.getListStateReference());
        } else {
          listState = generator.getListState();
        }
        found = true;
      }
      count++;
    }
    return listState;
  }

  /**
   * Gets the generator type.
   * @return [GeneratorType] The generator type.
   */
  public GeneratorType getGeneratorType() {
    return generatorType;
  }

  /**
   * Sets the generator type.
   * @param generatorType [GeneratorType] The generator type to set.
   */
  public void setGeneratorType(GeneratorType generatorType) {
    this.generatorType = generatorType;
  }

  /**
   * Gets the type of generator.
   * @return [GeneratorType] The type of generator.
   */
  @Override
  public GeneratorType getType() {
    return this.generatorType;
  }

  /**
   * Gets the list of reference states.
   * @return [List<State>] The list of reference states.
   */
  @Override
  public List<State> getReferenceList() {
    List<State> referenceList = new ArrayList<>();
    for (int i = 0; i < listState.size(); i++) {
      State value = listState.get(i);
      referenceList.add(value);
    }
    return referenceList;
  }

  /**
   * Gets the list of son states.
   * @return [List<State>] The list of son states.
   */
  @Override
  public List<State> getSonList() {
    List<State> sonList = new ArrayList<>();
    for (int i = 0; i < listState.size(); i++) {
      State value = listState.get(i);
      sonList.add(value);
    }
    return sonList;
  }

  /**
   * Awards an update to the reference state with a candidate state. 
   * @param stateCandidate [State] The candidate state.
   * @return [boolean] True if the update is awarded, false otherwise.
   */
  @Override
  public boolean awardUpdateREF(State stateCandidate) {
    return (stateCandidate.getNumber() == this.stateReferenceGA.getNumber());
  }

  /**
   * Gets the weight parameter for the Genetic Algorithm.
   * @return [float] The weight parameter.
   */
  @Override
  public float getWeight() {
    return this.weight;
  }

  /**
   * Sets the weight parameter for the Genetic Algorithm.
   * @param weight [float] The weight parameter to set.
   */
  @Override
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Gets the list of better gender counts.
   * @return [int[]] The list of better gender counts.
   */
  @Override
  public int[] getListCountBetterGender() {
    return this.listCountBetterGenderGeneticAlgorithm;
  }

  /**
   * Gets the list of gender counts.
   * @return [int[]] The list of gender counts.
   */
  @Override
  public int[] getListCountGender() {
    return this.listCountGender;
  }

  /**
   * Gets the list of trace values.
   * @return [float[]] The list of trace values.
   */
  @Override
  public float[] getTrace() {
    return this.listTrace;
  }

  /** Gets the reference count for the Genetic Algorithm.
   * @return [int] The reference count.
   */
  public static int getCountRef() {
    return countRef;
  }

  /** Sets the reference count for the Genetic Algorithm.
   * @param countRef [int] The reference count to set.
   */
  public static void setCountRef(int countRef) {
    GeneticAlgorithm.countRef = countRef;
  }
}