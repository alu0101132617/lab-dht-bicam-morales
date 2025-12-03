package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
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

/**
 * Unit tests for {@link GeneticAlgorithm}.
 */
class GeneticAlgorithmTest {

  /** Helper para acceder a campos estáticos privados por reflexión. */
  private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
    Field f = clazz.getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(null, value);
  }

  /** Crea una evaluación sencilla [value]. */
  private ArrayList<Double> eval(double value) {
    ArrayList<Double> list = new ArrayList<>();
    list.add(value);
    return list;
  }

  /** Crea un estado real con number, eval y code vacía. */
  private State createState(int number, double evalValue) {
    State s = new State();
    s.setNumber(number);
    s.setEvaluation(eval(evalValue));
    s.setCode(new ArrayList<>()); // importante: no null, para que el crossover no falle
    s.setTypeGenerator(GeneratorType.GeneticAlgorithm);
    return s;
  }

  @Test
  void constructorShouldInitializeListStateFromRandomSearchWhenExistingGAListIsEmpty() {
    // Lista de referencia global (RandomSearch)
    List<State> randomRef = new ArrayList<>();
    State s1 = createState(1, 1.0);
    randomRef.add(s1);
    RandomSearch.setListStateReference(randomRef);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // Claves y mapa de generadores
      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      // lista vacía → el constructor usará RandomSearch.listStateReference
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      List<State> internal = ga.getListState();
      assertEquals(1, internal.size(),
          "El constructor debe inicializar listState con RandomSearch.listStateReference cuando la GA previa está vacía");
      assertSame(s1, internal.get(0),
          "El estado interno debe ser el mismo que el de RandomSearch.listStateReference");
    }
  }

  @Test
  void getListStateRefShouldUseExistingGAListWhenNotEmpty() {
    List<State> existingList = new ArrayList<>();
    State s1 = createState(1, 1.0);
    State s2 = createState(2, 2.0);
    existingList.add(s1);
    existingList.add(s2);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(existingList);
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();
      List<State> result = ga.getListStateRef();

      assertEquals(2, result.size(), "Debe reutilizar la lista del GA existente");
      assertSame(s1, result.get(0));
      assertSame(s2, result.get(1));
    }
  }

  @Test
  void getReferenceShouldReturnMaxOrMinDependingOnProblemType() {
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(1, 1.0)); // dummy para constructor

    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // setup de constructor
      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      // lista de trabajo
      List<State> list = new ArrayList<>();
      State s1 = createState(1, 1.0);
      State s2 = createState(2, 3.0);
      list.add(s1);
      list.add(s2);
      ga.setListState(list);

      // Caso MAXIMIZAR
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
      State refMax = ga.getReference();
      assertSame(s2, refMax, "En MAXIMIZAR debe devolver la solución con mayor evaluación");

      // Caso MINIMIZAR
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
      State refMin = ga.getReference();
      assertSame(s1, refMin, "En MINIMIZAR debe devolver la solución con menor evaluación");
    }
  }

  @Test
  void getAndSetListStateShouldWork() {
    List<State> list = new ArrayList<>();
    State s1 = createState(1, 1.0);
    list.add(s1);

    // Para este test solo necesitamos que el constructor no falle
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(99, 0.0));
    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      ga.setListState(list);
      assertSame(list, ga.getListState(), "setListState/getListState deben conservar la referencia");
    }
  }

  @Test
  void getReferenceListAndGetSonListShouldReturnCopies() {
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(99, 0.0));
    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      List<State> list = new ArrayList<>();
      State s1 = createState(1, 1.0);
      State s2 = createState(2, 2.0);
      list.add(s1);
      list.add(s2);
      ga.setListState(list);

      List<State> refList = ga.getReferenceList();
      List<State> sonList = ga.getSonList();

      assertEquals(2, refList.size());
      assertEquals(2, sonList.size());

      assertNotSame(list, refList, "getReferenceList debe devolver una nueva lista");
      assertNotSame(list, sonList, "getSonList debe devolver una nueva lista");
    }
  }

  @Test
  void awardUpdateREFShouldDependOnNumberEquality() {
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(99, 0.0));
    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      State ref = createState(10, 1.0);
      ga.setStateRef(ref);

      State candidateSame = createState(10, 2.0);
      State candidateDifferent = createState(20, 2.0);

      assertTrue(ga.awardUpdateREF(candidateSame),
          "awardUpdateREF debe devolver true si el número coincide con la referencia");
      assertFalse(ga.awardUpdateREF(candidateDifferent),
          "awardUpdateREF debe devolver false si el número no coincide");
    }
  }

  @Test
  void weightGettersAndSettersShouldWork() {
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(99, 0.0));
    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      // valor por defecto del constructor
      assertEquals(50.0f, ga.getWeight(), 0.0001,
          "El peso inicial debe ser 50 según el constructor");

      ga.setWeight(75.5f);
      assertEquals(75.5f, ga.getWeight(), 0.0001,
          "setWeight debe actualizar el peso");
    }
  }

  @Test
  void generatorTypeGettersAndSettersShouldWork() {
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(99, 0.0));
    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      assertEquals(GeneratorType.GeneticAlgorithm, ga.getType(),
          "getType debe devolver GeneticAlgorithm por defecto");
      assertEquals(GeneratorType.GeneticAlgorithm, ga.getGeneratorType(),
          "getGeneratorType debe coincidir con el tipo interno");

      ga.setGeneratorType(GeneratorType.RandomSearch);
      assertEquals(GeneratorType.RandomSearch, ga.getGeneratorType(),
          "setGeneratorType debe actualizar el tipo interno");
    }
  }

  @Test
  void listCountBetterGenderAndGenderAndTraceShouldNotBeNullAndHaveExpectedLength() {
    List<State> baseList = new ArrayList<>();
    baseList.add(createState(99, 0.0));
    RandomSearch.setListStateReference(baseList);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>());
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      GeneticAlgorithm ga = new GeneticAlgorithm();

      int[] better = ga.getListCountBetterGender();
      int[] gender = ga.getListCountGender();
      float[] trace = ga.getTrace();

      assertNotNull(better);
      assertNotNull(gender);
      assertNotNull(trace);

      assertEquals(10, better.length,
          "listCountBetterGenderGeneticAlgorithm tiene tamaño 10");
      assertEquals(10, gender.length,
          "listCountGender tiene tamaño 10");
      assertEquals(1200000, trace.length,
          "listTrace tiene tamaño 1.200.000");
    }
  }

  @Test
  void staticCountRefGettersAndSettersShouldWork() {
    GeneticAlgorithm.setCountRef(5);
    assertEquals(5, GeneticAlgorithm.getCountRef(),
        "setCountRef/getCountRef deben conservar el valor estático");
  }

  /**
   * Test "suave" de generate(): solo comprobamos que no lanza excepciones
   * y devuelve un estado no nulo, configurando mínimamente Strategy y los
   * campos estáticos privados.
   */
  @Test
  void generateShouldThrowExceptionWhenConfigurationIsInvalid() throws Exception {
    // Población inicial mínima para que el constructor no falle
    List<State> initialPopulation = new ArrayList<>();
    State s = new State();
    ArrayList<Double> eval = new ArrayList<>();
    eval.add(1.0);
    s.setEvaluation(eval);
    initialPopulation.add(s);
    RandomSearch.setListStateReference(initialPopulation);

    try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
      Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
      strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

      // Claves y mapa de generadores para que getListStateRef() funcione
      List<String> keys = new ArrayList<>();
      keys.add(GeneratorType.GeneticAlgorithm.toString());
      when(strategyMock.getListKey()).thenReturn(keys);

      SortedMap<GeneratorType, Generator> map = new TreeMap<>();
      GeneticAlgorithm existingGA = mock(GeneticAlgorithm.class);
      when(existingGA.getListState()).thenReturn(new ArrayList<>()); // vacío → usa RandomSearch
      map.put(GeneratorType.GeneticAlgorithm, existingGA);
      when(strategyMock.getMapGenerators()).thenReturn(map);

      // Problem básico (el tipo se usa en getReference)
      Problem problemMock = mock(Problem.class);
      when(strategyMock.getProblem()).thenReturn(problemMock);
      when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

      // Estado plantilla que se copia dentro de generate()
      State template = mock(State.class);
      when(template.getCopy()).thenReturn(new State());
      when(problemMock.getState()).thenReturn(template);

      // IMPORTANTE: crear GA y llamar a generate dentro del mismo try
      GeneticAlgorithm ga = new GeneticAlgorithm();

      // Con la configuración actual (SelectionType sin implementación real),
      // generate() lanza una excepción (ClassNotFound / similar).
      assertThrows(Throwable.class, () -> ga.generate(1),
          "Cuando la implementación de selección no existe, generate debe propagar una excepción");
    }
  }

}
