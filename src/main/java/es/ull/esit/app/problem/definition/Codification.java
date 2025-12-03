package es.ull.esit.app.problem.definition;

/**
 * Abstract class representing a codification of a problem.
 */
public abstract class Codification {

  /**
   * Checks if the given state is valid according to the codification rules.
   * 
   * @param state [State] The state to be validated.
   * @return true if the state is valid, false otherwise.
   */
	public abstract boolean validState(State state);

  /**
   * Generates a random valid state according to the codification rules.
   * 
   * @param key [int] The key or index of the variable to generate.
   * @return [State] A randomly generated valid state.
   */
	public abstract Object getVariableAleatoryValue(int key);

  /**
   * Retrieves the total number of aleatory keys in the codification.
   * 
   * @return [int] The number of aleatory keys.
   */
	public abstract int getAleatoryKey ();

  /**
   * Retrieves the total number of variables in the codification.
   * 
   * @return [int] The number of variables.
   */
	public abstract int getVariableCount();

}