package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;

class MultiobjectiveHillClimbingRestartTest {

    /** Inyecta un CandidateValue simulado dentro de la instancia. */
    private void injectCandidateValue(MultiobjectiveHillClimbingRestart gen, CandidateValue mockCv)
            throws Exception {
        Field f = MultiobjectiveHillClimbingRestart.class.getDeclaredField("candidateValue");
        f.setAccessible(true);
        f.set(gen, mockCv);
    }

    @Test
    void constructorShouldInitializeBasicFieldsAndStatistics() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        assertEquals(GeneratorType.MultiobjectiveHillClimbingRestart, gen.getType(),
                "El tipo por defecto debe ser MultiobjectiveHillClimbingRestart");
        assertEquals(50.0f, gen.getWeight(), 0.0001,
                "El peso inicial debe ser 50");

        int[] better = gen.getListCountBetterGender();
        int[] gender = gen.getListCountGender();
        float[] trace = gen.getTrace();

        assertNotNull(better, "La lista de better gender no debe ser null");
        assertNotNull(gender, "La lista de gender no debe ser null");
        assertNotNull(trace, "La traza no debe ser null");

        assertEquals(10, better.length, "better gender debe tener longitud 10");
        assertEquals(10, gender.length, "gender debe tener longitud 10");
        assertEquals(1_200_000, trace.length, "trace debe tener longitud 1.200.000");
        assertEquals(gen.getWeight(), trace[0], 0.0001,
                "El primer valor de la traza debe ser el peso inicial");
    }

    @Test
    void generateShouldCallCandidateValueWithEmptyNeighbourhoodWhenNoStrategyOrProblem()
            throws Exception {

        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        CandidateValue cvMock = mock(CandidateValue.class);
        injectCandidateValue(gen, cvMock);

        State resultState = mock(State.class);
        when(cvMock.stateCandidate(
                any(), any(), any(), anyInt(), anyList()))
                .thenReturn(resultState);

        // Strategy.getStrategy() devolverá null → no se genera vecindario
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            strategyStatic.when(Strategy::getStrategy).thenReturn(null);

            State generated = gen.generate(1);

            assertSame(resultState, generated,
                    "generate debe devolver el estado producido por CandidateValue");
        }

        // stateReferenceHC es null por defecto en este test
        verify(cvMock).stateCandidate(
                isNull(),                      // reference
                eq(CandidateType.NotDominatedCandidate),
                eq(StrategyType.NORMAL),
                eq(1),
                anyList()                      // vecindario vacío
        );
    }

    @Test
    void generateShouldUseProblemOperatorWhenAvailable() throws Exception {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        // Referencia inicial
        State ref = mock(State.class);
        gen.setStateRef(ref);

        CandidateValue cvMock = mock(CandidateValue.class);
        injectCandidateValue(gen, cvMock);

        State expectedCandidate = mock(State.class);
        List<State> neighbourhood = new ArrayList<>();
        neighbourhood.add(mock(State.class));
        neighbourhood.add(mock(State.class));

        // Mocks de Strategy / Problem / Operator
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            Operator operatorMock = mock(Operator.class);

            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getOperator()).thenReturn(operatorMock);
            when(operatorMock.generatedNewState(ref, 2)).thenReturn(neighbourhood);

            when(cvMock.stateCandidate(ref,
                    CandidateType.NotDominatedCandidate,
                    StrategyType.NORMAL,
                    2,
                    neighbourhood)).thenReturn(expectedCandidate);

            State generated = gen.generate(2);

            assertSame(expectedCandidate, generated,
                    "generate debe aplicar el operador y luego CandidateValue");

            verify(operatorMock).generatedNewState(ref, 2);
            verify(cvMock).stateCandidate(ref,
                    CandidateType.NotDominatedCandidate,
                    StrategyType.NORMAL,
                    2,
                    neighbourhood);
        }
    }

    @Test
    void getReferenceAndSetStateRefShouldWork() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();
        State ref = mock(State.class);

        assertNull(gen.getReference(), "Por defecto la referencia debe ser null");

        gen.setStateRef(ref);
        assertSame(ref, gen.getReference(),
                "getReference debe devolver el estado de referencia establecido");

        State initial = mock(State.class);
        gen.setInitialReference(initial);
        assertSame(initial, gen.getReference(),
                "setInitialReference debe actualizar la referencia");
    }

    @Test
    void getReferenceListShouldAppendCopiesAndReturnNewList() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        State ref = mock(State.class);
        State copy = mock(State.class);
        when(ref.copy()).thenReturn(copy);

        gen.setStateRef(ref);

        List<State> first = gen.getReferenceList();
        assertEquals(1, first.size(), "Primera llamada debe añadir una copia");
        assertSame(copy, first.get(0));

        List<State> second = gen.getReferenceList();
        assertEquals(2, second.size(), "Segunda llamada debe añadir otra copia");
        assertNotSame(first, second, "Cada llamada debe devolver una lista nueva");
    }

    @Test
    void awardUpdateREFShouldReturnFalseForNullsOrEqual() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        State s = mock(State.class);

        // referencia null
        assertFalse(gen.awardUpdateREF(s),
                "Si la referencia es null debe devolver false");

        gen.setStateRef(s);

        // candidato null
        assertFalse(gen.awardUpdateREF(null),
                "Si el candidato es null debe devolver false");

        // mismo objeto
        assertFalse(gen.awardUpdateREF(s),
                "Si referencia y candidato son iguales debe devolver false");
    }

    @Test
    void awardUpdateREFShouldReturnTrueForDifferentStates() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        State ref = mock(State.class);
        State candidate = mock(State.class);

        gen.setStateRef(ref);

        assertTrue(gen.awardUpdateREF(candidate),
                "Para estados distintos debe devolver true");
    }

    @Test
    void generatorTypeAndGetTypeShouldBeConsistent() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        assertEquals(GeneratorType.MultiobjectiveHillClimbingRestart, gen.getType(),
                "El tipo inicial debe ser MultiobjectiveHillClimbingRestart");
        assertEquals(GeneratorType.MultiobjectiveHillClimbingRestart, gen.getGeneratorType(),
                "getGeneratorType debe coincidir con getType inicialmente");

        gen.setGeneratorType(GeneratorType.HillClimbing);
        assertEquals(GeneratorType.HillClimbing, gen.getGeneratorType(),
                "setGeneratorType debe actualizar el tipo");
        assertEquals(GeneratorType.HillClimbing, gen.getType(),
                "getType debe reflejar el nuevo tipo");
    }

    @Test
    void weightGettersAndSettersShouldWork() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        assertEquals(50.0f, gen.getWeight(), 0.0001,
                "Peso por defecto debe ser 50");

        gen.setWeight(80.0f);
        assertEquals(80.0f, gen.getWeight(), 0.0001,
                "setWeight debe actualizar el peso");
    }

    @Test
    void listCountBetterGenderGenderAndTraceShouldBeInitialized() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        int[] better = gen.getListCountBetterGender();
        int[] gender = gen.getListCountGender();
        float[] trace = gen.getTrace();

        assertNotNull(better);
        assertNotNull(gender);
        assertNotNull(trace);

        assertEquals(10, better.length);
        assertEquals(10, gender.length);
        assertEquals(1_200_000, trace.length);
    }

    @Test
    void getSonListShouldReturnEmptyList() {
        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        List<State> sons = gen.getSonList();

        assertNotNull(sons, "La lista de hijos no debe ser null");
        assertTrue(sons.isEmpty(), "En esta implementación la lista de hijos debe estar vacía");
    }

    @Test
    void sizeNeighborsStaticGetterAndSetterShouldWork() {
        MultiobjectiveHillClimbingRestart.setSizeNeighbors(7);
        assertEquals(7, MultiobjectiveHillClimbingRestart.getSizeNeighbors(),
                "getSizeNeighbors debe devolver el valor estático establecido");
    }

    @Test
    void updateReferenceShouldReturnEarlyWhenReferenceOrCandidateNull()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        MultiobjectiveHillClimbingRestart gen = new MultiobjectiveHillClimbingRestart();

        // referencia null → early return sin tocar Strategy
        State candidate = mock(State.class);
        assertDoesNotThrow(() -> gen.updateReference(candidate, 1),
                "No debe lanzar excepción si la referencia es null (early return)");

        // referencia no null pero candidato null → early return
        gen.setStateRef(mock(State.class));
        assertDoesNotThrow(() -> gen.updateReference(null, 1),
                "No debe lanzar excepción si el candidato es null (early return)");
    }
}
