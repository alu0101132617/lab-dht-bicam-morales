/**
 * @(#) IFFactoryCandidate.java
 */

package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.local_search.candidate_type.CandidateType;
import main.java.es.ull.esit.app.local_search.candidate_type.SearchCandidate;




public interface IFFactoryCandidate
{
	SearchCandidate createSearchCandidate(CandidateType typeCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
