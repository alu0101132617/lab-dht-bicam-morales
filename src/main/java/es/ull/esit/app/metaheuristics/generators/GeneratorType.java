package main.java.es.ull.esit.app.metaheuristics.generators;

public enum GeneratorType {
	HillClimbing, TabuSearch, SimulatedAnnealing, RandomSearch, LimitThreshold, HillClimbingRestart, //un punto
	GeneticAlgorithm, EvolutionStrategies, DistributionEstimationAlgorithm, ParticleSwarmOptimization, //poblaciones de puntos
	MultiGenerator, 
	MultiobjectiveTabuSearch, MultiobjectiveStochasticHillClimbing,MultiCaseSimulatedAnnealing, MultiobjectiveHillClimbingRestart, MultiobjectiveHillClimbingDistance; //mutiobjetivos
}
