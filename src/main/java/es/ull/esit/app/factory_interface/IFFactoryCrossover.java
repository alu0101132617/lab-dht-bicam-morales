package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.evolutionary_algorithms.complement.Crossover;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.CrossoverType;




public interface IFFactoryCrossover {
	Crossover createCrossover(CrossoverType Crossovertype)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
