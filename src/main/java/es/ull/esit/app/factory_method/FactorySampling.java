package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import es.ull.esit.app.evolutionary_algorithms.complement.Sampling;
import es.ull.esit.app.evolutionary_algorithms.complement.SamplingType;
import es.ull.esit.app.factory_interface.IFFSampling;

/**
 * Class that implements the factory method for creating Sampling instances.
 */
public class FactorySampling implements IFFSampling {

  /**
   * Factory method to create a Sampling based on the provided SamplingType.
   * @param typesampling [SamplingType] The type of sampling strategy to create.
   * @return [Sampling] An instance of the specified Sampling type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	public Sampling createSampling(SamplingType typesampling) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + typesampling.toString();
		return (Sampling) FactoryLoader.getInstance(className);
	}
}
