package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;
import es.ull.esit.app.factory_interface.IFFactoryReplace;

/**
 * Class that implements the factory method for creating Replace instances.
 */
public class FactoryReplace implements IFFactoryReplace {

 
	/**
   * Factory method to create a Replace based on the provided ReplaceType.
   * 
   * @param typereplace [ReplaceType] The type of replace strategy to create.
   * @return [Replace] An instance of the specified Replace type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   * 
   */
	public Replace createReplace( ReplaceType typereplace ) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String className = "evolutionary_algorithms.complement." + typereplace.toString();
		return (Replace) FactoryLoader.getInstance(className);
	}
}
