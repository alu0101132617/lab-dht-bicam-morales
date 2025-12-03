package es.ull.esit.app.factory_method;

import es.ull.esit.app.evolutionary_algorithms.complement.Distribution;
import es.ull.esit.app.evolutionary_algorithms.complement.DistributionType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FactoryDistributionTest {

    @Test
    void createDistributionShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryDistribution factory = new FactoryDistribution();

        DistributionType type = DistributionType.values()[0];
        String expectedClassName = "evolutionary_algorithms.complement." + type.toString();

        Distribution expectedInstance = mock(Distribution.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            Distribution result = factory.createDistribution(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}
