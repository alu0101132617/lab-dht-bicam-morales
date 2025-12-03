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

class AcceptBestTest {

    private ArrayList<Double> eval(double v) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(v);
        return list;
    }

    @Test
    void acceptCandidateInMaximizationShouldAcceptIfCandidateIsBetterOrEqual()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InvocationTargetException,
                   InstantiationException, IllegalAccessException, NoSuchMethodException {

        AcceptBest strategy = new AcceptBest();

        State current = mock(State.class);
        State better = mock(State.class);
        State worse = mock(State.class);

        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(better.getEvaluation()).thenReturn(eval(12.0));
        when(worse.getEvaluation()).thenReturn(eval(8.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // candidato mejor -> true
            assertTrue(strategy.acceptCandidate(current, better));

            // candidato igual -> true
            when(better.getEvaluation()).thenReturn(eval(10.0));
            assertTrue(strategy.acceptCandidate(current, better));

            // candidato peor -> false
            assertFalse(strategy.acceptCandidate(current, worse));
        }
    }

    @Test
    void acceptCandidateInMinimizationShouldAcceptIfCandidateIsBetterOrEqual()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InvocationTargetException,
                   InstantiationException, IllegalAccessException, NoSuchMethodException {

        AcceptBest strategy = new AcceptBest();

        State current = mock(State.class);
        State better = mock(State.class);
        State worse = mock(State.class);

        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(better.getEvaluation()).thenReturn(eval(8.0));   // mejor (menor)
        when(worse.getEvaluation()).thenReturn(eval(12.0));   // peor (mayor)

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            // candidato mejor -> true
            assertTrue(strategy.acceptCandidate(current, better));

            // candidato igual -> true
            when(better.getEvaluation()).thenReturn(eval(10.0));
            assertTrue(strategy.acceptCandidate(current, better));

            // candidato peor -> false
            assertFalse(strategy.acceptCandidate(current, worse));
        }
    }
}
