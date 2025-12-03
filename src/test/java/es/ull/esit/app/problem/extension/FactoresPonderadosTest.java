package es.ull.esit.app.problem.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.ObjetiveFunction;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class FactoresPonderadosTest {

    private State createState() {
        State s = new State();
        s.getCode().add("x");
        return s;
    }

    @Test
    void evaluationStateShouldComputeWeightedSumForMaxProblem() {
        FactoresPonderados fp = new FactoresPonderados();
        State state = createState();

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            // Objetivos
            ObjetiveFunction f1 = mock(ObjetiveFunction.class);
            ObjetiveFunction f2 = mock(ObjetiveFunction.class);

            when(f1.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            when(f2.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            when(f1.getWeight()).thenReturn(2.0f);
            when(f2.getWeight()).thenReturn(3.0f);

            when(f1.evaluation(state)).thenReturn(0.5);
            when(f2.evaluation(state)).thenReturn(0.2);

            List<ObjetiveFunction> funcs = new ArrayList<>();
            funcs.add(f1);
            funcs.add(f2);

            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            when(problemMock.getFunction()).thenReturn(funcs);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            fp.evaluationState(state);

            // Para MAXIMIZAR:
            // f1 (MAX): term = 0.5 * 2 = 1.0
            // f2 (MIN): term = (1 - 0.2) * 3 = 0.8 * 3 = 2.4
            // total = 3.4
            assertEquals(1, state.getEvaluation().size());
            assertEquals(3.4, state.getEvaluation().get(0), 1e-6);
        }
    }

    @Test
    void evaluationStateShouldComputeWeightedSumForMinProblem() {
        FactoresPonderados fp = new FactoresPonderados();
        State state = createState();

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            ObjetiveFunction f1 = mock(ObjetiveFunction.class);
            ObjetiveFunction f2 = mock(ObjetiveFunction.class);

            when(f1.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            when(f2.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            when(f1.getWeight()).thenReturn(1.0f);
            when(f2.getWeight()).thenReturn(2.0f);

            when(f1.evaluation(state)).thenReturn(0.1);
            when(f2.evaluation(state)).thenReturn(0.3);

            List<ObjetiveFunction> funcs = new ArrayList<>();
            funcs.add(f1);
            funcs.add(f2);

            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
            when(problemMock.getFunction()).thenReturn(funcs);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            fp.evaluationState(state);

            // Para MINIMIZAR:
            // f1 (MAX): term = (1 - 0.1) * 1 = 0.9
            // f2 (MIN): term = 0.3 * 2 = 0.6
            // total = 1.5
            assertEquals(1, state.getEvaluation().size());
            assertEquals(1.5, state.getEvaluation().get(0), 1e-6);
        }
    }
}
