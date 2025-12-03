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

class TruncationSelectionTest {

  private ArrayList<Double> eval(double value) {
    ArrayList<Double> list = new ArrayList<>();
    list.add(value);
    return list;
  }

  @Test
  void orderBetterShouldSortDescendingByEvaluation() {
    TruncationSelection selection = new TruncationSelection();

    State s1 = mock(State.class);
    State s2 = mock(State.class);
    State s3 = mock(State.class);

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(3.0));
    when(s3.getEvaluation()).thenReturn(eval(2.0));

    List<State> list = new ArrayList<>();
    list.add(s1);
    list.add(s2);
    list.add(s3);

    List<State> ordered = selection.orderBetter(list);

    assertEquals(3, ordered.size());
    // Debe quedar: s2 (3.0), s3 (2.0), s1 (1.0)
    assertSame(s2, ordered.get(0));
    assertSame(s3, ordered.get(1));
    assertSame(s1, ordered.get(2));
  }

  @Test
  void ascOrderBetterShouldSortAscendingByEvaluation() {
    TruncationSelection selection = new TruncationSelection();

    State s1 = mock(State.class);
    State s2 = mock(State.class);
    State s3 = mock(State.class);

    when(s1.getEvaluation()).thenReturn(eval(3.0));
    when(s2.getEvaluation()).thenReturn(eval(1.0));
    when(s3.getEvaluation()).thenReturn(eval(2.0));

    List<State> list = new ArrayList<>();
    list.add(s1);
    list.add(s2);
    list.add(s3);

    List<State> ordered = selection.ascOrderBetter(list);

    assertEquals(3, ordered.size());
    // Debe quedar: s2 (1.0), s3 (2.0), s1 (3.0)
    assertSame(s2, ordered.get(0));
    assertSame(s3, ordered.get(1));
    assertSame(s1, ordered.get(2));
  }

  @Test
  void selectionInMaximizationShouldPickBestIndividuals() {
    TruncationSelection selection = new TruncationSelection();

    State s1 = mock(State.class); // 1.0
    State s2 = mock(State.class); // 4.0
    State s3 = mock(State.class); // 3.0
    State s4 = mock(State.class); // 2.0

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(4.0));
    when(s3.getEvaluation()).thenReturn(eval(3.0));
    when(s4.getEvaluation()).thenReturn(eval(2.0));

    List<State> population = new ArrayList<>();
    population.add(s1);
    population.add(s2);
    population.add(s3);
    population.add(s4);

    int truncation = 2;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      List<State> selected = selection.selection(population, truncation);

      // Deben seleccionarse los 2 mejores: s2 (4.0), s3 (3.0)
      assertEquals(truncation, selected.size());
      assertSame(s2, selected.get(0));
      assertSame(s3, selected.get(1));
    }
  }

  @Test
  void selectionInMinimizationShouldPickBestIndividualsAscending() {
    TruncationSelection selection = new TruncationSelection();

    State s1 = mock(State.class); // 1.0 (mejor)
    State s2 = mock(State.class); // 4.0
    State s3 = mock(State.class); // 3.0
    State s4 = mock(State.class); // 2.0

    when(s1.getEvaluation()).thenReturn(eval(1.0));
    when(s2.getEvaluation()).thenReturn(eval(4.0));
    when(s3.getEvaluation()).thenReturn(eval(3.0));
    when(s4.getEvaluation()).thenReturn(eval(2.0));

    List<State> population = new ArrayList<>();
    population.add(s1);
    population.add(s2);
    population.add(s3);
    population.add(s4);

    int truncation = 2;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

      List<State> selected = selection.selection(population, truncation);

      // En minimización, los mejores son los de menor evaluación: s1 (1.0) y s4 (2.0)
      assertEquals(truncation, selected.size());
      assertSame(s1, selected.get(0));
      assertSame(s4, selected.get(1));
    }
  }
}
