package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DominanceTest {

    // Helper para crear evaluaciones (IMPORTANTE: ArrayList<Double>)
    private ArrayList<Double> eval(double... values) {
        ArrayList<Double> list = new ArrayList<>();
        for (double v : values) {
            list.add(v);
        }
        return list;
    }

    /**
     * Configura Strategy.getStrategy() con:
     * - Problem con typeProblem
     * - Generator con un tipo cualquiera (no MultiobjectiveHillClimbingDistance)
     */
    private void prepareStrategy(Strategy strategyMock, ProblemType typeProblem) throws Exception {
        Problem problemMock = mock(Problem.class);
        when(strategyMock.getProblem()).thenReturn(problemMock);
        when(problemMock.getTypeProblem()).thenReturn(typeProblem);

        Generator generatorMock = mock(Generator.class);
        // Elegimos un tipo distinto de MultiobjectiveHillClimbingDistance para
        // no ejecutar distanceCalculateAdd en estos tests.
        when(generatorMock.getType()).thenReturn(GeneratorType.DistributionEstimationAlgorithm);

        Field genField = Strategy.class.getDeclaredField("generator");
        genField.setAccessible(true);
        genField.set(strategyMock, generatorMock);
    }

    // ------------------- Tests de dominance(...) -------------------

    @Test
    void dominanceShouldBeTrueInMaximizationWhenXIsBetterAndNotWorse() throws Exception {
        Dominance dominance = new Dominance();

        State x = mock(State.class);
        State y = mock(State.class);

        // X = (2, 2), Y = (1, 2) -> X domina a Y en maximización
        when(x.getEvaluation()).thenReturn(eval(2.0, 2.0));
        when(y.getEvaluation()).thenReturn(eval(1.0, 2.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MAXIMIZAR);

            boolean result = dominance.dominance(x, y);

            assertTrue(result, "En maximización, X debería dominar a Y cuando es mejor en al menos un objetivo y no peor en ninguno");
        }
    }

    @Test
    void dominanceShouldBeFalseInMaximizationWhenXIsBetterInOneAndWorseInAnother() throws Exception {
        Dominance dominance = new Dominance();

        State x = mock(State.class);
        State y = mock(State.class);

        // X = (2, 0), Y = (1, 1) -> X es mejor en el primer objetivo pero peor en el segundo
        when(x.getEvaluation()).thenReturn(eval(2.0, 0.0));
        when(y.getEvaluation()).thenReturn(eval(1.0, 1.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MAXIMIZAR);

            boolean result = dominance.dominance(x, y);

            assertFalse(result, "En maximización, X no debe dominar a Y si es peor en algún objetivo");
        }
    }

    @Test
    void dominanceShouldBeTrueInMinimizationWhenXIsSmallerAndNotWorse() throws Exception {
        Dominance dominance = new Dominance();

        State x = mock(State.class);
        State y = mock(State.class);

        // Minimización: X = (1, 2), Y = (2, 2) -> X domina a Y
        when(x.getEvaluation()).thenReturn(eval(1.0, 2.0));
        when(y.getEvaluation()).thenReturn(eval(2.0, 2.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MINIMIZAR);

            boolean result = dominance.dominance(x, y);

            assertTrue(result, "En minimización, X debe dominar a Y cuando es menor en al menos un objetivo y no mayor en ninguno");
        }
    }

    @Test
    void dominanceShouldBeFalseWhenAllObjectivesAreEqual() throws Exception {
        Dominance dominance = new Dominance();

        State x = mock(State.class);
        State y = mock(State.class);

        // Igualdad total -> nadie domina a nadie
        when(x.getEvaluation()).thenReturn(eval(1.0, 1.0));
        when(y.getEvaluation()).thenReturn(eval(1.0, 1.0));

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MAXIMIZAR);

            boolean result = dominance.dominance(x, y);

            assertFalse(result, "Si todas las evaluaciones son iguales, X no debería dominar a Y");
        }
    }

    // ------------------- Tests de listDominance(...) -------------------

    @Test
    void listDominanceShouldAddSolutionWhenItDominatesAndIsNotDuplicate() throws Exception {
        Dominance dominance = new Dominance();

        State solutionX = mock(State.class);
        State listElement = mock(State.class);

        // solutionX domina al elemento de la lista
        when(solutionX.getEvaluation()).thenReturn(eval(2.0, 2.0));
        when(listElement.getEvaluation()).thenReturn(eval(1.0, 2.0));

        // copy y comparator
        when(solutionX.copy()).thenReturn(solutionX);
        when(solutionX.comparator(any(State.class))).thenReturn(false); // no es igual a nadie

        List<State> list = new ArrayList<>();
        list.add(listElement);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MAXIMIZAR);

            boolean added = dominance.listDominance(solutionX, list);

            assertTrue(added, "Cuando X domina y no es duplicado, debe añadirse a la lista");
            assertEquals(1, list.size(), "La lista debe contener solo a la nueva solución tras eliminar la dominada");
            assertSame(solutionX, list.get(0), "La solución añadida debe ser la copia de solutionX");
        }
    }

    @Test
    void listDominanceShouldReturnFalseWhenSolutionIsDominatedByList() throws Exception {
        Dominance dominance = new Dominance();

        State solutionX = mock(State.class);
        State dominating = mock(State.class);

        // Elemento de la lista domina a solutionX
        when(solutionX.getEvaluation()).thenReturn(eval(1.0, 1.0));
        when(dominating.getEvaluation()).thenReturn(eval(2.0, 2.0));

        List<State> list = new ArrayList<>();
        list.add(dominating);

        // comparator no importa en este caso porque terminamos antes
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MAXIMIZAR);

            boolean added = dominance.listDominance(solutionX, list);

            assertFalse(added, "Si alguna solución de la lista domina a X, X no debe añadirse");
            assertEquals(1, list.size(), "La lista debe permanecer con el mismo elemento");
            assertSame(dominating, list.get(0));
        }
    }

    @Test
    void listDominanceShouldNotAddSolutionWhenItIsDuplicate() throws Exception {
        Dominance dominance = new Dominance();

        State solutionX = mock(State.class);
        State existing = mock(State.class);

        // Igualdad total en evaluaciones -> nadie domina a nadie
        when(solutionX.getEvaluation()).thenReturn(eval(2.0, 2.0));
        when(existing.getEvaluation()).thenReturn(eval(2.0, 2.0));

        // comparator dice que solutionX es igual a existing
        when(solutionX.comparator(existing)).thenReturn(true);

        List<State> list = new ArrayList<>();
        list.add(existing);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            prepareStrategy(strategyMock, ProblemType.MAXIMIZAR);

            boolean added = dominance.listDominance(solutionX, list);

            assertFalse(added, "Si la solución ya existe en la lista, no debe añadirse de nuevo");
            assertEquals(1, list.size(), "La lista debe seguir con un solo elemento");
            assertSame(existing, list.get(0));
        }
    }
}
