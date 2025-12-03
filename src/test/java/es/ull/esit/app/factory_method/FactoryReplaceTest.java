package es.ull.esit.app.factory_method;

import es.ull.esit.app.evolutionary_algorithms.complement.Replace;
import es.ull.esit.app.evolutionary_algorithms.complement.ReplaceType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FactoryReplaceTest {

    @Test
    void createReplaceShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryReplace factory = new FactoryReplace();

        ReplaceType type = ReplaceType.values()[0];
        String expectedClassName = "evolutionary_algorithms.complement." + type.toString();

        Replace expectedInstance = mock(Replace.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            Replace result = factory.createReplace(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}
