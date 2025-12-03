package es.ull.esit.app.metaheuristics.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import es.ull.esit.app.factory_method.FactoryGenerator;

/**
 * Runnable that creates a Distribution Estimation Algorithm (DEA) generator
 * in a separate thread and updates the MultiGenerator list.
 */
public class InstanceDE implements Runnable {

  /** Logger for this class. */
  private static final Logger LOGGER = Logger.getLogger(InstanceDE.class.getName());

  /** Flag to indicate if the thread has finished its work. */
  private volatile boolean terminate = false;

  /**
   * Creates a DEA generator using FactoryGenerator and replaces the existing
   * DEA entry in MultiGenerator, if found.
   */
  @Override
  public void run() {
    FactoryGenerator factoryGenerator = new FactoryGenerator();
    Generator generatorDE;

    try {
      generatorDE = factoryGenerator.createGenerator(GeneratorType.DistributionEstimationAlgorithm);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Error creating DistributionEstimationAlgorithm generator in InstanceDE.", e);
      terminate = true;
      return;
    }

    if (generatorDE == null) {
      LOGGER.severe("FactoryGenerator returned null for DistributionEstimationAlgorithm generator.");
      terminate = true;
      return;
    }

    Generator[] generators = MultiGenerator.getListGenerators();
    if (generators == null) {
      LOGGER.severe("MultiGenerator.getListGenerators() returned null in InstanceDE.");
      terminate = true;
      return;
    }

    for (int i = 0; i < generators.length; i++) {
      Generator current = generators[i];
      if (current != null
          && GeneratorType.DistributionEstimationAlgorithm.equals(current.getType())) {
        generators[i] = generatorDE;
        break;
      }
    }

    terminate = true;
  }

  /**
   * Indicates whether this task has terminated.
   *
   * @return true if the task has finished; false otherwise.
   */
  public boolean isTerminate() {
    return terminate;
  }

  /**
   * Sets the termination flag. Intended mainly for testing.
   *
   * @param terminate true if the task should be marked as finished; false otherwise.
   */
  public void setTerminate(boolean terminate) {
    this.terminate = terminate;
  }
}

