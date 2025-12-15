package es.ull.esit.app.local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheuristics.generators.MultiCaseSimulatedAnnealing;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class AcceptMulticaseTest {

    /**
     * Deterministic Random used inside AcceptMulticase so that
     * accept / reject decisions depending on random() are reproducible.
     */
    private static class TestRandom extends SecureRandom {
        @Override
        public float nextFloat() {
            // Always 0 → if pAccept > 0, the candidate is accepted.
            return 0.0f;
        }
    }

    private AcceptMulticase acceptMulticase;

    @BeforeEach
    void setUp() throws Exception {
        acceptMulticase = new AcceptMulticase();

        // Inject a deterministic Random into the private field "rdm"
        Field rdmField = AcceptMulticase.class.getDeclaredField("rdm");
        rdmField.setAccessible(true);
        rdmField.set(acceptMulticase, new TestRandom());

        // Reasonable temperatures for MC-SA logic
        MultiCaseSimulatedAnnealing.setTinitial(1.0);
        MultiCaseSimulatedAnnealing.setTfinal(0.1);
    }

    /** Helper: build an ArrayList<Double> (no List → avoids cast issues) */
    private ArrayList<Double> eval(double... values) {
        ArrayList<Double> list = new ArrayList<>();
        for (double v : values) {
            list.add(v);
        }
        return list;
    }

    @Test
    void acceptCandidateShouldReturnNonNullAndNotThrowWhenListIsEmpty() {
        State current = mock(State.class);
        State candidate = mock(State.class);

        // Evaluations NUNCA nulas
        when(current.copy()).thenReturn(current);
        when(current.getEvaluation()).thenReturn(eval(1.0, 1.0));
        when(candidate.getEvaluation()).thenReturn(eval(2.0, 2.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            Generator generatorMock = mock(Generator.class);
            when(generatorMock.getType()).thenReturn(GeneratorType.HillClimbing);
            when(strategyMock.getGenerator()).thenReturn(generatorMock);

            // Empty reference list → Dominance no revienta
            when(strategyMock.getListRefPoblacFinal()).thenReturn(new ArrayList<>());

            Boolean result = assertDoesNotThrow(
                    () -> acceptMulticase.acceptCandidate(current, candidate),
                    "acceptCandidate no debería lanzar excepciones con lista de referencia vacía"
            );

            assertNotNull(result, "El resultado no debe ser null");
        }
    }

    @Test
    void acceptCandidateShouldReturnNonNullAndNotThrowWhenListIsNotEmpty() {
        State current = mock(State.class);
        State candidate = mock(State.class);

        when(current.copy()).thenReturn(current);
        when(current.getEvaluation()).thenReturn(eval(1.0, 1.0));
        when(candidate.getEvaluation()).thenReturn(eval(5.0, 5.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            Generator generatorMock = mock(Generator.class);
            when(generatorMock.getType()).thenReturn(GeneratorType.HillClimbing);
            when(strategyMock.getGenerator()).thenReturn(generatorMock);

            // Lista de referencia con una solución válida (evaluation no nula)
            List<State> refList = new ArrayList<>();
            State refState = mock(State.class);
            when(refState.getEvaluation()).thenReturn(eval(2.0, 2.0));
            refList.add(refState);
            when(strategyMock.getListRefPoblacFinal()).thenReturn(refList);

            Boolean result = assertDoesNotThrow(
                    () -> acceptMulticase.acceptCandidate(current, candidate),
                    "acceptCandidate no debería lanzar excepciones con lista de referencia no vacía"
            );

            assertNotNull(result, "El resultado no debe ser null");
        }
    }
}
