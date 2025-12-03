package es.ull.esit.app.problem.definition;

import java.util.List;

/**
 * Abstract class that defines the operators of the problem.
 */
public abstract class Operator {
    /**
     * Generates new states from the current state using the specified operator number.
     * 
     * @param stateCurrent [State] Current state from which new states will be generated.
     * @param operatornumber [Integer] The operator number to be applied.
     * @return [List<State>] A list of newly generated states.
     */
		public abstract List<State> generatedNewState(State stateCurrent, Integer operatornumber);

    /**
     * Generates random states using the specified operator number.
     * 
     * @param operatornumber [Integer] The operator number to be applied.
     * @return [List<State>] A list of randomly generated states.
     */
		public abstract List<State> generateRandomState (Integer operatornumber);

	}

