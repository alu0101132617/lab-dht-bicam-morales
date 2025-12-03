package es.ull.esit.app.factory_method;


import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.factory_interface.IFFactoryGenerator;
import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;

/**
 * Class that implements the factory method for creating Generator instances.
 */
public class FactoryGenerator implements IFFactoryGenerator {
  
  /**
   * Factory method to create a Generator based on the provided GeneratorType.
   * 
   * @param generatorType [GeneratorType] The type of generator strategy to create.
   * @return [Generator] An instance of the specified Generator type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	public Generator createGenerator(GeneratorType generatorType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "metaheuristics.generators." + generatorType.toString();
		return (Generator) FactoryLoader.getInstance(className);
	}
}
