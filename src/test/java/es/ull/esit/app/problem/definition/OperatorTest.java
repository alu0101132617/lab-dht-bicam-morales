package es.ull.esit.app.problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class OperatorTest {

    /** Implementación mínima de Operator para pruebas. */
    static class DummyOperator extends Operator {

        @Override
        public List<State> generatedNewState(State stateCurrent, Integer operatornumber) {
            List<State> list = new ArrayList<>();
            // Para la prueba, generamos un solo estado copia del actual
            if (stateCurrent != null) {
                list.add(stateCurrent.copy());
            }
            return list;
        }

        @Override
        public List<State> generateRandomState(Integer operatornumber) {
            List<State> list = new ArrayList<>();
            State s = new State();
            s.getCode().add(operatornumber);
            list.add(s);
            return list;
        }
    }

    @Test
    void generatedNewStateShouldReturnCopy() {
        DummyOperator op = new DummyOperator();

        State original = new State();
        original.getCode().add("x");

        List<State> neigh = op.generatedNewState(original, 0);

        assertEquals(1, neigh.size(), "Debe generarse un estado");
        assertNotSame(original, neigh.get(0), "El estado generado debe ser copia, no la misma instancia");
        assertEquals(original.getCode(), neigh.get(0).getCode(), "El código debe ser igual");
    }

    @Test
    void generateRandomStateShouldUseOperatorNumberAsCode() {
        DummyOperator op = new DummyOperator();

        List<State> randomStates = op.generateRandomState(42);

        assertEquals(1, randomStates.size());
        assertEquals(42, randomStates.get(0).getCode().get(0),
                "El código del estado aleatorio debe contener el número de operador");
    }
}
