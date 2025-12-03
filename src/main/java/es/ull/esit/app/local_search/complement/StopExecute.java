package es.ull.esit.app.local_search.complement;

/**
 * Class that determines whether to stop the execution of local search based on iteration count.
 */
public class StopExecute {
  /**
   * Determines if the local search should stop based on the current iteration count and maximum allowed iterations.
   * @param countIterationsCurrent [int] Current iteration count.
   * @param countmaxIterations [int] Maximum allowed iterations.
   * @return [Boolean] True if the search should stop, false otherwise.
   */
	public Boolean stopIterations(int countIterationsCurrent, int countmaxIterations) {
		return (countIterationsCurrent < countmaxIterations);
	}
}
