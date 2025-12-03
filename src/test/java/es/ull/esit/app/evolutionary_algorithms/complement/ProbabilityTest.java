package es.ull.esit.app.evolutionary_algorithms.complement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTest {

  @Test
  void defaultValuesShouldBeNullOrZero() {
    Probability probability = new Probability();

    assertNull(probability.getKey(), "El valor por defecto de 'key' debe ser null");
    assertNull(probability.getValue(), "El valor por defecto de 'value' debe ser null");
    assertEquals(0.0f, probability.getProbability(), 0.000001f,
        "El valor por defecto de 'probability' debe ser 0.0f");
  }

  @Test
  void settersShouldUpdateFieldsCorrectly() {
    Probability probability = new Probability();

    Object key = "clave";
    Object value = 123;
    float prob = 0.75f;

    probability.setKey(key);
    probability.setValue(value);
    probability.setProbability(prob);

    assertSame(key, probability.getKey(), "setKey debe actualizar correctamente el campo 'key'");
    assertSame(value, probability.getValue(), "setValue debe actualizar correctamente el campo 'value'");
    assertEquals(prob, probability.getProbability(), 0.000001f,
        "setProbability debe actualizar correctamente el campo 'probability'");
  }

  @Test
  void settersShouldAllowNullValues() {
    Probability probability = new Probability();

    probability.setKey(null);
    probability.setValue(null);

    assertNull(probability.getKey(), "setKey debe permitir asignar null");
    assertNull(probability.getValue(), "setValue debe permitir asignar null");
  }
}
