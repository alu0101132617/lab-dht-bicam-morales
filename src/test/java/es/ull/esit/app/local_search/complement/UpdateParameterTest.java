package es.ull.esit.app.local_search.complement;

import es.ull.esit.app.factory_method.FactoryLoader;
import es.ull.esit.app.metaheuristics.generators.*;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class UpdateParameterTest {

    @Test
    void updateParameterShouldJustIncrementWhenNoCountRefMatches()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        Integer current = 0;

        // En este caso no se entra en ningún if -> no hace falta mockear nada
        Integer result = UpdateParameter.updateParameter(current);

        assertEquals(1, result,
                "Debe incrementar el contador en 1 cuando no coincide con ningún countRef-1");
    }

    private Strategy prepareStrategyStaticMock(MockedStatic<Strategy> strategyStatic) {
        Strategy strategyMock = mock(Strategy.class, Mockito.CALLS_REAL_METHODS);
        strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
        return strategyMock;
    }

    @Test
    void updateParameterShouldSwitchToGeneticAlgorithmAtReference()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        int ref = GeneticAlgorithm.getCountRef();
        Integer current = ref - 1;

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class);
             MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {

            prepareStrategyStaticMock(strategyStatic);

            Generator generatorMock = mock(Generator.class);
            String className = "metaheuristics.generators." + GeneratorType.GeneticAlgorithm.toString();
            loaderStatic.when(() -> FactoryLoader.getInstance(className))
                        .thenReturn(generatorMock);

            Integer result = UpdateParameter.updateParameter(current);

            assertEquals(ref, result);
        }
    }

    @Test
    void updateParameterShouldSwitchToEvolutionStrategiesAtReference()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        int ref = EvolutionStrategies.getCountRef();
        Integer current = ref - 1;

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class);
             MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {

            prepareStrategyStaticMock(strategyStatic);

            Generator generatorMock = mock(Generator.class);
            String className = "metaheuristics.generators." + GeneratorType.EvolutionStrategies.toString();
            loaderStatic.when(() -> FactoryLoader.getInstance(className))
                        .thenReturn(generatorMock);

            Integer result = UpdateParameter.updateParameter(current);

            assertEquals(ref, result);
        }
    }

    @Test
    void updateParameterShouldSwitchToDistributionEstimationAlgorithmAtReference()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        int ref = DistributionEstimationAlgorithm.getCountRef();
        Integer current = ref - 1;

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class);
             MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {

            prepareStrategyStaticMock(strategyStatic);

            Generator generatorMock = mock(Generator.class);
            String className = "metaheuristics.generators." + GeneratorType.DistributionEstimationAlgorithm.toString();
            loaderStatic.when(() -> FactoryLoader.getInstance(className))
                        .thenReturn(generatorMock);

            Integer result = UpdateParameter.updateParameter(current);

            assertEquals(ref, result);
        }
    }

    @Test
    void updateParameterShouldSwitchToParticleSwarmOptimizationAtReference()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        int ref = ParticleSwarmOptimization.getCountRef();
        Integer current = ref - 1;

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class);
             MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {

            prepareStrategyStaticMock(strategyStatic);

            Generator generatorMock = mock(Generator.class);
            String className = "metaheuristics.generators." + GeneratorType.ParticleSwarmOptimization.toString();
            loaderStatic.when(() -> FactoryLoader.getInstance(className))
                        .thenReturn(generatorMock);

            Integer result = UpdateParameter.updateParameter(current);

            assertEquals(ref, result);
        }
    }
}
