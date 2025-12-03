package es.ull.esit.app.problem.definition;

import es.ull.esit.app.problem.definition.Problem.ProblemType;

/**
 * Abstract class representing an objective function for evaluating states.
 */
public abstract class ObjetiveFunction {
	
  /** The type of problem this objective function is associated with. */
	private ProblemType typeProblem;
  /** The weight of the objective function in multi-objective scenarios. */
	private float weight;
	
  /**
   * Gets the weight of the objective function.
   * 
   * @return [float] The weight of the objective function.
   */
	public float getWeight() {
		return weight;
	}

  /** 
   * Sets the weight of the objective function.
   * 
   * @param weight [float] The weight to set.
   */
	public void setWeight(float weight) {
		this.weight = weight;
	}

  /**
   * Gets the type of problem associated with this objective function.
   * 
   * @return [ProblemType] The type of problem.
   */
	public ProblemType getTypeProblem() {
		return typeProblem;
	}

  /**
   * Sets the type of problem associated with this objective function.
   * 
   * @param typeProblem [ProblemType] The type of problem to set.
   */
	public void setTypeProblem(ProblemType typeProblem) {
		this.typeProblem = typeProblem;
	}

  /**
   * Evaluates the given state and returns its objective value.
   * @param state [State] The state to evaluate.
   * @return [Double] The objective value of the state.
   */
	public abstract Double evaluation(State state);
}
