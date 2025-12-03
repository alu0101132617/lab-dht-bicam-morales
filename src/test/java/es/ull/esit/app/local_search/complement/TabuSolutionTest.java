package es.ull.esit.app.local_search.complement;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TabuSolutionsTest {

    @Test
    void filterNeighborhoodShouldReturnSameListWhenTabuListIsEmpty() {
        // Dejamos la lista tabu vacía
        TabuSolutions.listTabu.clear();

        TabuSolutions tabuSolutions = new TabuSolutions();

        State s1 = new State();
        State s2 = new State();

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);

        List<State> result = tabuSolutions.filterNeighborhood(neighborhood);

        // Debe devolver la MISMA lista (no una copia)
        assertSame(neighborhood, result);
        assertEquals(2, result.size());
        assertTrue(result.contains(s1));
        assertTrue(result.contains(s2));
    }

    @Test
    void filterNeighborhoodShouldRemoveTabuStatesButNotAll() {
        TabuSolutions.listTabu.clear();

        TabuSolutions tabuSolutions = new TabuSolutions();

        State tabu = new State();
        State allowed = new State();

        // El mismo objeto "tabu" está en la lista tabu y en el vecindario
        TabuSolutions.listTabu.add(tabu);

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(tabu);
        neighborhood.add(allowed);

        List<State> result = tabuSolutions.filterNeighborhood(neighborhood);

        assertEquals(1, result.size(), "Debe quedar solo el estado no tabu");
        assertSame(allowed, result.get(0));
    }

    @Test
    void filterNeighborhoodShouldThrowWhenAllStatesAreTabu() {
        TabuSolutions.listTabu.clear();

        TabuSolutions tabuSolutions = new TabuSolutions();

        State tabu1 = new State();
        State tabu2 = new State();

        TabuSolutions.listTabu.add(tabu1);
        TabuSolutions.listTabu.add(tabu2);

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(tabu1);
        neighborhood.add(tabu2);

        assertThrows(IllegalArgumentException.class,
                () -> tabuSolutions.filterNeighborhood(neighborhood),
                "Si todos los estados del vecindario son tabu, debe lanzar IllegalArgumentException");
    }
}
