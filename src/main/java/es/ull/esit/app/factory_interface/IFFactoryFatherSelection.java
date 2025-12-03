package es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;


/**
 * Interface for the Factory of FatherSelection objects.
 */
public interface IFFactoryFatherSelection {
	/**
   * Method to create a FatherSelection object based on the type of selection.
   * @param selectionType [SelectionType] The type of selection.
   * @return [FatherSelection] The created FatherSelection object.
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
	FatherSelection createSelectFather(SelectionType selectionType)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
