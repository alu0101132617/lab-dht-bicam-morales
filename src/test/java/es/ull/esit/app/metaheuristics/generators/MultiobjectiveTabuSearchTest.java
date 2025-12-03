package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
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

class MultiobjectiveTabuSearchTest {

    /**
     * Inyecta un mock de CandidateValue en el campo privado candidateValue.
     * Si algo falla en la reflexión, lanza AssertionError (unchecked) para no
     * ensuciar las lambdas de assertDoesNotThrow.
     */
    private CandidateValue injectCandidateValueMock(MultiobjectiveTabuSearch ts) {
        try {
            Field field = MultiobjectiveTabuSearch.class.getDeclaredField("candidateValue");
            field.setAccessible(true);
            CandidateValue mockCandidateValue = mock(CandidateValue.class);
            field.set(ts, mockCandidateValue);
            return mockCandidateValue;
        } catch (Exception e) {
            throw new AssertionError("Error injecting CandidateValue mock via reflection", e);
        }
    }

    @Test
    void constructorShouldInitializeFields() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();

        // Tipo de generador por defecto
        assertEquals(GeneratorType.MultiobjectiveTabuSearch, ts.getType(),
                "Generator type debe ser MultiobjectiveTabuSearch por defecto");

        // Peso inicial
        assertEquals(50.0f, ts.getWeight(), 0.0001,
                "El peso inicial debe ser 50");

        // Traza
        float[] trace = ts.getTrace();
        assertNotNull(trace);
        assertTrue(trace.length >= 1);
        assertEquals(50.0f, trace[0], 0.0001,
                "La traza en la posición 0 debe inicializarse con el peso");

