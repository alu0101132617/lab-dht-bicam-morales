package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UnivariateTest {

    @Test
    void distributionShouldReturnEmptyListWhenFathersIsNullOrEmpty() {
        Univariate univariate = new Univariate();

        // Caso fathers == null
        List<Probability> resultNull = univariate.distribution(null);
        assertNotNull(resultNull, "El resultado no debe ser null aunque la lista de padres sea null");
        assertTrue(resultNull.isEmpty(), "Si fathers es null, la lista de Probabilities debe ser vacía");

        // Caso fathers vacío
        List<State> emptyFathers = new ArrayList<>();
        List<Probability> resultEmpty = univariate.distribution(emptyFathers);
        assertNotNull(resultEmpty);
        assertTrue(resultEmpty.isEmpty(), "Si fathers está vacío, la lista de Probabilities debe ser vacía");
    }

    @Test
    void distributionShouldComputeProbabilitiesPerVariableSkippingMinusOne() {
        Univariate univariate = new Univariate();

        // Creamos 3 padres, cada uno con 2 variables (índices 0 y 1)
        // Usamos State real asumiendo que tiene setCode(ArrayList<Object>) como en otras partes del proyecto

        // Padre 1: [0, 1]
        State father1 = new State();
        ArrayList<Object> code1 = new ArrayList<>();
        code1.add(0);
        code1.add(1);
        father1.setCode(code1);

        // Padre 2: [0, -1]  (en la variable 1 se ignora -1)
        State father2 = new State();
        ArrayList<Object> code2 = new ArrayList<>();
        code2.add(0);
        code2.add(-1);
        father2.setCode(code2);

        // Padre 3: [2, 1]
        State father3 = new State();
        ArrayList<Object> code3 = new ArrayList<>();
        code3.add(2);
        code3.add(1);
        father3.setCode(code3);

        List<State> fathers = new ArrayList<>();
        fathers.add(father1);
        fathers.add(father2);
        fathers.add(father3);

        /*
         * cantV = 2 (dos variables)
         *
         * Variable 0: valores = [0,0,2]
         *   freq: 0 -> 2, 2 -> 1
         *   probs: 0 -> 2/3, 2 -> 1/3   (sobre 3 padres)
         *
         * Variable 1: valores = [1,-1,1]  (-1 se ignora)
         *   freq: 1 -> 2
         *   prob: 1 -> 2/3   (se divide entre fathers.size() = 3 igualmente)
         */

        List<Probability> probabilities = univariate.distribution(fathers);

        // Debe haber 3 Probability en total
        assertEquals(3, probabilities.size());

        // Buscamos las probabilidades esperadas sin depender del orden del HashMap
        boolean foundVar0Val0 = false;
        boolean foundVar0Val2 = false;
        boolean foundVar1Val1 = false;

        for (Probability p : probabilities) {
            int varIndex = (Integer) p.getKey();
            int value = (Integer) p.getValue();
            float prob = p.getProbability();

            if (varIndex == 0 && value == 0) {
                foundVar0Val0 = true;
                assertEquals(2.0f / 3.0f, prob, 1e-6f);
            } else if (varIndex == 0 && value == 2) {
                foundVar0Val2 = true;
                assertEquals(1.0f / 3.0f, prob, 1e-6f);
            } else if (varIndex == 1 && value == 1) {
                foundVar1Val1 = true;
                assertEquals(2.0f / 3.0f, prob, 1e-6f);
            }
        }

        assertTrue(foundVar0Val0, "Debe existir probabilidad para variable 0, valor 0");
        assertTrue(foundVar0Val2, "Debe existir probabilidad para variable 0, valor 2");
        assertTrue(foundVar1Val1, "Debe existir probabilidad para variable 1, valor 1");
    }

    @Test
    void getListKeyShouldReturnKeysInOrderFromSortedMap() {
        Univariate univariate = new Univariate();

        SortedMap<String, Object> map = new TreeMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        /*
         * TreeMap mantiene las claves ordenadas, así que:
         *  map.keySet().toString() -> "[A, B, C]"
         * El método getListKey hace parsing de esa cadena y debe devolver ["A","B","C"].
         */

        List<String> keys = univariate.getListKey(map);

        assertEquals(3, keys.size());
        assertEquals("A", keys.get(0));
        assertEquals("B", keys.get(1));
        assertEquals("C", keys.get(2));
    }
}
