package es.ull.esit.app.problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.ull.esit.app.problem.definition.Problem.ProblemType;

class ObjetiveFunctionTest {

    /** Implementación mínima de ObjetiveFunction para pruebas. */
    static class DummyObjetiveFunction extends ObjetiveFunction {

        @Override
        public Double evaluation(State state) {
            // Para la prueba, devolvemos simplemente el tamaño del código como double
            return (double) state.getCode().size();
        }
    }

    @Test
    void gettersAndSettersShouldWork() {
        DummyObjetiveFunction f = new DummyObjetiveFunction();

        f.setWeight(2.5f);
        assertEquals(2.5f, f.getWeight(), 0.0001f, "getWeight debe devolver el valor establecido");

        f.setTypeProblem(ProblemType.MAXIMIZAR);
        assertEquals(ProblemType.MAXIMIZAR, f.getTypeProblem(),
                "getTypeProblem debe devolver el tipo establecido");
    }

    @Test
    void evaluationShouldReturnExpectedValue() {
        DummyObjetiveFunction f = new DummyObjetiveFunction();

        State s = new State();
        s.getCode().add("a");
        s.getCode().add("b");
        s.getCode().add("c");

        Double value = f.evaluation(s);
        assertEquals(3.0, value, 0.0001,
                "La evaluación debe ser el tamaño del código como double");
    }
}
