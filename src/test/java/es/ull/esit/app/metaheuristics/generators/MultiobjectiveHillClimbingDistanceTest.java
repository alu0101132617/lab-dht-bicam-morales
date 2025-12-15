package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import es.ull.esit.app.factory_interface.IFFactoryAcceptCandidate;
import es.ull.esit.app.factory_method.FactoryAcceptCandidate;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;

class MultiobjectiveHillClimbingDistanceTest {

  static class TestState extends State {
    private final int id;

    TestState(int id) {
      this.id = id;
    }

    int getId() {
      return id;
    }

    @Override
    public State copy() {
      return new TestState(id);
    }

    @Override
    public boolean comparator(State other) {
      if (!(other instanceof TestState ts))
        return false;
      return this.id == ts.id;
    }

    @Override
    public double distance(State other) {
      if (!(other instanceof TestState ts))
        return 0.0;
      return (double) Math.abs(this.id - ts.id);
    }

    @Override
    public String toString() {
      return "TestState(" + id + ")";
    }
  }

  @BeforeEach
  void resetStatics() {
    MultiobjectiveHillClimbingDistance.sizeNeighbors = 3;
    MultiobjectiveHillClimbingDistance.distanceSolution = new ArrayList<>();
  }

  private static void setField(Object target, String fieldName, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object getField(Object target, String fieldName) {
    try {
      Field f = target.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      return f.get(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object invokePrivate(Object target, String method, Class<?>[] types, Object[] args) {
    try {
      Method m = target.getClass().getDeclaredMethod(method, types);
      m.setAccessible(true);
      return m.invoke(target, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void constructorShouldSetDefaultsAndInitTrace() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();

    assertNotNull(getField(gen, "candidatevalue"));
    assertNotNull(getField(gen, "typeAcceptation"));
    assertNotNull(getField(gen, "strategy"));
    assertNotNull(getField(gen, "typeCandidate"));

    assertEquals(50.0f, gen.getWeight(), 0.0001);
    float[] trace = gen.getTrace();
    assertEquals(1, trace.length);
    assertEquals(50.0f, trace[0], 0.0001);

    assertEquals(GeneratorType.MultiobjectiveHillClimbingDistance, gen.getType());
    assertEquals(GeneratorType.MultiobjectiveHillClimbingDistance, gen.getGeneratorType());
    assertTrue(gen.getSonList().isEmpty());
  }

  @Test
  void setWeightShouldAppendToTrace() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    gen.setWeight(10f);
    gen.setWeight(20f);
    assertArrayEquals(new float[] { 50f, 10f, 20f }, gen.getTrace(), 0.0001f);
  }

  @Test
  void awardUpdateREFShouldReturnFalseOnNull() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    assertFalse(gen.awardUpdateREF(null));
  }

  @Test
  void awardUpdateREFShouldUpdateReferenceAndTrace() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    TestState s = new TestState(7);

    assertTrue(gen.awardUpdateREF(s));
    assertNotNull(gen.getReference());
    assertEquals(7, ((TestState) gen.getReference()).getId());
    assertArrayEquals(new float[] { 50f }, gen.getTrace(), 0.0001f);

    gen.setWeight(99f);
    assertTrue(gen.awardUpdateREF(new TestState(8)));
    assertArrayEquals(new float[] { 50f, 99f }, gen.getTrace(), 0.0001f);
  }

  @Test
  void getReferenceListShouldAppendCopyEachCall() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    gen.setStateRef(new TestState(1));

    List<State> l1 = gen.getReferenceList();
    List<State> l2 = gen.getReferenceList();

    assertSame(l1, l2);
    assertEquals(2, l2.size());
    assertNotSame(l2.get(0), l2.get(1));
    assertEquals(1, ((TestState) l2.get(0)).getId());
    assertEquals(1, ((TestState) l2.get(1)).getId());
  }

  @Test
  void distanceCalculateAddShouldReturnStaticDistanceSolutionWhenOnlyOneSolution() {
    MultiobjectiveHillClimbingDistance.distanceSolution.add(0.0);

    List<State> sols = List.of(new TestState(1));
    List<Double> out = MultiobjectiveHillClimbingDistance.distanceCalculateAdd(sols);

    assertSame(MultiobjectiveHillClimbingDistance.distanceSolution, out);
    assertEquals(List.of(0.0), out);
  }

  @Test
  void distanceCalculateAddShouldUpdateDistancesWhenMoreThanOneSolution() {
    MultiobjectiveHillClimbingDistance.distanceSolution = new ArrayList<>(List.of(10.0));

    List<State> sols = new ArrayList<>();
    sols.add(new TestState(1));
    sols.add(new TestState(4));

    List<Double> out = MultiobjectiveHillClimbingDistance.distanceCalculateAdd(sols);

    assertEquals(List.of(13.0, 3.0), out);
    assertEquals(List.of(13.0, 3.0), MultiobjectiveHillClimbingDistance.distanceSolution);
  }

  @Test
  void solutionMoreDistanceShouldReturnMostDistantOrNull() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();

    List<State> states = List.of(new TestState(1), new TestState(2), new TestState(3));
    List<Double> dist = List.of(1.0, 99.0, 2.0);

    State best = (State) invokePrivate(
        gen, "solutionMoreDistance",
        new Class<?>[] { List.class, List.class },
        new Object[] { states, dist });
    assertNotNull(best);
    assertEquals(2, ((TestState) best).getId());

    State none = (State) invokePrivate(
        gen, "solutionMoreDistance",
        new Class<?>[] { List.class, List.class },
        new Object[] { states, List.of() });
    assertNull(none);
  }

  @Test
  void containsShouldDetectVisitedByComparator() {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    setField(gen, "visitedState", new ArrayList<>(List.of(new TestState(5))));

    boolean found = (boolean) invokePrivate(gen, "contains", new Class<?>[] { State.class },
        new Object[] { new TestState(5) });
    assertTrue(found);

    boolean notFound = (boolean) invokePrivate(gen, "contains", new Class<?>[] { State.class },
        new Object[] { new TestState(6) });
    assertFalse(notFound);
  }

  @Test
  void generateShouldUseStrategyOperatorAndCandidateValue() throws Exception {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    gen.setStateRef(new TestState(10));

    CandidateValue cv = mock(CandidateValue.class);
    TestState expected = new TestState(99);
    when(cv.stateCandidate(any(), any(), any(), anyInt(), anyList())).thenReturn(expected);
    setField(gen, "candidatevalue", cv);

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    List<State> neighborhood = List.of(new TestState(1), new TestState(2));
    when(strategyMock.getProblem().getOperator().generatedNewState(any(), anyInt())).thenReturn(neighborhood);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      State out = gen.generate(7);
      assertSame(expected, out);

      verify(cv).stateCandidate(any(), any(), any(), eq(7), eq(neighborhood));
    }
  }

