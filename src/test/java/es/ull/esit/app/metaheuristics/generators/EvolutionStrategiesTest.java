package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class EvolutionStrategiesTest {

  /** Crea un estado real con una evaluación sencilla (ArrayList<Double>). */
  private State createStateWithEval(double value) {
    State s = new State();
    ArrayList<Double> eval = new ArrayList<>();
    eval.add(value);
    s.setEvaluation(eval);
    return s;
  }

  /**
   * Configura el Strategy estático para que el constructor de EvolutionStrategies
   * pueda llamar a getListStateRef() sin reventar.
   * 
   * - getListKey() contiene EvolutionStrategies.
   * - getMapGenerators() devuelve un generador ES existente mockeado.
   * - si ese generador tiene lista vacía → se usará
   * RandomSearch.listStateReference.
   */
  private void prepareBasicStrategyForConstructor(Strategy strategyMock, List<State> randomList) {

    // Lista de claves: solo EvolutionStrategies
    List<String> keys = new ArrayList<>();
    keys.add(GeneratorType.EvolutionStrategies.toString());
    when(strategyMock.getListKey()).thenReturn(keys);

    // Mapa de generadores con una entrada EvolutionStrategies mockeada
    SortedMap<GeneratorType, Generator> map = new TreeMap<>();
    EvolutionStrategies existingES = mock(EvolutionStrategies.class);
    when(existingES.getListStateReference()).thenReturn(new ArrayList<>()); // vacío → usa RandomSearch
    map.put(GeneratorType.EvolutionStrategies, existingES);
    when(strategyMock.getMapGenerators()).thenReturn(map);

    // Lista estática de RandomSearch
    RandomSearch.setListStateReference(randomList);
  }

  @Test
  void constructorShouldInitializeReferenceListFromRandomSearchWhenEmptyInMap() {
    List<State> randomList = new ArrayList<>();
    State s1 = createStateWithEval(1.0);
    randomList.add(s1);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // No necesitamos Problem aquí para el constructor
      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      List<State> ref = es.getListStateReference();
      assertEquals(1, ref.size(),
          "La lista de referencia debería inicializarse con RandomSearch.listStateReference");
      assertSame(s1, ref.get(0),
          "El estado de referencia debe ser el mismo que el de RandomSearch");
    }
  }

  @Test
  void getReferenceShouldReturnMaxOrMinDependingOnProblemType() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0)); // para que el constructor tenga algo

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      // Sobrescribimos la lista de referencia con valores conocidos
      List<State> list = new ArrayList<>();
      State s1 = createStateWithEval(1.0);
      State s2 = createStateWithEval(3.0);
      list.add(s1);
      list.add(s2);
      es.setListStateReference(list);

      // MAXIMIZAR       → estado con eval 3.0
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR      );
      State refMax = es.getReference();
      assertEquals(3.0, refMax.getEvaluation().get(0), 1e-6,
          "En MAXIMIZAR       debe devolver el estado con mayor evaluación");

      // MINIMIZAR    → estado con eval 1.0
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR   );
      State refMin = es.getReference();
      assertEquals(1.0, refMin.getEvaluation().get(0), 1e-6,
          "En MINIMIZAR    debe devolver el estado con menor evaluación");
    }
  }

  @Test
  void getReferenceListShouldReturnCopyOfInternalList() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      List<State> internal = new ArrayList<>();
      internal.add(createStateWithEval(1.0));
      internal.add(createStateWithEval(2.0));
      es.setListStateReference(internal);

      List<State> returned = es.getReferenceList();

      assertEquals(internal.size(), returned.size(),
          "La lista devuelta debe tener el mismo tamaño");
      assertNotSame(internal, returned,
          "getReferenceList debe devolver una NUEVA lista, no la referencia interna");
      assertSame(internal.get(0), returned.get(0),
          "Los elementos deben ser los mismos objetos State");
    }
  }

  @Test
  void awardUpdateREFShouldReturnTrueOrFalseAccordingToProblemTypeAndEvaluations() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      // Referencia con valor 2.0
      List<State> list = new ArrayList<>();
      State ref = createStateWithEval(2.0);
      list.add(ref);
      es.setListStateReference(list);

      State better = createStateWithEval(3.0);
      State worse = createStateWithEval(1.0);

      // MAXIMIZAR      
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR      );
      assertTrue(es.awardUpdateREF(better),
          "En MAXIMIZAR       debe devolver true si el candidato es mejor que la referencia");
      assertFalse(es.awardUpdateREF(worse),
          "En MAXIMIZAR       debe devolver false si el candidato es peor que la referencia");

      // MINIMIZAR    
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR    );
      assertTrue(es.awardUpdateREF(worse),
          "En MINIMIZAR    debe devolver true si el candidato es menor que la referencia");
      assertFalse(es.awardUpdateREF(better),
          "En MINIMIZAR    debe devolver false si el candidato es mayor que la referencia");
    }
  }

  @Test
  void weightGettersAndSettersShouldWork() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      assertEquals(50.0f, es.getWeight(), 1e-6,
          "El peso inicial debe ser 50 según el constructor");

      es.setWeight(80.0f);
      assertEquals(80.0f, es.getWeight(), 1e-6,
          "setWeight debe actualizar el peso");
    }
  }

  @Test
  void typeGettersAndSettersShouldWork() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      assertEquals(GeneratorType.EvolutionStrategies, es.getType(),
          "getType debe devolver EvolutionStrategies");
      assertEquals(GeneratorType.EvolutionStrategies, es.getTypeGenerator(),
          "getTypeGenerator debe devolver EvolutionStrategies inicialmente");

      es.setTypeGenerator(GeneratorType.GeneticAlgorithm);
      assertEquals(GeneratorType.GeneticAlgorithm, es.getTypeGenerator(),
          "setTypeGenerator debe actualizar el tipo");
    }
  }

  @Test
  void stateReferenceSettersShouldWork() {
    // Mock estático de Strategy para evitar NullPointer y controlar el tipo de
    // problema
    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // Problem -> MAXIMIZAR     
      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR     );

      // Para el constructor: lista de keys y mapa de generadores
      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.EvolutionStrategies.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      EvolutionStrategies existingES = mock(EvolutionStrategies.class);
      when(existingES.getListStateReference()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.EvolutionStrategies, existingES);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      // Lista de referencia inicial de RandomSearch para el constructor
      List<State> randomList = new ArrayList<>();
      State base = new State();
      ArrayList<Double> evalBase = new ArrayList<>();
      evalBase.add(0.0);
      base.setEvaluation(evalBase);
      randomList.add(base);
      RandomSearch.setListStateReference(randomList);

      // Creamos la instancia real de EvolutionStrategies
      EvolutionStrategies es = new EvolutionStrategies();

      // Creamos dos estados: uno "peor" y uno "mejor"
      State worse = new State();
      ArrayList<Double> evalWorse = new ArrayList<>();
      evalWorse.add(1.0);
      worse.setEvaluation(evalWorse);

      State best = new State();
      ArrayList<Double> evalBest = new ArrayList<>();
      evalBest.add(10.0);
      best.setEvaluation(evalBest);

      // La lista de referencia contiene ambos, siendo 'best' claramente el mejor
      List<State> refList = new ArrayList<>();
      refList.add(worse);
      refList.add(best);
      es.setListStateReference(refList);

      // Fijamos explícitamente la referencia al mejor
      es.setStateRef(best);

      // getReference debe devolver el mejor de la lista, que coincide con 'best'
      State result = es.getReference();
      assertSame(best, result,
          "Después de setStateRef y siendo ese estado el de mejor evaluación, getReference debe devolverlo");
    }
  }

  @Test
  void listCountBetterGenderGenderAndTraceShouldBeInitializedWithExpectedLengths() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();

      int[] better = es.getListCountBetterGender();
      int[] gender = es.getListCountGender();
      float[] trace = es.getTrace();

      assertNotNull(better, "getListCountBetterGender no debe devolver null");
      assertNotNull(gender, "getListCountGender no debe devolver null");
      assertNotNull(trace, "getTrace no debe devolver null");

      assertEquals(10, better.length,
          "listCountBetterGenderEvolutionStrategies debe tener longitud 10");
      assertEquals(10, gender.length,
          "listCountGender debe tener longitud 10");
      assertEquals(1200000, trace.length,
          "listTrace debe tener longitud 1.200.000");

      // Además, comprobamos que el primer elemento del trace coincide con el peso
      assertEquals(es.getWeight(), trace[0], 1e-6,
          "El primer elemento de trace debe ser el peso inicial");
    }
  }

  @Test
  void staticCountRefGettersAndSettersShouldWork() {
    // No necesitamos mockear Strategy para probar los estáticos
    EvolutionStrategies.setCountRef(5);
    assertEquals(5, EvolutionStrategies.getCountRef(),
        "getCountRef debe devolver el valor establecido con setCountRef");

    EvolutionStrategies.setCountRef(0);
    assertEquals(0, EvolutionStrategies.getCountRef(),
        "setCountRef debe permitir cambiar el valor");
  }

  @Test
  void getSonListShouldReturnEmptyList() {
    List<State> randomList = new ArrayList<>();
    randomList.add(createStateWithEval(0.0));

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      prepareBasicStrategyForConstructor(strategyMock, randomList);

      EvolutionStrategies es = new EvolutionStrategies();
      List<State> sons = es.getSonList();

      assertNotNull(sons, "getSonList no debe devolver null");
      assertTrue(sons.isEmpty(), "En EvolutionStrategies getSonList debería devolver una lista vacía");
    }
  }
}
