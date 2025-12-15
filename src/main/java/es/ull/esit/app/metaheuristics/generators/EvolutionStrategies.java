package es.ull.esit.app.metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import es.ull.esit.app.evolutionary_algorithms.complement.Mutation;
import es.ull.esit.app.evolutionary_algorithms.complement.MutationType;
import es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;
import es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;
import es.ull.esit.app.factory_interface.IFFactoryFatherSelection;
import es.ull.esit.app.factory_interface.IFFactoryMutation;
import es.ull.esit.app.factory_interface.IFFactoryReplace;
import es.ull.esit.app.factory_method.FactoryFatherSelection;
import es.ull.esit.app.factory_method.FactoryMutation;
import es.ull.esit.app.factory_method.FactoryReplace;

/**
 * Class that implements the Evolution Strategies generator.
 */
public class EvolutionStrategies extends Generator {
	
  /** State reference for the Evolution Strategies. */
	private State stateReferenceES;
  /** List of reference states for the Evolution Strategies. */
	private List<State> listStateReference = new ArrayList<>();

  /** Random number generator. */
  private SecureRandom random = new SecureRandom();
  
  /** Type of generator. */
	private GeneratorType generatorType;
  /** Mutation probability. */
	private static double pm;
  /** Types for mutation, replacement, and selection. */
	private static MutationType mutationType;
  /** Type of replacement strategy. */
	private static ReplaceType replaceType;
  /** Type of selection strategy. */
	private static SelectionType selectionType;
  /** Count reference for the generator. */
	private static int countRef = 0;
  /** Truncation parameter. */
	private static int truncation;
  /** Weight for the generator. */
	private float weight = 50;
	
  /** Lists for tracking performance metrics. */
	private int[] listCountBetterGenderEvolutionStrategies = new int[10];
  /** List for tracking gender counts. */
	private int[] listCountGender = new int[10];
  /** Trace list for the generator. */
	private float[] listTrace = new float[1200000];
	
	public EvolutionStrategies() {
		super();
    
    this.listCountBetterGenderEvolutionStrategies = new int[10];
    this.listCountGender = new int[10];
    this.listTrace = new float[1200000];

    this.listStateReference = getListStateRef(); 
    this.generatorType = GeneratorType.EvolutionStrategies;
    this.weight = 50.0f;

    this.listTrace[0] = this.weight;
    this.listCountBetterGenderEvolutionStrategies[0] = 0;
    this.listCountGender[0] = 0;
	}

  /**
   * Generates a new state using the Evolution Strategies method.
   * @param operatornumber The operator number (not used in this implementation).
   * @return A new generated state.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException If a method cannot be found.
   */
	@Override
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {

    	IFFactoryFatherSelection iffatherselection = new FactoryFatherSelection();
    	FatherSelection selection = iffatherselection.createSelectFather(selectionType);
    	List<State> fathers = selection.selection(this.listStateReference, truncation);
    	int pos1 = random.nextInt(fathers.size());
    	State candidate = (State) Strategy.getStrategy().getProblem().getState().getCopy();
    	candidate.setCode(new ArrayList<>(fathers.get(pos1).getCode()));
    	candidate.setEvaluation(fathers.get(pos1).getEvaluation());
    	candidate.setNumber(fathers.get(pos1).getNumber());
    	candidate.setTypeGenerator(fathers.get(pos1).getTypeGenerator());
    	
    	IFFactoryMutation iffactorymutation = new FactoryMutation();
    	Mutation mutation = iffactorymutation.createMutation(mutationType);
    	candidate = mutation.mutation(candidate, pm);
    	return candidate;
	}

