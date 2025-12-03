package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TowPointsMutationTest {

  @Test
  void mutationShouldSwapValuesAtTwoRandomPositions() {
    TowPointsMutation mutation = new TowPointsMutation();

    // Código inicial del individuo: [1, 2, 3]
    ArrayList<Object> code = new ArrayList<>();
    code.add(1); // índice 0
    code.add(2); // índice 1
    code.add(3); // índice 2

    State individual = mock(State.class);
    when(individual.getCode()).thenReturn(code);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // Forzamos índices aleatorios y valores:
      // key1 = 0, key2 = 2
      when(strategyMock.getProblem().getCodification().getAleatoryKey()).thenReturn(0, 2);

      // value1 asociado a key1=0, value2 asociado a key2=2
      when(strategyMock.getProblem().getCodification().getVariableAleatoryValue(0)).thenReturn(10);
      when(strategyMock.getProblem().getCodification().getVariableAleatoryValue(2)).thenReturn(30);

      // pm da igual, el operador siempre muta
      State result = mutation.mutation(individual, 0.0);

      // Devuelve el mismo individuo
      assertSame(individual, result);

      /*
       * En el código:
       * newind.getCode().set(key1, value2);
       * newind.getCode().set(key2, value1);
       *
       * Así que:
       * índice 0 pasa a 30
       * índice 2 pasa a 10
       * índice 1 se mantiene 2
       */
      assertEquals(30, code.get(0));
      assertEquals(2, code.get(1));
      assertEquals(10, code.get(2));
    }
  }
}
