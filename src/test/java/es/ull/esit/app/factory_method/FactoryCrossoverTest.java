package es.ull.esit.app.factory_method;

import es.ull.esit.app.evolutionary_algorithms.complement.Crossover;
import es.ull.esit.app.evolutionary_algorithms.complement.CrossoverType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FactoryCrossoverTest {

    @Test
    void createCrossoverShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryCrossover factory = new FactoryCrossover();

        CrossoverType type = CrossoverType.values()[0];
        String expectedClassName = "evolutionary_algorithms.complement." + type.toString();

        Crossover expectedInstance = mock(Crossover.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            Crossover result = factory.createCrossover(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}
