package es.ull.esit.app.metaheuristics.generators;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import es.ull.esit.app.problem.definition.State;


/**
 * Abstract class for all the generators.
 */
public abstract class Generator {

  /** Abstract method to generate a new state. 
   * @param operatornumber [Integer] Number of the operator to be used.
   * @throws NoSuchMethodException If the method does not exist.
   * @throws InvocationTargetException If the method cannot be invoked.
   * @throws IllegalAccessException If there is no access to the method.
   * @throws InstantiationException If there is an error during the instantiation.
   * @throws ClassNotFoundException If the class is not found.
   * @throws SecurityException If a security violation occurs.
   * @throws IllegalArgumentException If an illegal argument is provided. 
   * */
	public abstract State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

  /**
   * Abstract method to update the reference state.
   * @param operatornumber [Integer] Number of the operator to be used.
   * @throws NoSuchMethodException If the method does not exist.
   * @throws InvocationTargetException If the method cannot be invoked.
   * @throws IllegalAccessException If there is no access to the method.
   * @throws InstantiationException If there is an error during the instantiation.
   * @throws ClassNotFoundException If the class is not found.
   * @throws SecurityException If a security violation occurs.
   * @throws IllegalArgumentException If an illegal argument is provided. 
   */
	public abstract void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

  /**
   * Abstract method to get the reference state.
   * @return [State] The reference state.
   */
	public abstract State getReference();

  /**
   * Abstract method to set the initial reference state.
   * @param stateInitialRef [State] The initial reference state.
   */
	public abstract void setInitialReference (State stateInitialRef);

  /**
   * Abstract method to get the type of generator.
   * @return [GeneratorType] The type of generator.
   */
	public abstract GeneratorType getType ();

  /**
   * Abstract method to get the list of reference states.
   * @return [List<State>] The list of reference states.
   */
	public abstract List<State> getReferenceList();

  /**
   * Abstract method to get the list of son states.
   * @return [List<State>] The list of son states.
   */
	public abstract List<State> getSonList ();

  /**
   * Abstract method to decide whether to award an update to the reference state.
   * @param stateCandidate [State] The candidate state.
   * @return [boolean] True if the update is awarded, false otherwise.
   */
	public abstract boolean awardUpdateREF(State stateCandidate);

  /**
   * Abstract method to decide whether to award an update to the son state.
   * @param stateCandidate [State] The candidate state.
   * @return [boolean] True if the update is awarded, false otherwise.
   */
	public abstract void setWeight(float weight);

  /**
   * Abstract method to get the weight of the generator.
   * @return [float] The weight of the generator.
   */
	public abstract float getWeight();

	/**
   * Abstract method to get the trace of the generator.
   * @return [float[]] The trace of the generator.
   */
	public abstract float[] getTrace();

  /** Counter for gender statistics. */
	public int countGender;
  /** Counter for better gender statistics. */
	public int countBetterGender;
  /** List of counts for better gender statistics. */
	public abstract int[] getListCountBetterGender();
  /** List of counts for gender statistics. */
	public abstract int[] getListCountGender();
  /** List to hold counts of gender statistics. */
	public int[] listCountBetterGender; 
}
