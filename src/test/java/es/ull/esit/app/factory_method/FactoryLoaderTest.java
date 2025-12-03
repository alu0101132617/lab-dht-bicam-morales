package es.ull.esit.app.factory_method;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class FactoryLoaderTest {

    @Test
    void getInstanceShouldThrowIllegalArgumentExceptionOnNullOrEmpty() {
        // null
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> FactoryLoader.getInstance(null),
                "Debe lanzar IllegalArgumentException si className es null");
        assertTrue(ex1.getMessage().contains("no puede ser nulo o vacío"));

        // vacío
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> FactoryLoader.getInstance(""),
                "Debe lanzar IllegalArgumentException si className es vacío");
        assertTrue(ex2.getMessage().contains("no puede ser nulo o vacío"));
    }

    @Test
    void getInstanceShouldCreateNewInstanceUsingReflection()
            throws ClassNotFoundException, InvocationTargetException,
                   NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Usamos una clase estándar con constructor sin argumentos
        Object instance = FactoryLoader.getInstance("java.lang.Object");

        assertNotNull(instance, "FactoryLoader debe crear una instancia no nula");
        assertEquals(Object.class, instance.getClass(),
                "La instancia creada debe ser de tipo java.lang.Object");
    }

    @Test
    void privateConstructorShouldBeInvocableForCoverage()
            throws Exception {

        Constructor<FactoryLoader> constructor =
                FactoryLoader.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
                "El constructor de FactoryLoader debe ser privado");

        constructor.setAccessible(true);
        // Simplemente lo invocamos para cubrir la línea de constructor
        FactoryLoader loader = constructor.newInstance();
        assertNotNull(loader);
    }
}
