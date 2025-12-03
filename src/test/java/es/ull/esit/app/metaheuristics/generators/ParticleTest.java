package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the {@link Particle} class.
 */
class ParticleTest {

  /**
   * Helper method to create a simple evaluation list.
   */
  private ArrayList<Double> eval(double... values) {
    ArrayList<Double> list = new ArrayList<>();
    for (double v : values) {
      list.add(v);
    }
    return list;
  }

  @Test
  void defaultConstructorShouldInitialiseFields() {
    Particle particle = new Particle();

    assertNotNull(particle.getStateActual(), "Current state should not be null");
    assertNotNull(particle.getStatePBest(), "Personal best state should not be null");
    assertNotNull(particle.getVelocity(), "Velocity should not be null");
    assertEquals(GeneratorType.ParticleSwarmOptimization, particle.getType(),
        "Generator type should be PSO");
    assertEquals(50.0f, particle.getWeight(), 0.0001, "Default weight should be 50");
    assertNotNull(particle.getTrace(), "Trace array should not be null");

    // getReferenceList() adds current pBest to the internal list
    assertEquals(1, particle.getReferenceList().size(),
        "First call to getReferenceList should add exactly one pBest");
  }

  @Test
  void fullConstructorShouldCopyVelocityDefensively() {
    State pBest = new State();
    State actual = new State();
    List<Object> velocity = new ArrayList<>();
    velocity.add(1.0);
    velocity.add(2.0);

    Particle particle = new Particle(pBest, actual, velocity);

    assertEquals(velocity, particle.getVelocity(),
        "Velocity should be copied from constructor argument");
    // Changing original list should not affect particle's internal velocity
    velocity.set(0, 99.0);
    assertNotEquals(velocity, particle.getVelocity(),
        "Velocity inside particle should be a defensive copy");
  }

  @Test
  void generateShouldUpdateStateCodeInContinuousMode() {
    // ---- Template state used by Strategy.getStrategy().getProblem().getState() ----
    State templateState = new State();
    ArrayList<Object> templateCode = new ArrayList<>();
    templateCode.add(0.0);
    templateCode.add(0.0);
    templateState.setCode(templateCode);

    Problem problemMock = mock(Problem.class);
    when(problemMock.getState()).thenReturn(templateState);

    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getProblem()).thenReturn(problemMock);
    when(strategyMock.getCountMax()).thenReturn(100); // avoid division by zero

    // ---- Static mock for Strategy.getStrategy() ----
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // ---- Configure static PSO parameters ----
      ParticleSwarmOptimization.setWmax(0.9);
      ParticleSwarmOptimization.setWmin(0.2);
      ParticleSwarmOptimization.setLearning1(2);
      ParticleSwarmOptimization.setLearning2(2);
      ParticleSwarmOptimization.setCountCurrentIterPSO(1);
      ParticleSwarmOptimization.setBinary(false);

      // Create lBest[0] for the swarm
      State lBestState = new State();
      ArrayList<Object> lBestCode = new ArrayList<>();
      lBestCode.add(1.0);
      lBestCode.add(1.0);
      lBestState.setCode(lBestCode);
      lBestState.setEvaluation(eval(1.0));

      // Set static lBest via reflection (there is only a getter in the class)
      try {
        java.lang.reflect.Field lBestField =
            ParticleSwarmOptimization.class.getDeclaredField("lBest");
        lBestField.setAccessible(true);
        lBestField.set(null, new State[] { lBestState });
      } catch (Exception e) {
        fail("Failed to set static lBest via reflection: " + e.getMessage());
      }

      // ---- Create a particle with pBest and actual states ----
      State pBest = new State();
      ArrayList<Object> pCode = new ArrayList<>();
      pCode.add(1.0);
      pCode.add(2.0);
      pBest.setCode(pCode);
      pBest.setEvaluation(eval(1.0));

      State actual = new State();
      ArrayList<Object> aCode = new ArrayList<>();
      aCode.add(0.5);
      aCode.add(1.5);
      actual.setCode(aCode);
      actual.setEvaluation(eval(0.5));

      Particle particle = new Particle(pBest, actual, null);

      State result = assertDoesNotThrow(() -> particle.generate(1),
          "generate should not throw in continuous mode with proper setup");

