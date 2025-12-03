package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;

/**
 * Class that provides a method to load and instantiate classes by name.
 */
public class FactoryLoader {

  /**
   * Factory method to get an instance of a class given its name.
   * 
   * @param className [String] The fully qualified name of the class to instantiate.
   * @return [Object] An instance of the specified class.
   * @throws ClassNotFoundException If the class cannot be located.
   * @throws IllegalAccessException If the class or its nullary constructor is not accessible.
   * @throws InstantiationException If the class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
   * @throws InvocationTargetException If the underlying constructor throws an exception.
   * @throws NoSuchMethodException If the class does not have a nullary constructor.
   */
  public static Object getInstance(String className)
      throws ClassNotFoundException, IllegalAccessException,
      InstantiationException, InvocationTargetException,
      NoSuchMethodException {

    if (className == null || className.isEmpty()) {
      throw new IllegalArgumentException("El nombre de la clase no puede ser nulo o vacío");
    }

    Class<?> c = Class.forName(className);

    return c.getDeclaredConstructor().newInstance();
  }

  private FactoryLoader() {
    // Private constructor to prevent instantiation of this utility class.
  }

}