  @Test
  void updateReferenceShouldInitializeParetoListWhenEmptyAndAcceptImmediately() throws Exception {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    List<State> refFinal = new ArrayList<>();
    when(strategyMock.getListRefPoblacFinal()).thenReturn(refFinal);

    TestState candidateState = new TestState(10);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(true);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class);
        MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
            FactoryAcceptCandidate.class,
            (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any()))
                .thenReturn(acceptable))) {

      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      gen.updateReference(candidateState, 0);

      assertFalse(refFinal.isEmpty());
      assertEquals(1, refFinal.size());
      assertEquals(0.0, MultiobjectiveHillClimbingDistance.distanceSolution.get(0));

      assertEquals(10, ((TestState) gen.getReference()).getId());

      @SuppressWarnings("unchecked")
      List<State> listStateReference = (List<State>) getField(gen, "listStateReference");
      assertFalse(listStateReference.isEmpty());
    }
  }

  @Test
  void updateReferenceShouldFallbackToMostDistantWhenNotAcceptedAndNeighborhoodHasUnvisited() throws Exception {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    gen.setStateRef(new TestState(1));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);

    List<State> refFinal = new ArrayList<>(List.of(new TestState(1), new TestState(9), new TestState(3)));
    when(strategyMock.getListRefPoblacFinal()).thenReturn(refFinal);
    MultiobjectiveHillClimbingDistance.distanceSolution = new ArrayList<>(List.of(1.0, 100.0, 2.0));

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false);

    when(strategyMock.getProblem().getOperator().generatedNewState(any(), anyInt()))
        .thenReturn(List.of(new TestState(123)));

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class);
        MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
            FactoryAcceptCandidate.class,
            (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any()))
                .thenReturn(acceptable))) {

      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      gen.updateReference(new TestState(50), 0);

      assertEquals(9, ((TestState) gen.getReference()).getId());

      @SuppressWarnings("unchecked")
      List<State> visited = (List<State>) getField(gen, "visitedState");
      assertFalse(visited.isEmpty());
      assertEquals(9, ((TestState) visited.get(0)).getId());
    }
  }

  @Test
  void updateReferenceShouldRandomRestartWhenNotAcceptedAndFallbackFailsButRandomAccepted() throws Exception {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    gen.setStateRef(new TestState(1));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    List<State> refFinal = new ArrayList<>(List.of(new TestState(1)));
    when(strategyMock.getListRefPoblacFinal()).thenReturn(refFinal);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false, true);

    // 🔧 romper cadena: guardar mocks intermedios
    var problemMock = strategyMock.getProblem();
    var operatorMock = problemMock.getOperator();

    // Fallback falla: neighborhood todo "contenido"
    setField(gen, "visitedState", new ArrayList<>(List.of(new TestState(777))));
    when(operatorMock.generatedNewState(any(), anyInt()))
        .thenReturn(List.of(new TestState(777)));

    // Random restart: devuelve NO contenido
    when(operatorMock.generateRandomState(anyInt()))
        .thenReturn(List.of(new TestState(42)));
    doNothing().when(problemMock).evaluate(any());

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class);
        MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
            FactoryAcceptCandidate.class,
            (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any()))
                .thenReturn(acceptable))) {

      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      gen.updateReference(new TestState(99), 0);

      assertEquals(42, ((TestState) gen.getReference()).getId());

      @SuppressWarnings("unchecked")
      List<State> visited = (List<State>) getField(gen, "visitedState");
      assertTrue(visited.isEmpty()); // se resetea al aceptar
    }
  }

  @Test
  void updateReferenceShouldEndWithoutChangingReferenceWhenNoAcceptNoFallbackNoRandomRestart() throws Exception {
    MultiobjectiveHillClimbingDistance gen = new MultiobjectiveHillClimbingDistance();
    gen.setStateRef(new TestState(1));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    when(strategyMock.getListRefPoblacFinal())
        .thenReturn(new ArrayList<>(List.of(new TestState(1))));

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false);

    // 🔧 romper cadena: guardar mocks intermedios
    var problemMock = strategyMock.getProblem();
    var operatorMock = problemMock.getOperator();

    // Fallback = false: neighborhood todo contenido
    setField(gen, "visitedState", new ArrayList<>(List.of(new TestState(7), new TestState(8), new TestState(9))));
    when(operatorMock.generatedNewState(any(), anyInt()))
        .thenReturn(List.of(new TestState(7), new TestState(8)));

    // Random restart falla: siempre devuelve contenido
    when(operatorMock.generateRandomState(anyInt()))
        .thenReturn(List.of(new TestState(7)));
    doNothing().when(problemMock).evaluate(any());

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class);
        MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
            FactoryAcceptCandidate.class,
            (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any()))
                .thenReturn(acceptable))) {

      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      gen.updateReference(new TestState(123), 0);

      // No cambia referencia
      assertEquals(1, ((TestState) gen.getReference()).getId());

      @SuppressWarnings("unchecked")
      List<State> listStateReference = (List<State>) getField(gen, "listStateReference");
      assertFalse(listStateReference.isEmpty());
    }
  }
}
