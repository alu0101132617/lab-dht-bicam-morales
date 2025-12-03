package es.ull.esit.app.problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CodificationTest {

    /** Implementación mínima de Codification para pruebas. */
    static class DummyCodification extends Codification {

        private final int variableCount;
        private final int aleatoryKey;

        DummyCodification(int variableCount, int aleatoryKey) {
            this.variableCount = variableCount;
            this.aleatoryKey = aleatoryKey;
        }

        @Override
        public boolean validState(State state) {
            // Para la prueba, consideramos válido si no es null y tiene código no vacío
            return state != null && state.getCode() != null;
        }

        @Override
        public Object getVariableAleatoryValue(int key) {
            // Para la prueba, devolvemos simplemente el propio key como Integer
            return key;
        }

        @Override
        public int getAleatoryKey() {
            return aleatoryKey;
        }

        @Override
        public int getVariableCount() {
            return variableCount;
        }
    }

    @Test
    void codificationMethodsShouldWorkAsExpected() {
        DummyCodification cod = new DummyCodification(5, 3);

        assertEquals(5, cod.getVariableCount(), "getVariableCount debe devolver el valor configurado");
        assertEquals(3, cod.getAleatoryKey(), "getAleatoryKey debe devolver el valor configurado");

        assertEquals(7, cod.getVariableAleatoryValue(7),
                "getVariableAleatoryValue debe devolver el propio índice como Integer");

        State s = new State();
        assertTrue(cod.validState(s), "validState debe devolver true para un estado no null con código");

        assertFalse(cod.validState(null), "validState debe devolver false para estado null");
    }
}
