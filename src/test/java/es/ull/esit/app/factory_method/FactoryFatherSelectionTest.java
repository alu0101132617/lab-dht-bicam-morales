package es.ull.esit.app.factory_method;

import es.ull.esit.app.evolutionary_algorithms.complement.FatherSelection;
import es.ull.esit.app.evolutionary_algorithms.complement.SelectionType;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FactoryFatherSelectionTest {

    @Test
    void createSelectFatherShouldUseFactoryLoaderWithCorrectClassName()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        FactoryFatherSelection factory = new FactoryFatherSelection();

        SelectionType type = SelectionType.values()[0];
        String expectedClassName = "evolutionary_algorithms.complement." + type.toString();

        FatherSelection expectedInstance = mock(FatherSelection.class);

        try (MockedStatic<FactoryLoader> loaderStatic = Mockito.mockStatic(FactoryLoader.class)) {
            loaderStatic.when(() -> FactoryLoader.getInstance(expectedClassName))
                        .thenReturn(expectedInstance);

            FatherSelection result = factory.createSelectFather(type);

            assertSame(expectedInstance, result);
            loaderStatic.verify(() -> FactoryLoader.getInstance(expectedClassName), times(1));
        }
    }
}

