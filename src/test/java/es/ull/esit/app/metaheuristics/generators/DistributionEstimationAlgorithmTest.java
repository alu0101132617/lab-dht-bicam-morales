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

import es.ull.esit.app.evolutionary_algorithms.complement.DistributionType;
import es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;
import es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;

class DistributionEstimationAlgorithmTest {

    /**
     * Crea una instancia de DistributionEstimationAlgorithm
     * con un Strategy.getStrategy() mockeado de forma mínima
     * para que el constructor no falle.
     */
    private DistributionEstimationAlgorithm createDEAWithBasicStrategyMock(List<State> randomSearchList) {
        // Preparamos la lista estática de RandomSearch
        RandomSearch.setListStateReference(randomSearchList);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // Lista de claves de generadores: solo DEA
            List<String> keys = new ArrayList<>();
            keys.add(GeneratorType.DistributionEstimationAlgorithm.toString());
            when(strategyMock.getListKey()).thenReturn(keys);

            // Mapa de generadores con una entrada DEA mockeada
            SortedMap<GeneratorType, Generator> map = new TreeMap<>();
            DistributionEstimationAlgorithm existingDEA = mock(DistributionEstimationAlgorithm.class);
            when(existingDEA.getListReference()).thenReturn(new ArrayList<>()); // vacío → tira de RandomSearch
            map.put(GeneratorType.DistributionEstimationAlgorithm, existingDEA);
            when(strategyMock.getMapGenerators()).thenReturn(map);

            // No usamos el Problem aquí, pero por si acaso:
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // Importante: creamos la instancia DENTRO del try
            DistributionEstimationAlgorithm dea = new DistributionEstimationAlgorithm();
            return dea;
        }
    }

    /** Crea un estado real con una evaluación simple (ArrayList<Double>) */
    private State createStateWithEval(double value) {
        State s = new State();
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(value);
        s.setEvaluation(eval);
        return s;
    }

    @Test
    void constructorShouldInitializeReferenceListFromRandomSearchWhenEmptyInMap() {
        // Preparamos una lista de estados en RandomSearch
        List<State> randomList = new ArrayList<>();
        State s1 = createStateWithEval(1.0);
        randomList.add(s1);

        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(randomList);

        List<State> ref = dea.getListReference();
        assertEquals(1, ref.size(), "La referencia debería haberse inicializado con RandomSearch.listStateReference");
        assertSame(s1, ref.get(0), "El estado de referencia debe ser el mismo que el de RandomSearch");
    }

    @Test
    void maxValueShouldReturnStateWithMaximumEvaluation() {
        // Aunque el constructor llama a Strategy, aquí no nos importa el contenido
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        State s1 = createStateWithEval(1.0);
        State s2 = createStateWithEval(3.0);
        State s3 = createStateWithEval(2.0);

        List<State> list = new ArrayList<>();
        list.add(s1);
        list.add(s2);
        list.add(s3);

        State max = dea.maxValue(list);

        assertEquals(3.0, max.getEvaluation().get(0), 0.0001,
                "maxValue debe devolver el estado con mayor evaluación");
    }

    @Test
    void getReferenceShouldReturnMaxOrMinDependingOnProblemType() {
        // Necesitamos controlar Strategy.getStrategy().getProblem().getTypeProblem()
        List<State> randomList = new ArrayList<>();
        randomList.add(createStateWithEval(0.0)); // para el constructor

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            // Setup para constructor (igual que helper, pero inline)
            List<String> keys = new ArrayList<>();
            keys.add(GeneratorType.DistributionEstimationAlgorithm.toString());
            when(strategyMock.getListKey()).thenReturn(keys);

            SortedMap<GeneratorType, Generator> map = new TreeMap<>();
            DistributionEstimationAlgorithm existingDEA = mock(DistributionEstimationAlgorithm.class);
            when(existingDEA.getListReference()).thenReturn(new ArrayList<>());
            map.put(GeneratorType.DistributionEstimationAlgorithm, existingDEA);
            when(strategyMock.getMapGenerators()).thenReturn(map);

            RandomSearch.setListStateReference(randomList);

            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);

            DistributionEstimationAlgorithm dea = new DistributionEstimationAlgorithm();

            // Ahora sobreescribimos la lista de referencia para probar getReference()
            List<State> refList = new ArrayList<>();
            State s1 = createStateWithEval(1.0);
            State s2 = createStateWithEval(3.0);
            refList.add(s1);
            refList.add(s2);
            dea.setListReference(refList);

            // Caso MAXIMIZAR → debe devolver el de valor 3.0
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            State refMax = dea.getReference();
            assertEquals(3.0, refMax.getEvaluation().get(0), 0.0001,
                    "En MAXIMIZAR debe devolver el estado con mayor evaluación");

            // Caso MINIMIZAR → debe devolver el de valor 1.0
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
            State refMin = dea.getReference();
            assertEquals(1.0, refMin.getEvaluation().get(0), 0.0001,
                    "En MINIMIZAR debe devolver el estado con menor evaluación");
        }
    }

    @Test
    void getReferenceListShouldReturnCopyOfInternalList() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        List<State> internal = new ArrayList<>();
        internal.add(createStateWithEval(1.0));
        internal.add(createStateWithEval(2.0));

        dea.setListReference(internal);

        List<State> returned = dea.getReferenceList();

        assertEquals(internal.size(), returned.size(), "La lista devuelta debe tener el mismo tamaño");
        assertNotSame(internal, returned, "getReferenceList debe devolver una lista nueva, no la referencia interna");
        assertSame(internal.get(0), returned.get(0), "Los elementos deben ser los mismos objetos State");
    }

    @Test
    void awardUpdateREFShouldReturnTrueIfStateIsInReferenceList() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        State s1 = createStateWithEval(1.0);
        State s2 = createStateWithEval(2.0);

        List<State> ref = new ArrayList<>();
        ref.add(s1);
        dea.setListReference(ref);

        assertTrue(dea.awardUpdateREF(s1), "Debe devolver true si el estado está en la lista de referencia");
        assertFalse(dea.awardUpdateREF(s2), "Debe devolver false si el estado NO está en la lista de referencia");
    }

    @Test
    void weightGettersAndSettersShouldWork() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        // Valor por defecto del constructor
        assertEquals(50.0f, dea.getWeight(), 0.0001,
                "El peso inicial debe ser 50 según el constructor");

        dea.setWeight(80.0f);
        assertEquals(80.0f, dea.getWeight(), 0.0001,
                "setWeight debe actualizar el peso");
    }

    @Test
    void distributionTypeGettersAndSettersShouldWork() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        // Solo existe UNIVARIATE en tu enum
        assertEquals(DistributionType.UNIVARIATE, dea.getDistributionType(),
                "Por defecto debe ser UNIVARIATE");

        // Reaplicamos el mismo valor para verificar el setter
        dea.setDistributionType(DistributionType.UNIVARIATE);
        assertEquals(DistributionType.UNIVARIATE, dea.getDistributionType(),
                "setDistributionType debe mantener UNIVARIATE");
    }

    @Test
    void generatorTypeAndGetTypeShouldBeConsistent() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        assertEquals(GeneratorType.DistributionEstimationAlgorithm, dea.getType(),
                "getType debe devolver el tipo de generador del DEA");
        assertEquals(GeneratorType.DistributionEstimationAlgorithm, dea.getGeneratorType(),
                "getGeneratorType debe devolver DistributionEstimationAlgorithm");

        dea.setGeneratorType(GeneratorType.GeneticAlgorithm);
        assertEquals(GeneratorType.GeneticAlgorithm, dea.getGeneratorType(),
                "setGeneratorType debe actualizar el tipo de generador");
    }

    @Test
    void listCountBetterGenderAndGenderAndTraceShouldNotBeNullAndHaveExpectedLength() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        int[] better = dea.getListCountBetterGender();
        int[] gender = dea.getListCountGender();
        float[] trace = dea.getTrace();

        assertNotNull(better, "getListCountBetterGender no debe devolver null");
        assertNotNull(gender, "getListCountGender no debe devolver null");
        assertNotNull(trace, "getTrace no debe devolver null");

        assertEquals(10, better.length, "listCountBetterGenderDistribution tiene tamaño 10");
        assertEquals(10, gender.length, "listCountGender tiene tamaño 10");
        assertEquals(1200000, trace.length, "listTrace tiene tamaño 1.200.000");
    }

    @Test
    void replaceTypeAndSelectionTypeStaticSettersAndGettersShouldWork() {
        DistributionEstimationAlgorithm dea = createDEAWithBasicStrategyMock(new ArrayList<>());

        // ReplaceType: usamos el primer valor disponible en el enum
        ReplaceType anyReplaceType = ReplaceType.values()[0];
        DistributionEstimationAlgorithm.setReplaceType(anyReplaceType);
        assertEquals(anyReplaceType, dea.getReplaceType(),
                "getReplaceType debe devolver el valor estático establecido");

        // SelectionType: también el primer valor disponible
        SelectionType anySelectionType = SelectionType.values()[0];
        DistributionEstimationAlgorithm.setSelectionType(anySelectionType);
        assertEquals(anySelectionType, dea.getSelectionType(),
                "getSelectionType debe devolver el valor estático establecido");
    }
}
