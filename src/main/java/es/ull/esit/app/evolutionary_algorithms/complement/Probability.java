package es.ull.esit.app.evolutionary_algorithms.complement;

/**
 * Class that represents a probability with an associated key and value.
 */
public class Probability {

  /** The key associated with this probability. */
  private Object key;
  /** The value associated with this probability. */
  private Object value;
  /** The probability value. */
  private float prob;

  /**
   * Gets the probability value.
   * @return [float] The probability value.
   */
  public float getProbability() {
    return prob;
  }

  /**
   * Sets the probability value.
   * @param probability [float] The probability value to set.
   */
  public void setProbability(float probability) {
    this.prob = probability;
  }

  /**
   * Gets the key associated with this probability.
   * @return [Object] The key associated with this probability.
   */
  public Object getKey() {
    return key;
  }

  /**
   * Sets the key associated with this probability.
   * @param key [Object] The key to set.
   */
  public void setKey(Object key) {
    this.key = key;
  }

  /**
   * Gets the value associated with this probability.
   * @return [Object] The value associated with this probability.
   */
  public Object getValue() {
    return value;
  }

  /**
   * Sets the value associated with this probability.
   * @param value [Object] The value to set.
   */
  public void setValue(Object value) {
    this.value = value;
  }
}
