package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnePointMutationTest {

    @Test
    void mutationShouldModifyStateWhenProbabilityAllows() {
        OnePointMutation mutation = new OnePointMutation();

        // Código inicial del estado: [A, B, C]
        ArrayList<Object> code = new ArrayList<>();
        code.add("A");
        code.add("B");
        code.add("C");

        State state = mock(State.class);
        when(state.getCode()).thenReturn(code);

        try (MockedStatic<ThreadLocalRandom> tlrStatic = Mockito.mockStatic(ThreadLocalRandom.class);
             MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {

            // Mock de ThreadLocalRandom.current().nextDouble() -> probM = 0.2
            ThreadLocalRandom randomMock = mock(ThreadLocalRandom.class);
            tlrStatic.when(ThreadLocalRandom::current).thenReturn(randomMock);
            when(randomMock.nextDouble()).thenReturn(0.2);

            // Mock de Strategy y encadenado hasta Codification
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // índice aleatorio = 1, valor aleatorio en ese índice = "X"
            when(strategyMock.getProblem().getCodification().getAleatoryKey()).thenReturn(1);
            when(strategyMock.getProblem().getCodification().getVariableAleatoryValue(1)).thenReturn("X");

            // pm = 0.5 >= probM = 0.2 -> se ejecuta la mutación
            State result = mutation.mutation(state, 0.5);

            // Se devuelve el mismo objeto
            assertSame(state, result);

            // El código debe haberse modificado en la posición 1
            assertEquals(3, code.size());
            assertEquals("A", code.get(0));
            assertEquals("X", code.get(1)); // mutado
            assertEquals("C", code.get(2));
        }
    }

    @Test
    void mutationShouldNotModifyStateWhenProbabilityTooLow() {
        OnePointMutation mutation = new OnePointMutation();

        // Código inicial del estado: [A, B, C]
        ArrayList<Object> code = new ArrayList<>();
        code.add("A");
        code.add("B");
        code.add("C");

        State state = mock(State.class);
        when(state.getCode()).thenReturn(code);

        try (MockedStatic<ThreadLocalRandom> tlrStatic = Mockito.mockStatic(ThreadLocalRandom.class);
             MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {

            // probM = 0.9, pm = 0.5 -> pm >= probM es falso → NO hay mutación
            ThreadLocalRandom randomMock = mock(ThreadLocalRandom.class);
            tlrStatic.when(ThreadLocalRandom::current).thenReturn(randomMock);
            when(randomMock.nextDouble()).thenReturn(0.9);

            // Aun si Strategy estuviese mal, no debe llamarse en esta rama
            strategyStatic.when(Strategy::getStrategy).thenThrow(
                    new AssertionError("Strategy.getStrategy() no debería llamarse cuando no hay mutación")
            );

            State result = mutation.mutation(state, 0.5);

            // Se devuelve el mismo objeto
            assertSame(state, result);

            // El código debe permanecer intacto
            assertEquals(3, code.size());
            assertEquals("A", code.get(0));
            assertEquals("B", code.get(1));
            assertEquals("C", code.get(2));
        }
    }
}
