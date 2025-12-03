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

import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Operator;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.State;

class MultiobjectiveStochasticHillClimbingTest {

    /** Crea un State real con evaluación simple. */
    private State createStateWithEval(double value) {
        State s = new State();
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(value);
        s.setEvaluation(eval);
        return s;
    }

    /** Inyecta un valor en un campo privado de MultiobjectiveStochasticHillClimbing. */
    private void setPrivateField(MultiobjectiveStochasticHillClimbing hc, String fieldName, Object value)
            throws Exception {
        Field f = MultiobjectiveStochasticHillClimbing.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(hc, value);
    }

    // --------------------------------------------------------
    //  CONSTRUCTOR
    // --------------------------------------------------------
    @Test
    void constructorShouldInitializeFieldsCorrectly() throws Exception {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();

        Field accField = MultiobjectiveStochasticHillClimbing.class.getDeclaredField("typeAcceptation");
        accField.setAccessible(true);
        assertEquals(AcceptType.AcceptNotDominated, accField.get(hc));

        Field stratField = MultiobjectiveStochasticHillClimbing.class.getDeclaredField("strategy");
        stratField.setAccessible(true);
        assertEquals(StrategyType.NORMAL, stratField.get(hc));

        Field candTypeField = MultiobjectiveStochasticHillClimbing.class.getDeclaredField("typeCandidate");
        candTypeField.setAccessible(true);
        assertEquals(CandidateType.NotDominatedCandidate, candTypeField.get(hc));

        Field candValField = MultiobjectiveStochasticHillClimbing.class.getDeclaredField("candidateValue");
        candValField.setAccessible(true);
        assertNotNull(candValField.get(hc));

        assertEquals(GeneratorType.MultiobjectiveStochasticHillClimbing, hc.getType());
        assertEquals(50.0f, hc.getWeight(), 0.0001);

        float[] trace = hc.getTrace();
        assertNotNull(trace);
        assertEquals(50.0f, trace[0], 0.0001);
    }

    // --------------------------------------------------------
    //  GENERATE
    // --------------------------------------------------------
    @Test
    void generateShouldUseCandidateValueAndReturnCandidate()
            throws Exception {

        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();

        State ref = createStateWithEval(1.0);
        hc.setStateRef(ref);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            Operator operatorMock = mock(Operator.class);

            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getOperator()).thenReturn(operatorMock);

            State neighbour = createStateWithEval(2.0);
            List<State> neighbourhood = new ArrayList<>();
            neighbourhood.add(neighbour);

            when(operatorMock.generatedNewState(ref, 0)).thenReturn(neighbourhood);

            CandidateValue candidateValueMock = mock(CandidateValue.class);
            setPrivateField(hc, "candidateValue", candidateValueMock);

            State expected = createStateWithEval(3.0);
            when(candidateValueMock.stateCandidate(eq(ref),
                                                   eq(CandidateType.NotDominatedCandidate),
                                                   eq(StrategyType.NORMAL),
                                                   eq(0),
                                                   eq(neighbourhood)))
                    .thenReturn(expected);

            State result = hc.generate(0);
            assertSame(expected, result);
        }
    }

    @Test
    void generateShouldWorkEvenIfProblemOrOperatorIsNull()
            throws Exception {

        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();
        State ref = createStateWithEval(1.0);
        hc.setStateRef(ref);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {

            Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            when(strategyMock.getProblem()).thenReturn(null);

            CandidateValue candidateValueMock = mock(CandidateValue.class);
            setPrivateField(hc, "candidateValue", candidateValueMock);

            when(candidateValueMock.stateCandidate(eq(ref),
                                                   eq(CandidateType.NotDominatedCandidate),
                                                   eq(StrategyType.NORMAL),
                                                   eq(1),
                                                   anyList()))
                    .thenReturn(ref);

            State result = hc.generate(1);
            assertSame(ref, result);
        }
    }

    // --------------------------------------------------------
    //  REFERENCE MANAGEMENT
    // --------------------------------------------------------
    @Test
    void getReferenceAndSetStateRefAndSetInitialReferenceShouldWork() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();

        assertNull(hc.getReference());

        State s = createStateWithEval(5.0);
        hc.setStateRef(s);
        assertSame(s, hc.getReference());

        State s2 = createStateWithEval(10.0);
        hc.setInitialReference(s2);
        assertSame(s2, hc.getReference());
    }

    @Test
    void getReferenceListShouldAccumulateCopiesOfReference() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();
        State s = createStateWithEval(1.0);
        hc.setStateRef(s);

        List<State> first = hc.getReferenceList();
        assertEquals(1, first.size());

        List<State> second = hc.getReferenceList();
        assertEquals(2, second.size());
    }

    @Test
    void getAndSetGeneratorTypeShouldWork() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();

        assertEquals(GeneratorType.MultiobjectiveStochasticHillClimbing, hc.getGeneratorType());
        assertEquals(GeneratorType.MultiobjectiveStochasticHillClimbing, hc.getType());

        hc.setGeneratorType(GeneratorType.HillClimbing);
        assertEquals(GeneratorType.HillClimbing, hc.getGeneratorType());
        assertEquals(GeneratorType.HillClimbing, hc.getType());
    }

    @Test
    void getSonListShouldReturnEmptyList() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();
        assertTrue(hc.getSonList().isEmpty());
    }

    @Test
    void awardUpdateREFShouldBeCorrect() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();

        State s = createStateWithEval(3.0);
        State t = createStateWithEval(4.0);

        assertFalse(hc.awardUpdateREF(s));

        hc.setStateRef(s);
        assertFalse(hc.awardUpdateREF(null));

        assertTrue(hc.awardUpdateREF(t));
    }

    @Test
    void weightSetAndGetShouldWork() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();
        assertEquals(50.0f, hc.getWeight(), 0.0001);
        hc.setWeight(80.0f);
        assertEquals(80.0f, hc.getWeight(), 0.0001);
    }

    @Test
    void listCountBetterGenderGenderAndTraceShouldBeInitialized() {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();

        assertEquals(10, hc.getListCountBetterGender().length);
        assertEquals(10, hc.getListCountGender().length);
        assertEquals(1200000, hc.getTrace().length);
    }

    // --------------------------------------------------------
    //  🔥 TEST CORREGIDO: YA NO ESPERA ClassNotFoundException
    // --------------------------------------------------------
    @Test
    void updateReferenceShouldNotThrowWithFixedFactoryPackage() throws Exception {
        MultiobjectiveStochasticHillClimbing hc = new MultiobjectiveStochasticHillClimbing();
        State ref = createStateWithEval(1.0);
        State cand = createStateWithEval(2.0);
        hc.setStateRef(ref);

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {

            Strategy strategyMock = mock(Strategy.class, RETURNS_DEEP_STUBS);
            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);

            Problem problemMock = mock(Problem.class);
            Operator operatorMock = mock(Operator.class);

            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getOperator()).thenReturn(operatorMock);

            // 🔥 ESTE ERA EL NPE → SOLUCIÓN:
            when(problemMock.getTypeProblem()).thenReturn(Problem.ProblemType.MAXIMIZAR);

            when(operatorMock.generatedNewState(any(), anyInt()))
                    .thenReturn(new ArrayList<>());

            assertDoesNotThrow(() -> hc.updateReference(cand, 1));
        }
    }
}
