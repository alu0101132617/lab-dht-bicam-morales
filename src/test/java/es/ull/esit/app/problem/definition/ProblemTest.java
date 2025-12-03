package es.ull.esit.app.problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.ull.esit.app.problem.definition.Problem.ProblemType;

class ProblemTest {

    /** ObjetiveFunction simple para pruebas: evalúa como longitud del código. */
    static class LengthObjetiveFunction extends ObjetiveFunction {
        @Override
        public Double evaluation(State state) {
            return (double) state.getCode().size();
        }
    }

    /** Codification dummy para setters/getters. */
    static class DummyCodification extends Codification {
        @Override
        public boolean validState(State state) { return true; }
        @Override
        public Object getVariableAleatoryValue(int key) { return key; }
        @Override
        public int getAleatoryKey() { return 1; }
        @Override
        public int getVariableCount() { return 5; }
    }

    /** Operator dummy para setters/getters. */
    static class DummyOperator extends Operator {
        @Override
        public List<State> generatedNewState(State stateCurrent, Integer operatornumber) {
            return new ArrayList<>();
        }
        @Override
        public List<State> generateRandomState(Integer operatornumber) {
            return new ArrayList<>();
        }
    }

    @Test
    void gettersAndSettersShouldWork() {
        Problem p = new Problem();

        // Functions
        List<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(new LengthObjetiveFunction());
        p.setFunction(functions);

        assertEquals(1, p.getFunction().size(),
                "getFunction debe devolver la lista establecida");

        // State
        State s = new State();
        p.setState(s);
        assertSame(s, p.getState());

        // TypeProblem
        p.setTypeProblem(ProblemType.MINIMIZAR);
        assertEquals(ProblemType.MINIMIZAR, p.getTypeProblem());

        // Codification
        DummyCodification cod = new DummyCodification();
        p.setCodification(cod);
        assertSame(cod, p.getCodification());

        // Operator
        DummyOperator op = new DummyOperator();
        p.setOperator(op);
        assertSame(op, p.getOperator());

        // possibleValue
        p.setPossibleValue(10);
        assertEquals(10, p.getPossibleValue());
    }

    @Test
    void setFunctionShouldMakeDefensiveCopy() {
        Problem p = new Problem();

        List<ObjetiveFunction> original = new ArrayList<>();
        original.add(new LengthObjetiveFunction());

        p.setFunction(original);

        // Modificamos la lista original, la del problema no debe verse afectada
        original.clear();

        assertEquals(1, p.getFunction().size(),
                "La lista interna de funciones debe ser una copia defensiva");
    }

    @Test
    void evaluateWithNullTypeSolutionMethodShouldUseFirstObjective()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException {

        Problem p = new Problem();

        List<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(new LengthObjetiveFunction());
        p.setFunction(functions);

        // typeSolutionMethod se mantiene null para entrar en la rama 'if'
        State s = new State();
        s.getCode().add("a");
        s.getCode().add("b");

        p.evaluate(s);

        assertNotNull(s.getEvaluation(), "La evaluación no debe ser null");
        assertEquals(1, s.getEvaluation().size(),
                "Debe haber una única componente de evaluación");
        assertEquals(2.0, s.getEvaluation().get(0), 0.0001,
                "La evaluación debe coincidir con la de la función objetivo");
    }
}
