package es.ull.esit.app.metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.local_search.complement.TabuSolutions;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.Problem;
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class TabuSearchTest {

    @BeforeEach
    void cleanTabu() {
        TabuSolutions.listTabu.clear();
    }

    // Helpers
    private CandidateType getTypeCandidate(TabuSearch ts) throws Exception {
        Field f = TabuSearch.class.getDeclaredField("typeCandidate");
        f.setAccessible(true);
        return (CandidateType) f.get(ts);
    }

    private AcceptType getAcceptType(TabuSearch ts) throws Exception {
        Field f = TabuSearch.class.getDeclaredField("typeAcceptation");
        f.setAccessible(true);
        return (AcceptType) f.get(ts);
    }

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    @Test
    void constructorWithoutStrategyUsesRandomCandidateType() throws Exception {
        try (MockedStatic<Strategy> staticStr = mockStatic(Strategy.class)) {
            staticStr.when(Strategy::getStrategy).thenReturn(null);

            TabuSearch ts = new TabuSearch();

            assertEquals(AcceptType.AcceptAnyone, getAcceptType(ts));
            assertEquals(CandidateType.RandomCandidate, getTypeCandidate(ts));
            assertEquals(50.0f, ts.getWeight(), 0.0001f);
        }
    }

    @Test
    void constructorWithMaximizarProblemUsesGreaterCandidate() throws Exception {
        try (MockedStatic<Strategy> staticStr = mockStatic(Strategy.class)) {

            Strategy stratMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            when(stratMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            staticStr.when(Strategy::getStrategy).thenReturn(stratMock);

            TabuSearch ts = new TabuSearch();

            assertEquals(CandidateType.GreaterCandidate, getTypeCandidate(ts));
        }
    }

    @Test
    void constructorWithNonMaxProblemUsesSmallerCandidate() throws Exception {
        try (MockedStatic<Strategy> staticStr = mockStatic(Strategy.class)) {

            Strategy stratMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);
            ProblemType otherType = mock(ProblemType.class);

            when(stratMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(otherType);

            staticStr.when(Strategy::getStrategy).thenReturn(stratMock);

            TabuSearch ts = new TabuSearch();

            assertEquals(CandidateType.SmallerCandidate, getTypeCandidate(ts));
        }
    }

    // --------------------------------------------------------
    // generate()
    // --------------------------------------------------------
    @Test
    void generateWithoutStrategyReturnsNull() throws Exception {
        try (MockedStatic<Strategy> staticStr = mockStatic(Strategy.class)) {
            staticStr.when(Strategy::getStrategy).thenReturn(null);

            TabuSearch ts = new TabuSearch();
            assertNull(ts.generate(1));
        }
    }

    @Test
    void generateWithValidContextCallsCandidateValueAndReturnsCandidate() throws Exception {
        try (MockedStatic<Strategy> staticStr = mockStatic(Strategy.class)) {

            Strategy stratMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class, RETURNS_DEEP_STUBS);
            staticStr.when(Strategy::getStrategy).thenReturn(stratMock);
            when(stratMock.getProblem()).thenReturn(problemMock);

            TabuSearch ts = new TabuSearch();

            CandidateValue cvMock = mock(CandidateValue.class);
            Field f = TabuSearch.class.getDeclaredField("candidateValue");
            f.setAccessible(true);
            f.set(ts, cvMock);

            State ref = mock(State.class);
            State expected = mock(State.class);
            ts.setInitialReference(ref);

            when(cvMock.stateCandidate(any(), any(), any(), anyInt(), anyList()))
                    .thenReturn(expected);

            assertSame(expected, ts.generate(0));
        }
    }

    // --------------------------------------------------------
    // updateReference()
    // --------------------------------------------------------
    @Test
    void updateReferenceWithNullsDoesNothing() throws Exception {
        TabuSearch ts = new TabuSearch();
        ts.updateReference(null, 0); // ref null -> no crash

        State ref = mock(State.class);
        ts.setInitialReference(ref);
        ts.updateReference(null, 0); // cand null -> nothing
    }

    // 🔥 TEST CORREGIDO COMPLETO
    @Test
    void updateReferenceManagesTabuListAndUpdatesReference() throws Exception {
        TabuSearch ts = new TabuSearch();

        State ref = mock(State.class);
        State s1 = mock(State.class);
        State s2 = mock(State.class);
        State s3 = mock(State.class);

        when(s1.comparator(s1)).thenReturn(true);
        when(s2.comparator(s2)).thenReturn(true);
        when(s3.comparator(s3)).thenReturn(true);

        ts.setInitialReference(ref);

        // 1) Primer candidato
        ts.updateReference(s1, 0);
        assertSame(s1, ts.getReference());
        assertTrue(TabuSolutions.listTabu.contains(s1));

        // 2) Mismo candidato -> no duplica
        ts.updateReference(s1, 1);
        assertSame(s1, ts.getReference());

        // 3) Llenar Tabu para forzar rama de lista llena
        while (TabuSolutions.listTabu.size() < TabuSolutions.maxelements) {
            TabuSolutions.listTabu.add(mock(State.class));
        }

        // 4) Nuevo candidato s2
        ts.updateReference(s2, 2);
        assertSame(s2, ts.getReference());
        assertTrue(TabuSolutions.listTabu.contains(s2));
        assertTrue(TabuSolutions.listTabu.size() <= TabuSolutions.maxelements);

        // 5) s2 de nuevo
        ts.updateReference(s2, 3);
        assertSame(s2, ts.getReference());
        assertTrue(TabuSolutions.listTabu.size() <= TabuSolutions.maxelements);

        // 6) Nuevo candidato s3
        ts.updateReference(s3, 4);
        assertSame(s3, ts.getReference());
        assertTrue(TabuSolutions.listTabu.contains(s3));
        assertTrue(TabuSolutions.listTabu.size() <= TabuSolutions.maxelements);
    }

    // --------------------------------------------------------
    // Otros getters / setters
    // --------------------------------------------------------
    @Test
    void gettersSettersAndListsWork() {
        TabuSearch ts = new TabuSearch();

        ts.setWeight(7.5f);
        assertEquals(7.5f, ts.getWeight(), 0.0001f);

        State s = mock(State.class);
        ts.setInitialReference(s);
        assertSame(s, ts.getReference());

        List<State> list1 = ts.getReferenceList();
        assertEquals(1, list1.size());

        List<State> list2 = ts.getReferenceList();
        assertEquals(2, list2.size());

        assertTrue(ts.getSonList().isEmpty());

        assertNotNull(ts.getListCountBetterGender());
        assertNotNull(ts.getListCountGender());
        assertNotNull(ts.getTrace());
    }
}
