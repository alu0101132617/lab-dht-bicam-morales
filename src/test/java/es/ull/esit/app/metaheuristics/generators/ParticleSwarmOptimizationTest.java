package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the {@link ParticleSwarmOptimization} class.
 */
class ParticleSwarmOptimizationTest {

  /**
   * Helper to create a simple evaluation list.
   */
  private ArrayList<Double> eval(double value) {
    ArrayList<Double> list = new ArrayList<>();
    list.add(value);
    return list;
  }

  /**
   * Helper to set private static int fields via reflection.
   */
  private void setStaticIntField(Class<?> clazz, String fieldName, int value) {
    try {
      Field f = clazz.getDeclaredField(fieldName);
      f.setAccessible(true);
      f.setInt(null, value);
    } catch (Exception e) {
      fail("Failed to set static field " + fieldName + ": " + e.getMessage());
    }
  }

  @Test
  void constructorShouldInitialiseWithNoParticlesWhenNoRandomSearchStates() {
    // Force zero swarms and zero particles per swarm
    setStaticIntField(ParticleSwarmOptimization.class, "coutSwarm", 0);
    setStaticIntField(ParticleSwarmOptimization.class, "countParticleBySwarm", 0);
    setStaticIntField(ParticleSwarmOptimization.class, "countRef", 0);

    // Ensure RandomSearch reference list is empty
    RandomSearch.setListStateReference(new ArrayList<>());

    // Mock Strategy.getStrategy() to avoid NPE in getListStateRef()
    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    when(strategyMock.getMapGenerators()).thenReturn(map);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      ParticleSwarmOptimization pso = new ParticleSwarmOptimization();

      assertNotNull(pso, "PSO instance should not be null");
      assertTrue(pso.getListParticle().isEmpty(),
          "With no RandomSearch states and no swarms, particle list must be empty");
      assertEquals(0, ParticleSwarmOptimization.getLBest() == null
              ? 0
              : ParticleSwarmOptimization.getLBest().length,
          "lBest should be empty when no swarms exist");
      assertNull(pso.getReference(),
          "Reference should be null when no global best is defined");
      assertEquals(GeneratorType.ParticleSwarmOptimization, pso.getType(),
          "Type should be ParticleSwarmOptimization");
      assertEquals(50.0f, pso.getWeight(), 0.0001,
          "Default PSO weight should be 50");
    }
  }

  @Test
  void setInitialReferenceShouldSetGBestWhenNull() {
    // Basic mock so constructor does not crash
    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    when(strategyMock.getMapGenerators()).thenReturn(map);

    ParticleSwarmOptimization pso;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      pso = new ParticleSwarmOptimization();

      // Force gBest to null using setter
      pso.setGBest(null);

      State initial = new State();
      pso.setInitialReference(initial);

      assertSame(initial, pso.getStateReferencePSO(),
          "State reference should be set to initial reference");
      assertSame(initial, pso.getGBest(),
          "gBest should be set to initial reference when previously null");
    }
  }

  @Test
  void awardUpdateREFShouldRespectProblemType() {
    // Basic mock so constructor does not crash
    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    when(strategyMock.getMapGenerators()).thenReturn(map);

    ParticleSwarmOptimization pso;
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      pso = new ParticleSwarmOptimization();
    }

    // Candidate with evaluation 10
    State candidate = new State();
    candidate.setEvaluation(eval(10.0));

    // Current gBest with evaluation 5
    State gBest = new State();
    gBest.setEvaluation(eval(5.0));
    pso.setGBest(gBest);

    Problem problemMock = mock(Problem.class);
    Strategy strategyMock2 = mock(Strategy.class);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      // Maximization: 10 > 5 -> true
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
      when(strategyMock2.getProblem()).thenReturn(problemMock);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock2);

      assertTrue(pso.awardUpdateREF(candidate),
          "In maximization, candidate with higher eval should be considered better than gBest");

      // Minimization: 10 > 5 -> false
      reset(problemMock, strategyMock2);
      Problem problemMock2 = mock(Problem.class);
      Strategy strategyMock3 = mock(Strategy.class);
      when(problemMock2.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
      when(strategyMock3.getProblem()).thenReturn(problemMock2);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock3);

      assertFalse(pso.awardUpdateREF(candidate),
          "In minimization, candidate with higher eval should NOT be better than gBest");
    }
  }

  @Test
  void gBestInicialShouldReturnBestAccordingToProblemType() {
    // 1) Construimos una instancia PSO con mocks mínimos para que el constructor no falle
    Strategy strategyCtor = mock(Strategy.class);
    when(strategyCtor.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> emptyMap = new TreeMap<>();
    when(strategyCtor.getMapGenerators()).thenReturn(emptyMap);

    ParticleSwarmOptimization pso;
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyCtor);
      pso = new ParticleSwarmOptimization();
    }

    // 2) Preparamos lBest manualmente DESPUÉS del constructor (éste lo pisa)
    State s1 = new State();
    s1.setEvaluation(eval(3.0));

    State s2 = new State();
    s2.setEvaluation(eval(7.0));

    try {
      Field lBestField = ParticleSwarmOptimization.class.getDeclaredField("lBest");
      lBestField.setAccessible(true);
      lBestField.set(null, new State[] { s1, s2 });
    } catch (Exception e) {
      fail("Failed to set static lBest via reflection: " + e.getMessage());
    }

    // 3) Ahora testeamos gBestInicial() para Max y Min con mocks separados

    // --- Maximización ---
    Problem problemMax = mock(Problem.class);
    when(problemMax.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
    Strategy strategyMax = mock(Strategy.class);
    when(strategyMax.getProblem()).thenReturn(problemMax);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMax);

      State bestMax = pso.gBestInicial();
      assertSame(s2, bestMax,
          "In maximization, gBestInicial should return state with highest eval (s2)");
    }

    // --- Minimización ---
    Problem problemMin = mock(Problem.class);
    when(problemMin.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
    Strategy strategyMin = mock(Strategy.class);
    when(strategyMin.getProblem()).thenReturn(problemMin);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMin);

      State bestMin = pso.gBestInicial();
      assertSame(s1, bestMin,
          "In minimization, gBestInicial should return state with lowest eval (s1)");
    }
  }

  @Test
  void updateReferenceShouldBeNoOpWhenNoParticles() {
    // Force zero swarms and zero particles per swarm
    setStaticIntField(ParticleSwarmOptimization.class, "coutSwarm", 0);
    setStaticIntField(ParticleSwarmOptimization.class, "countParticleBySwarm", 0);
    setStaticIntField(ParticleSwarmOptimization.class, "countRef", 0);

    RandomSearch.setListStateReference(new ArrayList<>());

    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    when(strategyMock.getMapGenerators()).thenReturn(map);

    ParticleSwarmOptimization pso;

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      pso = new ParticleSwarmOptimization();
    }

    State candidate = new State();
    assertDoesNotThrow(() -> pso.updateReference(candidate, 1),
        "updateReference should be a no-op and not throw when there are no particles");
    assertTrue(pso.getListParticle().isEmpty(),
        "Particle list must remain empty after updateReference");
  }

  @Test
  void getSonListShouldReturnCurrentParticleStates() {
    // Mock Strategy para que el constructor de PSO no rompa
    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    when(strategyMock.getMapGenerators()).thenReturn(map);

    ParticleSwarmOptimization pso;
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      pso = new ParticleSwarmOptimization();
    }

    // Construimos manualmente algunas partículas con estados actuales
    Particle p1 = new Particle();
    State s1 = new State();
    p1.setStateActual(s1);

    Particle p2 = new Particle();
    State s2 = new State();
    p2.setStateActual(s2);

    List<Particle> particles = new ArrayList<>();
    particles.add(p1);
    particles.add(p2);

    pso.setListParticle(particles);

    List<State> sons = pso.getSonList();
    assertEquals(2, sons.size(),
        "getSonList should return one state per particle with non-null current state");
    assertTrue(sons.contains(s1));
    assertTrue(sons.contains(s2));
  }

  @Test
  void generateShouldReturnNullWhenNoParticles() throws IllegalArgumentException,
      SecurityException, ClassNotFoundException, InstantiationException,
      IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    // Basic mock so constructor does not explode
    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getListKey()).thenReturn(new ArrayList<>());
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    when(strategyMock.getMapGenerators()).thenReturn(map);

    ParticleSwarmOptimization pso;
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
      pso = new ParticleSwarmOptimization();
    }

    State result = pso.generate(1);
    assertNull(result, "generate should return null when there are no particles");
  }
}
