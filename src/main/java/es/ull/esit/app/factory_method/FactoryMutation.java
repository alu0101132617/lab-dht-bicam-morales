package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import es.ull.esit.app.evolutionary_algorithms.complement.Mutation;
import es.ull.esit.app.evolutionary_algorithms.complement.MutationType;
import es.ull.esit.app.factory_interface.IFFactoryMutation;

/**
 * Class that implements the factory method for creating Mutation instances.
 */
public class FactoryMutation implements IFFactoryMutation {
  /**
   * Factory method to create a Mutation based on the provided MutationType.
   * @param typeMutation [MutationType] The type of mutation strategy to create.
   * @return [Mutation] An instance of the specified Mutation type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	public Mutation createMutation(MutationType typeMutation) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + typeMutation.toString();
		return  (Mutation) FactoryLoader.getInstance(className);
	}
}
