package es.ull.esit.app.local_search.complement;

import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that manages the tabu list for Tabu Search in local search algorithms.
 */
public class TabuSolutions {

  /** List of tabu states (accesible desde otros paquetes como MultiobjectiveTabuSearch y TabuSearch). */
  public static final List<State> listTabu = new ArrayList<>();

  /** Maximum number of elements allowed in the tabu list (nombre esperado: maxelements). */
  public static int maxelements = 100;

  /**
   * Filters the neighborhood states by removing those present in the tabu list.
   * @param listNeighborhood [List<State>] List of neighborhood states.
   * @return [List<State>] Filtered list of neighborhood states.
   */
  public List<State> filterNeighborhood(List<State> listNeighborhood) {
    List<State> listFiltrate;

    if (!listTabu.isEmpty()) {
      for (int i = listNeighborhood.size() - 1; i >= 0; i--) {
        int countTabu = 0;
        while (listTabu.size() > countTabu) {
          if (listNeighborhood.get(i).equals(listTabu.get(countTabu))) {
            listNeighborhood.remove(i);
            break; // ya hemos eliminado este índice, salimos del while
          }
          countTabu++;
        }
      }
      listFiltrate = listNeighborhood;
      if (listFiltrate.isEmpty()) {
        throw new IllegalArgumentException("All neighborhood states are in the tabu list.");
      }
    } else {
      listFiltrate = listNeighborhood;
    }
    return listFiltrate;
  }
}
