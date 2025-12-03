package es.ull.esit.app.config.tspdynamic;

/**
 * Class that represents the state of the TSP problem.
 */
public class TSPState {
  
  /** Value associated with the state */
	private int value;
  /** Identifier of the city associated with the state */
	private int idCity;
	
  /**
   * Gets the value associated with the state.
   * @return [int] the value.
   */
	public int getValue() {
		return value;
	}

  /**
   * Sets the value associated with the state.
   * @param value [int] the value to set.
   */
	public void setValue(int value) {
		this.value = value;
	}

  /**
   * Gets the identifier of the city associated with the state.
   * @return [int] the identifier of the city.
   */
	public int getIdCity() {
		return idCity;
	}

  /**
   * Sets the identifier of the city associated with the state.
   * @param idCity [int] the identifier of the city to set.
   */
	public void setIdCity(int idCity) {
		this.idCity = idCity;
	}

}
