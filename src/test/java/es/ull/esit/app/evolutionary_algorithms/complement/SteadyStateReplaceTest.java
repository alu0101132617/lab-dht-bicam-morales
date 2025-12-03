
package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SteadyStateReplaceTest {

  private ArrayList<Double> eval(double value) {
    ArrayList<Double> list = new ArrayList<>();
    list.add(value);
    return list;
  }

  @Test
  void replaceInMaximizationShouldReplaceWorstWhenCandidateIsBetterOrEqual() {
    SteadyStateReplace replaceOperator = new SteadyStateReplace();

    // Población con 3 individuos
    State s1 = mock(State.class); // peor (1.0)
    State s2 = mock(State.class); // 2.0
    State s3 = mock(State.class); // mejor (3.0)

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(2.0));
    when(s3.getEvaluation()).thenReturn(eval(3.0));

    List<State> population = new ArrayList<>();
    population.add(s1); // índice 0
    population.add(s2);
    population.add(s3);

    // Candidato mejor que el peor (>= 1.0)
    State candidate = mock(State.class);
    when(candidate.getEvaluation()).thenReturn(eval(2.5));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      // Act
      List<State> result = replaceOperator.replace(candidate, population);

      // Assert: se debe reemplazar el peor (s1) por el candidato
      assertSame(population, result, "Debe devolverse la misma lista de población");
      assertEquals(3, result.size());

      assertSame(candidate, result.get(0), "El peor individuo (s1) debe ser reemplazado por el candidato");
      assertSame(s2, result.get(1));
      assertSame(s3, result.get(2));
    }
  }

  @Test
  void replaceInMaximizationShouldNotReplaceWhenCandidateIsWorse() {
    SteadyStateReplace replaceOperator = new SteadyStateReplace();

    State s1 = mock(State.class); // peor (1.0)
    State s2 = mock(State.class); // 2.0
    State s3 = mock(State.class); // mejor (3.0)

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(2.0));
    when(s3.getEvaluation()).thenReturn(eval(3.0));

    List<State> population = new ArrayList<>();
    population.add(s1);
    population.add(s2);
    population.add(s3);

    // Candidato peor que el peor (0.5 < 1.0) -> no debe reemplazar
    State candidate = mock(State.class);
    when(candidate.getEvaluation()).thenReturn(eval(0.5));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      List<State> result = replaceOperator.replace(candidate, population);

      // La población debe permanecer igual
      assertSame(population, result);
      assertEquals(3, result.size());
      assertSame(s1, result.get(0));
      assertSame(s2, result.get(1));
      assertSame(s3, result.get(2));
    }
  }

  @Test
  void replaceInMinimizationShouldReplaceWorstWhenCandidateIsBetterOrEqual() {
    SteadyStateReplace replaceOperator = new SteadyStateReplace();

    // Población para minimización: cuanto más pequeño, mejor
    State s1 = mock(State.class); // mejor (1.0)
    State s2 = mock(State.class); // 2.0
    State s3 = mock(State.class); // peor (4.0)

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(2.0));
    when(s3.getEvaluation()).thenReturn(eval(4.0));

    List<State> population = new ArrayList<>();
    population.add(s1);
    population.add(s2);
    population.add(s3);

    // Candidato mejor o igual que el peor (por ejemplo 3.0 <= 4.0)
    State candidate = mock(State.class);
    when(candidate.getEvaluation()).thenReturn(eval(3.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

      List<State> result = replaceOperator.replace(candidate, population);

      // Debe reemplazarse el peor (s3) por el candidato
      assertSame(population, result);
      assertEquals(3, result.size());
      assertSame(s1, result.get(0));
      assertSame(s2, result.get(1));
      assertSame(candidate, result.get(2));
    }
  }

  @Test
  void minValueShouldReturnStateWithMinimumEvaluation() {
    SteadyStateReplace replaceOperator = new SteadyStateReplace();

    State s1 = mock(State.class);
    State s2 = mock(State.class);
    State s3 = mock(State.class);

    when(s1.getEvaluation()).thenReturn(eval(5.0));
    when(s2.getEvaluation()).thenReturn(eval(2.0)); // mínimo
    when(s3.getEvaluation()).thenReturn(eval(3.0));

    List<State> population = new ArrayList<>();
    population.add(s1);
    population.add(s2);
    population.add(s3);

    State minState = replaceOperator.minValue(population);

    assertSame(s2, minState, "minValue debe devolver el estado con evaluación mínima");
  }

  @Test
  void maxValueShouldReturnStateWithMaximumEvaluation() {
    SteadyStateReplace replaceOperator = new SteadyStateReplace();

    State s1 = mock(State.class);
    State s2 = mock(State.class);
    State s3 = mock(State.class);

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(4.0)); // máximo
    when(s3.getEvaluation()).thenReturn(eval(3.0));

    List<State> population = new ArrayList<>();
    population.add(s1);
    population.add(s2);
    population.add(s3);

    State maxState = replaceOperator.maxValue(population);

    assertSame(s2, maxState, "maxValue debe devolver el estado con evaluación máxima");
  }
}
