package main.java.es.ull.esit.app.factory_method;

import java.lang.reflect.InvocationTargetException;


import main.java.es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;
import main.java.es.ull.esit.app.factory_interface.IFFactoryFatherSelection;




public class FactoryFatherSelection implements IFFactoryFatherSelection{
    private FatherSelection selection;
	
    public FatherSelection createSelectFather(SelectionType selectionType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	String className = "evolutionary_algorithms.complement." + selectionType.toString();
		selection = (FatherSelection) FactoryLoader.getInstance(className);
		return selection;
	}
}
