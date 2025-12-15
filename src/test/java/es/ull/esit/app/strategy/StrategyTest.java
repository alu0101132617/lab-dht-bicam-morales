package es.ull.esit.app.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import es.ull.esit.app.factory_method.FactoryGenerator;
import es.ull.esit.app.local_search.acceptation_type.Dominance;
import es.ull.esit.app.local_search.complement.StopExecute;
import es.ull.esit.app.metaheuristics.generators.DistributionEstimationAlgorithm;
import es.ull.esit.app.metaheuristics.generators.EvolutionStrategies;
import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheuristics.generators.GeneticAlgorithm;
import es.ull.esit.app.metaheuristics.generators.MultiGenerator;
import es.ull.esit.app.metaheuristics.generators.ParticleSwarmOptimization;
import es.ull.esit.app.metaheuristics.generators.RandomSearch;
import es.ull.esit.app.problem.definition.ObjetiveFunction;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class StrategyTest {

  // -------------------- helpers reflexión --------------------
  private static void setField(Object target, String name, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T getField(Object target, String name) {
    try {
      Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      return (T) f.get(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // -------------------- dummy generator para MultiGenerator --------------------
  static class DummyGen extends Generator {
    private final GeneratorType type;
    private final float[] trace = new float[100];
    private final int[] better = new int[10];
    private final int[] gender = new int[10];
    private float weight = 10f;
    private State ref;
    private List<State> refList = new ArrayList<>();

    DummyGen(GeneratorType type) { this.type = type; }

    void setReference(State s) { this.ref = s; }
    void setReferenceList(List<State> l) { this.refList = l; }

    @Override public State generate(Integer operatornumber) { return ref; }
    @Override public void updateReference(State stateCandidate, Integer countIterationsCurrent) {}
    @Override public State getReference() { return ref; }
    @Override public List<State> getReferenceList() { return refList; }
    @Override public List<State> getSonList() { return List.of(); }
    @Override public GeneratorType getType() { return type; }
    @Override public void setInitialReference(State stateInitialRef) { this.ref = stateInitialRef; }
    @Override public boolean awardUpdateREF(State stateCandidate) { return false; }
    @Override public float getWeight() { return weight; }
    @Override public void setWeight(float weight) { this.weight = weight; }
    @Override public int[] getListCountBetterGender() { return better; }
    @Override public int[] getListCountGender() { return gender; }
    @Override public float[] getTrace() { return trace; }
  }

  @BeforeEach
  void resetSingleton() {
    Strategy.destroyExecute();
  }

  // ============================================================
  // Singleton
  // ============================================================
  @Test
  void getStrategyShouldReturnSingletonAndDestroyExecuteResets() {
    Strategy s1 = Strategy.getStrategy();
    Strategy s2 = Strategy.getStrategy();
    assertSame(s1, s2);

    Strategy.destroyExecute();
    Strategy s3 = Strategy.getStrategy();
    assertNotSame(s1, s3);
  }

  // ============================================================
  // updateCountGender / updateWeight
  // ============================================================
  @Test
  void updateCountGenderShouldAccumulateAndResetCounters() {
    Strategy s = Strategy.getStrategy();

    // periodo = 0 para simplificar
    setField(s, "periodo", 0);

    DummyGen g1 = new DummyGen(GeneratorType.HillClimbing);
    DummyGen g2 = new DummyGen(GeneratorType.GeneticAlgorithm);
    DummyGen gMulti = new DummyGen(GeneratorType.MultiGenerator);

    g1.countGender = 3;
    g1.countBetterGender = 2;
    g2.countGender = 5;
    g2.countBetterGender = 4;

    MultiGenerator.setListGenerators(new Generator[] { g1, g2, gMulti });

    s.updateCountGender();

    assertEquals(3, g1.getListCountGender()[0]);
    assertEquals(2, g1.getListCountBetterGender()[0]);
    assertEquals(5, g2.getListCountGender()[0]);
    assertEquals(4, g2.getListCountBetterGender()[0]);

    // Se resetean contadores
    assertEquals(0, g1.countGender);
    assertEquals(0, g1.countBetterGender);
    assertEquals(0, g2.countGender);
    assertEquals(0, g2.countBetterGender);
  }

  @Test
  void updateWeightShouldSetAllNonMultiGeneratorWeightsTo50() {
    Strategy s = Strategy.getStrategy();

    DummyGen g1 = new DummyGen(GeneratorType.HillClimbing);
    DummyGen g2 = new DummyGen(GeneratorType.GeneticAlgorithm);
    DummyGen gMulti = new DummyGen(GeneratorType.MultiGenerator);

    g1.setWeight(1f);
    g2.setWeight(2f);
    gMulti.setWeight(999f);

    MultiGenerator.setListGenerators(new Generator[] { g1, g2, gMulti });

    s.updateWeight();

    assertEquals(50f, g1.getWeight(), 1e-6);
    assertEquals(50f, g2.getWeight(), 1e-6);
    // MultiGenerator no se toca
    assertEquals(999f, gMulti.getWeight(), 1e-6);
  }

  // ============================================================
  // calculateOffLinePerformance
  // ============================================================
  @Test
  void calculateOffLinePerformanceShouldStoreOfflineMetric() {
    Strategy s = Strategy.getStrategy();
    // countPeriodChange debe ser !=0
    setField(s, "countPeriodChange", 10);

    s.calculateOffLinePerformance(50f, 0); // 50/10 = 5
    float[] offline = getField(s, "listOfflineError");
    assertEquals(5f, offline[0], 1e-6f);
  }

  // ============================================================
  // newGenerator + initialize/initializeGenerators + getListKey
  // ============================================================
  @Test
  void newGeneratorShouldUseFactoryGenerator() throws Exception {
    Strategy s = Strategy.getStrategy();

    Generator dummy = mock(Generator.class);

    try (MockedConstruction<FactoryGenerator> fc = mockConstruction(
        FactoryGenerator.class,
        (mock, ctx) -> when(mock.createGenerator(eq(GeneratorType.HillClimbing))).thenReturn(dummy)
    )) {
      Generator out = s.newGenerator(GeneratorType.HillClimbing);
      assertSame(dummy, out);
    }
  }

  @Test
  void initializeGeneratorsAndGetListKeyShouldFillMap() throws Exception {
    Strategy s = Strategy.getStrategy();

    // FactoryGenerator devuelve un Generator mock para cualquier tipo
    try (MockedConstruction<FactoryGenerator> fc = mockConstruction(
        FactoryGenerator.class,
        (mock, ctx) -> when(mock.createGenerator(any())).thenAnswer(inv -> {
          Generator g = mock(Generator.class);
          when(g.getType()).thenReturn((GeneratorType) inv.getArgument(0));
          return g;
        })
    )) {
      s.initializeGenerators();

      // mapGenerators debe existir y tener todas las claves
      List<String> keys = s.getListKey();
      assertNotNull(keys);
      assertFalse(keys.isEmpty());

      // Debe contener al menos algunas claves conocidas (no dependemos del orden exacto)
      assertTrue(keys.toString().contains("HillClimbing"));
      assertTrue(keys.toString().contains("GeneticAlgorithm"));
    }
  }

  @Test
  void initializeShouldAlsoFillMap() throws Exception {
    Strategy s = Strategy.getStrategy();
    try (MockedConstruction<FactoryGenerator> fc = mockConstruction(
        FactoryGenerator.class,
        (mock, ctx) -> when(mock.createGenerator(any())).thenReturn(mock(Generator.class))
    )) {
      s.initialize();
      List<String> keys = s.getListKey();
      assertNotNull(keys);
      assertFalse(keys.isEmpty());
    }
  }

  // ============================================================
  // update(Integer) -> cubre todas las ramas usando getCountRef()-1
  // ============================================================
  @Test
  void updateShouldReplaceGeneratorAtRefBoundaries() throws Exception {
    Strategy s = Strategy.getStrategy();

    try (MockedConstruction<FactoryGenerator> fc = mockConstruction(
        FactoryGenerator.class,
        (mock, ctx) -> when(mock.createGenerator(any())).thenReturn(mock(Generator.class))
    )) {

      // 1) GeneticAlgorithm
      s.update(GeneticAlgorithm.getCountRef() - 1);
      assertNotNull(Strategy.getStrategy().generator);

      // Verificamos que algún FactoryGenerator construido recibió el tipo correcto
      boolean calledGA = fc.constructed().stream()
          .anyMatch(f -> mockingDetails(f).getInvocations().stream()
              .anyMatch(inv -> inv.getMethod().getName().equals("createGenerator")
                  && inv.getArguments().length == 1
                  && inv.getArguments()[0] == GeneratorType.GeneticAlgorithm));
      assertTrue(calledGA);

      // 2) EvolutionStrategies
      s.update(EvolutionStrategies.getCountRef() - 1);
      assertNotNull(Strategy.getStrategy().generator);

      boolean calledES = fc.constructed().stream()
          .anyMatch(f -> mockingDetails(f).getInvocations().stream()
              .anyMatch(inv -> inv.getMethod().getName().equals("createGenerator")
                  && inv.getArguments().length == 1
                  && inv.getArguments()[0] == GeneratorType.EvolutionStrategies));
      assertTrue(calledES);

      // 3) DistributionEstimationAlgorithm
      s.update(DistributionEstimationAlgorithm.getCountRef() - 1);
      assertNotNull(Strategy.getStrategy().generator);

      boolean calledEDA = fc.constructed().stream()
          .anyMatch(f -> mockingDetails(f).getInvocations().stream()
              .anyMatch(inv -> inv.getMethod().getName().equals("createGenerator")
                  && inv.getArguments().length == 1
                  && inv.getArguments()[0] == GeneratorType.DistributionEstimationAlgorithm));
      assertTrue(calledEDA);

      // 4) ParticleSwarmOptimization
      s.update(ParticleSwarmOptimization.getCountRef() - 1);
      assertNotNull(Strategy.getStrategy().generator);

      boolean calledPSO = fc.constructed().stream()
          .anyMatch(f -> mockingDetails(f).getInvocations().stream()
              .anyMatch(inv -> inv.getMethod().getName().equals("createGenerator")
                  && inv.getArguments().length == 1
                  && inv.getArguments()[0] == GeneratorType.ParticleSwarmOptimization));
      assertTrue(calledPSO);
    }
  }


  // ============================================================
  // updateRef / updateRefMultiG / updateRefGenerator (ambas ramas)
  // ============================================================
  @Test
  void updateRefGeneratorShouldRecomputeEvaluationForSingleReferenceTypes() {
    Strategy s = Strategy.getStrategy();

    // Problem con 1 objetivo
    Problem problem = mock(Problem.class, RETURNS_DEEP_STUBS);
    ObjetiveFunction f0 = mock(ObjetiveFunction.class);
    when(f0.evaluation(any(State.class))).thenReturn(42.0);
    when(problem.getFunction()).thenReturn(List.of(f0));
    setField(s, "problem", problem);

    // Generador tipo HillClimbing -> usa getReference()
    DummyGen g = new DummyGen(GeneratorType.HillClimbing);

    State ref = mock(State.class);
    ArrayList<Double> eval = new ArrayList<>(List.of(0.0));
    when(ref.getEvaluation()).thenReturn(eval);
    g.setReference(ref);

    s.updateRefGenerator(g);

    assertEquals(42.0, eval.get(0), 1e-9);
  }

  @Test
  void updateRefGeneratorShouldRecomputeEvaluationForPopulationTypes() {
    Strategy s = Strategy.getStrategy();

    Problem problem = mock(Problem.class, RETURNS_DEEP_STUBS);
    ObjetiveFunction f0 = mock(ObjetiveFunction.class);
    when(f0.evaluation(any(State.class))).thenReturn(7.0);
    when(problem.getFunction()).thenReturn(List.of(f0));
    setField(s, "problem", problem);

    // Generador GA -> usa getReferenceList()
    DummyGen g = new DummyGen(GeneratorType.GeneticAlgorithm);

    State a = mock(State.class);
    State b = mock(State.class);

    ArrayList<Double> ea = new ArrayList<>(List.of(0.0));
    ArrayList<Double> eb = new ArrayList<>(List.of(0.0));
    when(a.getEvaluation()).thenReturn(ea);
    when(b.getEvaluation()).thenReturn(eb);

    g.setReferenceList(List.of(a, b));

    s.updateRefGenerator(g);

    assertEquals(7.0, ea.get(0), 1e-9);
    assertEquals(7.0, eb.get(0), 1e-9);
  }

  @Test
  void updateRefShouldHandleMultiGeneratorAndNonMulti() {
    Strategy s = Strategy.getStrategy();

    Problem problem = mock(Problem.class, RETURNS_DEEP_STUBS);
    ObjetiveFunction f0 = mock(ObjetiveFunction.class);
    when(f0.evaluation(any(State.class))).thenReturn(1.0);
    when(problem.getFunction()).thenReturn(List.of(f0));
    setField(s, "problem", problem);

    // set generator normal
    DummyGen normal = new DummyGen(GeneratorType.HillClimbing);
    State ref = mock(State.class);
    when(ref.getEvaluation()).thenReturn(new ArrayList<>(List.of(0.0)));
    normal.setReference(ref);
    setField(s, "generator", normal);

    // updateRef con tipo no Multi -> bestState = generator.getReference()
    s.updateRef(GeneratorType.HillClimbing);
    assertSame(ref, s.getBestState());

    // updateRef con Multi -> bestState de MultiGenerator listStateReference last
    State last = mock(State.class);
    MultiGenerator.setListStateReference(new ArrayList<>(List.of(mock(State.class), last)));

    DummyGen g1 = new DummyGen(GeneratorType.HillClimbing);
    State r1 = mock(State.class);
    when(r1.getEvaluation()).thenReturn(new ArrayList<>(List.of(0.0)));
    g1.setReference(r1);

    MultiGenerator.setListGenerators(new Generator[] { g1, new DummyGen(GeneratorType.MultiGenerator) });

    s.updateRef(GeneratorType.MultiGenerator);
    assertSame(last, s.getBestState());
  }

  // ============================================================
  // executeStrategy: sin bucle (StopExecute = true desde el inicio)
  // + activa flags para cubrir inicializaciones
  // ============================================================
  @Test
  void executeStrategyShouldInitializeAndFinishWhenStopImmediately() throws Exception {
    Strategy s = Strategy.getStrategy();

    // Flags para cubrir ramas iniciales
    setField(s, "calculateTime", true);
    setField(s, "saveListStates", true);
    setField(s, "saveListBestStates", true);
    setField(s, "saveFreneParetoMonoObjetivo", true);

    // StopExecute: parar al instante
    StopExecute stop = mock(StopExecute.class);
    when(stop.stopIterations(anyInt(), anyInt())).thenReturn(Boolean.TRUE);
    s.setStopexecute(stop);

    // Problem mock
    Problem problem = mock(Problem.class, RETURNS_DEEP_STUBS);
    when(problem.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
    doNothing().when(problem).evaluate(any(State.class));
    s.setProblem(problem);

    // Estado inicial generado por RandomSearch
    State initialState = mock(State.class);
    when(initialState.getEvaluation()).thenReturn(new ArrayList<>(List.of(0.0)));

    // RandomSearch constructor mock: generate() devuelve initialState
    try (MockedConstruction<RandomSearch> rs = mockConstruction(RandomSearch.class,
        (mock, ctx) -> when(mock.generate(anyInt())).thenReturn(initialState));
         // FactoryGenerator mock: createGenerator devuelve un Generator dummy
         MockedConstruction<FactoryGenerator> fg = mockConstruction(FactoryGenerator.class,
        (mock, ctx) -> {
          Generator gen = mock(Generator.class);
          when(gen.getReferenceList()).thenReturn(List.of(initialState));
          doNothing().when(gen).setInitialReference(any(State.class));
          when(gen.getType()).thenReturn(GeneratorType.HillClimbing);
          when(mock.createGenerator(any())).thenReturn(gen);
        })) {

      // Ejecuta con generador NO MultiGenerator para evitar inicializaciones pesadas de MultiGenerator
      s.executeStrategy(
          100,   // countmaxIterations
          10,    // countIterationsChange
          1,     // operatornumber
          GeneratorType.HillClimbing
      );
    }

    // Verifica que se inicializaron estructuras por flags
    assertNotNull(getField(s, "listStates"));
    assertNotNull(getField(s, "listBest"));
    assertNotNull(s.notDominated); // Dominance creado

    // bestState debe existir
    assertNotNull(s.getBestState());
  }
}
