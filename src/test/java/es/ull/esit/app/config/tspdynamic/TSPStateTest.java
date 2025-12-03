package es.ull.esit.app.config.tspdynamic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TSPStateTest {

    @Test
    void defaultValuesShouldBeZero() {
        TSPState state = new TSPState();

        // Por defecto, los tipos primitivos int se inicializan a 0 en Java
        assertEquals(0, state.getValue(), "El valor por defecto de 'value' debe ser 0");
        assertEquals(0, state.getIdCity(), "El valor por defecto de 'idCity' debe ser 0");
    }

    @Test
    void setValueShouldUpdateValue() {
        TSPState state = new TSPState();
        state.setValue(42);

        assertEquals(42, state.getValue(), "setValue debe actualizar correctamente el campo 'value'");
    }

    @Test
    void setIdCityShouldUpdateIdCity() {
        TSPState state = new TSPState();
        state.setIdCity(7);

        assertEquals(7, state.getIdCity(), "setIdCity debe actualizar correctamente el campo 'idCity'");
    }

    @Test
    void setIdCityCanHandleNegativeValues() {
        TSPState state = new TSPState();
        state.setIdCity(-1);

        // No hay restricciones en la clase, así que debe aceptar valores negativos
        assertEquals(-1, state.getIdCity(), "setIdCity debe almacenar también valores negativos");
    }
}
