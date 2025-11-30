/**
 * @(#) IFFactoryAcceptCandidate.java
 */

package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.local_search.acceptation_type.AcceptType;
import main.java.es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;





public interface IFFactoryAcceptCandidate
{
	AcceptableCandidate createAcceptCandidate(AcceptType typeacceptation) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

}
