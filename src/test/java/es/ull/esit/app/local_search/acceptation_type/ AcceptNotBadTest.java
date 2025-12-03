package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.ObjetiveFunction;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcceptNotBadTest {

    private ArrayList<Double> eval(double value) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    private ArrayList<ObjetiveFunction> dummyFunctions(int count) {
        ArrayList<ObjetiveFunction> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(mock(ObjetiveFunction.class));
        }
        return list;
    }

    @Test
    void acceptCandidateInMaximizationShouldAcceptIfCandidateIsBetterOrEqual() {
        AcceptNotBad strategy = new AcceptNotBad();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // current = 10.0
        when(current.getEvaluation()).thenReturn(eval(10.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);

            // Lista de funciones con tamaño > 0 para recorrer el bucle for
            when(problemMock.getFunction()).thenReturn(dummyFunctions(2));
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // Caso candidato mejor (12.0 >= 10.0) -> true
            when(candidate.getEvaluation()).thenReturn(eval(12.0));
            assertTrue(strategy.acceptCandidate(current, candidate));

            // Caso candidato igual (10.0 >= 10.0) -> true
            when(candidate.getEvaluation()).thenReturn(eval(10.0));
            assertTrue(strategy.acceptCandidate(current, candidate));

            // Caso candidato peor (8.0 >= 10.0) -> false
            when(candidate.getEvaluation()).thenReturn(eval(8.0));
            assertFalse(strategy.acceptCandidate(current, candidate));
        }
    }

    @Test
    void acceptCandidateInMinimizationShouldAcceptIfCandidateIsBetterOrEqual() {
        AcceptNotBad strategy = new AcceptNotBad();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // current = 10.0
        when(current.getEvaluation()).thenReturn(eval(10.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);

            // Lista de funciones para el bucle
            when(problemMock.getFunction()).thenReturn(dummyFunctions(3));
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            // Candidato mejor (8.0 <= 10.0) -> true
            when(candidate.getEvaluation()).thenReturn(eval(8.0));
            assertTrue(strategy.acceptCandidate(current, candidate));

            // Candidato igual (10.0 <= 10.0) -> true
            when(candidate.getEvaluation()).thenReturn(eval(10.0));
            assertTrue(strategy.acceptCandidate(current, candidate));

            // Candidato peor (12.0 <= 10.0) -> false
            when(candidate.getEvaluation()).thenReturn(eval(12.0));
            assertFalse(strategy.acceptCandidate(current, candidate));
        }
    }
}
