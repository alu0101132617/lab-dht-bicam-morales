package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class LimitRouletteTest {

    @Test
    void generatorGetterAndSetterShouldWork() {
        LimitRoulette roulette = new LimitRoulette();
        Generator generatorMock = mock(Generator.class);

        assertNull(roulette.getGenerator(), "Por defecto el generator debe ser null");

        roulette.setGenerator(generatorMock);
        assertSame(generatorMock, roulette.getGenerator(),
                "getGenerator debe devolver el mismo objeto pasado a setGenerator");
    }

    @Test
    void limitHighGetterAndSetterShouldWork() {
        LimitRoulette roulette = new LimitRoulette();

        assertEquals(0.0f, roulette.getLimitHigh(), 0.0001,
                "Por defecto limitHigh debería ser 0.0");

        roulette.setLimitHigh(10.5f);
        assertEquals(10.5f, roulette.getLimitHigh(), 0.0001,
                "getLimitHigh debe devolver el valor establecido por setLimitHigh");
    }

    @Test
    void limitLowGetterAndSetterShouldWork() {
        LimitRoulette roulette = new LimitRoulette();

        assertEquals(0.0f, roulette.getLimitLow(), 0.0001,
                "Por defecto limitLow debería ser 0.0");

        roulette.setLimitLow(-3.25f);
        assertEquals(-3.25f, roulette.getLimitLow(), 0.0001,
                "getLimitLow debe devolver el valor establecido por setLimitLow");
    }
}
