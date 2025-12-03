package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GenerationalReplaceTest {

    @Test
    void replaceShouldRemoveFirstAndAddCandidateAtEnd()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        GenerationalReplace replaceOperator = new GenerationalReplace();

        // Creamos una población con dos estados
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State candidate = mock(State.class);

        List<State> population = new ArrayList<>();
        population.add(s1); // índice 0 (el que debe eliminarse)
        population.add(s2); // índice 1

        // Act
        List<State> result = replaceOperator.replace(candidate, population);

        // Assert: se devuelve la misma lista modificada
        assertSame(population, result, "replace debe devolver la misma lista que recibe");

        // El primer elemento (s1) debe haberse eliminado
        assertEquals(2, result.size(), "El tamaño de la población debe seguir siendo 2");
        assertSame(s2, result.get(0), "El segundo individuo original debe pasar a ser el primero");
        assertSame(candidate, result.get(1), "El candidato debe añadirse al final de la lista");
        assertFalse(result.contains(s1), "El primer individuo original debe eliminarse de la lista");
    }

    @Test
    void replaceOnSingleElementListShouldReplaceOnlyElement()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        GenerationalReplace replaceOperator = new GenerationalReplace();

        State original = mock(State.class);
        State candidate = mock(State.class);

        List<State> population = new ArrayList<>();
        population.add(original);

        // Act
        List<State> result = replaceOperator.replace(candidate, population);

        // Assert: la lista mantiene tamaño 1, pero con el candidato
        assertSame(population, result);
        assertEquals(1, result.size());
        assertSame(candidate, result.get(0));
        assertFalse(result.contains(original));
    }
}
