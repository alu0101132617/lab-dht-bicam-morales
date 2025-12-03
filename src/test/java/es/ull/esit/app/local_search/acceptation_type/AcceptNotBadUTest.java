package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcceptNotBadUTest {

    private ArrayList<Double> eval(double value) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    @Test
    void maxShouldAcceptWhenDifferenceIsBelowThreshold()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadU acceptor = new AcceptNotBadU();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // current = 10, candidate = 9 -> result = 1
        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(9.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // Umbral 2 -> 1 < 2 => se acepta
            when(strategyMock.getThreshold()).thenReturn(2.0);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            assertTrue(result, "En maximización, si (current - candidate) < threshold debe aceptarse");
        }
    }

    @Test
    void maxShouldRejectWhenDifferenceIsAboveOrEqualThreshold()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadU acceptor = new AcceptNotBadU();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // current = 10, candidate = 8 -> result = 2
        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(8.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // Umbral 1 -> 2 < 1 es falso => se rechaza
            when(strategyMock.getThreshold()).thenReturn(1.0);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            assertFalse(result, "En maximización, si (current - candidate) >= threshold no debe aceptarse");
        }
    }

    @Test
    void minShouldAcceptWhenDifferenceIsAboveThreshold()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadU acceptor = new AcceptNotBadU();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // Minimizar:
        // current = 10, candidate = 12 -> resultMin = -2
        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(12.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            // Umbral -5 -> -2 > -5 => se acepta
            when(strategyMock.getThreshold()).thenReturn(-5.0);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            assertTrue(result, "En minimización, si (current - candidate) > threshold debe aceptarse");
        }
    }

    @Test
    void minShouldRejectWhenDifferenceIsBelowOrEqualThreshold()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadU acceptor = new AcceptNotBadU();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // Minimizar:
        // current = 10, candidate = 12 -> resultMin = -2
        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(12.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            // Umbral 0 -> -2 > 0 es falso => se rechaza
            when(strategyMock.getThreshold()).thenReturn(0.0);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            assertFalse(result, "En minimización, si (current - candidate) <= threshold no debe aceptarse");
        }
    }
}
