package main.java.es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.problem.extension.SolutionMethod;
import main.java.es.ull.esit.app.problem.extension.TypeSolutionMethod;
import main.java.es.ull.esit.app.factory_interface.IFFactorySolutionMethod;

public class FactorySolutionMethod implements IFFactorySolutionMethod {

	private SolutionMethod solutionMethod;
	
	@Override
	public SolutionMethod createdSolutionMethod(TypeSolutionMethod method) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "problem.extension." + method.toString();
		solutionMethod = (SolutionMethod) FactoryLoader.getInstance(className);
		return solutionMethod;
	}

}
