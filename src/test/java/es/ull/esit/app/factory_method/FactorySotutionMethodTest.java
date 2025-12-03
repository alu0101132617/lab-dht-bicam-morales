package es.ull.esit.app.factory_method;

import es.ull.esit.app.problem.extension.SolutionMethod;
import es.ull.esit.app.problem.extension.TypeSolutionMethod;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FactorySolutionMethodTest {

    @Test
    void createdSolutionMethodShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactorySolutionMethod factory = new FactorySolutionMethod();

        TypeSolutionMethod type = TypeSolutionMethod.values()[0];
        String expectedClassName = "problem.extension." + type.toString();

        SolutionMethod expectedInstance = mock(SolutionMethod.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            SolutionMethod result = factory.createdSolutionMethod(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}

