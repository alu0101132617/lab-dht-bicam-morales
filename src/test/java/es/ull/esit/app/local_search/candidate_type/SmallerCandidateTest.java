package es.ull.esit.app.local_search.candidate_type;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SmallerCandidateTest {

    // Helper: evaluaciones como ArrayList<Double>
    private ArrayList<Double> eval(double value) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    @Test
    void stateSearchShouldReturnOnlyElementWhenNeighborhoodHasSizeOne()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        SmallerCandidate candidate = new SmallerCandidate();

        State onlyState = mock(State.class);
        when(onlyState.getEvaluation()).thenReturn(eval(5.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(onlyState);

        State result = candidate.stateSearch(neighborhood);

        assertSame(onlyState, result,
                "Con un solo vecino, debe devolver ese mismo estado");
    }

    @Test
    void stateSearchShouldReturnStateWithSmallestValueWhenThereIsOne()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        SmallerCandidate candidate = new SmallerCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        // Valores: s1 = 5.0, s2 = 3.0, s3 = 4.0 -> el menor es s2
        when(s1.getEvaluation()).thenReturn(eval(5.0));
        when(s2.getEvaluation()).thenReturn(eval(3.0));
        when(s3.getEvaluation()).thenReturn(eval(4.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);

        State result = candidate.stateSearch(neighborhood);

        assertSame(s2, result,
                "Debe devolver el estado con menor valor de evaluación");
    }

    @Test
    void stateSearchShouldReturnNullWhenNoElementIsSmallerThanFirst()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        SmallerCandidate candidate = new SmallerCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        // Todos >= primer valor: s1 = 3.0, s2 = 4.0, s3 = 5.0
        // Nunca entra en (counter < currentCount) → stateSmaller se queda null
        when(s1.getEvaluation()).thenReturn(eval(3.0));
        when(s2.getEvaluation()).thenReturn(eval(4.0));
        when(s3.getEvaluation()).thenReturn(eval(5.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);
        neighborhood.add(s3);

        State result = candidate.stateSearch(neighborhood);

        assertNull(result,
                "Si ningún vecino es menor que el primero, según la implementación actual debe devolver null");
    }
}
