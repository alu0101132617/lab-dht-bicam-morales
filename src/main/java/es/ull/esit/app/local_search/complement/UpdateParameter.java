package es.ull.esit.app.local_search.complement;

import es.ull.esit.app.factory_interface.IFFactoryGenerator;
import es.ull.esit.app.factory_method.FactoryGenerator;
import es.ull.esit.app.metaheuristics.generators.DistributionEstimationAlgorithm;
import es.ull.esit.app.metaheuristics.generators.EvolutionStrategies;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheuristics.generators.GeneticAlgorithm;
import es.ull.esit.app.metaheuristics.generators.ParticleSwarmOptimization;

import java.lang.reflect.InvocationTargetException;

import es.ull.esit.app.metaheurictics.strategy.Strategy;


/**
 * Class responsible for updating parameters during the local search process.
 */
public class UpdateParameter {

  /** Private constructor to prevent instantiation */
  private UpdateParameter() { 
  }
	
  /**
   * Updates the current iteration count and changes the generator based on predefined reference counts.
   * @param countIterationsCurrent [Integer] Current iteration count.
   * @return [Integer] Updated iteration count.
   * @throws IllegalArgumentException If an illegal argument is provided.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class is not found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method is not found.
   */
	public static Integer updateParameter(Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {//HashMap<String, Object> map, 
		countIterationsCurrent = countIterationsCurrent + 1;
    IFFactoryGenerator ifFactoryGenerator;
		// Here update parameter for update and change generator.
		if(countIterationsCurrent.equals(GeneticAlgorithm.getCountRef() - 1)){
			ifFactoryGenerator = new FactoryGenerator();
			Strategy.getStrategy().setGenerator(ifFactoryGenerator.createGenerator(GeneratorType.GeneticAlgorithm));
		}
		else{
			if(countIterationsCurrent.equals(EvolutionStrategies.getCountRef() - 1)){
				ifFactoryGenerator = new FactoryGenerator();
				Strategy.getStrategy().setGenerator(ifFactoryGenerator.createGenerator(GeneratorType.EvolutionStrategies));
			}			
			if(countIterationsCurrent.equals(DistributionEstimationAlgorithm.getCountRef() - 1)){
				ifFactoryGenerator = new FactoryGenerator();
				Strategy.getStrategy().setGenerator(ifFactoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm));
			}
			if(countIterationsCurrent.equals(ParticleSwarmOptimization.getCountRef() - 1)){
				ifFactoryGenerator = new FactoryGenerator();
				Strategy.getStrategy().setGenerator(ifFactoryGenerator.createGenerator(GeneratorType.ParticleSwarmOptimization));
			}
		}
		return countIterationsCurrent;
	}
}
	


