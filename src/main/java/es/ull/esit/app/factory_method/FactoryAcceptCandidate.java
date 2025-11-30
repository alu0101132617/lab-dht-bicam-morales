/**
 * @(#) FactoryAcceptCandidate.java
 */

package main.java.es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.local_search.acceptation_type.AcceptType;
import main.java.es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;


import main.java.es.ull.esit.app.factory_interface.IFFactoryAcceptCandidate;




public class FactoryAcceptCandidate implements IFFactoryAcceptCandidate{
	private AcceptableCandidate acceptCandidate;
	
	public AcceptableCandidate createAcceptCandidate( AcceptType typeacceptation ) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String className = "local_search.acceptation_type." + typeacceptation.toString();
		acceptCandidate = (AcceptableCandidate) FactoryLoader.getInstance(className);
		return acceptCandidate;
	}
}
