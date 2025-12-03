package es.ull.esit.app.evolutionary_algorithms.complement;

/**
 * Class that represents a range with associated probability data.
 */
public class Range {

  /** The probability data associated with this range. */
  private Probability data;
  /** The maximum value of the range. */
  private float max;
  /** The minimum value of the range. */
  private float min;

  /** 
   * Gets the probability data associated with this range.
   * @return [Probability] The probability data.
   */
  public Probability getData() {
    return data;
  }

  /** 
   * Sets the probability data associated with this range.
   * @param data [Probability] The probability data to set.
   */
  public void setData(Probability data) {
    this.data = data;
  }

  /** 
   * Gets the maximum value of the range.
   * @return [float] The maximum value.
   */
  public float getMax() {
    return max;
  }

  /** 
   * Sets the maximum value of the range.
   * @param max [float] The maximum value to set.
   */
  public void setMax(float max) {
    this.max = max;
  }

  /** 
   * Gets the minimum value of the range.
   * @return [float] The minimum value.
   */
  public float getMin() {
    return min;
  }

  /** 
   * Sets the minimum value of the range.
   * @param min [float] The minimum value to set.
   */
  public void setMin(float min) {
    this.min = min;
  }
}
