package es.ull.esit.app.problem.extension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.ObjetiveFunction;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class MultiObjetivoPuroTest {

  @Test
  void evaluationStateShouldComputeAllBranchCombinationsAndSetEvaluation() {
    // --- mocks base ---
    State state = mock(State.class);

    // Creamos 4 funciones para cubrir todas las ramas
    ObjetiveFunction fMax1 = mock(ObjetiveFunction.class);
    when(fMax1.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
    when(fMax1.evaluation(state)).thenReturn(0.20);

    ObjetiveFunction fMin1 = mock(ObjetiveFunction.class);
    // "MINIMIZAR" normalmente existe; si tu enum no tiene MINIMIZAR, cambia a la constante correcta.
    when(fMin1.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
    when(fMin1.evaluation(state)).thenReturn(0.30);

    ObjetiveFunction fMax2 = mock(ObjetiveFunction.class);
    when(fMax2.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
    when(fMax2.evaluation(state)).thenReturn(0.40);

    ObjetiveFunction fMin2 = mock(ObjetiveFunction.class);
    when(fMin2.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
    when(fMin2.evaluation(state)).thenReturn(0.50);

    List<ObjetiveFunction> functions = List.of(fMax1, fMin1, fMax2, fMin2);

    // Mock de Strategy.getStrategy().getProblem().getFunction()/getTypeProblem()
    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);

    // IMPORTANTE: romper cadena con variables intermedias para evitar "UnfinishedStubbing"
    var problemMock = strategyMock.getProblem();
    when(problemMock.getFunction()).thenReturn(functions);

    // --- Ejecutamos 2 veces para cubrir ambos tipos del problema: MAX y MIN ---
    MultiObjetivoPuro method = new MultiObjetivoPuro();

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      // ========== Caso A: Problema MAXIMIZAR ==========
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      method.evaluationState(state);

      ArgumentCaptor<ArrayList<Double>> capA = ArgumentCaptor.forClass(ArrayList.class);
      verify(state, times(1)).setEvaluation(capA.capture());

      ArrayList<Double> evalA = capA.getValue();
      assertEquals(4, evalA.size());

      // Problema MAX:
      // fMax1 (MAX) => 0.20
      // fMin1 (MIN) => 1 - 0.30 = 0.70
      // fMax2 (MAX) => 0.40
      // fMin2 (MIN) => 1 - 0.50 = 0.50
      assertEquals(0.20, evalA.get(0), 1e-9);
      assertEquals(0.70, evalA.get(1), 1e-9);
      assertEquals(0.40, evalA.get(2), 1e-9);
      assertEquals(0.50, evalA.get(3), 1e-9);

      // reset interacciones para el segundo caso
      clearInvocations(state);

      // ========== Caso B: Problema MINIMIZAR ==========
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

      method.evaluationState(state);

      ArgumentCaptor<ArrayList<Double>> capB = ArgumentCaptor.forClass(ArrayList.class);
      verify(state, times(1)).setEvaluation(capB.capture());

      ArrayList<Double> evalB = capB.getValue();
      assertEquals(4, evalB.size());

      // Problema MIN:
      // fMax1 (MAX) => 1 - 0.20 = 0.80
      // fMin1 (MIN) => 0.30
      // fMax2 (MAX) => 1 - 0.40 = 0.60
      // fMin2 (MIN) => 0.50
      assertEquals(0.80, evalB.get(0), 1e-9);
      assertEquals(0.30, evalB.get(1), 1e-9);
      assertEquals(0.60, evalB.get(2), 1e-9);
      assertEquals(0.50, evalB.get(3), 1e-9);
    }
  }
}
