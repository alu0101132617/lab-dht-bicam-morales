package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnePointCrossoverTest {

    /**
     * Random de prueba que devuelve valores deterministas:
     * - nextDouble(): un valor fijo
     * - nextInt(bound): primero la posición de corte, luego la elección (0 o 1)
     */
    private static class TestRandom extends Random {
        private final double doubleValue;
        private final int posValue;
        private final int choiceValue;
        private int intCallCount = 0;

        TestRandom(double doubleValue, int posValue, int choiceValue) {
            this.doubleValue = doubleValue;
            this.posValue = posValue;
            this.choiceValue = choiceValue;
        }

        @Override
        public double nextDouble() {
            return doubleValue;
        }

        @Override
        public int nextInt(int bound) {
            if (intCallCount == 0) {
                intCallCount++;
                return posValue;   // primera llamada: posición de corte
            } else {
                return choiceValue; // segunda llamada: elección 0 o 1
            }
        }
    }

    /**
     * Inyecta un Random controlado en la instancia de OnePointCrossover, usando reflexión.
     */
    private void injectRandom(OnePointCrossover crossover, Random random) throws Exception {
        Field randomField = OnePointCrossover.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(crossover, random);
    }

    @Test
    void crossoverShouldReturnCopyWhenRandomAbovePc() throws Exception {
        OnePointCrossover crossover = new OnePointCrossover();

        // Random que siempre produce number > pc (0.9 > 0.5)
        injectRandom(crossover, new TestRandom(0.9, 0, 0));

        State father1 = mock(State.class);
        State father2 = mock(State.class);
        State copy = mock(State.class);

        when(father1.getCopy()).thenReturn(copy);

        // pc = 0.5, pero number = 0.9 -> NO hay cruce
        State result = crossover.crossover(father1, father2, 0.5);

        // Debe devolver la copia, sin llamar a setCode
        assertSame(copy, result, "Debe devolverse el individuo copia sin modificar");
        verify(copy, never()).setCode(any());
        // No se necesita Strategy en este camino
    }

    @Test
    void crossoverShouldUseInd1WhenChoiceIsZero() throws Exception {
        OnePointCrossover crossover = new OnePointCrossover();

        // number = 0.1 (<= pc), pos = 1, choice = 0 -> usa ind1
        injectRandom(crossover, new TestRandom(0.1, 1, 0));

        State father1 = mock(State.class);
        State father2 = mock(State.class);
        State newInd = mock(State.class);

        // Códigos de los padres
        ArrayList<Object> codeFather1 = new ArrayList<>();
        codeFather1.add("A");
        codeFather1.add("B");
        codeFather1.add("C");
        codeFather1.add("D");

        ArrayList<Object> codeFather2 = new ArrayList<>();
        codeFather2.add("a");
        codeFather2.add("b");
        codeFather2.add("c");
        codeFather2.add("d");

        when(father1.getCode()).thenReturn(codeFather1);
        when(father2.getCode()).thenReturn(codeFather2);
        when(father1.getCopy()).thenReturn(newInd);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // variableCount = tamaño del código (4)
            when(strategyMock.getProblem().getCodification().getVariableCount()).thenReturn(codeFather1.size());

            // Act
            State result = crossover.crossover(father1, father2, 0.8);

            // Devuelve la copia modificada
            assertSame(newInd, result);

            // Capturamos el código que se le pasa a setCode
            ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
            verify(newInd).setCode(captor.capture());

            ArrayList<Object> offspringCode = captor.getValue();

            /*
             * father1: [A, B, C, D]
             * father2: [a, b, c, d]
             * pos = 1
             * i <= 1 -> ind1: from father1, ind2: from father2
             * i >  1 -> ind1: from father2, ind2: from father1
             *
             * ind1 = [A, B, c, d]
             */
            assertEquals(4, offspringCode.size());
            assertEquals("A", offspringCode.get(0));
            assertEquals("B", offspringCode.get(1));
            assertEquals("c", offspringCode.get(2));
            assertEquals("d", offspringCode.get(3));
        }
    }

    @Test
    void crossoverShouldUseInd2WhenChoiceIsOne() throws Exception {
        OnePointCrossover crossover = new OnePointCrossover();

        // number = 0.1 (<= pc), pos = 1, choice = 1 -> usa ind2
        injectRandom(crossover, new TestRandom(0.1, 1, 1));

        State father1 = mock(State.class);
        State father2 = mock(State.class);
        State newInd = mock(State.class);

        // Códigos de los padres
        ArrayList<Object> codeFather1 = new ArrayList<>();
        codeFather1.add("A");
        codeFather1.add("B");
        codeFather1.add("C");
        codeFather1.add("D");

        ArrayList<Object> codeFather2 = new ArrayList<>();
        codeFather2.add("a");
        codeFather2.add("b");
        codeFather2.add("c");
        codeFather2.add("d");

        when(father1.getCode()).thenReturn(codeFather1);
        when(father2.getCode()).thenReturn(codeFather2);
        when(father1.getCopy()).thenReturn(newInd);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            when(strategyMock.getProblem().getCodification().getVariableCount()).thenReturn(codeFather1.size());

            // Act
            State result = crossover.crossover(father1, father2, 0.8);

            assertSame(newInd, result);

            // Capturamos el código que se le pasa a setCode
            ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
            verify(newInd).setCode(captor.capture());

            ArrayList<Object> offspringCode = captor.getValue();

            /*
             * ind2 (para choice = 1):
             * i <= 1 -> from father2: [a, b]
             * i > 1  -> from father1: [C, D]
             * ind2 = [a, b, C, D]
             */
            assertEquals(4, offspringCode.size());
            assertEquals("a", offspringCode.get(0));
            assertEquals("b", offspringCode.get(1));
            assertEquals("C", offspringCode.get(2));
            assertEquals("D", offspringCode.get(3));
        }
    }
}
