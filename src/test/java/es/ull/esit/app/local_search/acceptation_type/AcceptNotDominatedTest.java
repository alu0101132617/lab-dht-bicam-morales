package es.ull.esit.app.local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class AcceptNotDominatedTest {

    /** IMPORTANTE: devolvemos ArrayList<Double> */
    private ArrayList<Double> eval(double... values) {
        ArrayList<Double> list = new ArrayList<>();
        for (double v : values) {
            list.add(v);
        }
        return list;
    }

    @Test
    void whenReferenceListIsEmptyMethodShouldNotThrowAndReturnNonNull() {
        AcceptNotDominated strategy = new AcceptNotDominated();

        State current = mock(State.class);
        State candidate = mock(State.class);

        when(current.copy()).thenReturn(current);
        when(current.getEvaluation()).thenReturn(eval(5.0));
        when(candidate.getEvaluation()).thenReturn(eval(10.0)); // candidato mejor

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            when(strategyMock.getListRefPoblacFinal()).thenReturn(new ArrayList<>());

            Boolean accept = assertDoesNotThrow(
                    () -> strategy.acceptCandidate(current, candidate),
                    "acceptCandidate no debería lanzar excepciones con lista vacía");

            assertNotNull(accept, "El resultado no debe ser null");
        }
    }

    @Test
    void whenReferenceListIsNotEmptyMethodShouldNotThrowAndReturnNonNull() {
        AcceptNotDominated strategy = new AcceptNotDominated();

        State current = mock(State.class);
        State candidate = mock(State.class);

        when(current.copy()).thenReturn(current);
        when(current.getEvaluation()).thenReturn(eval(5.0));
        when(candidate.getEvaluation()).thenReturn(eval(7.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // lista con una solución cualquiera
            List<State> refList = new ArrayList<>();
            State refSolution = mock(State.class);
            when(refSolution.getEvaluation()).thenReturn(eval(6.0));
            refList.add(refSolution);
            when(strategyMock.getListRefPoblacFinal()).thenReturn(refList);

            Boolean accept = assertDoesNotThrow(
                    () -> strategy.acceptCandidate(current, candidate),
                    "acceptCandidate no debería lanzar excepciones con lista no vacía");

            assertNotNull(accept, "El resultado no debe ser null");
        }
    }
}
