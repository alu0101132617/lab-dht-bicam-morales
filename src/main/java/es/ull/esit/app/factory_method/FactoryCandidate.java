
package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.SearchCandidate;

import es.ull.esit.app.factory_interface.IFFactoryCandidate;

/**
 * Class that implements the factory method for creating SearchCandidate instances.
 */
public class FactoryCandidate implements IFFactoryCandidate{
  
  /**
   * Factory method to create a SearchCandidate based on the provided CandidateType.
   *
   * @param typeCandidate [CandidateType] The type of candidate strategy to create.
   * @return [SearchCandidate] An instance of the specified SearchCandidate type.
   * @throws IllegalArgumentException If the provided type is invalid.
   * @throws SecurityException If there is a security violation during instantiation.
   * @throws ClassNotFoundException If the class corresponding to the type is not found.
   * @throws InstantiationException If there is an error during instantiation.
   * @throws IllegalAccessException If there is an illegal access during instantiation.
   * @throws InvocationTargetException If the constructor throws an exception.
   * @throws NoSuchMethodException If the constructor is not found.
   */
	public SearchCandidate createSearchCandidate(CandidateType typeCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = "local_search.candidate_type." + typeCandidate.toString();
		return (SearchCandidate) FactoryLoader.getInstance(className);
	}
}
