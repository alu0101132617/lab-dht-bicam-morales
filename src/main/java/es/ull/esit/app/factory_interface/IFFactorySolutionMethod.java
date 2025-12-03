package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.problem.extension.SolutionMethod;
import es.ull.esit.app.problem.extension.TypeSolutionMethod;

/**
 * Interface for the Factory of SolutionMethod objects.
 */
public interface IFFactorySolutionMethod {
	
  /**
   * Method to create a SolutionMethod object based on the type of method.
   * @param method [TypeSolutionMethod] The type of solution method.
   * @return [SolutionMethod] The created SolutionMethod object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	SolutionMethod createdSolutionMethod(TypeSolutionMethod  method) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;

}
