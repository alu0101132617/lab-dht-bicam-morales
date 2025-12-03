package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.evolutionary_algorithms.complement.Distribution;
import es.ull.esit.app.evolutionary_algorithms.complement.DistributionType;

/**
 * Interface for the Factory of Distribution objects.
 */
public interface IFFactoryDistribution {
  /**
   * Method to create a Distribution object based on the type of distribution.
   * @param typedistribution [DistributionType] The type of distribution.
   * @return [Distribution] The created Distribution object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	Distribution createDistribution(DistributionType typedistribution) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
