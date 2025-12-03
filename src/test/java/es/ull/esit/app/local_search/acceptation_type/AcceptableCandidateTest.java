package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests básicos para la clase abstracta AcceptableCandidate usando una
 * implementación de prueba.
 */
class AcceptableCandidateTest {

    /**
     * Implementación mínima de prueba de AcceptableCandidate
     * que acepta el candidato si no es null.
     */
    private static class TestAcceptableCandidate extends AcceptableCandidate {
        @Override
        public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
            return stateCandidate != null;
        }
    }

    @Test
    void acceptCandidateShouldReturnTrueWhenCandidateIsNotNull() throws Exception {
        AcceptableCandidate strategy = new TestAcceptableCandidate();

        State current = new State();
        State candidate = new State();

        Boolean result = strategy.acceptCandidate(current, candidate);

        assertTrue(result, "Debe devolver true cuando el candidato no es null");
    }

    @Test
    void acceptCandidateShouldReturnFalseWhenCandidateIsNull() throws Exception {
        AcceptableCandidate strategy = new TestAcceptableCandidate();

        State current = new State();
        State candidate = null;

        Boolean result = strategy.acceptCandidate(current, candidate);

        assertFalse(result, "Debe devolver false cuando el candidato es null");
    }
}
