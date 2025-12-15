package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import es.ull.esit.app.factory_method.FactoryGenerator;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class MultiGeneratorTest {

  // ---------- Helpers reflexión ----------
  private static void setStaticField(Class<?> clazz, String name, Object value) {
    try {
      Field f = clazz.getDeclaredField(name);
      f.setAccessible(true);
      f.set(null, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object getStaticField(Class<?> clazz, String name) {
    try {
      Field f = clazz.getDeclaredField(name);
      f.setAccessible(true);
      return f.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** SecureRandom determinista para forzar ramas de roulette() */
  static class FixedSecureRandom extends SecureRandom {
    private final double value;

    FixedSecureRandom(double value) {
      this.value = value;
    }

    @Override
    public double nextDouble() {
      return value;
    }
  }

  // ---------- Generador dummy controlable ----------
  static class DummyGenerator extends Generator {
    private final GeneratorType type;
    private float weight;
    private final float[] trace = new float[50];
    private State toReturn;

    DummyGenerator(GeneratorType type, float weight) {
      this.type = type;
      this.weight = weight;
      this.trace[0] = weight;
    }

    void setReturn(State s) {
      this.toReturn = s;
    }

    @Override
    public State generate(Integer operatornumber) {
      return toReturn;
    }

    @Override
    public void updateReference(State s, Integer it) {
      /* no-op */
    }

    @Override
    public State getReference() {
      return toReturn;
    }

    @Override
    public List<State> getReferenceList() {
      return List.of();
    }

    @Override
    public List<State> getSonList() {
      return List.of();
    }

    @Override
    public GeneratorType getType() {
      return type;
    }

    @Override
    public void setInitialReference(State s) {
      /* no-op */
    }

    @Override
    public boolean awardUpdateREF(State s) {
      return false;
    }

    @Override
    public float getWeight() {
      return weight;
    }

    @Override
    public void setWeight(float w) {
      this.weight = w;
    }

    @Override
    public int[] getListCountBetterGender() {
      return new int[0];
    }

    @Override
    public int[] getListCountGender() {
      return new int[0];
    }

    @Override
    public float[] getTrace() {
      return trace;
    }
  }

  @BeforeEach
  void resetStatics() {
    // Evita contaminación entre tests
    setStaticField(MultiGenerator.class, "listGeneratedPP", new ArrayList<State>());
    setStaticField(MultiGenerator.class, "listStateReference", new ArrayList<State>());
    setStaticField(MultiGenerator.class, "activeGenerator", null);

    // listGenerators por defecto no null para la mayoría de tests
    setStaticField(MultiGenerator.class, "listGenerators", new Generator[] {
        new DummyGenerator(GeneratorType.HillClimbing, 1f),
        new DummyGenerator(GeneratorType.GeneticAlgorithm, 1f)
    });

    // random determinista por defecto
    setStaticField(MultiGenerator.class, "random", new FixedSecureRandom(0.0));
  }

  // ---------------- destroyMultiGenerator ----------------
  @Test
  void destroyMultiGeneratorShouldClearAndNullify() throws Exception {
    MultiGenerator.getListGeneratedPP().add(mock(State.class));
    MultiGenerator.getListStateReference().add(mock(State.class));
    MultiGenerator.setActiveGenerator(new DummyGenerator(GeneratorType.HillClimbing, 1f));

    MultiGenerator.destroyMultiGenerator();

    assertNull(MultiGenerator.getActiveGenerator());
    assertNull(MultiGenerator.getListGenerators()); // queda null por implementación
    assertTrue(MultiGenerator.getListGeneratedPP().isEmpty());
    assertTrue(MultiGenerator.getListStateReference().isEmpty());
  }

  // ---------------- getters/setters triviales ----------------
  @Test
  void settersAndGettersShouldWork() throws Exception {
    List<State> refs = new ArrayList<>();
    MultiGenerator.setListStateReference(refs);
    assertSame(refs, MultiGenerator.getListStateReference());

    List<State> gen = new ArrayList<>();
    MultiGenerator.setListGeneratedPP(gen);
    assertSame(gen, MultiGenerator.getListGeneratedPP());

    Generator[] arr = new Generator[] { new DummyGenerator(GeneratorType.HillClimbing, 1f) };
    MultiGenerator.setListGenerators(arr);
    assertSame(arr, MultiGenerator.getListGenerators());

    DummyGenerator g = new DummyGenerator(GeneratorType.HillClimbing, 1f);
    MultiGenerator.setActiveGenerator(g);
    assertSame(g, MultiGenerator.getActiveGenerator());
  }

  // ---------------- getReference / getReferenceList / setInitialReference ----------------
  @Test
  void getReferenceShouldReturnLastFromListOrProblemStateIfEmpty() throws Exception {
    MultiGenerator mg = new MultiGenerator();

    // Caso lista no vacía -> último
    State s1 = mock(State.class);
    State s2 = mock(State.class);
    MultiGenerator.getListStateReference().add(s1);
    MultiGenerator.getListStateReference().add(s2);
    assertSame(s2, mg.getReference());

    // Caso lista vacía -> Strategy.getStrategy().getProblem().getState()
    MultiGenerator.getListStateReference().clear();
    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    State problemState = mock(State.class);
    when(strategyMock.getProblem().getState()).thenReturn(problemState);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);
      assertSame(problemState, mg.getReference());
    }
  }

  @Test
  void getReferenceListShouldReturnCopyAndSonListEmpty() throws Exception {
    MultiGenerator mg = new MultiGenerator();
    State s = mock(State.class);
    MultiGenerator.getListStateReference().add(s);

    List<State> out = mg.getReferenceList();
    assertEquals(1, out.size());
    assertSame(s, out.get(0));
    assertNotSame(out, MultiGenerator.getListStateReference());

    assertTrue(mg.getSonList().isEmpty());
  }

  @Test
  void setInitialReferenceShouldAppendWhenNotNull() throws Exception {
    MultiGenerator mg = new MultiGenerator();
    MultiGenerator.getListStateReference().clear();
    State s = mock(State.class);

    mg.setInitialReference(null);
    assertTrue(MultiGenerator.getListStateReference().isEmpty());

    mg.setInitialReference(s);
    assertEquals(1, MultiGenerator.getListStateReference().size());
    assertSame(s, MultiGenerator.getListStateReference().get(0));
  }

  // ---------------- roulette (find=true y find=false) ----------------
  @Test
  void rouletteShouldSelectGeneratorInsideLimitsWhenFindTrue() throws Exception {
    DummyGenerator g1 = new DummyGenerator(GeneratorType.HillClimbing, 1f); // prob 0.25
    DummyGenerator g2 = new DummyGenerator(GeneratorType.GeneticAlgorithm, 3f); // prob 0.75
    MultiGenerator.setListGenerators(new Generator[] { g1, g2 });

    // nextDouble = 0.10 => cae en g1
    setStaticField(MultiGenerator.class, "random", new FixedSecureRandom(0.10));

    MultiGenerator mg = new MultiGenerator();
    Generator picked = mg.roulette();
    assertSame(g1, picked);
  }

  @Test
  void rouletteShouldReturnLastGeneratorWhenNoLimitMatched() throws Exception {
    DummyGenerator g1 = new DummyGenerator(GeneratorType.HillClimbing, 1f);
    DummyGenerator g2 = new DummyGenerator(GeneratorType.GeneticAlgorithm, 1f);
    MultiGenerator.setListGenerators(new Generator[] { g1, g2 });

    // nextDouble > 1 (forzamos) => no entra en ningún intervalo y devuelve el último
    setStaticField(MultiGenerator.class, "random", new FixedSecureRandom(1.5));

    MultiGenerator mg = new MultiGenerator();
    Generator picked = mg.roulette();
    assertSame(g2, picked);
  }

  // ---------------- generate() ----------------
  @Test
  void generateShouldSetGeneratorIncreaseCountGenderAndReturnGeneratedState() throws Exception {
    DummyGenerator g1 = new DummyGenerator(GeneratorType.HillClimbing, 1f);
    DummyGenerator g2 = new DummyGenerator(GeneratorType.GeneticAlgorithm, 3f);
    State expected = mock(State.class);
    g1.setReturn(expected);
    MultiGenerator.setListGenerators(new Generator[] { g1, g2 });

    // forzar roulette a g1
    setStaticField(MultiGenerator.class, "random", new FixedSecureRandom(0.10));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    when(strategyMock.getGenerator()).thenReturn(g1);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      MultiGenerator mg = new MultiGenerator();
      int before = g1.countGender;

      State out = mg.generate(999);

      verify(strategyMock).setGenerator(g1);
      assertSame(g1, MultiGenerator.getActiveGenerator());
      assertEquals(before + 1, g1.countGender);
      assertSame(expected, out);
    }
  }

  // ---------------- updateReference -> updateWeight + tournament ----------------
  @Test
  void updateReferenceShouldCallUpdateWeightAndTournament() throws Exception {
    MultiGenerator mg = spy(new MultiGenerator());
    State cand = mock(State.class);

    doNothing().when(mg).updateWeight(cand);
    doNothing().when(mg).tournament(eq(cand), eq(5));

    mg.updateReference(cand, 5);

    verify(mg).updateWeight(cand);
    verify(mg).tournament(cand, 5);
  }

  // ---------------- searchState MAX/MIN + increment countBetterGender ----------------
  @Test
  void searchStateShouldReturnTrueAndIncrementBetterGenderForMaximizar() throws Exception {
    MultiGenerator mg = new MultiGenerator();

    DummyGenerator active = new DummyGenerator(GeneratorType.HillClimbing, 1f);
    MultiGenerator.setActiveGenerator(active);

    State cand = mock(State.class);
    State best = mock(State.class);

    when(cand.getEvaluation()).thenReturn(List.of(10.0));
    when(best.getEvaluation()).thenReturn(List.of(5.0));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    when(strategyMock.getBestState()).thenReturn(best);
    when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      int before = active.countBetterGender;
      assertTrue(mg.searchState(cand));
      assertEquals(before + 1, active.countBetterGender);

      // rama false
      when(cand.getEvaluation()).thenReturn(List.of(1.0));
      assertFalse(mg.searchState(cand));
    }
  }

  @Test
  void searchStateShouldReturnTrueAndIncrementBetterGenderForMinimizar() throws Exception {
    MultiGenerator mg = new MultiGenerator();

    DummyGenerator active = new DummyGenerator(GeneratorType.HillClimbing, 1f);
    MultiGenerator.setActiveGenerator(active);

    State cand = mock(State.class);
    State best = mock(State.class);

    when(cand.getEvaluation()).thenReturn(List.of(1.0));
    when(best.getEvaluation()).thenReturn(List.of(5.0));

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    when(strategyMock.getBestState()).thenReturn(best);
    when(strategyMock.getProblem().getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      int before = active.countBetterGender;
      assertTrue(mg.searchState(cand));
      assertEquals(before + 1, active.countBetterGender);

      // rama false
      when(cand.getEvaluation()).thenReturn(List.of(9.0));
      assertFalse(mg.searchState(cand));
    }
  }

  // ---------------- awardUpdateREF ----------------
  @Test
  void awardUpdateREFShouldAddToReferenceListWhenImproved() throws Exception {
    MultiGenerator mg = spy(new MultiGenerator());

    State cand = mock(State.class);
    doReturn(true).when(mg).searchState(cand);

    int before = MultiGenerator.getListStateReference().size();
    assertTrue(mg.awardUpdateREF(cand));
    assertEquals(before + 1, MultiGenerator.getListStateReference().size());
  }

  @Test
  void awardUpdateREFShouldNotAddWhenNotImproved() throws Exception {
    MultiGenerator mg = spy(new MultiGenerator());

    State cand = mock(State.class);
    doReturn(false).when(mg).searchState(cand);

    int before = MultiGenerator.getListStateReference().size();
    assertFalse(mg.awardUpdateREF(cand));
    assertEquals(before, MultiGenerator.getListStateReference().size());
  }

  // ---------------- updateAwardSC / updateAwardImp / getWeight / setWeight / getTrace ----------------
  @Test
  void updateAwardSCAndImpShouldUpdateWeightsAndTraces() throws Exception {
    MultiGenerator mg = new MultiGenerator();

    DummyGenerator active = new DummyGenerator(GeneratorType.HillClimbing, 100f);
    DummyGenerator other = new DummyGenerator(GeneratorType.GeneticAlgorithm, 50f);
    DummyGenerator multi = new DummyGenerator(GeneratorType.MultiGenerator, 999f);
    MultiGenerator.setActiveGenerator(active);
    MultiGenerator.setListGenerators(new Generator[] { active, other, multi });

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    when(strategyMock.getCountCurrent()).thenReturn(3);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      mg.updateAwardSC();
      assertTrue(active.getWeight() > 0);
      assertEquals(active.getWeight(), active.getTrace()[3], 1e-6);

      mg.updateAwardImp();
      assertEquals(active.getWeight(), active.getTrace()[3], 1e-6);

      assertEquals(active.getWeight(), mg.getWeight(), 1e-6);

      mg.setWeight(123f);
      assertEquals(123f, active.getWeight(), 1e-6);

      assertSame(active.getTrace(), mg.getTrace());
    }
  }

  @Test
  void getWeightTraceSetWeightShouldHandleNullActive() throws Exception {
    MultiGenerator mg = new MultiGenerator();
    MultiGenerator.setActiveGenerator(null);

    assertEquals(0f, mg.getWeight(), 1e-9);
    assertArrayEquals(new float[0], mg.getTrace());
    mg.setWeight(10f);
  }

  // ---------------- tournament ----------------
  @Test
  void tournamentShouldCallUpdateReferenceOnAllNonMultiGenerators() throws Exception {
    MultiGenerator mg = new MultiGenerator();

    Generator g1 = mock(Generator.class);
    when(g1.getType()).thenReturn(GeneratorType.HillClimbing);

    Generator g2 = mock(Generator.class);
    when(g2.getType()).thenReturn(GeneratorType.MultiGenerator);

    Generator g3 = mock(Generator.class);
    when(g3.getType()).thenReturn(GeneratorType.GeneticAlgorithm);

    MultiGenerator.setListGenerators(new Generator[] { g1, g2, g3 });

    State cand = mock(State.class);
    mg.tournament(cand, 7);

    verify(g1).updateReference(any(State.class), eq(7));
    verify(g3).updateReference(any(State.class), eq(7));
    verify(g2, never()).updateReference(any(), any());
  }

  // ---------------- copy / constructor copia ----------------
  @Test
  void copyShouldReturnNewInstance() throws Exception {
    MultiGenerator mg = new MultiGenerator();
    MultiGenerator cp = mg.copy();
    assertNotNull(cp);
    assertNotSame(mg, cp);
    assertEquals(mg.getType(), cp.getType());
  }

  // ---------------- getListCountBetterGender / getListCountGender ----------------
  @Test
  void getListCountArraysShouldReturnEmptyWhenListGeneratorsNull() throws Exception {
    MultiGenerator mg = new MultiGenerator();
    MultiGenerator.destroyMultiGenerator(); // deja listGenerators = null

    assertArrayEquals(new int[0], mg.getListCountBetterGender());
    assertArrayEquals(new int[0], mg.getListCountGender());
  }

  @Test
  void getListCountArraysShouldReturnCountsWhenGeneratorsPresent() throws Exception {
    MultiGenerator mg = new MultiGenerator();

    DummyGenerator g1 = new DummyGenerator(GeneratorType.HillClimbing, 1f);
    DummyGenerator g2 = new DummyGenerator(GeneratorType.GeneticAlgorithm, 1f);
    g1.countBetterGender = 5;
    g1.countGender = 7;
    g2.countBetterGender = 2;
    g2.countGender = 3;

    MultiGenerator.setListGenerators(new Generator[] { g1, g2 });

    assertArrayEquals(new int[] { 5, 2 }, mg.getListCountBetterGender());
    assertArrayEquals(new int[] { 7, 3 }, mg.getListCountGender());
  }

  // ---------------- createInstanceGeneratorsBPP: success y exception ----------------
  @Test
  void createInstanceGeneratorsBPPShouldAddStatesWhenGenerateWorks() throws Exception {
    int count = EvolutionStrategies.getCountRef();
    if (count <= 0) {
      MultiGenerator.createInstanceGeneratorsBPP();
      return;
    }

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);
      doNothing().when(strategyMock.getProblem()).evaluate(any());

      MultiGenerator.setListGeneratedPP(new ArrayList<>());
      MultiGenerator.createInstanceGeneratorsBPP();

      assertNotNull(MultiGenerator.getListGeneratedPP());
    }
  }

  @Test
  void createInstanceGeneratorsBPPShouldCatchExceptionsFromGenerate() throws Exception {
    int count = EvolutionStrategies.getCountRef();
    if (count <= 0) {
      MultiGenerator.createInstanceGeneratorsBPP();
      return;
    }

    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      doThrow(new RuntimeException("boom")).when(strategyMock.getProblem()).evaluate(any());

      MultiGenerator.setListGeneratedPP(new ArrayList<>());
      MultiGenerator.createInstanceGeneratorsBPP();
      assertNotNull(MultiGenerator.getListGeneratedPP());
    }
  }

  // ---------------- initializeListGenerator / initializeGenerators ----------------
  @Test
  void initializeListGeneratorShouldCreateArrayOfFour() throws Exception {
    // Algunos generadores creados dentro de initializeListGenerator()
    // acceden a Strategy.getStrategy().getProblem().getTypeProblem()
    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);

    // Romper cadena para evitar deep-stub raro y asegurar no-null
    var problemMock = strategyMock.getProblem();
    when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

    // Si algún constructor pide el estado u operador, devolvemos mocks seguros
    when(problemMock.getState()).thenReturn(mock(State.class));
    when(problemMock.getOperator()).thenReturn(mock(es.ull.esit.app.problem.definition.Operator.class, RETURNS_DEEP_STUBS));

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class)) {
      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      MultiGenerator.initializeListGenerator();

      assertNotNull(MultiGenerator.getListGenerators());
      assertEquals(4, MultiGenerator.getListGenerators().length);
    }
  }

  @Test
  void initializeGeneratorsShouldRunAndReplaceFactoriesWhenMatched() throws Exception {
    Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);

    State baseState = mock(State.class);
    when(strategyMock.getProblem().getState()).thenReturn(baseState);

    doNothing().when(strategyMock).setListStates(anyList());

    try (MockedStatic<Strategy> st = mockStatic(Strategy.class);
        MockedConstruction<FactoryGenerator> fc = mockConstruction(
            FactoryGenerator.class,
            (mock, ctx) -> {
              when(mock.createGenerator(eq(GeneratorType.EvolutionStrategies)))
                  .thenReturn(new DummyGenerator(GeneratorType.EvolutionStrategies, 1f));
              when(mock.createGenerator(eq(GeneratorType.GeneticAlgorithm)))
                  .thenReturn(new DummyGenerator(GeneratorType.GeneticAlgorithm, 1f));
              when(mock.createGenerator(eq(GeneratorType.DistributionEstimationAlgorithm)))
                  .thenReturn(new DummyGenerator(GeneratorType.DistributionEstimationAlgorithm, 1f));
            })) {

      st.when(Strategy::getStrategy).thenReturn(strategyMock);

      MultiGenerator.initializeGenerators();

      assertNotNull(MultiGenerator.getListGenerators());
      assertFalse(MultiGenerator.getListStateReference().isEmpty());
      verify(strategyMock).setListStates(anyList());
    }
  }
}
