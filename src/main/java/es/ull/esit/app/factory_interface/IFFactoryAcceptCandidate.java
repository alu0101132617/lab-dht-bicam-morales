package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;

/**
 * Interface for the Factory of AcceptCandidate objects
 */
public interface IFFactoryAcceptCandidate {

  /**
   * Method to create an AcceptCandidate object based on the type of acceptation.
   * @param typeacceptation [AcceptType] The type of acceptation.
   * @return [AcceptableCandidate] The created AcceptCandidate object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	AcceptableCandidate createAcceptCandidate(AcceptType typeacceptation) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

}
