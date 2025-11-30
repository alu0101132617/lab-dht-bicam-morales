package main.java.es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import main.java.es.ull.esit.app.evolutionary_algorithms.complement.Distribution;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.DistributionType;
import main.java.es.ull.esit.app.factory_interface.IFFactoryDistribution;




public class FactoryDistribution implements IFFactoryDistribution {
	private Distribution distribution;

	public Distribution createDistribution(DistributionType distributiontype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + distributiontype.toString();
		distribution = (Distribution) FactoryLoader.getInstance(className);
		return distribution;
	}
}