        // Contadores
        int[] better = ts.getListCountBetterGender();
        int[] gender = ts.getListCountGender();
        assertNotNull(better);
        assertNotNull(gender);
        assertEquals(10, better.length);
        assertEquals(10, gender.length);
    }

    @Test
    void stateReferenceGettersAndSettersShouldWork() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        State ref = mock(State.class);

        ts.setStateRef(ref);
        assertSame(ref, ts.getReference());
        assertSame(ref, ts.getStateReferenceTS());

        State ref2 = mock(State.class);
        ts.setInitialReference(ref2);
        assertSame(ref2, ts.getReference());
        assertSame(ref2, ts.getStateReferenceTS());

        State ref3 = mock(State.class);
        ts.setStateReferenceTS(ref3);
        assertSame(ref3, ts.getReference());
        assertSame(ref3, ts.getStateReferenceTS());
    }

    @Test
    void typeGeneratorGetterShouldReturnDefaultType() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        assertEquals(GeneratorType.MultiobjectiveTabuSearch, ts.getType(),
                "getType() debe devolver el tipo por defecto");
    }

    @Test
    void generateShouldUseCandidateValueAndReturnCandidate()
            throws IllegalArgumentException, SecurityException, ClassNotFoundException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException, NoSuchMethodException {

        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        State referenceState = mock(State.class);
        ts.setStateRef(referenceState);

        CandidateValue candidateValueMock = injectCandidateValueMock(ts);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            Operator operatorMock = mock(Operator.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getOperator()).thenReturn(operatorMock);

            List<State> neighbourhood = new ArrayList<>();
            State neighbour = mock(State.class);
            neighbourhood.add(neighbour);

            Integer operatorNumber = 3;
            when(operatorMock.generatedNewState(referenceState, operatorNumber))
                    .thenReturn(neighbourhood);

            State expectedCandidate = mock(State.class);
            when(candidateValueMock.stateCandidate(
                    referenceState,
                    CandidateType.RandomCandidate,
                    StrategyType.TABU,
                    operatorNumber,
                    neighbourhood
            )).thenReturn(expectedCandidate);

            State result = ts.generate(operatorNumber);

            assertSame(expectedCandidate, result,
                    "generate debe devolver el candidato producido por CandidateValue");

            verify(candidateValueMock).stateCandidate(
                    referenceState,
                    CandidateType.RandomCandidate,
                    StrategyType.TABU,
                    operatorNumber,
                    neighbourhood
            );
        }
    }

    @Test
    void generateShouldWorkEvenWhenStrategyIsNull()
            throws IllegalArgumentException, SecurityException, ClassNotFoundException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException, NoSuchMethodException {

        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        State referenceState = mock(State.class);
        ts.setStateRef(referenceState);

        CandidateValue candidateValueMock = injectCandidateValueMock(ts);
        List<State> emptyNeighbourhood = new ArrayList<>();

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            strategyStatic.when(Strategy::getStrategy).thenReturn(null);

            State expectedCandidate = mock(State.class);
            when(candidateValueMock.stateCandidate(
                    referenceState,
                    CandidateType.RandomCandidate,
                    StrategyType.TABU,
                    1,
                    emptyNeighbourhood
            )).thenReturn(expectedCandidate);

            State result = ts.generate(1);

            assertSame(expectedCandidate, result,
                    "generate debe seguir llamando a CandidateValue aunque Strategy sea null");

            verify(candidateValueMock).stateCandidate(
                    referenceState,
                    CandidateType.RandomCandidate,
                    StrategyType.TABU,
                    1,
                    emptyNeighbourhood
            );
        }
    }

    @Test
    void setTypeCandidateShouldAffectGenerateBehaviour()
            throws IllegalArgumentException, SecurityException, ClassNotFoundException,
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException, NoSuchMethodException {

        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        State referenceState = mock(State.class);
        ts.setStateRef(referenceState);

        // Cambiamos el tipo de candidato
        ts.setTypeCandidate(CandidateType.NotDominatedCandidate);

        CandidateValue candidateValueMock = injectCandidateValueMock(ts);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            Operator operatorMock = mock(Operator.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getOperator()).thenReturn(operatorMock);

            List<State> neighbourhood = new ArrayList<>();
            when(operatorMock.generatedNewState(referenceState, 2))
                    .thenReturn(neighbourhood);

            State expectedCandidate = mock(State.class);
            when(candidateValueMock.stateCandidate(
                    referenceState,
                    CandidateType.NotDominatedCandidate,
                    StrategyType.TABU,
                    2,
                    neighbourhood
            )).thenReturn(expectedCandidate);

            State result = ts.generate(2);
            assertSame(expectedCandidate, result);

            verify(candidateValueMock).stateCandidate(
                    referenceState,
                    CandidateType.NotDominatedCandidate,
                    StrategyType.TABU,
                    2,
                    neighbourhood
            );
        }
    }

    @Test
    void updateReferenceShouldReturnEarlyWhenReferenceOrCandidateNull() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();

        assertDoesNotThrow(() ->
                ts.updateReference(null, 0),
                "updateReference no debe lanzar si referencia y candidato son null");

        State ref = mock(State.class);
        ts.setStateRef(ref);

        assertDoesNotThrow(() ->
                ts.updateReference(null, 1),
                "updateReference no debe lanzar si el candidato es null");
    }

    @Test
    void getReferenceListShouldAccumulateReferencesAndReturnCopy() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        State ref = mock(State.class);
        ts.setStateRef(ref);

        List<State> first = ts.getReferenceList();
        assertEquals(1, first.size());
        assertSame(ref, first.get(0));

        first.add(mock(State.class));

        List<State> second = ts.getReferenceList();
        assertEquals(2, second.size(),
                "Cada llamada debe añadir la referencia interna una vez");
        assertSame(ref, second.get(0));
        assertSame(ref, second.get(1));
    }

    @Test
    void getSonListShouldReturnEmptyListCopy() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();
        List<State> sons1 = ts.getSonList();
        assertNotNull(sons1);
        assertTrue(sons1.isEmpty());

        sons1.add(mock(State.class));
        List<State> sons2 = ts.getSonList();
        assertTrue(sons2.isEmpty(),
                "Modificar la lista devuelta no debe afectar a llamadas posteriores");
    }

    @Test
    void awardUpdateREFShouldBehaveAccordingToEquality() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();

        State candidate = mock(State.class);
        assertFalse(ts.awardUpdateREF(candidate),
                "Si la referencia es null, debe devolver false");

        State ref = mock(State.class);
        ts.setStateRef(ref);

        assertFalse(ts.awardUpdateREF(ref),
                "Si el candidato es exactamente la misma referencia, debe devolver false");

        State other = mock(State.class);
        assertTrue(ts.awardUpdateREF(other),
                "Si el candidato es distinto, awardUpdateREF debe devolver true");
    }

    @Test
    void weightAndTraceShouldBeConsistentAndListsHaveExpectedLength() {
        MultiobjectiveTabuSearch ts = new MultiobjectiveTabuSearch();

        assertEquals(50.0f, ts.getWeight(), 0.0001f);

        ts.setWeight(42.0f);
        assertEquals(42.0f, ts.getWeight(), 0.0001f);

        int[] better = ts.getListCountBetterGender();
        int[] gender = ts.getListCountGender();
        assertEquals(10, better.length);
        assertEquals(10, gender.length);

        float[] trace = ts.getTrace();
        assertNotNull(trace);
        assertTrue(trace.length >= 1);
    }
}
