package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.evolutionary_algorithms.complement.Crossover;
import es.ull.esit.app.evolutionary_algorithms.complement.CrossoverType;

/**
 * Interface for the Factory of Crossover objects.
 */
public interface IFFactoryCrossover {
  /**
   * Method to create a Crossover object based on the type of crossover.
   * @param crossovertype [CrossoverType] The type of crossover.
   * @return [Crossover] The created Crossover object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	Crossover createCrossover(CrossoverType crossovertype)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
