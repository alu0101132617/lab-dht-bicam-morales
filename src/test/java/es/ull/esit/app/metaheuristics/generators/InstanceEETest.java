package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.factory_method.FactoryGenerator;

class InstanceEETest {

    private Generator createGeneratorMock(GeneratorType type) {
        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(type);
        return gen;
    }

    @Test
    void runShouldReplaceExistingEEGeneratorAndSetTerminateTrue() {
        InstanceEE instance = new InstanceEE();

        Generator existingEE = createGeneratorMock(GeneratorType.EvolutionStrategies);
        Generator[] generatorArray = new Generator[] { existingEE };

        Generator newEE = createGeneratorMock(GeneratorType.EvolutionStrategies);

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.EvolutionStrategies))
                        .thenReturn(newEE);
                });
            MockedStatic<MultiGenerator> multiStatic =
                Mockito.mockStatic(MultiGenerator.class)
        ) {
            multiStatic.when(MultiGenerator::getListGenerators).thenReturn(generatorArray);

            assertFalse(instance.isTerminate());
            instance.run();
            assertTrue(instance.isTerminate());
            assertSame(newEE, generatorArray[0],
                "El generador EvolutionStrategies debe haber sido reemplazado");
        }
    }

    @Test
    void runShouldSetTerminateWhenFactoryThrowsException() {
        InstanceEE instance = new InstanceEE();

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.EvolutionStrategies))
                        .thenThrow(new RuntimeException("boom"));
                })
        ) {
            instance.run();
            assertTrue(instance.isTerminate(),
                "Si FactoryGenerator lanza excepción, terminate debe ser true");
        }
    }

    @Test
    void runShouldSetTerminateWhenFactoryReturnsNull() {
        InstanceEE instance = new InstanceEE();

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.EvolutionStrategies))
                        .thenReturn(null);
                })
        ) {
            instance.run();
            assertTrue(instance.isTerminate(),
                "Si FactoryGenerator devuelve null, terminate debe ser true");
        }
    }

    @Test
    void runShouldSetTerminateWhenMultiGeneratorReturnsNullArray() {
        InstanceEE instance = new InstanceEE();
        Generator newEE = createGeneratorMock(GeneratorType.EvolutionStrategies);

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.EvolutionStrategies))
                        .thenReturn(newEE);
                });
            MockedStatic<MultiGenerator> multiStatic =
                Mockito.mockStatic(MultiGenerator.class)
        ) {
            multiStatic.when(MultiGenerator::getListGenerators).thenReturn(null);

            instance.run();
            assertTrue(instance.isTerminate(),
                "Si MultiGenerator devuelve null, terminate debe ser true");
        }
    }

    @Test
    void terminateGetterAndSetterShouldWork() {
        InstanceEE instance = new InstanceEE();
        assertFalse(instance.isTerminate());
        instance.setTerminate(true);
        assertTrue(instance.isTerminate());
    }
}
