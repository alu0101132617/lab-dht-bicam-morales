package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;

/**
 * Interface for the Factory of Replace objects.
 */
public interface IFFactoryReplace {
  /**
   * Method to create a Replace object based on the type of replace.
   * @param typereplace [ReplaceType] The type of replace.
   * @return [Replace] The created Replace object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	Replace createReplace(ReplaceType typereplace)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
