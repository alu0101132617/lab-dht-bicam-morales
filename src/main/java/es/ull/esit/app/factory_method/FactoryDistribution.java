package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import es.ull.esit.app.evolutionary_algorithms.complement.Distribution;
import es.ull.esit.app.evolutionary_algorithms.complement.DistributionType;
import es.ull.esit.app.factory_interface.IFFactoryDistribution;

/**
 * Class that implements the factory method for creating Distribution instances.
 */
public class FactoryDistribution implements IFFactoryDistribution {

  /**
   * Factory method to create a Distribution based on the provided DistributionType.
   *
   * @param distributiontype [DistributionType] The type of distribution strategy to create.
   * @return [Distribution] An instance of the specified Distribution type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	public Distribution createDistribution(DistributionType distributiontype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + distributiontype.toString();
		return (Distribution) FactoryLoader.getInstance(className);
	}
}
