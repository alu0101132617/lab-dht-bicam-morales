package es.ull.esit.app.metaheuristics.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import es.ull.esit.app.factory_method.FactoryGenerator;

/**
 * Runnable that creates an Evolution Strategies (ES) generator
 * in a separate thread and updates the MultiGenerator list.
 */
public class InstanceEE implements Runnable {

  /** Logger for this class. */
  private static final Logger LOGGER = Logger.getLogger(InstanceEE.class.getName());

  /** Flag to indicate if the thread has finished its work. */
  private volatile boolean terminate = false;

  /**
   * Creates an EvolutionStrategies generator using FactoryGenerator and replaces
   * the existing ES entry in MultiGenerator, if found.
   */
  @Override
  public void run() {
    FactoryGenerator factoryGenerator = new FactoryGenerator();
    Generator generatorEE;

    try {
      generatorEE = factoryGenerator.createGenerator(GeneratorType.EvolutionStrategies);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Error creating EvolutionStrategies generator in InstanceEE.", e);
      terminate = true;
      return;
    }

    if (generatorEE == null) {
      LOGGER.severe("FactoryGenerator returned null for EvolutionStrategies generator.");
      terminate = true;
      return;
    }

    Generator[] generators = MultiGenerator.getListGenerators();
    if (generators == null) {
      LOGGER.severe("MultiGenerator.getListGenerators() returned null in InstanceEE.");
      terminate = true;
      return;
    }

    for (int i = 0; i < generators.length; i++) {
      Generator current = generators[i];
      if (current != null
          && GeneratorType.EvolutionStrategies.equals(current.getType())) {
        generators[i] = generatorEE;
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
