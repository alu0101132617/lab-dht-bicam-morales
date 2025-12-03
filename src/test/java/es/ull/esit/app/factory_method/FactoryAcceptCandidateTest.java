package es.ull.esit.app.factory_method;

import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FactoryAcceptCandidateTest {

    @Test
    void createAcceptCandidateShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryAcceptCandidate factory = new FactoryAcceptCandidate();

        // Usamos cualquier valor del enum (por ejemplo, el primero)
        AcceptType type = AcceptType.values()[0];

        // IMPORTANTE: mismo paquete que usa FactoryAcceptCandidate
        String expectedClassName =
                "es.ull.esit.app.local_search.acceptation_type." + type.toString();

        AcceptableCandidate expectedInstance = mock(AcceptableCandidate.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            AcceptableCandidate result = factory.createAcceptCandidate(type);

            assertSame(expectedInstance, result,
                    "La factoría debe devolver la instancia proporcionada por FactoryLoader");

            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}

