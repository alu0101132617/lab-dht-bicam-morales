package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.factory_interface.IFFactoryAcceptCandidate;

/**
 * Class that implements the factory method for creating AcceptableCandidate instances.
 */
public class FactoryAcceptCandidate implements IFFactoryAcceptCandidate {
  
  /**
   * Factory method to create an AcceptableCandidate based on the provided AcceptType.
   *
   * @param typeacceptation [AcceptType] The type of acceptation strategy to create.
   * @return [AcceptableCandidate] An instance of the specified AcceptableCandidate type.
   */
  @Override
  public AcceptableCandidate createAcceptCandidate(AcceptType typeacceptation)
      throws IllegalArgumentException, SecurityException,
             ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException,
             NoSuchMethodException {

    // OJO: aquí va el paquete REAL de tus clases de aceptación
    String className = "es.ull.esit.app.local_search.acceptation_type." + typeacceptation.toString();
    return (AcceptableCandidate) FactoryLoader.getInstance(className);
  }
}
