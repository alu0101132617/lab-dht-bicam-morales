package es.ull.esit.app.evolutionary_algorithms.complement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RangeTest {

  @Test
  void defaultValuesShouldBeNullOrZero() {
    Range range = new Range();

    assertNull(range.getData(), "Por defecto, 'data' debe ser null");
    assertEquals(0.0f, range.getMin(), 0.000001f, "Por defecto, 'min' debe ser 0.0f");
    assertEquals(0.0f, range.getMax(), 0.000001f, "Por defecto, 'max' debe ser 0.0f");
  }

  @Test
  void settersShouldUpdateFieldsCorrectly() {
    Range range = new Range();

    Probability probability = new Probability();
    probability.setProbability(0.8f);

    float min = 1.5f;
    float max = 3.5f;

    range.setData(probability);
    range.setMin(min);
    range.setMax(max);

    assertSame(probability, range.getData(), "setData debe actualizar correctamente el campo 'data'");
    assertEquals(min, range.getMin(), 0.000001f, "setMin debe actualizar correctamente el campo 'min'");
    assertEquals(max, range.getMax(), 0.000001f, "setMax debe actualizar correctamente el campo 'max'");
  }

  @Test
  void settersShouldAllowNullData() {
    Range range = new Range();

    range.setData(null);

    assertNull(range.getData(), "setData debe permitir asignar null");
  }
}
