package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.factory_method.FactoryAcceptCandidate;
import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class MultiCaseSimulatedAnnealingTest {

  /** Utilidad para cambiar un int privado de instancia por reflexión. */
  private void setPrivateIntField(Object target, String fieldName, int value) throws Exception {
    Field f = MultiCaseSimulatedAnnealing.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    f.setInt(target, value);
  }

  /** Utilidad para leer un int privado de instancia por reflexión. */
  private int getPrivateIntField(Object target, String fieldName) throws Exception {
    Field f = MultiCaseSimulatedAnnealing.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    return f.getInt(target);
  }

  /** Utilidad para cambiar un double estático privado por reflexión (alpha). */
  private void setPrivateStaticDoubleField(Class<?> clazz, String fieldName, double value) throws Exception {
    Field f = clazz.getDeclaredField(fieldName);
    f.setAccessible(true);
    f.setDouble(null, value);
  }

  /** Crea un State con una evaluación simple de un solo valor. */
  private State createStateWithEval(double value) {
    State s = new State();
    ArrayList<Double> eval = new ArrayList<>();
    eval.add(value);
    s.setEvaluation(eval); // setEvaluation probablemente espera ArrayList<Double>
    return s;
  }

  @Test
  void constructorShouldInitializeFieldsCorrectly() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, sa.getType(),
        "El tipo por defecto debe ser MultiCaseSimulatedAnnealing");
    assertEquals(GeneratorType.MultiCaseSimulatedAnnealing, sa.getTypeGenerator(),
        "getTypeGenerator debe devolver el mismo tipo");

    assertEquals(50.0f, sa.getWeight(), 0.0001,
        "El peso inicial debe ser 50");

    int[] better = sa.getListCountBetterGender();
    int[] gender = sa.getListCountGender();
    float[] trace = sa.getTrace();

    assertNotNull(better);
    assertNotNull(gender);
    assertNotNull(trace);

    assertEquals(10, better.length);
    assertEquals(10, gender.length);
    assertEquals(1_200_000, trace.length);

    // trace[0] se inicializa con el peso
    assertEquals(50.0f, trace[0], 0.0001);
    // Los contadores se inicializan a cero
    assertEquals(0, better[0]);
    assertEquals(0, gender[0]);
  }

  @Test
  void generateShouldCallCandidateValueWithEmptyNeighborhoodWhenNoProblemOrReference()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException, InvocationTargetException,
             NoSuchMethodException, NoSuchFieldException {

    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    // Inyectamos un CandidateValue mock
    CandidateValue candidateValueMock = mock(CandidateValue.class);

    Field f = MultiCaseSimulatedAnnealing.class.getDeclaredField("candidateValue");
    f.setAccessible(true);
    try {
      f.set(sa, candidateValueMock);
    } catch (IllegalAccessException e) {
      fail(e);
    }

    // Mock estático de Strategy -> Problem nulo
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      when(strategyMock.getProblem()).thenReturn(null);

      State expected = new State();
      when(candidateValueMock.stateCandidate(
          any(), any(), any(), anyInt(), anyList()))
          .thenReturn(expected);

      State result = sa.generate(1);

      assertSame(expected, result, "generate debe devolver el estado devuelto por CandidateValue");
      // Verificamos que se llamó con neighbourhood vacío
      verify(candidateValueMock).stateCandidate(
          isNull(), // stateReferenceSA == null
          eq(CandidateType.RandomCandidate),
          eq(StrategyType.NORMAL),
          eq(1),
          anyList()
      );
    }
  }

  @Test
  void generateShouldUseNeighborhoodWhenProblemAndReferencePresent()
      throws Exception {

    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();
    State ref = new State();
    sa.setStateRef(ref);

    // Inyectamos el CandidateValue mock
    CandidateValue candidateValueMock = mock(CandidateValue.class);

    Field f = MultiCaseSimulatedAnnealing.class.getDeclaredField("candidateValue");
    f.setAccessible(true);
    f.set(sa, candidateValueMock);

    // Mock profundo de Problem + Operator vía RETURNS_DEEP_STUBS
    Problem problemMock = mock(Problem.class, Mockito.RETURNS_DEEP_STUBS);
    List<State> neigh = new ArrayList<>();
    neigh.add(new State());
    when(problemMock.getOperator().generatedNewState(ref, 2)).thenReturn(neigh);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      State expected = new State();
      when(candidateValueMock.stateCandidate(
          any(), any(), any(), anyInt(), anyList()))
          .thenReturn(expected);

      State result = sa.generate(2);

      assertSame(expected, result);
      verify(problemMock.getOperator()).generatedNewState(ref, 2);
      verify(candidateValueMock).stateCandidate(
          eq(ref),
          eq(CandidateType.RandomCandidate),
          eq(StrategyType.NORMAL),
          eq(2),
          eq(neigh)
      );
    }
  }

  @Test
  void referenceSettersAndGettersShouldWork() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    assertNull(sa.getReference(), "Por defecto la referencia es null");

    State s = new State();
    sa.setStateRef(s);
    assertSame(s, sa.getReference());

    State s2 = new State();
    sa.setInitialReference(s2);
    assertSame(s2, sa.getReference(), "setInitialReference debe actualizar la referencia");
  }

  @Test
  void updateReferenceShouldAcceptAndUpdateTemperatureWhenIterationMatches()
      throws Exception {

    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();
    State initialRef = new State();
    sa.setStateRef(initialRef);
    State candidateState = new State();

    // Preparamos temperatura y alpha
    MultiCaseSimulatedAnnealing.setTinitial(10.0);
    setPrivateStaticDoubleField(MultiCaseSimulatedAnnealing.class, "alpha", 0.5);

    // countIterationsT = 5
    setPrivateIntField(sa, "countIterationsT", 5);

    try (MockedConstruction<FactoryAcceptCandidate> factoryConstruction =
             Mockito.mockConstruction(FactoryAcceptCandidate.class,
                 (mockFactory, context) -> {
                   AcceptableCandidate accMock = mock(AcceptableCandidate.class);
                   when(mockFactory.createAcceptCandidate(any(AcceptType.class)))
                       .thenReturn(accMock);
                   when(accMock.acceptCandidate(any(), any())).thenReturn(true);
                 })) {

      sa.updateReference(candidateState, 5);
    }

    // La referencia debe haberse actualizado (copy del candidato o similar)
    assertNotNull(sa.getReference(), "La referencia no debe ser null tras aceptar");
    assertNotSame(initialRef, sa.getReference(),
        "La referencia debería haber cambiado respecto a la inicial");

    // Temperatura debe haberse actualizado a 10 * 0.5
    assertEquals(5.0, MultiCaseSimulatedAnnealing.getTinitial(), 1e-6);

    // countIterationsT debe haberse incrementado (5 + 5 = 10)
    int updatedCount = getPrivateIntField(sa, "countIterationsT");
    assertEquals(10, updatedCount);
  }

  @Test
  void updateReferenceShouldNotChangeReferenceOrTemperatureWhenRejectedOrIterationDifferent()
      throws Exception {

    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();
    State initialRef = new State();
    sa.setStateRef(initialRef);
    State candidateState = new State();

    // Temperatura inicial
    MultiCaseSimulatedAnnealing.setTinitial(20.0);
    setPrivateStaticDoubleField(MultiCaseSimulatedAnnealing.class, "alpha", 0.5);

    // countIterationsT = 5, pero pasamos 3 -> no debe actualizar T
    setPrivateIntField(sa, "countIterationsT", 5);

    try (MockedConstruction<FactoryAcceptCandidate> factoryConstruction =
             Mockito.mockConstruction(FactoryAcceptCandidate.class,
                 (mockFactory, context) -> {
                   AcceptableCandidate accMock = mock(AcceptableCandidate.class);
                   when(mockFactory.createAcceptCandidate(any(AcceptType.class)))
                       .thenReturn(accMock);
                   when(accMock.acceptCandidate(any(), any())).thenReturn(false);
                 })) {

      sa.updateReference(candidateState, 3);
    }

    // Referencia no debe cambiar
    assertSame(initialRef, sa.getReference(), "La referencia debe mantenerse al rechazar");

    // Temperatura no cambia porque countIterationsCurrent != countIterationsT
    assertEquals(20.0, MultiCaseSimulatedAnnealing.getTinitial(), 1e-6);

    int updatedCount = getPrivateIntField(sa, "countIterationsT");
    assertEquals(5, updatedCount, "countIterationsT debe mantenerse");
  }

  @Test
  void getReferenceListShouldReturnCopyAndAppendOnEachCall() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    // Con referencia nula no se añade nada
    List<State> list1 = sa.getReferenceList();
    assertTrue(list1.isEmpty(), "Con referencia null la lista debe estar vacía");

    State ref = new State();
    sa.setStateRef(ref);

    List<State> list2 = sa.getReferenceList();
    assertEquals(1, list2.size(), "Primera llamada con referencia añade un elemento");

    List<State> list3 = sa.getReferenceList();
    assertEquals(2, list3.size(), "Segunda llamada vuelve a añadir la referencia");

    // Cada llamada devuelve una copia de la lista interna
    assertNotSame(list2, list3, "getReferenceList debe devolver una nueva lista");
  }

  @Test
  void getSonListShouldReturnEmptyList() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();
    List<State> sons = sa.getSonList();
    assertNotNull(sons);
    assertTrue(sons.isEmpty(), "MultiCaseSimulatedAnnealing no mantiene lista de hijos explícita");
  }

  @Test
  void awardUpdateRefShouldReturnFalseWhenReferenceOrCandidateNull() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    State candidate = createStateWithEval(10.0);
    // reference null
    assertFalse(sa.awardUpdateREF(candidate));

    sa.setStateRef(createStateWithEval(5.0));
    // candidate null
    assertFalse(sa.awardUpdateREF(null));
  }

  @Test
  void awardUpdateRefShouldReturnFalseWhenEvaluationsMissing() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    State ref = new State();
    State cand = new State();
    // Evaluaciones null
    sa.setStateRef(ref);
    assertFalse(sa.awardUpdateREF(cand));

    // Evaluaciones vacías
    ref.setEvaluation(new ArrayList<>());
    cand.setEvaluation(new ArrayList<>());
    assertFalse(sa.awardUpdateREF(cand));
  }

  @Test
  void awardUpdateRefShouldUseFallbackWhenProblemIsNull() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    State ref = createStateWithEval(5.0);
    State cand = createStateWithEval(7.0);
    sa.setStateRef(ref);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      when(strategyMock.getProblem()).thenReturn(null);

      assertTrue(sa.awardUpdateREF(cand),
          "Si el problema es null debe comportarse como maximización por defecto");
    }
  }

  @Test
  void awardUpdateRefShouldCompareCorrectlyForMaxAndMin() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    State ref = createStateWithEval(5.0);
    State better = createStateWithEval(7.0);
    State worse = createStateWithEval(3.0);
    sa.setStateRef(ref);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      // Caso MAXIMIZAR
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      assertTrue(sa.awardUpdateREF(better),
          "En MAXIMIZAR un valor mayor debe considerarse mejora");
      assertFalse(sa.awardUpdateREF(worse),
          "En MAXIMIZAR un valor menor no debe considerarse mejora");

      // Caso MINIMIZAR
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

      assertTrue(sa.awardUpdateREF(worse),
          "En MINIMIZAR un valor menor debe considerarse mejora");
      assertFalse(sa.awardUpdateREF(better),
          "En MINIMIZAR un valor mayor no debe considerarse mejora");
    }
  }

  @Test
  void weightAndTypeGeneratorSettersShouldWork() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    sa.setWeight(80.0f);
    assertEquals(80.0f, sa.getWeight(), 0.0001);

    sa.setTypeGenerator(GeneratorType.GeneticAlgorithm);
    assertEquals(GeneratorType.GeneticAlgorithm, sa.getTypeGenerator());
    assertEquals(GeneratorType.GeneticAlgorithm, sa.getType());
  }

  @Test
  void staticTemperatureAccessorsShouldWork() {
    MultiCaseSimulatedAnnealing.setTfinal(123.45);
    assertEquals(123.45, MultiCaseSimulatedAnnealing.tfinal(), 1e-6);

    MultiCaseSimulatedAnnealing.setTinitial(10.0);
    assertEquals(10.0, MultiCaseSimulatedAnnealing.getTinitial(), 1e-6);
  }

  @Test
  void listAccessorsShouldReturnInternalArrays() {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();
    int[] better = sa.getListCountBetterGender();
    int[] gender = sa.getListCountGender();
    float[] trace = sa.getTrace();

    assertNotNull(better);
    assertNotNull(gender);
    assertNotNull(trace);
  }

  @Test
  void typeAcceptationAndCandidateAreCorrectByDefault() throws Exception {
    MultiCaseSimulatedAnnealing sa = new MultiCaseSimulatedAnnealing();

    Field acceptTypeField = MultiCaseSimulatedAnnealing.class.getDeclaredField("typeAcceptation");
    acceptTypeField.setAccessible(true);
    AcceptType acceptType = (AcceptType) acceptTypeField.get(sa);
    assertEquals(AcceptType.AcceptMulticase, acceptType);

    Field strategyField = MultiCaseSimulatedAnnealing.class.getDeclaredField("strategy");
    strategyField.setAccessible(true);
    StrategyType strategyType = (StrategyType) strategyField.get(sa);
    assertEquals(StrategyType.NORMAL, strategyType);

    Field candidateTypeField = MultiCaseSimulatedAnnealing.class.getDeclaredField("typeCandidate");
    candidateTypeField.setAccessible(true);
    CandidateType candidateType = (CandidateType) candidateTypeField.get(sa);
    assertEquals(CandidateType.RandomCandidate, candidateType);
  }
}

