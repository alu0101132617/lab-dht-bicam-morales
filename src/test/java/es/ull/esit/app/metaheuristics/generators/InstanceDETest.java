package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.factory_method.FactoryGenerator;

class InstanceDETest {

    /** Helper que crea un Generator mock con un tipo concreto. */
    private Generator createGeneratorMock(GeneratorType type) {
        Generator gen = mock(Generator.class);
        when(gen.getType()).thenReturn(type);
        return gen;
    }

    @Test
    void runShouldReplaceExistingDEGeneratorAndSetTerminateTrue() {
        InstanceDE instance = new InstanceDE();

        // Generador inicial en MultiGenerator
        Generator existingDE = createGeneratorMock(GeneratorType.DistributionEstimationAlgorithm);
        Generator[] generatorArray = new Generator[] { existingDE };

        // Nuevo generador que creará FactoryGenerator
        Generator newDE = createGeneratorMock(GeneratorType.DistributionEstimationAlgorithm);

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.DistributionEstimationAlgorithm))
                        .thenReturn(newDE);
                });
            MockedStatic<MultiGenerator> multiStatic =
                Mockito.mockStatic(MultiGenerator.class)
        ) {
            multiStatic.when(MultiGenerator::getListGenerators).thenReturn(generatorArray);

            assertFalse(instance.isTerminate(), "Al inicio terminate debe ser false");
            instance.run();
            assertTrue(instance.isTerminate(), "Tras run() debe ponerse terminate a true");

            // Debe haber reemplazado el generador en la posición correspondiente
            assertSame(newDE, generatorArray[0], "El generador DEA debe haber sido reemplazado");
        }
    }

    @Test
    void runShouldSetTerminateWhenFactoryThrowsException() {
        InstanceDE instance = new InstanceDE();

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.DistributionEstimationAlgorithm))
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
        InstanceDE instance = new InstanceDE();

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.DistributionEstimationAlgorithm))
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
        InstanceDE instance = new InstanceDE();
        Generator newDE = createGeneratorMock(GeneratorType.DistributionEstimationAlgorithm);

        try (
            MockedConstruction<FactoryGenerator> factoryMock =
                Mockito.mockConstruction(FactoryGenerator.class, (mock, context) -> {
                    when(mock.createGenerator(GeneratorType.DistributionEstimationAlgorithm))
                        .thenReturn(newDE);
                });
            MockedStatic<MultiGenerator> multiStatic =
                Mockito.mockStatic(MultiGenerator.class)
        ) {
            // Simulamos que MultiGenerator falla y devuelve null
            multiStatic.when(MultiGenerator::getListGenerators).thenReturn(null);

            instance.run();
            assertTrue(instance.isTerminate(),
                "Si MultiGenerator devuelve null, terminate debe ser true");
        }
    }

    @Test
    void terminateGetterAndSetterShouldWork() {
        InstanceDE instance = new InstanceDE();
        assertFalse(instance.isTerminate(), "Por defecto terminate debe ser false");
        instance.setTerminate(true);
        assertTrue(instance.isTerminate(), "setTerminate(true) debe reflejarse en isTerminate()");
    }
}
