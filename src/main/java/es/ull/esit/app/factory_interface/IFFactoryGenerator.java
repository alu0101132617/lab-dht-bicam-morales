package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.metaheuristics.generators.Generator;
import main.java.es.ull.esit.app.metaheuristics.generators.GeneratorType;

public interface IFFactoryGenerator {
	
	Generator createGenerator(GeneratorType Generatortype)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
