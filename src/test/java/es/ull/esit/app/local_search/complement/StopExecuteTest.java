package es.ull.esit.app.local_search.complement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StopExecuteTest {

    @Test
    void shouldReturnTrueWhenCurrentIsLessThanMax() {
        StopExecute stopExecute = new StopExecute();

        Boolean result = stopExecute.stopIterations(3, 5);

        assertTrue(result, "Debe devolver true cuando la iteración actual es menor que el máximo");
    }

    @Test
    void shouldReturnFalseWhenCurrentEqualsMax() {
        StopExecute stopExecute = new StopExecute();

        Boolean result = stopExecute.stopIterations(5, 5);

        assertFalse(result, "Debe devolver false cuando la iteración actual es igual al máximo");
    }

    @Test
    void shouldReturnFalseWhenCurrentIsGreaterThanMax() {
        StopExecute stopExecute = new StopExecute();

        Boolean result = stopExecute.stopIterations(6, 5);

        assertFalse(result, "Debe devolver false cuando la iteración actual es mayor que el máximo");
    }
}