  /**
   * Gets the reference state for the Evolution Strategies.
   * @return The reference state.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException If a method cannot be found.
   */
	@Override
	public State getReference() {
		stateReferenceES = listStateReference.get(0);
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)){
			for (int i = 1; i < listStateReference.size(); i++) {
				if(stateReferenceES.getEvaluation().get(0) < listStateReference.get(i).getEvaluation().get(0))
					stateReferenceES = listStateReference.get(i);
			}
		}
		else{
			for (int i = 1; i < listStateReference.size(); i++) {
				if(stateReferenceES.getEvaluation().get(0) > listStateReference.get(i).getEvaluation().get(0))
					stateReferenceES = listStateReference.get(i);
			}
		}
		return stateReferenceES;
	}
	
  /**
   * Sets the reference state for the Evolution Strategies.
   * @param stateRef [State] The reference state to set.
   */
	public void setStateRef(State stateRef) {
		this.stateReferenceES = stateRef;
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
   * Sets the initial reference state for the Evolution Strategies.
   * @param stateInitialRef [State] The initial reference state to set.
   */
	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceES = stateInitialRef;
	}

  /**
   * Updates the reference list with a candidate state.
   * @param stateCandidate [State] The candidate state to consider for updating the reference list.
   * @param countIterationsCurrent [Integer] The current iteration count.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If a class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If illegal access occurs.
   * @throws InvocationTargetException If an invocation target error occurs.
   * @throws NoSuchMethodException If a method cannot be found.
   */
	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		IFFactoryReplace iffreplace = new FactoryReplace();
		Replace replace = iffreplace.createReplace(replaceType);
		listStateReference = replace.replace(stateCandidate, listStateReference);

	}
	
  /**
   * Gets the list of reference states for the Evolution Strategies.
   * @return [List<State>] The list of reference states.
   */
	public List<State> getListStateRef(){
		Boolean found = false;
		List<String> key = Strategy.getStrategy().getListKey();
		int count = 0;
		while((found.equals(false)) && (Strategy.getStrategy().getMapGenerators().size() > count)){
			if(key.get(count).equals(GeneratorType.EvolutionStrategies.toString())){
				GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
				EvolutionStrategies generator = (EvolutionStrategies) Strategy.getStrategy().getMapGenerators().get(keyGenerator);
				if(generator.getListStateReference().isEmpty()){
					listStateReference.addAll(RandomSearch.getListStateReference());
				}
				else{
					listStateReference = generator.getListStateReference();
				}
			        found = true;
			}
			count++;
		}
		return listStateReference;
	}

  /**
   * Gets the list of reference states.
   * @return [List<State>] The list of reference states.
   */
	public List<State> getListStateReference() {
		return listStateReference;
	}

  /**
   * Sets the list of reference states.
   * @param listStateReference [List<State>] The list of reference states to set.
   */
	public void setListStateReference(List<State> listStateReference) {
		this.listStateReference = listStateReference;
	}

  /**
   * Gets the type of generator.
   * @return [GeneratorType] The type of generator.
   */
	public GeneratorType getTypeGenerator() {
		return generatorType;
	}

  /**
   * Sets the type of generator.
   * @param generatorType [GeneratorType] The type of generator to set.
   */
	public void setTypeGenerator(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

  /**
   * Gets the list of reference states.
   * @return [List<State>] The list of reference states.
   */
	@Override
	public List<State> getReferenceList() {
		List<State> referenceList = new ArrayList<>();
		for (int i = 0; i < listStateReference.size(); i++) {
			State value = listStateReference.get(i);
			referenceList.add(value);
		}
		return referenceList;
	}

  /**
   * Awards an update to the reference state based on a candidate state.
   * @param stateCandidate [State] The candidate state to consider for awarding an update.
   * @return [boolean] True if the update was awarded, false otherwise.
   */
	@Override 
	public boolean awardUpdateREF(State stateCandidate) {
		boolean award = false;
    if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.MAXIMIZAR)){
      if(stateCandidate.getEvaluation().get(0) > this.getReference().getEvaluation().get(0)){
        award = true;
      }
    }
    else{
      if(stateCandidate.getEvaluation().get(0) < this.getReference().getEvaluation().get(0)){
        award = true;
      }
    }
    return award;
	}

  /**
   * Gets the weight of the generator.
   * @return [float] The weight of the generator.
   */
	@Override
	public float getWeight() {
		return this.weight;
	}

  /**
   * Sets the weight of the generator.
   * @param weight [float] The weight to set.
   */
	@Override
	public void setWeight(float weight) {
		this.weight = weight;
	}

  /**
   * Gets the list of counts of better genders.
   * @return [int[]] The list of counts of better genders.
   */
	@Override
	public int[] getListCountBetterGender() {
		return this.listCountBetterGenderEvolutionStrategies;
	}

  /**
   * Gets the list of counts of genders.
   * @return [int[]] The list of counts of genders.
   */
	@Override
	public int[] getListCountGender() {
		return this.listCountGender;
	}

  /**
   * Gets the trace of the generator.
   * @return [float[]] The trace of the generator.
   */
	@Override
	public float[] getTrace() {
		return this.listTrace;
	}

  /**
   * Gets the count reference for the generator.
   * @return [int] The count reference for the generator.
   */
  public static int getCountRef() {
    return countRef;
  }

  /**
   * Sets the count reference for the generator.
   * @param countRef [int] The count reference to set.
   */
  public static void setCountRef(int countRef) {
    EvolutionStrategies.countRef = countRef;
  }

  /**
   * Gets the list of son states.
   * @return [List<State>] The list of son states.
   */
  @Override
  public List<State> getSonList() {
    // Evolution Strategies does not maintain a separate son list.
    return new ArrayList<>();
  }

}
