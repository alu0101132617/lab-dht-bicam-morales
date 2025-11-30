package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.evolutionary_algorithms.complement.Mutation;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.MutationType;




public interface IFFactoryMutation {
	Mutation createMutation(MutationType typeMutation)throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ;
}
