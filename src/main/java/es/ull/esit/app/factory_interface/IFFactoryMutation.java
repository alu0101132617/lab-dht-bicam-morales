package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.evolutionary_algorithms.complement.Mutation;
import es.ull.esit.app.evolutionary_algorithms.complement.MutationType;


/**
 * Interface for the Factory of Mutation objects.
 */
public interface IFFactoryMutation {
	/**
   * Method to create a Mutation object based on the type of mutation.
   * @param typeMutation [MutationType] The type of mutation.
   * @return [Mutation] The created Mutation object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	Mutation createMutation(MutationType typeMutation)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
