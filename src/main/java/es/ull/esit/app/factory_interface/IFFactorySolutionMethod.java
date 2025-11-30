package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.problem.extension.SolutionMethod;
import main.java.es.ull.esit.app.problem.extension.TypeSolutionMethod;

public interface IFFactorySolutionMethod {
	
	SolutionMethod createdSolutionMethod(TypeSolutionMethod  method) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;

}
