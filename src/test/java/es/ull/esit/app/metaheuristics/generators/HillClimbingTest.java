package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


class HillClimbingTest {

  /** Helper: crea un estado con evaluación mono-objetivo. */
  private State createStateWithEval(double value) {
    State s = new State();
    ArrayList<Double> eval = new ArrayList<>();
    eval.add(value);
    s.setEvaluation(eval);
    return s;
  }

  @Test
  void constructorShouldConfigureCandidateTypeDependingOnProblemTypeAndInitializeFields() throws Exception {
    HillClimbing hcMax;
    HillClimbing hcMin;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      // Primera llamada → MAX, segunda llamada → MIN
      when(problemMock.getTypeProblem())
          .thenReturn(ProblemType.MAXIMIZAR, ProblemType.MINIMIZAR);

      hcMax = new HillClimbing();
      hcMin = new HillClimbing();

      Field typeCandField = HillClimbing.class.getDeclaredField("typeCandidate");
      typeCandField.setAccessible(true);
      CandidateType typeMax = (CandidateType) typeCandField.get(hcMax);
      CandidateType typeMin = (CandidateType) typeCandField.get(hcMin);

      assertEquals(CandidateType.GreaterCandidate, typeMax);
      assertEquals(CandidateType.SmallerCandidate, typeMin);

      assertEquals(GeneratorType.HillClimbing, hcMax.getType());
      assertEquals(GeneratorType.HillClimbing, hcMax.getGeneratorType());

      assertEquals(50.0f, hcMax.getWeight(), 0.0001f);
      assertEquals(50.0f, hcMin.getWeight(), 0.0001f);

      float[] trace = hcMax.getTrace();
      assertNotNull(trace);
      assertEquals(50.0f, trace[0], 0.0001f);

      int[] better = hcMax.getListCountBetterGender();
      int[] gender = hcMax.getListCountGender();
      assertNotNull(better);
      assertNotNull(gender);
      assertEquals(10, better.length);
      assertEquals(10, gender.length);
    }
  }

  @Test
  void generateShouldUseCandidateValueAndReturnCandidate() throws Exception {
    // Mock estático de Strategy.getStrategy() y todo lo que el constructor necesita
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // Problem y Operator que usará HillClimbing
      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR); // para el constructor
      Operator operatorMock = mock(Operator.class);
      when(problemMock.getOperator()).thenReturn(operatorMock);

      // AHORA sí podemos crear HillClimbing sin NPE en el constructor
      HillClimbing hc = new HillClimbing();

      // Inyectamos una referencia inicial
      State reference = mock(State.class);
      Field refField = HillClimbing.class.getDeclaredField("stateReferenceHC");
      refField.setAccessible(true);
      refField.set(hc, reference);

      // Preparamos el vecindario que devolverá el operador
      List<State> neighborhood = new ArrayList<>();
      when(operatorMock.generatedNewState(reference, 1)).thenReturn(neighborhood);

      // Mock de CandidateValue
      CandidateValue candidateValueMock = mock(CandidateValue.class);
      State expectedCandidate = mock(State.class);
      when(candidateValueMock.stateCandidate(
          eq(reference),
          any(), // CandidateType
          any(), // StrategyType
          eq(1),
          eq(neighborhood))).thenReturn(expectedCandidate);

      // Inyectamos el candidateValue correcto (ojo al nombre con V mayúscula)
      Field candidateField = HillClimbing.class.getDeclaredField("candidateValue");
      candidateField.setAccessible(true);
      candidateField.set(hc, candidateValueMock);

      // Ejecutamos generate
      State result = hc.generate(1);

      // Verificaciones
      assertSame(expectedCandidate, result);
      verify(candidateValueMock).stateCandidate(
          eq(reference),
          any(),
          any(),
          eq(1),
          eq(neighborhood));
    }
  }

  @Test
  void getReferenceListShouldIncludeReferenceAndReturnCopy() throws Exception {
    HillClimbing hc;
    State ref;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      hc = new HillClimbing();
      ref = new State();
      hc.setInitialReference(ref);
    }

    List<State> list1 = hc.getReferenceList();
    List<State> list2 = hc.getReferenceList();

    assertTrue(list1.contains(ref));
    assertTrue(list2.contains(ref));
    assertEquals(1, list1.size());
    assertEquals(1, list2.size());
    assertNotSame(list1, list2, "Debe devolver listas distintas (copias)");
  }

  @Test
  void getSonListShouldReturnEmptyList() throws Exception {
    HillClimbing hc;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      hc = new HillClimbing();
    }

    List<State> sons = hc.getSonList();
    assertNotNull(sons);
    assertTrue(sons.isEmpty(), "HillClimbing no mantiene hijos, debe devolver lista vacía");
  }

  @Test
  void awardUpdateREFShouldReturnTrueWhenCandidateBetterForMaximize() throws Exception {
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      HillClimbing hc = new HillClimbing();

      State ref = createStateWithEval(1.0);
      State better = createStateWithEval(2.0);
      State worse = createStateWithEval(0.5);

      hc.setInitialReference(ref);

      assertTrue(hc.awardUpdateREF(better),
          "En MAXIMIZAR un candidato mejor debe devolver true");
      assertFalse(hc.awardUpdateREF(worse),
          "En MAXIMIZAR un candidato peor debe devolver false");
    }
  }

  @Test
  void awardUpdateREFShouldReturnTrueWhenCandidateBetterForMinimize() throws Exception {
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

      HillClimbing hc = new HillClimbing();

      State ref = createStateWithEval(2.0);
      State better = createStateWithEval(1.0);
      State worse = createStateWithEval(3.0);

      hc.setInitialReference(ref);

      assertTrue(hc.awardUpdateREF(better),
          "En MINIMIZAR un candidato con menor evaluación debe devolver true");
      assertFalse(hc.awardUpdateREF(worse),
          "En MINIMIZAR un candidato peor debe devolver false");
    }
  }

  @Test
  void awardUpdateREFShouldReturnFalseWhenReferenceOrCandidateInvalid() throws Exception {
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      HillClimbing hc = new HillClimbing();

      // Referencia nula y candidato nulo
      assertFalse(hc.awardUpdateREF(null),
          "Si referencia o candidato son inválidos debe devolver false");

      // Referencia sin evaluación
      State ref = new State();
      State cand = new State();
      hc.setInitialReference(ref);
      assertFalse(hc.awardUpdateREF(cand),
          "Si las evaluaciones son nulas o vacías debe devolver false");
    }
  }

  @Test
  void weightSetAndGetShouldWork() throws Exception {
    HillClimbing hc;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      hc = new HillClimbing();
    }

    hc.setWeight(80.0f);
    assertEquals(80.0f, hc.getWeight(), 0.0001f);
  }

  @Test
  void generatorTypeGettersAndSettersShouldWork() throws Exception {
    HillClimbing hc;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      hc = new HillClimbing();
    }

    assertEquals(GeneratorType.HillClimbing, hc.getType());
    assertEquals(GeneratorType.HillClimbing, hc.getGeneratorType());

    hc.setGeneratorType(GeneratorType.TabuSearch);
    assertEquals(GeneratorType.TabuSearch, hc.getGeneratorType());
  }

  @Test
  void setTypeCandidateShouldChangeField() throws Exception {
    HillClimbing hc;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      hc = new HillClimbing();
    }

    hc.setTypeCandidate(CandidateType.SmallerCandidate);

    Field typeCandField = HillClimbing.class.getDeclaredField("typeCandidate");
    typeCandField.setAccessible(true);
    CandidateType type = (CandidateType) typeCandField.get(hc);

    assertEquals(CandidateType.SmallerCandidate, type);
  }

  @Test
  void setStateRefShouldUpdateReference() throws Exception {
    HillClimbing hc;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      hc = new HillClimbing();
    }

    State ref = new State();
    hc.setStateRef(ref);
    assertSame(ref, hc.getReference());
  }
}
