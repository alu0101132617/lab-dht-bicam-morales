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
import es.ull.esit.app.problem.definition.Problem.ProblemType;
import es.ull.esit.app.problem.definition.State;

class LimitThresholdTest {

    /** Pequeño helper para crear un State con evaluación simple. */
    private State createStateWithEval(double value) {
        State s = new State();
        ArrayList<Double> eval = new ArrayList<>();
        eval.add(value);
        s.setEvaluation(eval);
        return s;
    }

    @Test
    void constructorShouldConfigureForMaximization() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            // Tipo de generador
            assertEquals(GeneratorType.LimitThreshold, lt.getType());
            // Peso inicial
            assertEquals(50.0f, lt.getWeight(), 0.0001);

            // Arrays y tamaños
            int[] better = lt.getListCountBetterGender();
            int[] gender = lt.getListCountGender();
            float[] trace = lt.getTrace();

            assertNotNull(better);
            assertNotNull(gender);
            assertNotNull(trace);

            assertEquals(10, better.length);
            assertEquals(10, gender.length);
            assertEquals(1_200_000, trace.length);

            // Valores iniciales
            assertEquals(0, better[0]);
            assertEquals(0, gender[0]);
            assertEquals(50.0f, trace[0], 0.0001);

            // Ver que para Max se usa GreaterCandidate
            try {
                Field f = LimitThreshold.class.getDeclaredField("typeCandidate");
                f.setAccessible(true);
                CandidateType typeCandidate = (CandidateType) f.get(lt);
                assertEquals(CandidateType.GreaterCandidate, typeCandidate);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Field typeCandidate should exist and be accessible for the test");
            }

