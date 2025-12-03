package es.ull.esit.app.local_search.candidate_type;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GreaterCandidateTest {

    // Helper para crear evaluaciones (ArrayList<Double>, no solo List)
    private ArrayList<Double> eval(double value) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    @Test
    void stateSearchShouldReturnSingleElementWhenNeighborhoodHasSizeOne()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        GreaterCandidate greaterCandidate = new GreaterCandidate();

        State onlyState = mock(State.class);
        when(onlyState.getEvaluation()).thenReturn(eval(5.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(onlyState);

        State result = greaterCandidate.stateSearch(neighborhood);

        assertSame(onlyState, result,
                "Cuando el vecindario tiene un solo elemento, debe devolverse ese mismo estado");
    }

    @Test
    void stateSearchShouldReturnStateWithGreatestValueWhenThereIsOne()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        GreaterCandidate greaterCandidate = new GreaterCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        // Valores: s1 = 1.0, s2 = 3.0, s3 = 2.0 -> mayor es s2
        when(s1.getEvaluation()).thenReturn(eval(1.0));
        when(s2.getEvaluation()).thenReturn(eval(3.0));
        when(s3.getEvaluation()).thenReturn(eval(2.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);

        State result = greaterCandidate.stateSearch(neighborhood);

        assertSame(s2, result,
                "Debe devolver el estado con mayor valor de evaluación");
    }

    @Test
    void stateSearchShouldUseRandomWhenAllValuesAreEqual()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        GreaterCandidate greaterCandidate = new GreaterCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        // Todos con el mismo valor -> stateGreater se queda null y se usa random
        when(s1.getEvaluation()).thenReturn(eval(2.0));
        when(s2.getEvaluation()).thenReturn(eval(2.0));
        when(s3.getEvaluation()).thenReturn(eval(2.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);

        State result = greaterCandidate.stateSearch(neighborhood);

        assertNotNull(result, "El estado devuelto no debe ser null");
        assertTrue(neighborhood.contains(result),
                "El estado devuelto debe pertenecer al vecindario original");
    }
}
