package es.ull.esit.app.metaheurictics.strategy;

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
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class StrategyTest {

    @AfterEach
    void resetSingleton() {
        Strategy.destroyExecute();
    }

    // ---------- helpers de reflexión ----------

    private void setPrivateBoolean(Strategy s, String fieldName, boolean value) throws Exception {
        Field f = Strategy.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setBoolean(s, value);
    }

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

    // ---------- singleton / getters / setters básicos ----------

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

        s.setCountMax(10);
        assertEquals(10, s.getCountMax());

        s.setCountCurrent(3);
        assertEquals(3, s.getCountCurrent());

        s.setThreshold(0.7);
        assertEquals(0.7, s.getThreshold(), 1e-6);

        SortedMap<GeneratorType, Generator> map = new TreeMap<>();
        s.setMapGenerators(map);
        assertSame(map, s.getMapGenerators());

        Generator g = mock(Generator.class);
        s.setGenerator(g);
        assertSame(g, s.getGenerator());

        var states = new ArrayList<State>();
        s.setListStates(states);
        assertSame(states, s.getListStates());

        var refPob = new ArrayList<State>();
        s.setListRefPoblacFinal(refPob);
        assertSame(refPob, s.getListRefPoblacFinal());
    }

    // ---------- getListKey ----------

    @Test
    void getListKeyShouldReturnGeneratorTypeNames() {
        Strategy s = Strategy.getStrategy();

        SortedMap<GeneratorType, Generator> map = new TreeMap<>();
        map.put(GeneratorType.RandomSearch, mock(Generator.class));
        map.put(GeneratorType.HillClimbing, mock(Generator.class));
        s.setMapGenerators(map);

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
    void updateRefShouldUseGeneratorReferenceWhenNotMultiGenerator() {
        Strategy s = Strategy.getStrategy();

        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);
        State ref = new State();
        when(gen.getReference()).thenReturn(ref);

        s.setGenerator(gen);

        s.updateRef(GeneratorType.RandomSearch);

        assertSame(ref, s.getBestState());
    }

    // ---------- updateRef (rama MultiGenerator) ----------

    @Test
    void updateRefShouldUseMultiGeneratorListStateReferenceWhenMultiGenerator() {
        Strategy s = Strategy.getStrategy();

        State last = new State();
        last.setNumber(99);
        MultiGenerator.listStateReference = new ArrayList<>();
        MultiGenerator.listStateReference.add(new State());
        MultiGenerator.listStateReference.add(last);

        try (MockedStatic<MultiGenerator> multiStatic = Mockito.mockStatic(MultiGenerator.class)) {
            Generator g1 = mock(Generator.class);
            Generator g2 = mock(Generator.class);
            when(g1.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);
            when(g2.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);

            multiStatic.when(MultiGenerator::getListGenerators)
                       .thenReturn(new Generator[]{g1, g2});

            s.updateRef(GeneratorType.MultiGenerator);

            assertSame(last, s.getBestState());
        }
    }

    // ---------- updateRefMultiG ----------

    @Test
    void updateRefMultiGShouldIterateOverAllGenerators() {
        Strategy s = Strategy.getStrategy();

        try (MockedStatic<MultiGenerator> multiStatic = Mockito.mockStatic(MultiGenerator.class)) {
            Generator g1 = mock(Generator.class);
            Generator g2 = mock(Generator.class);
            when(g1.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);
            when(g2.getType()).thenReturn(GeneratorType.ParticleSwarmOptimization);

            multiStatic.when(MultiGenerator::getListGenerators)
                       .thenReturn(new Generator[]{g1, g2});

            assertDoesNotThrow(s::updateRefMultiG);
        }
    }

    // ---------- updateCountGender ----------

    @Test
    void updateCountGenderShouldAccumulateAndResetCounters() throws Exception {
        Strategy s = Strategy.getStrategy();

        setPrivateInt(s, "periodo", 1);

        try (MockedStatic<MultiGenerator> multiStatic = Mockito.mockStatic(MultiGenerator.class)) {
            Generator g1 = mock(Generator.class, CALLS_REAL_METHODS);
            Generator g2 = mock(Generator.class, CALLS_REAL_METHODS);

            when(g1.getType()).thenReturn(GeneratorType.HillClimbing);
            when(g2.getType()).thenReturn(GeneratorType.TabuSearch);

            int[] gender1 = new int[10];
            int[] better1 = new int[10];
            int[] gender2 = new int[10];
            int[] better2 = new int[10];

            when(g1.getListCountGender()).thenReturn(gender1);
            when(g1.getListCountBetterGender()).thenReturn(better1);
            when(g2.getListCountGender()).thenReturn(gender2);
            when(g2.getListCountBetterGender()).thenReturn(better2);

            g1.countGender = 2;
            g1.countBetterGender = 1;
            g2.countGender = 3;
            g2.countBetterGender = 2;

            multiStatic.when(MultiGenerator::getListGenerators)
                       .thenReturn(new Generator[]{g1, g2});

            s.updateCountGender();

            assertEquals(2, gender1[1]);
            assertEquals(1, better1[1]);
            assertEquals(3, gender2[1]);
            assertEquals(2, better2[1]);

            assertEquals(0, g1.countGender);
            assertEquals(0, g1.countBetterGender);
            assertEquals(0, g2.countGender);
            assertEquals(0, g2.countBetterGender);
        }
    }

    // ---------- updateWeight ----------

    @Test
    void updateWeightShouldSetWeightTo50ForAllNonMultiGenerators() {
        Strategy s = Strategy.getStrategy();

        try (MockedStatic<MultiGenerator> multiStatic = Mockito.mockStatic(MultiGenerator.class)) {
            Generator g1 = mock(Generator.class);
            Generator g2 = mock(Generator.class);
            Generator gMulti = mock(Generator.class);

            when(g1.getType()).thenReturn(GeneratorType.HillClimbing);
            when(g2.getType()).thenReturn(GeneratorType.RandomSearch);
            when(gMulti.getType()).thenReturn(GeneratorType.MultiGenerator);

            multiStatic.when(MultiGenerator::getListGenerators)
                       .thenReturn(new Generator[]{g1, g2, gMulti});

            s.updateWeight();

            verify(g1).setWeight(50.0f);
            verify(g2).setWeight(50.0f);
            verify(gMulti, never()).setWeight(anyFloat());
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

    // ---------- initialize / initializeGenerators ----------

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

            SortedMap<GeneratorType, Generator> map = s.getMapGenerators();
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

            SortedMap<GeneratorType, Generator> map = s.getMapGenerators();
            assertNotNull(map);
            assertEquals(GeneratorType.values().length, map.size());
        }
    }

    // ---------- executeStrategy: rama else (sin entrar en el if grande) ----------

    @Test
    void executeStrategyShouldRunWithElseBranchOnly()
            throws Exception {

        Strategy s = Strategy.getStrategy();

        Problem p = mock(Problem.class);
        s.setProblem(p);
        when(p.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

        var evalCalls = new int[]{0};
        doAnswer(inv -> {
            State st = inv.getArgument(0);
            var ev = new ArrayList<Double>();
            if (evalCalls[0] == 0) {
                ev.add(1.0);
            } else {
                ev.add(2.0);
            }
            evalCalls[0]++;
            st.setEvaluation(ev);
            return null;
        }).when(p).evaluate(any(State.class));

        setPrivateBoolean(s, "saveListStates", true);
        setPrivateBoolean(s, "saveListBestStates", true);
        setPrivateBoolean(s, "saveFreneParetoMonoObjetivo", true);
        setPrivateBoolean(s, "calculateTime", true);
        setPrivateInt(s, "countPeriodChange", 5);

        State initial = new State();
        State candidate = new State();

        try (MockedStatic<UpdateParameter> updStatic = Mockito.mockStatic(UpdateParameter.class);
             MockedConstruction<RandomSearch> randomMocked =
                     Mockito.mockConstruction(RandomSearch.class, (mockRS, context) -> {
                         when(mockRS.generate(anyInt())).thenReturn(initial);
                     });
             MockedConstruction<FactoryGenerator> factoryMocked =
                     Mockito.mockConstruction(FactoryGenerator.class, (mockFG, context) -> {
                         Generator gen = mock(Generator.class);
                         when(gen.generate(anyInt())).thenReturn(candidate);
                         when(gen.getReference()).thenReturn(initial);
                         when(gen.getReferenceList()).thenReturn(new ArrayList<>());
                         doNothing().when(gen).setInitialReference(any());
                         doNothing().when(gen).updateReference(any(), anyInt());
                         when(gen.getType()).thenReturn(GeneratorType.RandomSearch);
                         when(mockFG.createGenerator(any(GeneratorType.class))).thenReturn(gen);
                     })) {

            updStatic.when(() -> UpdateParameter.updateParameter(anyInt()))
                     .thenAnswer(inv -> ((Integer) inv.getArgument(0)) + 1);

            // countmaxIterations = 3, countIterationsChange = 5 (no entra en if countCurrent == countChange)
            s.executeStrategy(
                    3,   // countmaxIterations
                    5,   // countIterationsChange
                    1,   // operatornumber
                    GeneratorType.RandomSearch
            );

            assertNotNull(s.getBestState());
            assertEquals(2.0, s.getBestState().getEvaluation().get(0), 1e-6);
        }
    }
}