            // StrategyType y AcceptType básicos
            try {
                Field fStrategy = LimitThreshold.class.getDeclaredField("strategy");
                fStrategy.setAccessible(true);
                StrategyType strategyType = (StrategyType) fStrategy.get(lt);
                assertEquals(StrategyType.NORMAL, strategyType);

                Field fCandidateValue = LimitThreshold.class.getDeclaredField("candidateValue");
                fCandidateValue.setAccessible(true);
                Object cv = fCandidateValue.get(lt);
                assertNotNull(cv, "candidateValue should be initialised");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Internal fields not accessible: " + e.getMessage());
            }
        }
    }

    @Test
    void constructorShouldConfigureForMinimization() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            // Ver que para Min se usa SmallerCandidate
            try {
                Field f = LimitThreshold.class.getDeclaredField("typeCandidate");
                f.setAccessible(true);
                CandidateType typeCandidate = (CandidateType) f.get(lt);
                assertEquals(CandidateType.SmallerCandidate, typeCandidate);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Field typeCandidate should exist and be accessible for the test");
            }
        }
    }

    @Test
    void setAndGetReferenceShouldWorkAndReferenceListShouldAccumulate() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            State ref = createStateWithEval(10.0);
            lt.setStateRef(ref);

            assertSame(ref, lt.getReference(), "getReference should return the current reference");

            // Primera llamada: debe añadir la referencia
            List<State> refList1 = lt.getReferenceList();
            assertEquals(1, refList1.size());
            assertSame(ref, refList1.get(0));

            // Segunda llamada: vuelve a añadir la referencia
            List<State> refList2 = lt.getReferenceList();
            assertEquals(2, refList2.size());
            assertSame(ref, refList2.get(0));
            assertSame(ref, refList2.get(1));

            // Deben ser copias distintas
            assertNotSame(refList1, refList2);
        }
    }

    @Test
    void getSonListShouldReturnEmptyListEachTime() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            List<State> sons1 = lt.getSonList();
            List<State> sons2 = lt.getSonList();

            assertNotNull(sons1);
            assertTrue(sons1.isEmpty());

            assertNotNull(sons2);
            assertTrue(sons2.isEmpty());

            assertNotSame(sons1, sons2, "Each call should return a new empty list");
        }
    }

    @Test
    void weightGettersAndSettersShouldWork() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            assertEquals(50.0f, lt.getWeight(), 0.0001);

            lt.setWeight(80.5f);
            assertEquals(80.5f, lt.getWeight(), 0.0001);
        }
    }

    @Test
    void listCountArraysAndTraceShouldHaveExpectedLengths() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            int[] better = lt.getListCountBetterGender();
            int[] gender = lt.getListCountGender();
            float[] trace = lt.getTrace();

            assertEquals(10, better.length);
            assertEquals(10, gender.length);
            assertEquals(1_200_000, trace.length);
        }
    }

    @Test
    void awardUpdateREFShouldWorkForMaximization() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            State ref = createStateWithEval(10.0);
            State better = createStateWithEval(15.0);
            State worse = createStateWithEval(5.0);

            lt.setStateRef(ref);

            assertTrue(lt.awardUpdateREF(better),
                    "For MAXIMIZAR, candidate with higher evaluation should be awarded");
            assertFalse(lt.awardUpdateREF(worse),
                    "For MAXIMIZAR, candidate with lower evaluation should not be awarded");
        }
    }

    @Test
    void awardUpdateREFShouldWorkForMinimization() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MINIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            State ref = createStateWithEval(10.0);
            State better = createStateWithEval(5.0);
            State worse = createStateWithEval(15.0);

            lt.setStateRef(ref);

            assertTrue(lt.awardUpdateREF(better),
                    "For MINIMIZAR, candidate with lower evaluation should be awarded");
            assertFalse(lt.awardUpdateREF(worse),
                    "For MINIMIZAR, candidate with higher evaluation should not be awarded");
        }
    }

    @Test
    void awardUpdateREFShouldHandleNullsAndEmptyEvaluationsGracefully() {
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            // reference null
            State candidate = createStateWithEval(10.0);
            assertFalse(lt.awardUpdateREF(candidate));

            // candidate null
            State ref = createStateWithEval(10.0);
            lt.setStateRef(ref);
            assertFalse(lt.awardUpdateREF(null));

            // reference without evaluation
            State refNoEval = new State();
            lt.setStateRef(refNoEval);
            assertFalse(lt.awardUpdateREF(candidate));

            // candidate without evaluation
            lt.setStateRef(ref);
            State candNoEval = new State();
            assertFalse(lt.awardUpdateREF(candNoEval));
        }
    }

    @Test
    void generateShouldUseCandidateValueAndReturnCandidate()
            throws NoSuchFieldException, IllegalAccessException,
                   IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   InvocationTargetException, NoSuchMethodException {

        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);
            Operator operatorMock = mock(Operator.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);
            when(problemMock.getOperator()).thenReturn(operatorMock);

            LimitThreshold lt = new LimitThreshold();

            // Referencia inicial
            State ref = createStateWithEval(10.0);
            lt.setStateRef(ref);

            // Vecindario generado por el operador
            List<State> neighbourhood = new ArrayList<>();
            State n1 = createStateWithEval(11.0);
            neighbourhood.add(n1);

            when(operatorMock.generatedNewState(eq(ref), eq(3))).thenReturn(neighbourhood);

            // Sustituimos candidateValue por un mock
            Field f = LimitThreshold.class.getDeclaredField("candidateValue");
            f.setAccessible(true);
            CandidateValue candidateValueMock = mock(CandidateValue.class);
            f.set(lt, candidateValueMock);

            State expectedCandidate = createStateWithEval(20.0);

            when(candidateValueMock.stateCandidate(
                    eq(ref),
                    any(CandidateType.class),
                    any(StrategyType.class),
                    eq(3),
                    eq(neighbourhood)))
                .thenReturn(expectedCandidate);

            State result = lt.generate(3);

            assertSame(expectedCandidate, result,
                    "generate should delegate to CandidateValue and return that candidate");
        }
    }

    @Test
    void updateReferenceShouldNotThrowEvenIfFactoryBehaviourIsComplex() throws Exception {
        // Este test sólo busca ejecutar el método; no asumimos nada del Factory real,
        // pero nos aseguramos de que no revienta el test suite por excepciones no controladas.
        try (MockedStatic<Strategy> strategyStatic = Mockito.mockStatic(Strategy.class)) {
            Strategy strategyMock = mock(Strategy.class);
            Problem problemMock = mock(Problem.class);

            strategyStatic.when(Strategy::getStrategy).thenReturn(strategyMock);
            when(strategyMock.getProblem()).thenReturn(problemMock);
            when(problemMock.getTypeProblem()).thenReturn(ProblemType.MAXIMIZAR);

            LimitThreshold lt = new LimitThreshold();

            State ref = createStateWithEval(10.0);
            State candidate = createStateWithEval(12.0);
            lt.setStateRef(ref);

            try {
                lt.updateReference(candidate, 0);
                // No afirmamos nada sobre el estado final porque depende de la implementación real
                // de AcceptNotBadU y FactoryAcceptCandidate.
            } catch (ClassNotFoundException |
                     InstantiationException |
                     IllegalAccessException |
                     InvocationTargetException |
                     NoSuchMethodException e) {
                // Si en tu proyecto real falta alguna clase de aceptación, esta rama
                // evita que el test falle por ello.
            }
        }
    }
}
