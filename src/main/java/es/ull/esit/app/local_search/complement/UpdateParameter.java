package main.java.es.ull.esit.app.local_search.complement;

import main.java.es.ull.esit.app.factory_interface.IFFactoryGenerator;
import main.java.es.ull.esit.app.factory_method.FactoryGenerator;
import main.java.es.ull.esit.app.metaheuristics.generators.DistributionEstimationAlgorithm;
import main.java.es.ull.esit.app.metaheuristics.generators.EvolutionStrategies;
import main.java.es.ull.esit.app.metaheuristics.generators.GeneratorType;
import main.java.es.ull.esit.app.metaheuristics.generators.GeneticAlgorithm;
import main.java.es.ull.esit.app.metaheuristics.generators.ParticleSwarmOptimization;

import java.lang.reflect.InvocationTargetException;

import main.java.es.ull.esit.app.metaheurictics.strategy.Strategy;


public class UpdateParameter {
	
	private static IFFactoryGenerator ifFactoryGenerator;
	
	public static Integer updateParameter(Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {//HashMap<String, Object> map, 
		countIterationsCurrent = countIterationsCurrent + 1;
		//		Here update parameter for update and change generator.
		if(countIterationsCurrent.equals(GeneticAlgorithm.countRef - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.GeneticAlgorithm);
		}
		else{
			if(countIterationsCurrent.equals(EvolutionStrategies.countRef - 1)){
				ifFactoryGenerator = new FactoryGenerator();
				Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.EvolutionStrategies);
			}			
			if(countIterationsCurrent.equals(DistributionEstimationAlgorithm.countRef - 1)){
				ifFactoryGenerator = new FactoryGenerator();
				Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
			}
			if(countIterationsCurrent.equals(ParticleSwarmOptimization.countRef - 1)){
				ifFactoryGenerator = new FactoryGenerator();
				Strategy.getStrategy().generator = ifFactoryGenerator.createGenerator(GeneratorType.ParticleSwarmOptimization);
			}
		}
		return countIterationsCurrent;
	}
}
	


