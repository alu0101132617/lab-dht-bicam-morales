package es.ull.esit.app.problem.definition;

import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheuristics.generators.GeneratorType;

/**
 * Class that defines the state of the problem.
 */
public class State {

  /* Type of generator used to create the state. */
  protected GeneratorType typeGenerator;
  /* Evaluation values for the state. */
  protected List<Double> evaluation;
  /* Unique number identifying the state. */
  protected int number;
  /* Code representation of the state. */
  protected List<Object> code;

  /**
   * Copy constructor.
   * 
   * @param ps [State] State to copy.
   */
  public State(State ps) {
    typeGenerator = ps.getTypeGenerator();
    evaluation = ps.getEvaluation();
    number = ps.getNumber();
    code = new ArrayList<>(ps.getCode());
  }

  /**
   * Constructor with code initialization.
   * 
   * @param code [List<Object>] Code to initialize the state.
   */
  public State(List<Object> code) {
    super();
    this.code = code;
  }

  /**
   * Default constructor.
   * 
   */
  public State() {
    code = new ArrayList<>();
  }

  /**
   * Gets the code of the state.
   * 
   * @return [List<Object>] The code of the state.
   */
  public List<Object> getCode() {
    return code;
  }

  /**
   * Sets the code of the state.
   * 
   * @param listCode [List<Object>] The code to set for the state.
   */
  public void setCode(List<Object> listCode) {

    this.code = new ArrayList<>(listCode);
  }

  /**
   * Gets the type of generator used to create the state.
   * 
   * @return [GeneratorType] The type of generator used to create the state.
   */
  public GeneratorType getTypeGenerator() {
    return typeGenerator;
  }

  /**
   * Sets the type of generator used to create the state.
   * 
   * @param typeGenerator [GeneratorType] The type of generator to set.
   */
  public void setTypeGenerator(GeneratorType typeGenerator) {
    this.typeGenerator = typeGenerator;
  }

  /**
   * Gets the evaluation values for the state.
   * 
   * @return [ArrayList<Double>] The evaluation values for the state.
   */
  public List<Double> getEvaluation() {
    return evaluation;
  }

  /**
   * Sets the evaluation values for the state.
   * 
   * @param evaluation [ArrayList<Double>] The evaluation values to set for the state.
   */
  public void setEvaluation(List<Double> evaluation) {
    this.evaluation = new ArrayList<>(evaluation);
  }

  /**
   * Gets the unique number identifying the state.
   * 
   * @return [int] The unique number identifying the state.
   */
  public int getNumber() {
    return number;
  }

  /**
   * Sets the unique number identifying the state.
   * 
   * @param number [int] The unique number to set for the state.
   */
  public void setNumber(int number) {
    this.number = number;
  }

  /**
   * Creates a copy of the current state.
   * 
   * @return [State] A new State object that is a copy of the current state.
   */
  public State copy() {
    return new State(this);
  }

  /**
   * Creates a copy of the current state.
   * 
   * @return [Object] A new Object that is a copy of the current state.
   */
  public Object getCopy() {
    return new State(this.getCode());
  }

  /**
   * Compares the current state with another state.
   * 
   * @param state [State] The state to compare with.
   * @return [boolean] True if the states are equal, false otherwise.
   */
  public boolean comparator(State state) {

    boolean result = false;
    if (state.getCode().equals(getCode())) {
      result = true;
    }
    return result;
  }

  /**
   * Calculates the distance between the current state and another state.
   * 
   * @param state [State] The state to calculate the distance to.
   * @return [double] The distance between the two states.
   */
  public double distance(State state) {
    double distancia = 0;
    for (int i = 0; i < state.getCode().size(); i++) {
      if (!(state.getCode().get(i).equals(this.getCode().get(i)))) {
        distancia++;
      }
    }
    return distancia;
  }
}
