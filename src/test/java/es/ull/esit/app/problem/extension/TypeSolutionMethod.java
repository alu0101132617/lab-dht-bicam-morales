package es.ull.esit.app.problem.extension;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TypeSolutionMethodTest {

    @Test
    void enumShouldContainExpectedValues() {
        TypeSolutionMethod[] values = TypeSolutionMethod.values();
        assertTrue(values.length >= 2, "Debe haber al menos 2 valores");

        assertNotNull(TypeSolutionMethod.FactoresPonderados);
        assertNotNull(TypeSolutionMethod.MultiObjetivoPuro);
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(TypeSolutionMethod.FactoresPonderados,
                TypeSolutionMethod.valueOf("FactoresPonderados"));
        assertEquals(TypeSolutionMethod.MultiObjetivoPuro,
                TypeSolutionMethod.valueOf("MultiObjetivoPuro"));
    }
}
