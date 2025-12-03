package es.ull.esit.app.local_search.candidate_type;

import es.ull.esit.app.factory_method.FactoryLoader;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.local_search.complement.TabuSolutions;
import es.ull.esit.app.problem.definition.State;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidateValueTest {

    /**
     * Testea que newSearchCandidate use la Factory internamente y devuelva
     * la instancia de SearchCandidate que recibimos de FactoryLoader.
     */
    @Test
    void newSearchCandidateShouldUseFactoryAndReturnSearchCandidate()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        CandidateValue candidateValue = new CandidateValue();

        // Cogemos cualquier CandidateType existente sin asumir el nombre
        CandidateType type = CandidateType.values()[0];

        // Preparamos el SearchCandidate que queremos que devuelva la factoría
        SearchCandidate expectedSearchCandidate = mock(SearchCandidate.class);

        // Construimos el nombre de clase que usa FactoryCandidate internamente
        String className = "local_search.candidate_type." + type.toString();

        try (MockedStatic<FactoryLoader> loaderMock = Mockito.mockStatic(FactoryLoader.class)) {
            loaderMock
                .when(() -> FactoryLoader.getInstance(className))
                .thenReturn(expectedSearchCandidate);

            SearchCandidate result = candidateValue.newSearchCandidate(type);

            assertSame(expectedSearchCandidate, result,
                    "newSearchCandidate debe devolver el SearchCandidate creado por la factoría");
        }
    }

    /**
     * Testea stateCandidate en la rama donde la estrategia NO es TABU:
     * - No nos preocupamos por TabuSolutions.
     * - Confirmamos que se llama a newSearchCandidate y a stateSearch(auxList),
     *   y que se devuelve el estado obtenido.
     */
    @Test
    void stateCandidateShouldReturnStateFromSearchCandidateWhenStrategyIsNotTabu()
            throws IllegalArgumentException, SecurityException,
                   ClassNotFoundException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // Espiamos CandidateValue para poder sobreescribir newSearchCandidate
        CandidateValue candidateValue = Mockito.spy(new CandidateValue());

        State current = mock(State.class);
        State expectedState = mock(State.class);

        // Vecindario de ejemplo
        State neighbor1 = mock(State.class);
        State neighbor2 = mock(State.class);
        List<State> neighborhood = new ArrayList<>();
        neighborhood.add(neighbor1);
        neighborhood.add(neighbor2);

        // Elegimos un CandidateType válido cualquiera
        CandidateType type = CandidateType.values()[0];

        // Elegimos una estrategia NO TABU si existe; si no, será TABU, pero el test sigue siendo válido
        StrategyType strategyType = StrategyType.values()[0];
        for (StrategyType st : StrategyType.values()) {
            if (!st.equals(StrategyType.TABU)) {
                strategyType = st;
                break;
            }
        }

        // Mock del SearchCandidate que debe usarse internamente
        SearchCandidate searchCandidateMock = mock(SearchCandidate.class);

        // newSearchCandidate(...) debe devolver nuestro mock
        doReturn(searchCandidateMock).when(candidateValue).newSearchCandidate(type);

        // Cuando searchCandidate.stateSearch(auxList) sea llamado, devolver expectedState
        when(searchCandidateMock.stateSearch(anyList())).thenReturn(expectedState);

        Integer operatorNumber = 0; // valor cualquiera que no se usa en esta rama

        State result = candidateValue.stateCandidate(
                current,
                type,
                strategyType,
                operatorNumber,
                neighborhood
        );

        // Verificamos que devolvió exactamente el estado que dio SearchCandidate
        assertSame(expectedState, result,
                "stateCandidate debe devolver el estado devuelto por searchCandidate.stateSearch");

        // Verificamos que newSearchCandidate se invocó una vez con el tipo correcto
        verify(candidateValue, times(1)).newSearchCandidate(type);

        // Verificamos que stateSearch fue llamado con una lista que contiene los vecinos originales
        verify(searchCandidateMock, times(1)).stateSearch(argThat(list -> list.size() == 2
                && list.contains(neighbor1)
                && list.contains(neighbor2)));
    }

    /**
     * Test sencillo para getters y setters de tabusolution.
     */
    @Test
    void tabuSolutionGetterAndSetterShouldWork() {
        CandidateValue candidateValue = new CandidateValue();
        TabuSolutions tabu = mock(TabuSolutions.class);

        candidateValue.setTabusolution(tabu);

        assertSame(tabu, candidateValue.getTabusolution(),
                "getTabusolution debe devolver exactamente la instancia seteada");
    }
}