      assertNotNull(result, "generate must return a non-null state");
      assertNotNull(result.getCode(), "Updated state code must not be null");
      assertEquals(2, result.getCode().size(),
          "Updated code size must match original code size");
    }
  }

  @Test
  void updateReferenceShouldUpdatePBestInMaximization() {
    // pBest worse, actual better
    State pBest = new State();
    pBest.setCode(new ArrayList<>(Arrays.asList(0.0)));
    pBest.setEvaluation(eval(5.0));

    State actual = new State();
    actual.setCode(new ArrayList<>(Arrays.asList(1.0)));
    actual.setEvaluation(eval(10.0));

    Particle particle = new Particle(pBest, actual, null);

    // Strategy mock with MAXIMIZAR
    Problem problemMock = mock(Problem.class);
    when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getProblem()).thenReturn(problemMock);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      assertDoesNotThrow(() -> particle.updateReference(null, 1),
          "updateReference should not throw in maximization case");

      State updatedPBest = particle.getStatePBest();
      assertEquals(actual.getEvaluation().get(0),
          updatedPBest.getEvaluation().get(0),
          1e-6,
          "In maximization, pBest should be updated to better actual state");
      assertEquals(actual.getCode(), updatedPBest.getCode(),
          "pBest code should match actual code after update");
    }
  }

  @Test
  void updateReferenceShouldUseCandidateInMinimizationWhenBetter() {
    // pBest is worst, candidate is best, actual is in between
    State pBest = new State();
    pBest.setCode(new ArrayList<>(Arrays.asList(0.0)));
    pBest.setEvaluation(eval(10.0));

    State actual = new State();
    actual.setCode(new ArrayList<>(Arrays.asList(1.0)));
    actual.setEvaluation(eval(8.0));

    State candidate = new State();
    candidate.setCode(new ArrayList<>(Arrays.asList(2.0)));
    candidate.setEvaluation(eval(3.0)); // best value for minimization

    Particle particle = new Particle(pBest, actual, null);

    Problem problemMock = mock(Problem.class);
    when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

    Strategy strategyMock = mock(Strategy.class);
    when(strategyMock.getProblem()).thenReturn(problemMock);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      assertDoesNotThrow(() -> particle.updateReference(candidate, 1),
          "updateReference should not throw in minimization case");

      State updatedPBest = particle.getStatePBest();
      assertEquals(candidate.getEvaluation().get(0),
          updatedPBest.getEvaluation().get(0),
          1e-6,
          "In minimization, pBest should be updated to best (lowest) evaluation");
      assertEquals(candidate.getCode(), updatedPBest.getCode(),
          "pBest code should match candidate code after update");
    }
  }

  @Test
  void awardUpdateREFShouldReturnFalseWhenEvaluationsMissing() {
    Particle particle = new Particle();
    State candidate = new State(); // no evaluation set

    assertFalse(particle.awardUpdateREF(candidate),
        "awardUpdateREF must return false when evaluations are missing");
  }

  @Test
  void awardUpdateREFShouldRespectProblemType() {
    State pBest = new State();
    pBest.setEvaluation(eval(5.0));

    State candidate = new State();
    candidate.setEvaluation(eval(7.0)); // better for max, worse for min

    Particle particle = new Particle(pBest, new State(), null);

    Problem problemMock = mock(Problem.class);
    Strategy strategyMock = mock(Strategy.class);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      // Maximization
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      assertTrue(particle.awardUpdateREF(candidate),
          "In maximization, higher evaluation should be considered improvement");

      // Minimization
      reset(problemMock, strategyMock);
      Problem problemMock2 = mock(Problem.class);
      Strategy strategyMock2 = mock(Strategy.class);
      when(problemMock2.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
      when(strategyMock2.getProblem()).thenReturn(problemMock2);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock2);

      assertFalse(particle.awardUpdateREF(candidate),
          "In minimization, higher evaluation should NOT be considered improvement");
    }
  }

  @Test
  void getSonListShouldReturnCurrentStateWhenNotNull() {
    Particle particle = new Particle();
    State current = new State();
    particle.setStateActual(current);

    List<State> sons = particle.getSonList();
    assertEquals(1, sons.size(), "Son list should contain exactly one state");
    assertSame(current, sons.get(0),
        "Son list should contain the current state of the particle");
  }
}
