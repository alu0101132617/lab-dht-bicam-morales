package main.java.es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import main.java.es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;
import main.java.es.ull.esit.app.factory_interface.IFFactoryReplace;





public class FactoryReplace implements IFFactoryReplace {

private Replace replace;
	
	public Replace createReplace( ReplaceType typereplace ) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String className = "evolutionary_algorithms.complement." + typereplace.toString();
		replace = (Replace) FactoryLoader.getInstance(className);
		return replace;
	}
}
