package es.ull.esit.app.problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.ull.esit.app.metaheuristics.generators.GeneratorType;

class StateTest {

    @Test
    void defaultConstructorShouldInitializeCode() {
        State s = new State();
        assertNotNull(s.getCode(), "El código debe inicializarse");
        assertTrue(s.getCode().isEmpty(), "El código debe estar vacío inicialmente");
    }

    @Test
    void constructorWithCodeShouldCreateStateWithThatCode() {
        List<Object> code = new ArrayList<>();
        code.add("x");
        code.add("y");

        State s = new State(code);

        // Solo comprobamos que el contenido sea el mismo;
        // tu implementación NO hace copia defensiva, y eso es válido.
        assertEquals(code, s.getCode(),
                "El código debe coincidir con la lista pasada al constructor");
    }

    @Test
    void copyConstructorShouldCopyAllFields() {
        State original = new State();
        original.setNumber(5);
        original.setTypeGenerator(GeneratorType.TabuSearch);
        original.getCode().add("a");
        original.getCode().add("b");

        List<Double> eval = new ArrayList<>();
        eval.add(1.0);
        eval.add(2.0);
        original.setEvaluation(eval);

        State copy = new State(original);

        assertEquals(original.getNumber(), copy.getNumber());
        assertEquals(original.getTypeGenerator(), copy.getTypeGenerator());
        assertEquals(original.getCode(), copy.getCode());
        assertEquals(original.getEvaluation(), copy.getEvaluation());

        // Aquí sí hay copia defensiva para code, según tu implementación:
        // code = new ArrayList<>(ps.getCode());
        assertNotSame(original.getCode(), copy.getCode(),
                "El código debe copiarse en el copy constructor");
    }

    @Test
    void copyMethodShouldReturnNewInstance() {
        State original = new State();
        original.getCode().add("z");

        State copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getCode(), copy.getCode());
    }

    @Test
    void getCopyShouldReturnNewStateWithSameCodeList() {
        List<Object> code = new ArrayList<>();
        code.add(1);
        code.add(2);

        State s = new State(code);

        Object o = s.getCopy();
        assertTrue(o instanceof State);
        State copy = (State) o;

        assertEquals(s.getCode(), copy.getCode());
    }

    @Test
    void comparatorShouldCheckCodeEquality() {
        State s1 = new State();
        State s2 = new State();

        s1.getCode().add("a");
        s1.getCode().add("b");

        s2.getCode().add("a");
        s2.getCode().add("b");

        assertTrue(s1.comparator(s2),
                "comparator debe devolver true si los códigos son iguales");

        s2.getCode().set(1, "c");
        assertFalse(s1.comparator(s2),
                "comparator debe devolver false si los códigos difieren");
    }

    @Test
    void distanceShouldCountDifferentPositions() {
        State s1 = new State();
        State s2 = new State();

        s1.getCode().add("a");
        s1.getCode().add("b");
        s1.getCode().add("c");

        s2.getCode().add("a");
        s2.getCode().add("x");
        s2.getCode().add("y");

        double dist = s1.distance(s2);
        assertEquals(2.0, dist, 0.0001,
                "La distancia debe contar las posiciones distintas");
    }
}
