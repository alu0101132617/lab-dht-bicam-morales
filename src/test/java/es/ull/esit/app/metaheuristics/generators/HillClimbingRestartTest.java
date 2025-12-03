package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import es.ull.esit.app.factory_method.FactoryAcceptCandidate;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

/**
 * Unit tests for HillClimbingRestart.
 */
class HillClimbingRestartTest {

    /** Helper to create an evaluation list. */
    private ArrayList<Double> eval(double v) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(v);
        return list;
    }

    /** Helper to inject a mock CandidateValue into HillClimbingRestart via reflection. */
    private void injectCandidateValue(HillClimbingRestart hc, CandidateValue mockCandidateValue) throws Exception {
        Field f = HillClimbingRestart.class.getDeclaredField("candidatevalue");
        f.setAccessible(true);
        f.set(hc, mockCandidateValue);
    }

    /** Helper to read private field typeCandidate. */
    private CandidateType getTypeCandidate(HillClimbingRestart hc) throws Exception {
        Field f = HillClimbingRestart.class.getDeclaredField("typeCandidate");
        f.setAccessible(true);
        return (CandidateType) f.get(hc);
    }

    @Test
    void constructorShouldSetCandidateTypeAccordingToProblemType() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);

            // MAXIMIZAR → GreaterCandidate
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            HillClimbingRestart hcMax = new HillClimbingRestart();
            assertEquals(CandidateType.GreaterCandidate, getTypeCandidate(hcMax),
                    "For MAXIMIZAR, candidate type should be GreaterCandidate");

            // MINIMIZAR → SmallerCandidate
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
            HillClimbingRestart hcMin = new HillClimbingRestart();
            assertEquals(CandidateType.SmallerCandidate, getTypeCandidate(hcMin),
                    "For MINIMIZAR, candidate type should be SmallerCandidate");
        }
    }

    @Test
    void generateShouldRestartWhenCountMatchesAndReturnCandidate() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            // Mock Strategy and Problem
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class, Mockito.RETURNS_DEEP_STUBS);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            // Mock Operator
            Operator operatorMock = mock(Operator.class);
            when(problemMock.getOperator()).thenReturn(operatorMock);
            doNothing().when(problemMock).evaluate(any(State.class));

            // Strategy devuelve 0 en getCountCurrent()
            // El campo 'count' en HillClimbingRestart empieza en 0 → condición count == current se cumple y se produce restart.
            when(strategyMock.getCountCurrent()).thenReturn(0);

            // Create generator
            HillClimbingRestart hc = new HillClimbingRestart();

            // Reference actual (antigua) que se guardará internamente en el reinicio
            State oldRef = new State();
            hc.setStateRef(oldRef);

            // New random reference state generated on restart
            State randomRef = new State();
            List<State> randomList = new ArrayList<>();
            randomList.add(randomRef);
            when(operatorMock.generateRandomState(1)).thenReturn(randomList);

            // Neighborhood
            State neighbor = new State();
            List<State> neighborhood = new ArrayList<>();
            neighborhood.add(neighbor);
            when(operatorMock.generatedNewState(any(State.class), eq(1))).thenReturn(neighborhood);

            // CandidateValue mocked to control returned candidate
            CandidateValue candidateValueMock = mock(CandidateValue.class);
            State expectedCandidate = new State();
            when(candidateValueMock.stateCandidate(any(), any(), any(), eq(1), eq(neighborhood)))
                    .thenReturn(expectedCandidate);
            injectCandidateValue(hc, candidateValueMock);

            State result = hc.generate(1);

            assertSame(expectedCandidate, result, "generate should return the candidate from CandidateValue");
            assertSame(randomRef, hc.getReference(), "Reference should be updated to the new random state on restart");
        }
    }

    @Test
    void generateShouldNotRestartWhenCountDoesNotMatch() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class, Mockito.RETURNS_DEEP_STUBS);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            Operator operatorMock = mock(Operator.class);
            when(problemMock.getOperator()).thenReturn(operatorMock);

            // Strategy devuelve 5, pero el campo count (por defecto) es 0 → no hay restart
            when(strategyMock.getCountCurrent()).thenReturn(5);

            HillClimbingRestart hc = new HillClimbingRestart();

            // Pre-set reference
            State ref = new State();
            hc.setStateRef(ref);

            // Neighborhood solamente (no se espera generateRandomState)
            State neighbor = new State();
            List<State> neighborhood = new ArrayList<>();
            neighborhood.add(neighbor);
            when(operatorMock.generatedNewState(ref, 2)).thenReturn(neighborhood);

            CandidateValue candidateValueMock = mock(CandidateValue.class);
            State expectedCandidate = new State();
            // IMPORTANTE: todos los argumentos deben ser matchers
            when(candidateValueMock.stateCandidate(eq(ref), any(), any(), eq(2), eq(neighborhood)))
                    .thenReturn(expectedCandidate);
            injectCandidateValue(hc, candidateValueMock);

            State result = hc.generate(2);
            assertSame(expectedCandidate, result, "generate should return the candidate when no restart occurs");

            // Verificar que NO hubo restart
            verify(operatorMock, never()).generateRandomState(anyInt());
            verify(problemMock, never()).evaluate(any(State.class));
        }
    }

    @Test
    void updateReferenceShouldReplaceReferenceWhenCandidateAccepted() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class);
             MockedConstruction<FactoryAcceptCandidate> factoryConstruction =
                     Mockito.mockConstruction(FactoryAcceptCandidate.class,
                             (mockFactory, context) -> {
                                 AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
                                 when(mockFactory.createAcceptCandidate(any()))
                                         .thenReturn(acceptable);
                                 when(acceptable.acceptCandidate(any(), any()))
                                         .thenReturn(true);
                             })) {

            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();
            State ref = new State();
            State cand = new State();
            hc.setStateRef(ref);

            hc.updateReference(cand, 1);

            assertSame(cand, hc.getReference(),
                    "When AcceptableCandidate returns true, reference should be updated to candidate");
        }
    }

    @Test
    void updateReferenceShouldKeepReferenceWhenCandidateRejected() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class);
             MockedConstruction<FactoryAcceptCandidate> factoryConstruction =
                     Mockito.mockConstruction(FactoryAcceptCandidate.class,
                             (mockFactory, context) -> {
                                 AcceptableCandidate acceptable = mock(AcceptableCandidate.class);
                                 when(mockFactory.createAcceptCandidate(any()))
                                         .thenReturn(acceptable);
                                 when(acceptable.acceptCandidate(any(), any()))
                                         .thenReturn(false);
                             })) {

            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();
            State ref = new State();
            State cand = new State();
            hc.setStateRef(ref);

            hc.updateReference(cand, 1);

            assertSame(ref, hc.getReference(),
                    "When AcceptableCandidate returns false, reference must remain unchanged");
        }
    }

    @Test
    void getReferenceListShouldReturnListWithCurrentReferenceEachTime() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();

            State ref = new State();
            hc.setStateRef(ref);

            List<State> refList1 = hc.getReferenceList();
            assertFalse(refList1.isEmpty(), "Reference list should contain the current reference");
            assertSame(ref, refList1.get(0), "Current reference should be present in the list");

            // Modificamos la lista devuelta y comprobamos que podemos volver a obtener una lista coherente
            refList1.add(new State());

            List<State> refList2 = hc.getReferenceList();
            assertFalse(refList2.isEmpty(), "Second call should still return at least the current reference");
            assertSame(ref, refList2.get(0), "Current reference must still be the first element");
        }
    }

    @Test
    void getSonListShouldReturnEmptyList() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();
            List<State> sons = hc.getSonList();
            assertNotNull(sons, "getSonList should not return null");
            assertTrue(sons.isEmpty(), "HillClimbingRestart does not keep sons, list must be empty");
        }
    }

    @Test
    void awardUpdateREFShouldReturnTrueOrFalseAccordingToProblemTypeAndEvaluations() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);

            // MAXIMIZAR: candidate > reference → true
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            HillClimbingRestart hcMax = new HillClimbingRestart();
            State refMax = new State();
            refMax.setEvaluation(eval(1.0));
            State candBetter = new State();
            candBetter.setEvaluation(eval(2.0));
            hcMax.setStateRef(refMax);
            assertTrue(hcMax.awardUpdateREF(candBetter),
                    "For MAXIMIZAR, candidate with higher evaluation should be accepted");

            // MINIMIZAR: candidate < reference → true
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);
            HillClimbingRestart hcMin = new HillClimbingRestart();
            State refMin = new State();
            refMin.setEvaluation(eval(5.0));
            State candSmaller = new State();
            candSmaller.setEvaluation(eval(3.0));
            hcMin.setStateRef(refMin);
            assertTrue(hcMin.awardUpdateREF(candSmaller),
                    "For MINIMIZAR, candidate with lower evaluation should be accepted");

            // Null / invalid data → false
            assertFalse(hcMin.awardUpdateREF(null),
                    "If candidate is null, awardUpdateREF must return false");

            State invalid = new State(); // no evaluation set
            assertFalse(hcMin.awardUpdateREF(invalid),
                    "If evaluations are missing, awardUpdateREF must return false");
        }
    }

    @Test
    void weightSetAndGetShouldWork() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();

            assertEquals(50.0f, hc.getWeight(), 0.0001,
                    "Default weight should be 50 according to constructor");

            hc.setWeight(80.0f);
            assertEquals(80.0f, hc.getWeight(), 0.0001,
                    "setWeight must update the generator weight");
        }
    }

    @Test
    void statisticsArraysShouldBeInitializedWithExpectedLengths() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();

            int[] better = hc.getListCountBetterGender();
            int[] gender = hc.getListCountGender();
            float[] trace = hc.getTrace();

            assertNotNull(better);
            assertNotNull(gender);
            assertNotNull(trace);

            assertEquals(10, better.length, "Better gender array length must be 10");
            assertEquals(10, gender.length, "Gender array length must be 10");
            assertEquals(1200000, trace.length, "Trace array length must be 1,200,000");
        }
    }

    @Test
    void generatorTypeGettersAndSettersShouldWork() throws Exception {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, Mockito.RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            Problem problemMock = mock(Problem.class);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            HillClimbingRestart hc = new HillClimbingRestart();

            assertEquals(GeneratorType.HillClimbing, hc.getType(),
                    "Default generator type should be HillClimbing");
            assertEquals(GeneratorType.HillClimbing, hc.getGeneratorType(),
                    "getGeneratorType should match getType");

            hc.setGeneratorType(GeneratorType.GeneticAlgorithm);
            assertEquals(GeneratorType.GeneticAlgorithm, hc.getGeneratorType(),
                    "setGeneratorType must update the internal generator type");
        }
    }
}
