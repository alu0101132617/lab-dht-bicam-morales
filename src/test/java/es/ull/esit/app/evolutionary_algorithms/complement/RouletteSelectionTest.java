package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouletteSelectionTest {

    @Test
    void selectionShouldReturnParentsFromPopulation() {
        RouletteSelection selectionOperator = new RouletteSelection();

        // Creamos 3 individuos con diferente "peso" (fitness)
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        // IMPORTANTE: usar ArrayList<Double> para que encaje con la firma real
        ArrayList<Double> eval1 = new ArrayList<>();
        eval1.add(1.0); // peso 1

        ArrayList<Double> eval2 = new ArrayList<>();
        eval2.add(2.0); // peso 2

        ArrayList<Double> eval3 = new ArrayList<>();
        eval3.add(3.0); // peso 3

        when(s1.getEvaluation()).thenReturn(eval1);
        when(s2.getEvaluation()).thenReturn(eval2);
        when(s3.getEvaluation()).thenReturn(eval3);

        List<State> population = new ArrayList<>();
        population.add(s1);
        population.add(s2);
        population.add(s3);

        // Ejecutamos la selección
        List<State> parents = selectionOperator.selection(population, 0);

        // Se deben seleccionar tantos padres como individuos en la población
        assertEquals(population.size(), parents.size(),
                "Debe seleccionarse un padre por individuo de la población");

        // Todos los padres deben formar parte de la población original
        for (State parent : parents) {
            assertTrue(population.contains(parent),
                    "Cada padre seleccionado debe pertenecer a la población original");
        }
    }

    @Test
    void selectionOnEmptyListShouldReturnEmptyList() {
        RouletteSelection selectionOperator = new RouletteSelection();

        List<State> emptyPopulation = new ArrayList<>();

        List<State> parents = selectionOperator.selection(emptyPopulation, 0);

        assertNotNull(parents, "La lista resultante no debe ser null");
        assertTrue(parents.isEmpty(),
                "Si la población está vacía, la lista de padres también debe estar vacía");
    }
}
