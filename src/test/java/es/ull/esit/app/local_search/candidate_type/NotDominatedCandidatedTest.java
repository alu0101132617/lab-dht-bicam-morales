package es.ull.esit.app.local_search.candidate_type;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotDominatedCandidateTest {

    // Helper: evaluaciones como ArrayList<Double>
    private ArrayList<Double> eval(double... values) {
        ArrayList<Double> list = new ArrayList<>();
        for (double v : values) {
            list.add(v);
        }
        return list;
    }

    @Test
    void stateSearchShouldReturnOnlyElementWhenNeighborhoodSizeIsOne()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        NotDominatedCandidate candidate = new NotDominatedCandidate();

        State onlyState = mock(State.class);
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(onlyState);

        State result = candidate.stateSearch(neighborhood);

        assertSame(onlyState, result,
                "Con un solo vecino, debe devolver ese mismo estado");
    }

    @Test
    void stateSearchShouldKeepFirstStateWhenNextDoesNotDominate()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        NotDominatedCandidate candidate = new NotDominatedCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);

        // En maximización: s1 = 5, s2 = 3 -> s2 NO domina a s1
        when(s1.getEvaluation()).thenReturn(eval(5.0));
        when(s2.getEvaluation()).thenReturn(eval(3.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            doNothing().when(problemMock).evaluate(any(State.class));

            State result = candidate.stateSearch(neighborhood);

            assertSame(s1, result,
                    "Si el segundo estado no domina al primero, debe devolverse el primero");
        }
    }

    @Test
    void stateSearchShouldReturnDominatingStateWhenNextDominatesFirst()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        NotDominatedCandidate candidate = new NotDominatedCandidate();

        State s1 = mock(State.class);
        State s2 = mock(State.class);

        // En maximización: s1 = 3, s2 = 5 -> s2 domina a s1
        when(s1.getEvaluation()).thenReturn(eval(3.0));
        when(s2.getEvaluation()).thenReturn(eval(5.0));

        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(s1);
        neighborhood.add(s2);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            doNothing().when(problemMock).evaluate(any(State.class));

            State result = candidate.stateSearch(neighborhood);

            assertSame(s2, result,
                    "Si el segundo estado domina al primero, debe terminar devolviendo el segundo");
        }
    }
}
