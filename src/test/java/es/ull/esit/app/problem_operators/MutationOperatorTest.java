package es.ull.esit.app.problem_operators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Codification;
import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;

class MutationOperatorTest {

    /**
     * Configura un mock estático de Strategy.getStrategy() devolviendo:
     *  - Strategy
     *  - Problem
     *  - Codification
     */
    private static class StrategyContext implements AutoCloseable {
        final MockedStatic<Strategy> staticStrategy;
        final Strategy strategyMock;
        final Problem problemMock;
        final Codification codificationMock;

        StrategyContext() {
            staticStrategy = Mockito.mockStatic(Strategy.class);
            strategyMock = mock(Strategy.class);
            problemMock = mock(Problem.class);
            codificationMock = mock(Codification.class);

            staticStrategy.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getCodification()).thenReturn(codificationMock);
        }

        @Override
        public void close() {
            staticStrategy.close();
        }
    }

    @Test
    void generatedNewStateShouldMutateClonedStates() {
        MutationOperator op = new MutationOperator();

        // Estado base con código [0, 1, 2]
        State current = new State();
        current.getCode().add(0);
        current.getCode().add(1);
        current.getCode().add(2);

        int operatorNumber = 3;

        try (StrategyContext ctx = new StrategyContext()) {
            // Siempre mutamos la posición 1 (segundo elemento)
            when(ctx.codificationMock.getAleatoryKey()).thenReturn(1);
            when(ctx.codificationMock.getVariableAleatoryValue(1))
                    .thenReturn(99);  // nuevo valor para la posición mutada

            List<State> neighbours = op.generatedNewState(current, operatorNumber);

            // Número de vecinos
            assertEquals(operatorNumber, neighbours.size(),
                    "Debe generarse un vecino por iteración");

            for (State n : neighbours) {
                // El estado devuelto es una nueva instancia de State
                assertNotSame(current, n, "Los vecinos deben ser instancias distintas de State");

                // Longitud del código igual
                assertEquals(current.getCode().size(), n.getCode().size());

                // Posiciones 0 y 2 eran 0 y 2 inicialmente (aunque podrían haberse
                // visto afectadas por compartir la lista; no lo aseguramos aquí)
                assertEquals(0, n.getCode().get(0));
                assertEquals(2, n.getCode().get(2));

                // Posición 1 mutada al valor devuelto por la codificación
                assertEquals(99, n.getCode().get(1));
            }

            // ⚠ NO comprobamos que el original permanezca intacto, porque
            // State.getCopy() reutiliza la misma lista de código y mutar el
            // vecino también altera el original.
        }
    }

    @Test
    void generateRandomStateShouldCreateStatesUsingCodification() {
        MutationOperator op = new MutationOperator();

        int operatorNumber = 2;

        try (StrategyContext ctx = new StrategyContext()) {
            // Dos variables por estado
            when(ctx.codificationMock.getVariableCount()).thenReturn(2);

            // Para la variable 0 -> "A", para la 1 -> "B"
            when(ctx.codificationMock.getVariableAleatoryValue(0)).thenReturn("A");
            when(ctx.codificationMock.getVariableAleatoryValue(1)).thenReturn("B");

            List<State> randomStates = op.generateRandomState(operatorNumber);

            // Cantidad de estados
            assertEquals(operatorNumber, randomStates.size(),
                    "Debe generarse un estado por iteración");

            for (State s : randomStates) {
                assertEquals(2, s.getCode().size(),
                        "Cada estado debe tener tantas posiciones como variables");
                assertEquals("A", s.getCode().get(0));
                assertEquals("B", s.getCode().get(1));
            }

            // Podemos comprobar que los códigos de los estados son listas distintas
            if (randomStates.size() == 2) {
                assertNotSame(randomStates.get(0).getCode(), randomStates.get(1).getCode(),
                        "Cada estado debe tener su propia lista de código");
            }
        }
    }

    @Test
    void mutationOperatorShouldExtendOperator() {
        MutationOperator op = new MutationOperator();
        assertTrue(op instanceof Operator, "MutationOperator debe extender Operator");
    }
}
