package es.ull.esit.app.local_search.candidate_type;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RandomCandidateTest {

    @Test
    void stateSearchShouldReturnTheOnlyElementWhenNeighborhoodHasSizeOne() {
        RandomCandidate candidate = new RandomCandidate();

        State onlyState = mock(State.class);
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(onlyState);

        State result = candidate.stateSearch(neighborhood);

        assertSame(onlyState, result,
                "Con un solo vecino, debe devolver ese mismo estado");
    }

    @Test
    void stateSearchShouldReturnAnElementFromNeighborhoodWhenSizeIsGreaterThanOne() {
        RandomCandidate candidate = new RandomCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);

        State result = candidate.stateSearch(neighborhood);

        assertNotNull(result, "El resultado no debe ser null");
        assertTrue(neighborhood.contains(result),
                "El estado devuelto debe pertenecer al vecindario original");
    }
}
