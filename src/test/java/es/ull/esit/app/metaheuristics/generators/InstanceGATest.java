package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.factory_method.FactoryGenerator;

class InstanceGATest {

    private Generator createGeneratorMock(GeneratorType type) {
        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(type);
        return gen;
    }

    @Test
    void runShouldReplaceExistingGAGeneratorAndSetTerminateTrue() {
        InstanceGA instance = new InstanceGA();

        Generator existingGA = createGeneratorMock(GeneratorType.GeneticAlgorithm);
        Generator[] generatorArray = new Generator[] { existingGA };

        Generator newGA = createGeneratorMock(GeneratorType.GeneticAlgorithm);

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.GeneticAlgorithm))
                        .thenReturn(newGA);
                });
            MockedStatic<MultiGenerator> multiStatic =
                Mockito.mockStatic(MultiGenerator.class)
        ) {
            multiStatic.when(MultiGenerator::getListGenerators).thenReturn(generatorArray);

            assertFalse(instance.isTerminate());
            instance.run();
            assertTrue(instance.isTerminate());
            assertSame(newGA, generatorArray[0],
                "El generador GeneticAlgorithm debe haber sido reemplazado");
        }
    }

    @Test
    void runShouldSetTerminateWhenFactoryThrowsException() {
        InstanceGA instance = new InstanceGA();

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.GeneticAlgorithm))
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
        InstanceGA instance = new InstanceGA();

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.GeneticAlgorithm))
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
        InstanceGA instance = new InstanceGA();
        Generator newGA = createGeneratorMock(GeneratorType.GeneticAlgorithm);

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.GeneticAlgorithm))
                        .thenReturn(newGA);
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
        InstanceGA instance = new InstanceGA();
        assertFalse(instance.isTerminate());
        instance.setTerminate(true);
        assertTrue(instance.isTerminate());
    }
}
