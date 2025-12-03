package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.SearchCandidate;


/**
 * Interface for the Factory of Candidate objects.
 */
public interface IFFactoryCandidate
{
  /**
   * Method to create a Candidate object based on the type of candidate.
   * @param typeCandidate [CandidateType] The type of candidate.
   * @return [SearchCandidate] The created Candidate object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	SearchCandidate createSearchCandidate(CandidateType typeCandidate) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
