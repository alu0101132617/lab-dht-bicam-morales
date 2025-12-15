package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
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

class SimulatedAnnealingTest {

  // ----- Test State (compatible con tu State: distance devuelve double) -----
  static class TestState extends State {
    private final int id;
    TestState(int id) { this.id = id; }
    int getId() { return id; }

    @Override
    public State copy() { return new TestState(id); }

    @Override
    public boolean comparator(State other) {
      return (other instanceof TestState ts) && ts.id == this.id;
    }

    @Override
    public double distance(State other) {
      if (!(other instanceof TestState ts)) return 0.0;
      return Math.abs(this.id - ts.id);
    }

    @Override
    public String toString() { return "TestState(" + id + ")"; }
  }

  // ----- Reflexión helper -----
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

  @BeforeEach
  void resetStaticsAndInstanceSensitiveState() {
    // Reset estáticos para que los tests sean independientes
    SimulatedAnnealing.setAlpha(null);
    SimulatedAnnealing.setTinitial(null);
    SimulatedAnnealing.setTfinal(null);
    SimulatedAnnealing.setCountIterationsT(0);
  }

  // ---------------- Constructor / getters básicos ----------------
  @Test
  void constructorShouldInitDefaultsAndLinkStatsArray() {
    SimulatedAnnealing sa = new SimulatedAnnealing();

    assertEquals(GeneratorType.SimulatedAnnealing, sa.getType());
    assertEquals(GeneratorType.SimulatedAnnealing, sa.getTypeGenerator());

    assertEquals(50.0f, sa.getWeight(), 0.0001f);

    // trace y contadores inicializados
    float[] trace = sa.getTrace();
    assertNotNull(trace);
    assertTrue(trace.length >= 1);
    assertEquals(50.0f, trace[0], 0.0001f);

    assertNotNull(sa.getListCountBetterGender());
    assertNotNull(sa.getListCountGender());
    assertEquals(10, sa.getListCountBetterGender().length);
    assertEquals(10, sa.getListCountGender().length);

    // awardUpdateREF siempre false
    assertFalse(sa.awardUpdateREF(new TestState(1)));

    // getSonList no null y vacío
    assertNotNull(sa.getSonList());
    assertTrue(sa.getSonList().isEmpty());
  }

  @Test
  void typeGeneratorSetterGetterShouldWork() {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setTypeGenerator(GeneratorType.MultiobjectiveHillClimbingDistance);
    assertEquals(GeneratorType.MultiobjectiveHillClimbingDistance, sa.getType());
    assertEquals(GeneratorType.MultiobjectiveHillClimbingDistance, sa.getTypeGenerator());
  }

  @Test
  void referenceSettersAndGetReferenceListShouldWork() {
    SimulatedAnnealing sa = new SimulatedAnnealing();

    // referencia nula => lista vacía
    assertTrue(sa.getReferenceList().isEmpty());

    TestState s1 = new TestState(1);
    sa.setStateRef(s1);
    assertSame(s1, sa.getReference());

    List<State> l1 = sa.getReferenceList();
    assertEquals(1, l1.size());
    assertSame(s1, l1.get(0));

    // cada llamada devuelve copia de lista (nueva instancia)
    List<State> l2 = sa.getReferenceList();
    assertNotSame(l1, l2);
    assertEquals(2, l2.size());
  }

  @Test
  void staticCoolingConfigGettersSettersShouldWork() {
    SimulatedAnnealing.setAlpha(0.5);
    SimulatedAnnealing.setTinitial(100.0);
    SimulatedAnnealing.setTfinal(1.0);
    SimulatedAnnealing.setCountIterationsT(10);

    assertEquals(0.5, SimulatedAnnealing.getAlpha());
    assertEquals(100.0, SimulatedAnnealing.getTinitial());
    assertEquals(1.0, SimulatedAnnealing.getTfinal());
    assertEquals(10, SimulatedAnnealing.getCountIterationsT());
  }

