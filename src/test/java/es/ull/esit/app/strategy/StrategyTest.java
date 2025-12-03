package es.ull.esit.app.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.factory_method.FactoryGenerator;
import es.ull.esit.app.local_search.complement.StopExecute;
import es.ull.esit.app.local_search.complement.UpdateParameter;
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
import es.ull.esit.app.problem.definition.State;

class StrategyTest {

    @AfterEach
    void resetSingleton() {
        Strategy.destroyExecute();
    }

    // ---------- helpers reflexión ----------

    private void setPrivateInt(Strategy s, String fieldName, int value) throws Exception {
        Field f = Strategy.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setInt(s, value);
    }

    private float[] getOfflineErrorArray(Strategy s) throws Exception {
        Field f = Strategy.class.getDeclaredField("listOfflineError");
        f.setAccessible(true);
        return (float[]) f.get(s);
    }

    private void setMapGenerators(Strategy s, SortedMap<GeneratorType, Generator> map) throws Exception {
        Field f = Strategy.class.getDeclaredField("mapGenerators");
        f.setAccessible(true);
        f.set(s, map);
    }

    // ---------- singleton / getters / setters ----------

    @Test
    void singletonShouldWork() {
        Strategy s1 = Strategy.getStrategy();
        Strategy s2 = Strategy.getStrategy();
        assertSame(s1, s2);

        Strategy.destroyExecute();
        Strategy s3 = Strategy.getStrategy();
        assertNotSame(s1, s3);
    }

    @Test
    void basicGettersAndSettersShouldWork() {
        Strategy s = Strategy.getStrategy();

        State best = new State();
        s.setBestState(best);
        assertSame(best, s.getBestState());

        Problem p = new Problem();
        s.setProblem(p);
        assertSame(p, s.getProblem());

        StopExecute stop = mock(StopExecute.class);
        s.setStopexecute(stop);
        assertSame(stop, s.getStopexecute());

        UpdateParameter up = mock(UpdateParameter.class);
        s.setUpdateparameter(up);
        assertSame(up, s.getUpdateparameter());

        s.setCountMax(10);
        assertEquals(10, s.getCountMax());

        s.setCountCurrent(3);
        assertEquals(3, s.getCountCurrent());

        s.setThreshold(0.5);
        assertEquals(0.5, s.getThreshold(), 1e-6);
    }

    // ---------- getListKey ----------

