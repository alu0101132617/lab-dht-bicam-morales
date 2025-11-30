package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;




public interface IFFactoryReplace {
	Replace createReplace(ReplaceType typereplace)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
