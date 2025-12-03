package es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;
import es.ull.esit.app.factory_interface.IFFactoryFatherSelection;

/**
 * Class that implements the factory method for creating FatherSelection instances.
 */
public class FactoryFatherSelection implements IFFactoryFatherSelection{
    /**
     * Factory method to create a FatherSelection based on the provided SelectionType.
     * @param selectionType [SelectionType] The type of selection strategy to create.
     * @return [FatherSelection] An instance of the specified FatherSelection type.
     * @throws IllegalArgumentException If the provided type is invalid.
     * @throws SecurityException If there is a security violation during instantiation.
     * @throws ClassNotFoundException If the class corresponding to the type is not found.
     * @throws InstantiationException If there is an error during instantiation.
     * @throws IllegalAccessException If there is an illegal access during instantiation.
     * @throws InvocationTargetException If the constructor throws an exception.
     * @throws NoSuchMethodException If the constructor is not found.
     */
    public FatherSelection createSelectFather(SelectionType selectionType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	String className = "evolutionary_algorithms.complement." + selectionType.toString();
		  return (FatherSelection) FactoryLoader.getInstance(className);
	}
}
