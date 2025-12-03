package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcceptAnyoneTest {

    @Test
    void acceptCandidateShouldAlwaysReturnTrue() {
        AcceptAnyone strategy = new AcceptAnyone();

        State current = new State();
        State candidate = new State();

        assertTrue(strategy.acceptCandidate(current, candidate),
                "AcceptAnyone debe aceptar siempre el candidato");

        // Incluso con nulls, debería seguir devolviendo true
        assertTrue(strategy.acceptCandidate(null, null),
                "AcceptAnyone debe aceptar incluso cuando los estados son null");
    }
}
