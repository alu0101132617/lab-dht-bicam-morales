package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.ull.esit.app.problem.definition.State;

/**
 * Tests for the abstract class {@link Generator} using a simple concrete
 * implementation (DummyGenerator).
 */
class GeneratorTest {

  /**
   * Simple concrete implementation of Generator to be able to test
   * the abstract API.
   */
  private static class DummyGenerator extends Generator {

    private State reference = new State();
    private final List<State> referenceList = new ArrayList<>();
    private float weight;
    private final float[] trace = new float[] { 1.0f, 2.0f, 3.0f };
    private final int[] better = new int[] { 1, 2 };
    private final int[] gender = new int[] { 3, 4 };

    @Override
    public State generate(Integer operatornumber)
        throws IllegalArgumentException, SecurityException, ClassNotFoundException,
               InstantiationException, IllegalAccessException,
               InvocationTargetException, NoSuchMethodException {
      // Para este dummy simplemente devolvemos la referencia actual
      return reference;
    }

    @Override
    public void updateReference(State stateCandidate, Integer countIterationsCurrent)
        throws IllegalArgumentException, SecurityException, ClassNotFoundException,
               InstantiationException, IllegalAccessException,
               InvocationTargetException, NoSuchMethodException {
      // Guardamos el candidato como nueva referencia y lo añadimos a la lista
      this.reference = stateCandidate;
      this.referenceList.add(stateCandidate);
    }

    @Override
    public State getReference() {
      return reference;
    }

    @Override
    public void setInitialReference(State stateInitialRef) {
      this.reference = stateInitialRef;
      // También lo dejamos en la lista para poder comprobar getReferenceList
      this.referenceList.clear();
      this.referenceList.add(stateInitialRef);
    }

    @Override
    public GeneratorType getType() {
      // Devolvemos cualquier valor válido del enum
      return GeneratorType.RandomSearch;
    }

    @Override
    public List<State> getReferenceList() {
      return new ArrayList<>(referenceList);
    }

    @Override
    public List<State> getSonList() {
      // Este dummy no mantiene hijos; devolvemos lista vacía
      return new ArrayList<>();
    }

    @Override
    public boolean awardUpdateREF(State stateCandidate) {
      // Implementación trivial: siempre true
      return true;
    }

    @Override
    public void setWeight(float weight) {
      this.weight = weight;
    }

    @Override
    public float getWeight() {
      return this.weight;
    }

    @Override
    public float[] getTrace() {
      return this.trace;
    }

    @Override
    public int[] getListCountBetterGender() {
      return this.better;
    }

    @Override
    public int[] getListCountGender() {
      return this.gender;
    }
  }

  @Test
  void initialReferenceAndReferenceListShouldBeConsistent()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    DummyGenerator gen = new DummyGenerator();

    State initial = new State();
    gen.setInitialReference(initial);

    // getReference debe devolver el mismo objeto
    assertSame(initial, gen.getReference(),
        "getReference debe devolver el estado pasado a setInitialReference");

    // getReferenceList debe contener ese estado
    List<State> refList = gen.getReferenceList();
    assertEquals(1, refList.size(), "La lista de referencia debe contener exactamente un estado");
    assertSame(initial, refList.get(0),
        "La lista de referencia debe contener el estado inicial");
  }

  @Test
  void updateReferenceShouldChangeReferenceAndAddToList()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    DummyGenerator gen = new DummyGenerator();

    State initial = new State();
    State candidate = new State();

    gen.setInitialReference(initial);
    gen.updateReference(candidate, 1);

    assertSame(candidate, gen.getReference(),
        "Después de updateReference, la referencia debe ser el candidato");

    List<State> refList = gen.getReferenceList();
    assertEquals(2, refList.size(),
        "Después de updateReference, la lista debe contener dos estados");
    assertTrue(refList.contains(candidate),
        "La lista de referencia debe contener el candidato");
  }

  @Test
  void generateShouldReturnCurrentReference()
      throws IllegalArgumentException, SecurityException, ClassNotFoundException,
             InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException {

    DummyGenerator gen = new DummyGenerator();

    State initial = new State();
    gen.setInitialReference(initial);

    State generated = gen.generate(0);
    assertSame(initial, generated,
        "En DummyGenerator, generate debe devolver la referencia actual");
  }

  @Test
  void typeGetterShouldReturnConfiguredType() {
    DummyGenerator gen = new DummyGenerator();
    assertEquals(GeneratorType.RandomSearch, gen.getType(),
        "getType debe devolver el tipo configurado en DummyGenerator");
  }

  @Test
  void weightGettersAndSettersShouldWork() {
    DummyGenerator gen = new DummyGenerator();

    gen.setWeight(42.5f);
    assertEquals(42.5f, gen.getWeight(), 0.0001,
        "setWeight/getWeight deben conservar el valor asignado");
  }

  @Test
  void traceAndCountersShouldBeAccessibleAndNotNull() {
    DummyGenerator gen = new DummyGenerator();

    float[] trace = gen.getTrace();
    int[] better = gen.getListCountBetterGender();
    int[] gender = gen.getListCountGender();

    assertNotNull(trace, "getTrace no debe devolver null");
    assertTrue(trace.length > 0, "La traza debe tener al menos un elemento");

    assertNotNull(better, "getListCountBetterGender no debe devolver null");
    assertEquals(2, better.length, "En DummyGenerator el array de mejores tiene longitud 2");

    assertNotNull(gender, "getListCountGender no debe devolver null");
    assertEquals(2, gender.length, "En DummyGenerator el array de géneros tiene longitud 2");
  }

  @Test
  void publicCountersShouldBeReadableAndWritable() {
    DummyGenerator gen = new DummyGenerator();

    gen.countGender = 5;
    gen.countBetterGender = 3;

    assertEquals(5, gen.countGender, "countGender debe ser accesible y modificable");
    assertEquals(3, gen.countBetterGender, "countBetterGender debe ser accesible y modificable");
  }

  @Test
  void awardUpdateRefShouldReturnTrueInDummyImplementation() {
    DummyGenerator gen = new DummyGenerator();
    State candidate = new State();

    assertTrue(gen.awardUpdateREF(candidate),
        "En DummyGenerator, awardUpdateREF devuelve siempre true");
  }

  @Test
  void sonListShouldBeEmptyInDummyImplementation() {
    DummyGenerator gen = new DummyGenerator();
    List<State> sons = gen.getSonList();

    assertNotNull(sons, "getSonList no debe devolver null");
    assertTrue(sons.isEmpty(), "En DummyGenerator la lista de hijos debe estar vacía");
  }
}
