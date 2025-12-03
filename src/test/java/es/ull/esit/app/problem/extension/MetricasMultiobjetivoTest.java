package es.ull.esit.app.problem.extension;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.ull.esit.app.problem.definition.State;
import jxl.read.biff.BiffException;

class MetricasMultiobjetivoTest {

    private State createState(double... evals) {
        State s = new State();
        List<Double> e = new ArrayList<>();
        for (double d : evals) {
            e.add(d);
        }
        s.setEvaluation(e);
        return s;
    }

    @Test
    void calcularTasaErrorShouldWork() throws BiffException, IOException {
        MetricasMultiobjetivo m = new MetricasMultiobjetivo();

        State sA = createState(0.0);
        State sB = createState(1.0);
        State sC = createState(2.0);

        List<State> trueFront = Arrays.asList(sA, sB);
        List<State> currentFront = Arrays.asList(sA, sC);

        double tasa = m.calcularTasaError(currentFront, trueFront);

        // De 2 soluciones actuales, 1 no está en el frente verdadero -> 1/2 = 0.5
        assertEquals(0.5, tasa, 1e-6);
    }

    @Test
    void calcularDistanciaGeneracionalShouldWorkForSimpleCase() throws BiffException, IOException {
        MetricasMultiobjetivo m = new MetricasMultiobjetivo();

        State trueS = createState(0.0);
        State curS = createState(3.0);

        List<State> trueFront = Arrays.asList(trueS);
        List<State> currentFront = Arrays.asList(curS);

        // Con 1 objetivo, distancia cuadrática = (3-0)^2 = 9
        // distanciaGeneracional = sqrt(9)/1 = 3
        double dg = m.calcularDistanciaGeneracional(currentFront, trueFront);
        assertEquals(3.0, dg, 1e-5);
    }

    @Test
    void calcularDispersionShouldBeZeroForIdenticalSolutions() throws BiffException, IOException {
        MetricasMultiobjetivo m = new MetricasMultiobjetivo();

        State s1 = createState(1.0, 2.0);
        State s2 = createState(1.0, 2.0);

        List<State> solutions = Arrays.asList(s1, s2);

        double dispersion = m.calcularDispersion(solutions);

        // Todas las soluciones tienen la misma evaluación -> dispersión 0
        assertEquals(0.0, dispersion, 1e-6);
    }

    @Test
    void calcularMinMaxMediaShouldWork() {
        MetricasMultiobjetivo m = new MetricasMultiobjetivo();

        List<Double> values = Arrays.asList(1.0, 5.0, -2.0);

        double min = m.calcularMin(values);
        double max = m.calcularMax(values);
        double media = m.calcularMedia(values);

        assertEquals(-2.0, min, 1e-6);
        assertEquals(5.0, max, 1e-6);
        assertEquals((1.0 + 5.0 - 2.0) / 3.0, media, 1e-6);
    }
}
