package es.ull.esit.app.metaheuristics.generators;

/**
 * Enumeration of the different types of generators.
 */
public enum GeneratorType {
  TabuSearch,                           /** Tabu Search generator type. */
  SimulatedAnnealing,                   /** Simulated Annealing generator type. */
  StochasticHillClimbing,               /** Stochastic Hill Climbing generator type. */
  RandomSearch,                         /** Random Search generator type. */
  LimitThreshold,                       /** Limit Threshold generator type. */              
  HillClimbingRestart,                  /** Hill Climbing with Restart generator type. */
  HillClimbingDistance,                 /** Hill Climbing with Distance generator type. */
	GeneticAlgorithm,                     /** Genetic Algorithm generator type. */    
  EvolutionStrategies,                  /** Evolution Strategies generator type. */
  DistributionEstimationAlgorithm,      /** Distribution Estimation Algorithm generator type. */
  ParticleSwarmOptimization,            /** Particle Swarm Optimization generator type. */
	MultiGenerator,                       /** Multi-generator type. */   
	MultiobjectiveTabuSearch,             /** Multi-objective Tabu Search generator type. */     
  MultiobjectiveStochasticHillClimbing, /** Multi-objective Stochastic Hill Climbing generator type. */
  MultiCaseSimulatedAnnealing,          /** Multi-case Simulated Annealing generator type. */
  MultiobjectiveHillClimbingRestart,    /** Multi-objective Hill Climbing with Restart generator type. */
  MultiobjectiveHillClimbingDistance,   /** Multi-objective Hill Climbing with Distance generator type. */
  HillClimbing;                         /** Hill Climbing generator type. */
}
