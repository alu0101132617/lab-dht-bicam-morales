package es.ull.esit.app.factory_method;

import es.ull.esit.app.metaheuristics.generators.Generator;
import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FactoryGeneratorTest {

    @Test
    void createGeneratorShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryGenerator factory = new FactoryGenerator();

        GeneratorType type = GeneratorType.values()[0];
        String expectedClassName = "metaheuristics.generators." + type.toString();

        Generator expectedInstance = mock(Generator.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            Generator result = factory.createGenerator(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}