  // ---------------- generate() ----------------
  @Test
  void generateShouldReturnNullWhenStrategyIsNull() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(null);
      assertNull(sa.generate(0));
    }
  }

  @Test
  void generateShouldReturnNullWhenProblemOrOperatorMissing() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);

    // caso problem null
    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      when(strategyMock.getProblem()).thenReturn(null);
      st.when(Strategy::getStrategy).thenReturn(strategyMock);
      assertNull(sa.generate(1));
    }

    // caso operator null
    strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    var problemMock = mock(strategyMock.getProblem().getClass(), RETURNS_DEEP_STUBS); // no lo usamos realmente
  }

  @Test
  void generateShouldUseCandidateValueWhenContextAvailable() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    TestState ref = new TestState(10);
    sa.setStateRef(ref);

    // Inyectamos CandidateValue mock para controlar retorno
    CandidateValue cv = mock(CandidateValue.class);
    TestState expected = new TestState(99);
    when(cv.stateCandidate(any(), any(), any(), anyInt(), anyList())).thenReturn(expected);
    setField(sa, "candidateValue", cv);

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    var problemMock = strategyMock.getProblem();
    var operatorMock = problemMock.getOperator();

    List<State> neighbourhood = List.of(new TestState(1), new TestState(2));
    when(operatorMock.generatedNewState(any(), anyInt())).thenReturn(neighbourhood);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      State out = sa.generate(7);
      assertSame(expected, out);

      verify(operatorMock).generatedNewState(ref, 7);
      verify(cv).stateCandidate(eq(ref), any(), any(), eq(7), eq(neighbourhood));
    }
  }

  @Test
  void generateShouldReturnNullWhenOperatorIsNull() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    Strategy strategyMock = mock(Strategy.class);
    Object problemMock = mock(Object.class);

    when(strategyMock.getProblem()).thenReturn(null);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);
      assertNull(sa.generate(3));
    }
  }

  // ---------------- updateReference() defensivos ----------------
  @Test
  void updateReferenceShouldReturnImmediatelyWhenReferenceIsNull() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    // stateReferenceSA es null por defecto
    sa.updateReference(new TestState(1), 1);
    assertNull(sa.getReference()); // sigue null
  }

  @Test
  void updateReferenceShouldReturnImmediatelyWhenCandidateIsNull() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));
    sa.updateReference(null, 1);
    assertEquals(1, ((TestState) sa.getReference()).getId());
  }

  // ---------------- updateReference(): aceptación ----------------
  @Test
  void updateReferenceShouldUpdateReferenceWhenAccepted() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    TestState ref = new TestState(1);
    TestState cand = new TestState(2);
    sa.setStateRef(ref);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(ref, cand)).thenReturn(true);

    try (MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
        FactoryAcceptCandidate.class,
        (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any())).thenReturn(acceptable)
    )) {
      sa.updateReference(cand, 5);
      assertSame(cand, sa.getReference());
    }
  }

  @Test
  void updateReferenceShouldNotUpdateReferenceWhenRejected() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    TestState ref = new TestState(1);
    TestState cand = new TestState(2);
    sa.setStateRef(ref);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(ref, cand)).thenReturn(false);

    try (MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
        FactoryAcceptCandidate.class,
        (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any())).thenReturn(acceptable)
    )) {
      sa.updateReference(cand, 5);
      assertSame(ref, sa.getReference());
    }
  }

  // ---------------- updateReference(): cooling schedule ----------------
  @Test
  void updateReferenceCoolingShouldNotRunWhenCountIterationsCurrentIsNull() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    TestState ref = new TestState(1);
    TestState cand = new TestState(2);
    sa.setStateRef(ref);

    // configurar para que SI entraría si countIterationsCurrent no fuera null
    SimulatedAnnealing.setAlpha(0.5);
    SimulatedAnnealing.setTinitial(100.0);
    SimulatedAnnealing.setCountIterationsT(10);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false);

    try (MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
        FactoryAcceptCandidate.class,
        (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any())).thenReturn(acceptable)
    )) {
      sa.updateReference(cand, null);

      assertEquals(100.0, SimulatedAnnealing.getTinitial());
      assertEquals(10, SimulatedAnnealing.getCountIterationsT());
      assertEquals(0, (int) getField(sa, "countRept"));
    }
  }

  @Test
  void updateReferenceCoolingShouldNotRunWhenCountIterationsTNotPositiveOrAlphaOrTinitialNull() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false);

    try (MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
        FactoryAcceptCandidate.class,
        (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any())).thenReturn(acceptable)
    )) {

      // countIterationsT = 0 -> no entra
      SimulatedAnnealing.setCountIterationsT(0);
      SimulatedAnnealing.setAlpha(0.5);
      SimulatedAnnealing.setTinitial(100.0);
      sa.updateReference(new TestState(2), 0);
      assertEquals(100.0, SimulatedAnnealing.getTinitial());

      // alpha null -> no entra
      SimulatedAnnealing.setCountIterationsT(10);
      SimulatedAnnealing.setAlpha(null);
      SimulatedAnnealing.setTinitial(100.0);
      sa.updateReference(new TestState(2), 10);
      assertEquals(100.0, SimulatedAnnealing.getTinitial());

      // tinitial null -> no entra
      SimulatedAnnealing.setAlpha(0.5);
      SimulatedAnnealing.setTinitial(null);
      sa.updateReference(new TestState(2), 10);
      assertNull(SimulatedAnnealing.getTinitial());
    }
  }

  @Test
  void updateReferenceCoolingShouldSetCountReptFirstTimeAndUpdateTemperatureOnMatch() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    // preparar cooling
    SimulatedAnnealing.setAlpha(0.5);
    SimulatedAnnealing.setTinitial(100.0);
    SimulatedAnnealing.setCountIterationsT(10);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false);

    try (MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
        FactoryAcceptCandidate.class,
        (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any())).thenReturn(acceptable)
    )) {
      // primera llamada con iteración NO igual: solo debe fijar countRept
      sa.updateReference(new TestState(2), 9);
      assertEquals(10, (int) getField(sa, "countRept"));
      assertEquals(100.0, SimulatedAnnealing.getTinitial());
      assertEquals(10, SimulatedAnnealing.getCountIterationsT());

      // llamada cuando coincide: actualiza temperatura y countIterationsT
      sa.updateReference(new TestState(3), 10);
      assertEquals(50.0, SimulatedAnnealing.getTinitial());
      assertEquals(20, SimulatedAnnealing.getCountIterationsT()); // 10 + countRept(10)
    }
  }

  @Test
  void updateReferenceCoolingShouldNotUpdateWhenIterationDoesNotMatch() throws Exception {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    SimulatedAnnealing.setAlpha(0.5);
    SimulatedAnnealing.setTinitial(100.0);
    SimulatedAnnealing.setCountIterationsT(10);

    AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
    when(acceptable.acceptCandidate(any(), any())).thenReturn(false);

    try (MockedConstruction<FactoryAcceptCandidate> mocked = mockConstruction(
        FactoryAcceptCandidate.class,
        (mock, ctx) -> when(((IFFactoryAcceptCandidate) mock).createAcceptCandidate(any())).thenReturn(acceptable)
    )) {
      sa.updateReference(new TestState(2), 11);
      // countRept se habrá fijado a 10 en esta primera entrada
      assertEquals(10, (int) getField(sa, "countRept"));
      // pero no hay match => no cambia temperatura ni countIterationsT
      assertEquals(100.0, SimulatedAnnealing.getTinitial());
      assertEquals(10, SimulatedAnnealing.getCountIterationsT());
    }
  }

  // ---------------- getReferenceList devuelve copia nueva ----------------
  @Test
  void getReferenceListShouldReturnNewListInstanceAndNotNull() {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setStateRef(new TestState(1));

    List<State> a = sa.getReferenceList();
    List<State> b = sa.getReferenceList();

    assertNotNull(a);
    assertNotNull(b);
    assertNotSame(a, b);
    assertEquals(2, b.size());
  }

  // ---------------- setWeight / getWeight ----------------
  @Test
  void setWeightShouldUpdateWeight() {
    SimulatedAnnealing sa = new SimulatedAnnealing();
    sa.setWeight(12.5f);
    assertEquals(12.5f, sa.getWeight(), 0.0001f);
  }
}
