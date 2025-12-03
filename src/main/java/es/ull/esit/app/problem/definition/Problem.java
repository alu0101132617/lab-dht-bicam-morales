package es.ull.esit.app.problem.definition;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.problem.extension.SolutionMethod;
import es.ull.esit.app.problem.extension.TypeSolutionMethod;


import es.ull.esit.app.factory_interface.IFFactorySolutionMethod;
import es.ull.esit.app.factory_method.FactorySolutionMethod;

/**
 * Class that defines the problem to be solved.
 */
public class Problem {
  /* Enumeration for problem types: Maximization or Minimization. */
	public enum ProblemType {MAXIMIZAR, MINIMIZAR;}
  /* List of objective functions for the problem. */
	private ArrayList<ObjetiveFunction> function;
  /* Current state of the problem. */
	private State state;
  /* Type of the problem (Maximization or Minimization). */
	private ProblemType typeProblem;
  /* Codification method used in the problem. */
	private Codification codification;
  /* Operator used in the problem. */
	private Operator operator;
  /* Possible values for the problem. */
	private int possibleValue;
  /* Type of solution method used in the problem. */
	private TypeSolutionMethod typeSolutionMethod;
  /* Factory for creating solution methods. */
	private IFFactorySolutionMethod factorySolutionMethod;
	
  /**
   * Constructor for the Problem class.
   */
	public Problem() {
		super();
	}

  /**
   * Gets the list of objective functions.
   * @return List of objective functions.
   */
	public List<ObjetiveFunction> getFunction() {
		return function;
	}

  /**
   * Sets the list of objective functions.
   * @param function [List<ObjetiveFunction>] List of objective functions to set.
   */
	public void setFunction(List<ObjetiveFunction> function) {
    this.function = new ArrayList<>(function);
  }

  /**
   * Gets the current state of the problem.
   * @return [State] The current state.
   */
	public State getState() {
		return state;
	}

  /**
   * Sets the current state of the problem.
   * @param state [State] The state to set.
   */
	public void setState(State state) {
		this.state = state;
	}

  /**
   * Gets the type of the problem.
   * @return [ProblemType] The type of the problem.
   */
	public ProblemType getTypeProblem() {
		return typeProblem;
	}

  /**
   * Sets the type of the problem.
   * @param typeProblem [ProblemType] The type of the problem to set.
   */
	public void setTypeProblem(ProblemType typeProblem) {
		this.typeProblem = typeProblem;
	}

  /**
   * Gets the codification method used in the problem.
   * @return [Codification] The codification method.
   */
	public Codification getCodification() {
		return codification;
	}

  /**
   * Sets the codification method used in the problem.
   * @param codification [Codification] The codification method to set.
   */
	public void setCodification(Codification codification) {
		this.codification = codification;
	}

  /**
   * Gets the operator used in the problem.
   * @return [Operator] The operator.
   */
	public Operator getOperator() {
		return operator;
	}

  /**
   * Sets the operator used in the problem.
   * @param operator [Operator] The operator to set.
   */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

  /**
   * Gets the possible values for the problem.
   * @return [int] The possible values.
   */
	public int getPossibleValue() {
		return possibleValue;
	}

  /**
   * Sets the possible values for the problem.
   * @param possibleValue [int] The possible values to set.
   */
	public void setPossibleValue(int possibleValue) {
		this.possibleValue = possibleValue;
	}

  /**
   * Evaluates the given state using the defined objective functions or solution method.
   * 
   * @param state [State] The state to be evaluated.
   * @throws IllegalArgumentException If the argument is invalid.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
	public void evaluate(State state) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		double eval = 0;       
		ArrayList<Double> evaluation = new ArrayList<>(this.function.size());
		if (typeSolutionMethod == null) {
			eval= function.get(0).evaluation(state);
			evaluation.add(evaluation.size(), eval);
			state.setEvaluation(evaluation);
		}
		else {
			SolutionMethod method = newSolutionMethod(typeSolutionMethod);
			method.evaluationState(state);
		}
	}
	
  /**
   * Gets the type of solution method used in the problem.
   * @return [TypeSolutionMethod] The type of solution method.
   */
	public TypeSolutionMethod getTypeSolutionMethod() {
		return typeSolutionMethod;
	}

  /**
   * Sets the type of solution method used in the problem.
   * @param typeSolutionMethod [TypeSolutionMethod] The type of solution method to set.
   */
	public void setTypeSolutionMethod(TypeSolutionMethod typeSolutionMethod) {
		this.typeSolutionMethod = typeSolutionMethod;
	}

  /**
   * Gets the factory for creating solution methods.
   * @return [IFFactorySolutionMethod] The factory for solution methods.
   */
	public IFFactorySolutionMethod getFactorySolutionMethod() {
		return factorySolutionMethod;
	}

  /**
   * Sets the factory for creating solution methods.
   * @param factorySolutionMethod [IFFactorySolutionMethod] The factory to set.
   */
	public void setFactorySolutionMethod(
			IFFactorySolutionMethod factorySolutionMethod) {
		this.factorySolutionMethod = factorySolutionMethod;
	}
	
  /**
   * Creates a new solution method based on the specified type.
   * 
   * @param typeSolutionMethod [TypeSolutionMethod] The type of solution method to create.
   * @return [SolutionMethod] The newly created solution method.
   * @throws IllegalArgumentException If the argument is invalid.
   * @throws SecurityException If a security violation occurs.
   * @throws ClassNotFoundException If the class cannot be found.
   * @throws InstantiationException If an instantiation error occurs.
   * @throws IllegalAccessException If access to a class or method is denied.
   * @throws InvocationTargetException If an exception occurs during method invocation.
   * @throws NoSuchMethodException If a method cannot be found.
   */
	public SolutionMethod newSolutionMethod(TypeSolutionMethod typeSolutionMethod) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		factorySolutionMethod = new FactorySolutionMethod();
		return factorySolutionMethod.createdSolutionMethod(typeSolutionMethod);
	
	}
}

	
	

