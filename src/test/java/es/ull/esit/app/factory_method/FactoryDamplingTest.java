package es.ull.esit.app.factory_method;

import es.ull.esit.app.evolutionary_algorithms.complement.Sampling;
import es.ull.esit.app.evolutionary_algorithms.complement.SamplingType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FactorySamplingTest {

    @Test
    void createSamplingShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactorySampling factory = new FactorySampling();

        SamplingType type = SamplingType.values()[0];
        String expectedClassName = "evolutionary_algorithms.complement." + type.toString();

        Sampling expectedInstance = mock(Sampling.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            Sampling result = factory.createSampling(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}