    @Test
    void getListKeyShouldReturnGeneratorTypeNames() throws Exception {
        Strategy s = Strategy.getStrategy();

        SortedMap<GeneratorType, Generator> map = new TreeMap<>();
        map.put(GeneratorType.RandomSearch, mock(Generator.class));
        map.put(GeneratorType.HillClimbing, mock(Generator.class));

        setMapGenerators(s, map);

        var keys = s.getListKey();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("RandomSearch"));
        assertTrue(keys.contains("HillClimbing"));
    }

    // ---------- calculateOffLinePerformance ----------

    @Test
    void calculateOffLinePerformanceShouldStoreAverage() throws Exception {
        Strategy s = Strategy.getStrategy();

        setPrivateInt(s, "countPeriodChange", 20);
        float[] offline = getOfflineErrorArray(s);

        s.calculateOffLinePerformance(100f, 4);
        assertEquals(5.0f, offline[4], 1e-6);
    }

    // ---------- updateRefGenerator: caso local search ----------

    @Test
    void updateRefGeneratorShouldReevaluateSingleReferenceForLocalSearchGenerators() {
        Strategy s = Strategy.getStrategy();

        Problem p = mock(Problem.class);
        s.setProblem(p);

        ObjetiveFunction f = mock(ObjetiveFunction.class);
        var fs = new ArrayList<ObjetiveFunction>();
        fs.add(f);
        when(p.getFunction()).thenReturn(fs);

        State ref = new State();
        var eval = new ArrayList<Double>();
        eval.add(0.0);
        ref.setEvaluation(eval);

        when(f.evaluation(ref)).thenReturn(7.0);

        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(GeneratorType.HillClimbing);
        when(gen.getReference()).thenReturn(ref);

        s.updateRefGenerator(gen);

        assertEquals(7.0, ref.getEvaluation().get(0), 1e-6);
    }

    // ---------- updateRefGenerator: caso población ----------

    @Test
    void updateRefGeneratorShouldReevaluateReferenceListForPopulationGenerators() {
        Strategy s = Strategy.getStrategy();

        Problem p = mock(Problem.class);
        s.setProblem(p);

        ObjetiveFunction f = mock(ObjetiveFunction.class);
        var fs = new ArrayList<ObjetiveFunction>();
        fs.add(f);
        when(p.getFunction()).thenReturn(fs);

        State s1 = new State();
        State s2 = new State();
        var e1 = new ArrayList<Double>(); e1.add(0.0); s1.setEvaluation(e1);
        var e2 = new ArrayList<Double>(); e2.add(0.0); s2.setEvaluation(e2);

        var refs = new ArrayList<State>();
        refs.add(s1);
        refs.add(s2);

        when(f.evaluation(s1)).thenReturn(1.5);
        when(f.evaluation(s2)).thenReturn(2.5);

        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(GeneratorType.GeneticAlgorithm);
        when(gen.getReferenceList()).thenReturn(refs);

        s.updateRefGenerator(gen);

        assertEquals(1.5, s1.getEvaluation().get(0), 1e-6);
        assertEquals(2.5, s2.getEvaluation().get(0), 1e-6);
    }

    // ---------- updateRef (rama normal) ----------

    @Test
    void updateRefShouldUseGeneratorReferenceWhenNotMultiGenerator() throws Exception {
        Strategy s = Strategy.getStrategy();

        // Generador que NO dispara ninguna rama de updateRefGenerator
        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization); // no entra en ningún if
        State ref = new State();
        when(gen.getReference()).thenReturn(ref);

        Field fGen = Strategy.class.getDeclaredField("generator");
        fGen.setAccessible(true);
        fGen.set(s, gen);

        s.updateRef(GeneratorType.RandomSearch);

        assertSame(ref, s.getBestState(),
                "bestState debe establecerse al generator.getReference() en la rama normal");
    }

    // ---------- updateRefMultiG ----------

    @Test
    void updateRefMultiGShouldIterateOverAllGenerators() {
        Strategy s = Strategy.getStrategy();

        try (MockedStatic<MultiGenerator> multiStatic = Mockito.mockStatic(MultiGenerator.class)) {
            Generator g1 = mock(Generator.class);
            Generator g2 = mock(Generator.class);

            // Tipos seguros que no activan lógica interna de evaluación
            when(g1.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);
            when(g2.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);

            multiStatic.when(MultiGenerator::getListGenerators)
                       .thenReturn(new Generator[]{g1, g2});

            // Simplemente comprobamos que no lanza excepciones
            assertDoesNotThrow(s::updateRefMultiG);
        }
    }

    // ---------- update(Integer) ----------

    @Test
    void updateShouldSwitchGeneratorsBasedOnCountRef()
            throws Exception {

        Strategy s = Strategy.getStrategy();

        try (MockedStatic<GeneticAlgorithm> gaStatic = Mockito.mockStatic(GeneticAlgorithm.class);
             MockedStatic<EvolutionStrategies> esStatic = Mockito.mockStatic(EvolutionStrategies.class);
             MockedStatic<DistributionEstimationAlgorithm> deaStatic = Mockito.mockStatic(DistributionEstimationAlgorithm.class);
             MockedStatic<ParticleSwarmOptimization> psoStatic = Mockito.mockStatic(ParticleSwarmOptimization.class);
             MockedConstruction<FactoryGenerator> factoryMocked =
                     Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                         Generator g = mock(Generator.class);
                         when(mock.createGenerator(any())).thenReturn(g);
                     })) {

            gaStatic.when(GeneticAlgorithm::getCountRef).thenReturn(2);
            esStatic.when(EvolutionStrategies::getCountRef).thenReturn(3);
            deaStatic.when(DistributionEstimationAlgorithm::getCountRef).thenReturn(4);
            psoStatic.when(ParticleSwarmOptimization::getCountRef).thenReturn(5);

            // Para cada caso, simplemente llamamos a update y comprobamos que no falla
            assertDoesNotThrow(() -> s.update(1)); // GA
            assertDoesNotThrow(() -> s.update(2)); // ES
            assertDoesNotThrow(() -> s.update(3)); // DEA
            assertDoesNotThrow(() -> s.update(4)); // PSO
        }
    }

    // ---------- newGenerator ----------

    @Test
    void newGeneratorShouldUseFactoryGenerator()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        Strategy s = Strategy.getStrategy();

        try (MockedConstruction<FactoryGenerator> factoryMocked =
                     Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                         Generator gen = mock(Generator.class);
                         when(mock.createGenerator(GeneratorType.RandomSearch)).thenReturn(gen);
                     })) {

            Generator g = s.newGenerator(GeneratorType.RandomSearch);
            assertNotNull(g);
        }
    }

    // ---------- initialize e initializeGenerators ----------

    @Test
    void initializeShouldFillMapGenerators()
            throws Exception {

        Strategy s = Strategy.getStrategy();

        try (MockedConstruction<FactoryGenerator> factoryMocked =
                     Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                         Generator gen = mock(Generator.class);
                         when(mock.createGenerator(any())).thenReturn(gen);
                     })) {

            s.initialize();

            Field f = Strategy.class.getDeclaredField("mapGenerators");
            f.setAccessible(true);
            SortedMap<GeneratorType, Generator> map =
                    (SortedMap<GeneratorType, Generator>) f.get(s);

            assertNotNull(map);
            assertEquals(GeneratorType.values().length, map.size());
        }
    }

    @Test
    void initializeGeneratorsShouldAlsoFillMapGenerators()
            throws Exception {

        Strategy s = Strategy.getStrategy();

        try (MockedConstruction<FactoryGenerator> factoryMocked =
                     Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                         Generator gen = mock(Generator.class);
                         when(mock.createGenerator(any())).thenReturn(gen);
                     })) {
    
            s.initializeGenerators();

            Field f = Strategy.class.getDeclaredField("mapGenerators");
            f.setAccessible(true);
            SortedMap<GeneratorType, Generator> map =
                    (SortedMap<GeneratorType, Generator>) f.get(s);

            assertNotNull(map);
            assertEquals(GeneratorType.values().length, map.size());
        }
    }
}
