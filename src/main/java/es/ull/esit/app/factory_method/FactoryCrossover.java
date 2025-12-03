package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import es.ull.esit.app.evolutionary_algorithms.complement.Crossover;
import es.ull.esit.app.evolutionary_algorithms.complement.CrossoverType;
import es.ull.esit.app.factory_interface.IFFactoryCrossover;


/**
 * Class that implements the factory method for creating Crossover instances.
 */
public class FactoryCrossover implements IFFactoryCrossover {

  /**
   * Factory method to create a Crossover based on the provided CrossoverType.
   *
   * @param crossovertype [CrossoverType] The type of crossover strategy to create.
   * @return [Crossover] An instance of the specified Crossover type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	public Crossover createCrossover(CrossoverType crossovertype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + crossovertype.toString();
		return  (Crossover) FactoryLoader.getInstance(className);
	}
}
