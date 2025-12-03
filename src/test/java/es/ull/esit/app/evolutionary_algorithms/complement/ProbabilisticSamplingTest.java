package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ProbabilisticSampling operator.
 */
class ProbabilisticSamplingTest {

    @Test
    void samplingShouldGenerateOffspringBasedOnParentsFrequencies() {
        ProbabilisticSampling sampler = new ProbabilisticSampling();

        // Padres: ambos con el mismo código [0,0,0]
        State father1 = mock(State.class);
        State father2 = mock(State.class);

        ArrayList<Object> code1 = new ArrayList<>();
        ArrayList<Object> code2 = new ArrayList<>();
        // 3 variables, todas con valor 0
        code1.add(0); code1.add(0); code1.add(0);
        code2.add(0); code2.add(0); code2.add(0);

        when(father1.getCode()).thenReturn(code1);
        when(father2.getCode()).thenReturn(code2);

        List<State> fathers = new ArrayList<>();
        fathers.add(father1);
        fathers.add(father2);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // Posibles valores: {0,1,2}
            when(strategyMock.getProblem().getPossibleValue()).thenReturn(3);
            // Para listState()
            when(strategyMock.getCountCurrent()).thenReturn(10);
            // Usado solo en el caso de fallback, pero lo dejamos definido
            when(strategyMock.getProblem().getCodification().getVariableCount()).thenReturn(3);

            int countInd = 2;

            // Act
            List<State> offspring = sampler.sampling(fathers, countInd);

            // Assert
            assertEquals(countInd, offspring.size(), "Debe generarse el número correcto de descendientes");

            for (State child : offspring) {
                assertNotNull(child.getCode(), "El código del descendiente no debe ser null");
                // Debe tener tantas variables como los padres (3)
                assertEquals(3, child.getCode().size(), "Cada descendiente debe tener 3 genes");

                // Como todos los padres tenían 0 y la frecuencia de 0 es la máxima,
                // cualquier número aleatorio 1..sum siempre cae en el primer valor posible (0).
                for (Object gene : child.getCode()) {
                    assertTrue(gene instanceof Integer, "Cada gen debe ser un Integer");
                    assertEquals(0, gene, "El valor esperado para cada gen es 0");
                }
            }
        }
    }

    @Test
    void samplingShouldFallbackWhenNoOccurrencesForPossibleValues() {
        ProbabilisticSampling sampler = new ProbabilisticSampling();

        // Solo un padre con valores que NO coinciden con los posibles (porque posibles = 0 elementos)
        State father = mock(State.class);
        ArrayList<Object> code = new ArrayList<>();
        code.add(7);
        code.add(8);
        when(father.getCode()).thenReturn(code);

        List<State> fathers = new ArrayList<>();
        fathers.add(father);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // possibleValue = 0 → possibleValues.length = 0 → sumOccurrences = 0
            when(strategyMock.getProblem().getPossibleValue()).thenReturn(0);
            // Para fallback: r.nextInt(variableCount * 10)
            when(strategyMock.getProblem().getCodification().getVariableCount()).thenReturn(2);
            // Para listState()
            when(strategyMock.getCountCurrent()).thenReturn(1);

            int countInd = 1;

            // Act
            List<State> offspring = sampler.sampling(fathers, countInd);

            // Assert
            assertEquals(1, offspring.size());
            State child = offspring.get(0);
            assertNotNull(child.getCode());
            // cantV = tamaño del código del padre (2 variables)
            assertEquals(2, child.getCode().size(), "El descendiente debe tener 2 genes");

            // Hemos forzado la ruta en la que no se encuentra valor (sum = 0),
            // así que se usan valores aleatorios de fallback.
            for (Object gene : child.getCode()) {
                assertTrue(gene instanceof Integer, "Los genes generados por fallback deben ser Integer");
            }
        }
    }

    @Test
    void listStateShouldCreateEmptyStatesWithCorrectSize() {
        ProbabilisticSampling sampler = new ProbabilisticSampling();

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            when(strategyMock.getCountCurrent()).thenReturn(99);

            int countInd = 3;

            // Act
            List<State> states = sampler.listState(countInd);

            // Assert
            assertEquals(countInd, states.size(), "Debe crearse el número correcto de estados");

            for (State st : states) {
                assertNotNull(st.getCode(), "El código de cada estado no debe ser null");
                assertTrue(st.getCode().isEmpty(), "El código de cada estado debe empezar vacío");
            }
        }
    }
}
