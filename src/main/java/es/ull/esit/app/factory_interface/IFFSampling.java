package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.evolutionary_algorithms.complement.Sampling;
import es.ull.esit.app.evolutionary_algorithms.complement.SamplingType;

/**
 * Interface for the Factory of Sampling objects.
 */
public interface IFFSampling {
  /**
   * Method to create a Sampling object based on the type of sampling.
   * @param typesampling [SamplingType] The type of sampling.
   * @return [Sampling] The created Sampling object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */ 
	Sampling createSampling(SamplingType typesampling) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
