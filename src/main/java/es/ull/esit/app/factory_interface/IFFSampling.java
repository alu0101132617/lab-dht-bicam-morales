package main.java.es.ull.esit.app.factory_interface;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.evolutionary_algorithms.complement.Sampling;
import main.java.es.ull.esit.app.evolutionary_algorithms.complement.SamplingType;




public interface IFFSampling {
	Sampling createSampling(SamplingType typesampling) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
