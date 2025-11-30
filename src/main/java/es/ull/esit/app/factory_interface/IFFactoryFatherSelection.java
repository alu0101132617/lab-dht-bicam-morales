package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;




public interface IFFactoryFatherSelection {
	
	FatherSelection createSelectFather(SelectionType selectionType)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
