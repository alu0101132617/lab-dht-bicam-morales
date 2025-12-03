package es.ull.esit.app.evolutionary_algorithms.complement;

/**
 * Enum representing the types of replacement strategies in an evolutionary algorithm.
 */
public enum ReplaceType {
	STEADY_STATE_REPLACE,     /** Replacement strategy where only a few individuals are replaced at a time */
  GENERATIONAL_REPLACE;     /** Replacement strategy where the entire population is replaced each generation */
}
