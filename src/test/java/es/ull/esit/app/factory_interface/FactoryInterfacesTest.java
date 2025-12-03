package es.ull.esit.app.factory_interface;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para comprobar la estructura básica de las interfaces de fábrica.
 */
class FactoryInterfacesTest {

    @Test
    void IFFactoryAcceptCandidateShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryAcceptCandidate.class;
        assertTrue(clazz.isInterface(), "IFFactoryAcceptCandidate debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length, "Debe tener exactamente un método declarado");
        assertEquals("createAcceptCandidate", methods[0].getName(),
                "El método debe llamarse createAcceptCandidate");
    }

    @Test
    void IFFactoryCandidateShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryCandidate.class;
        assertTrue(clazz.isInterface(), "IFFactoryCandidate debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createSearchCandidate", methods[0].getName());
    }

    @Test
    void IFFactoryCrossoverShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryCrossover.class;
        assertTrue(clazz.isInterface(), "IFFactoryCrossover debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createCrossover", methods[0].getName());
    }

    @Test
    void IFFactoryDistributionShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryDistribution.class;
        assertTrue(clazz.isInterface(), "IFFactoryDistribution debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createDistribution", methods[0].getName());
    }

    @Test
    void IFFactoryFatherSelectionShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryFatherSelection.class;
        assertTrue(clazz.isInterface(), "IFFactoryFatherSelection debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createSelectFather", methods[0].getName());
    }

    @Test
    void IFFactoryGeneratorShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryGenerator.class;
        assertTrue(clazz.isInterface(), "IFFactoryGenerator debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createGenerator", methods[0].getName());
    }

    @Test
    void IFFactoryMutationShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryMutation.class;
        assertTrue(clazz.isInterface(), "IFFactoryMutation debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createMutation", methods[0].getName());
    }

    @Test
    void IFFactoryReplaceShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactoryReplace.class;
        assertTrue(clazz.isInterface(), "IFFactoryReplace debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createReplace", methods[0].getName());
    }

    @Test
    void IFFactorySolutionMethodShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFactorySolutionMethod.class;
        assertTrue(clazz.isInterface(), "IFFactorySolutionMethod debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createdSolutionMethod", methods[0].getName());
    }

    @Test
    void IFFSamplingShouldBeInterfaceWithSingleMethod() {
        Class<?> clazz = IFFSampling.class;
        assertTrue(clazz.isInterface(), "IFFSampling debe ser una interfaz");

        Method[] methods = clazz.getDeclaredMethods();
        assertEquals(1, methods.length);
        assertEquals("createSampling", methods[0].getName());
    }
}
