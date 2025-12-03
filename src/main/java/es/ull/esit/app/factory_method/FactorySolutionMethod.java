package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.problem.extension.SolutionMethod;
import es.ull.esit.app.problem.extension.TypeSolutionMethod;
import es.ull.esit.app.factory_interface.IFFactorySolutionMethod;

/**
 * Class that implements the factory method for creating SolutionMethod instances.
 */
public class FactorySolutionMethod implements IFFactorySolutionMethod {

  /**
   * Factory method to create a SolutionMethod based on the provided TypeSolutionMethod.
   * @param method [TypeSolutionMethod] The type of solution method strategy to create.
   * @return [SolutionMethod] An instance of the specified SolutionMethod type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	@Override
	public SolutionMethod createdSolutionMethod(TypeSolutionMethod method) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "problem.extension." + method.toString();
		return (SolutionMethod) FactoryLoader.getInstance(className);
	}

}
