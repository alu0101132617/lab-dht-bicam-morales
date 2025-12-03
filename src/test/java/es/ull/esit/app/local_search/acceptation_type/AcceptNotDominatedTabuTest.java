package es.ull.esit.app.local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class AcceptNotDominatedTabuTest {

    /** IMPORTANTE: devolvemos ArrayList<Double> */
    private ArrayList<Double> eval(double... values) {
        ArrayList<Double> list = new ArrayList<>();
        for (double v : values) {
            list.add(v);
        }
        return list;
    }

    @Test
    void whenListIsEmptyMethodShouldReturnTrueAndNotThrow() {
        AcceptNotDominatedTabu strategy = new AcceptNotDominatedTabu();

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

            Generator generatorMock = mock(Generator.class);
            when(generatorMock.getType()).thenReturn(GeneratorType.HillClimbing);
            when(strategyMock.getGenerator()).thenReturn(generatorMock);

            when(strategyMock.getListRefPoblacFinal()).thenReturn(new ArrayList<>());

            Boolean result = assertDoesNotThrow(
                    () -> strategy.acceptCandidate(current, candidate),
                    "acceptCandidate no debería lanzar excepciones");

            // La implementación actual siempre devuelve true
            assertTrue(result, "El método debe devolver true");
        }
    }

    @Test
    void whenListIsNotEmptyMethodShouldReturnTrueAndNotThrow() {
        AcceptNotDominatedTabu strategy = new AcceptNotDominatedTabu(); // nombre correcto

        State current = mock(State.class);
        State candidate = mock(State.class);

        when(current.copy()).thenReturn(current);
        when(current.getEvaluation()).thenReturn(eval(5.0));
        when(candidate.getEvaluation()).thenReturn(eval(10.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            Generator generatorMock = mock(Generator.class);
            when(generatorMock.getType()).thenReturn(GeneratorType.HillClimbing);
            when(strategyMock.getGenerator()).thenReturn(generatorMock);

            List<State> refList = new ArrayList<>();
            State refState = mock(State.class);
            when(refState.getEvaluation()).thenReturn(eval(6.0));
            refList.add(refState);
            when(strategyMock.getListRefPoblacFinal()).thenReturn(refList);

            Boolean result = assertDoesNotThrow(
                    () -> strategy.acceptCandidate(current, candidate),
                    "acceptCandidate no debería lanzar excepciones");

            assertTrue(result, "El método debe devolver true incluso con lista no vacía");
        }
    }
}
