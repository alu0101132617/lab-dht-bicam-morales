package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheuristics.generators.SimulatedAnnealing;
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

class AcceptNotBadTTest {

    private ArrayList<Double> eval(double value) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    @Test
    void maxShouldAcceptBetterCandidate()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadT acceptor = new AcceptNotBadT();

        State current = mock(State.class);
        State candidate = mock(State.class);

        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(12.0)); // mejor

        SimulatedAnnealing.setTinitial(1.0);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            assertTrue(result, "En maximización, un candidato mejor debe aceptarse");
        }
    }

    @Test
    void maxShouldHandleWorseCandidateWithoutErrors()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadT acceptor = new AcceptNotBadT();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // Candidato peor
        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(5.0));

        SimulatedAnnealing.setTinitial(1.0);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            // No nos importa si es true o false, solo que no sea null
            assertNotNull(result, "El resultado no debe ser null aunque el candidato sea peor");
        }
    }

    @Test
    void minShouldAcceptBetterCandidate()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadT acceptor = new AcceptNotBadT();

        State current = mock(State.class);
        State candidate = mock(State.class);

        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(8.0)); // mejor para minimizar

        SimulatedAnnealing.setTinitial(1.0);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            assertTrue(result, "En minimización, un candidato con valor menor debe aceptarse");
        }
    }

    @Test
    void minShouldHandleWorseCandidateWithoutErrors()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        AcceptNotBadT acceptor = new AcceptNotBadT();

        State current = mock(State.class);
        State candidate = mock(State.class);

        // Candidato peor (mayor valor en minimización)
        when(current.getEvaluation()).thenReturn(eval(10.0));
        when(candidate.getEvaluation()).thenReturn(eval(12.0));

        SimulatedAnnealing.setTinitial(1.0);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            Boolean result = acceptor.acceptCandidate(current, candidate);

            // De nuevo, no nos importa true/false, solo que no haya null ni excepciones
            assertNotNull(result, "El resultado no debe ser null aunque el candidato sea peor");
        }
    }
}
