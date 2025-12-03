package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;

/**
 * Interface for the Factory of Generator objects.
 */
public interface IFFactoryGenerator {
	
	/**
   * Method to create a Generator object based on the type of generator.
   * @param generatortype [GeneratorType] The type of generator.
   * @return [Generator] The created Generator object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	Generator createGenerator(GeneratorType generatortype)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
