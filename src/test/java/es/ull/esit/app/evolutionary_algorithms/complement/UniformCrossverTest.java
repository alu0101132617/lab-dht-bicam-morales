package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for UniformCrossover.
 */
class UniformCrossoverTest {

    /**
     * Random de prueba que siempre devuelve 1 en nextInt(2),
     * para que la máscara tenga mascara[0] = 1 y el resto a 0 (por la implementación).
     */
    private static class TestRandom extends Random {
        @Override
        public int nextInt(int bound) {
            return 1; // siempre 1
        }
    }

    /**
     * Inyecta un Random controlado en la instancia de UniformCrossover mediante reflexión.
     */
    private void injectRandom(UniformCrossover crossover, Random random) throws Exception {
        Field randomField = UniformCrossover.class.getDeclaredField("r");
        randomField.setAccessible(true);
        randomField.set(crossover, random);
    }

    @Test
    void mascaraShouldGenerateArrayUsingInternalRandom() throws Exception {
        UniformCrossover crossover = new UniformCrossover();

        // Inyectamos nuestro Random determinista
        injectRandom(crossover, new TestRandom());

        int length = 3;
        int[] mask = crossover.mascara(length);

        assertNotNull(mask, "La máscara no debe ser null");
        assertEquals(length, mask.length, "La máscara debe tener la longitud solicitada");

        /*
         * Debido a la implementación:
         *  - En el bucle, siempre se asigna mascara[0] = value
         *  - Los índices 1..length-1 nunca se tocan y quedan en 0
         * Con TestRandom (siempre 1), al final:
         *  mascara[0] = 1
         *  mascara[1] = 0
         *  mascara[2] = 0
         */
        assertEquals(1, mask[0], "El índice 0 de la máscara debe ser 1 con nuestro Random de prueba");
        assertEquals(0, mask[1], "Los índices distintos de 0 quedan en 0 según la implementación actual");
        assertEquals(0, mask[2], "Los índices distintos de 0 quedan en 0 según la implementación actual");
    }

    @Test
    void crossoverShouldUseMaskToChooseGenesFromParents() throws Exception {
        UniformCrossover crossover = new UniformCrossover();

        // Inyectamos el Random determinista para que la máscara sea [1,0,0,...]
        injectRandom(crossover, new TestRandom());

        // Padre 1: genes "A1", "A2", "A3"
        State father1 = mock(State.class);
        ArrayList<Object> codeFather1 = new ArrayList<>();
        codeFather1.add("A1");
        codeFather1.add("A2");
        codeFather1.add("A3");
        when(father1.getCode()).thenReturn(codeFather1);

        // Padre 2: genes "B1", "B2", "B3"
        State father2 = mock(State.class);
        ArrayList<Object> codeFather2 = new ArrayList<>();
        codeFather2.add("B1");
        codeFather2.add("B2");
        codeFather2.add("B3");
        when(father2.getCode()).thenReturn(codeFather2);

        // Copia del padre1 que se usará como base del hijo
        State child = mock(State.class);
        ArrayList<Object> codeChild = new ArrayList<>();
        codeChild.add("X1");
        codeChild.add("X2");
        codeChild.add("X3");
        when(child.getCode()).thenReturn(codeChild);

        when(father1.getCopy()).thenReturn(child);

        /*
         * Con TestRandom, la máscara generada por mascara(3) será:
         *  [1, 0, 0] según la implementación actual.
         *
         * En crossover:
         *  k = 0 -> mascara[0] == 1  => toma gene de father1 (A1)
         *  k = 1 -> mascara[1] == 0  => else -> toma gene de father2 (B2)
         *  k = 2 -> mascara[2] == 0  => else -> toma gene de father2 (B3)
         */

        State result = crossover.crossover(father1, father2, 0.8);

        // Debe devolver la misma instancia de child
        assertSame(child, result);

        assertEquals(3, codeChild.size());
        assertEquals("A1", codeChild.get(0), "El primer gen debe venir de father1");
        assertEquals("B2", codeChild.get(1), "El segundo gen debe venir de father2");
        assertEquals("B3", codeChild.get(2), "El tercer gen debe venir de father2");
    }
}
