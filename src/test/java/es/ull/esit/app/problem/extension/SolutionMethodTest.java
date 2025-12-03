package es.ull.esit.app.problem.extension;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.ull.esit.app.problem.definition.State;

class SolutionMethodTest {

    /** Implementación mínima para probar la clase abstracta. */
    static class DummySolutionMethod extends SolutionMethod {
        boolean called = false;

        @Override
        public void evaluationState(State state) {
            called = true;
            state.getCode().add("evaluated");
        }
    }

    @Test
    void evaluationStateShouldBeOverridable() {
        DummySolutionMethod sm = new DummySolutionMethod();
        State s = new State();

        sm.evaluationState(s);

        assertTrue(sm.called, "evaluationState debe haberse ejecutado");
        assertFalse(s.getCode().isEmpty(), "El estado debe modificarse en la implementación dummy");
    }
}
