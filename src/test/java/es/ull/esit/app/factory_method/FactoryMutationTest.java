package es.ull.esit.app.factory_method;

import es.ull.esit.app.evolutionary_algorithms.complement.Mutation;
import es.ull.esit.app.evolutionary_algorithms.complement.MutationType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FactoryMutationTest {

    @Test
    void createMutationShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryMutation factory = new FactoryMutation();

        MutationType type = MutationType.values()[0];
        String expectedClassName = "evolutionary_algorithms.complement." + type.toString();

        Mutation expectedInstance = mock(Mutation.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            Mutation result = factory.createMutation(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}

