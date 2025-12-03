package es.ull.esit.app.metaheuristics.generators;

/**
 * Class to define a limit roulette for a generator.
 * 
 */
public class LimitRoulette {

  /** Low limit of the roulette. */
	private float limitLow;
  /** High limit of the roulette. */
	private float limitHigh;
  /** Generator associated to the roulette. */
	private Generator generator;
	
  /**
   * Gets the generator associated to the limit roulette.
   * @return [Generator] Generator associated to the limit roulette.
   */
	public Generator getGenerator() {
		return generator;
	}

  /**
   * Sets the generator associated to the limit roulette.
   * @param generator [Generator] Generator to be associated to the limit roulette.
   */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}
  
  /**
   * Gets the high limit of the roulette.
   * @return [float] High limit of the roulette.
   */
	public float getLimitHigh() {
		return limitHigh;
	}

  /**
   * Sets the high limit of the roulette.
   * @param limitHigh [float] High limit of the roulette.
   */
	public void setLimitHigh(float limitHigh) {
		this.limitHigh = limitHigh;
	}

  /**
   * Gets the low limit of the roulette.
   * @return [float] Low limit of the roulette.
   */
	public float getLimitLow() {
		return limitLow;
	}

  /**
   * Sets the low limit of the roulette.
   * @param limitLow [float] Low limit of the roulette.
   */
	public void setLimitLow(float limitLow) {
		this.limitLow = limitLow;
	}
}
