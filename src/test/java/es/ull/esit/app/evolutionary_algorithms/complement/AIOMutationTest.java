package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.config.tspdynamic.TSPState;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AIOMutation class.
 */
class AIOMutationTest {

    @BeforeEach
    void clearPath() {
        // path es estático, lo limpiamos antes de cada test para evitar interferencias
        AIOMutation.path.clear();
    }

    @Test
    void sortedPathValueShouldSortStateCodeByValueAscending() {
        AIOMutation mutation = new AIOMutation();

        // Creamos una lista de TSPState desordenada por 'value'
        ArrayList<Object> code = new ArrayList<>();

        TSPState s1 = new TSPState();
        s1.setValue(30);
        s1.setIdCity(1);

        TSPState s2 = new TSPState();
        s2.setValue(10);
        s2.setIdCity(2);

        TSPState s3 = new TSPState();
        s3.setValue(20);
        s3.setIdCity(3);

        code.add(s1);
        code.add(s2);
        code.add(s3);

        // Mock del State para que devuelva nuestra lista
        State state = mock(State.class);
        when(state.getCode()).thenReturn(code);

        // Act
        mutation.sortedPathValue(state);

        // Assert: la lista interna de state (code) debe estar ordenada por 'value'
        assertEquals(3, code.size());
        assertEquals(10, ((TSPState) code.get(0)).getValue());
        assertEquals(20, ((TSPState) code.get(1)).getValue());
        assertEquals(30, ((TSPState) code.get(2)).getValue());
    }
   
    @Test
    void mutationShouldReverseSubPathBetweenTwoRandomKeys() {
        AIOMutation mutation = new AIOMutation();

        // Código del estado: 5 ciudades con idCity = 0..4 y value = 0..4
        ArrayList<Object> code = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TSPState cityState = new TSPState();
            cityState.setIdCity(i);
            cityState.setValue(i); // ya está ordenado por value
            code.add(cityState);
        }

        State state = mock(State.class);
        when(state.getCode()).thenReturn(code);

        // Mock estático de Strategy.getStrategy() y encadenado de getProblem().getCodification()
        try (MockedStatic<Strategy> mockedStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            mockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // Forzamos claves aleatorias deterministas: key = 1, key1 = 3
            when(strategyMock.getProblem().getCodification().getAleatoryKey()).thenReturn(1, 3);

            // Act
            State mutated = mutation.mutation(state, 1.0);

            // Debe devolver el mismo objeto state
            assertSame(state, mutated);

            /*
             * Con key = 1 y key1 = 3, el subcamino entre 1 y 3 se invierte.
             * Original: [0, 1, 2, 3, 4]
             * Después:  [0, 3, 2, 1, 4]
             */
            assertEquals(0, ((TSPState) code.get(0)).getIdCity());
            assertEquals(3, ((TSPState) code.get(1)).getIdCity());
            assertEquals(2, ((TSPState) code.get(2)).getIdCity());
            assertEquals(1, ((TSPState) code.get(3)).getIdCity());
            assertEquals(4, ((TSPState) code.get(4)).getIdCity());

            // Al final de mutation, path debe quedar vacío
            assertTrue(AIOMutation.path.isEmpty(), "path debe quedar vacío después de mutation()");
        }
    }

    @Test
    void fillPathShouldFillWithVariableCountIndices() {
        // Mock estático de Strategy para controlar variableCount()
        try (MockedStatic<Strategy> mockedStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            mockedStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // Forzamos variableCount = 4
            when(strategyMock.getProblem().getCodification().getVariableCount()).thenReturn(4);

            // Act
            AIOMutation.fillPath();

            // Assert: path = [0,1,2,3]
            assertEquals(4, AIOMutation.path.size());
            assertEquals(0, (int) (Integer) AIOMutation.path.get(0));
            assertEquals(1, (int) (Integer) AIOMutation.path.get(1));
            assertEquals(2, (int) (Integer) AIOMutation.path.get(2));
            assertEquals(3, (int) (Integer) AIOMutation.path.get(3));
        }
    }
}
