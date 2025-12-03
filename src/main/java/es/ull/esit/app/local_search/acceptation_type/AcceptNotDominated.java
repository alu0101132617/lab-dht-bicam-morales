package es.ull.esit.app.local_search.acceptation_type;

import es.ull.esit.app.metaheurictics.strategy.Strategy;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the acceptation type "Accept Not Dominated".
 */
public class AcceptNotDominated extends AcceptableCandidate {
	
  /**
   * Decides whether to accept or not a candidate state.
   * @param stateCurrent [State] Current state.
   * @param stateCandidate [State] Candidate state.
   * @return [Boolean] True if the candidate state is accepted, false otherwise.
   */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		Boolean accept = false;
		Dominance dominace = new Dominance();
		
		if(Strategy.getStrategy().getListRefPoblacFinal().isEmpty()){
			Strategy.getStrategy().getListRefPoblacFinal().add(stateCurrent.copy());
		}
		if(!dominace.dominance(stateCurrent, stateCandidate))
		{
			//Verificando si la soluci�n candidata domina a alguna de las soluciones de la lista de soluciones no dominadas
			//De ser as� se eliminan de la lista y se adiciona la nueva soluci�n en la lista
			//De lo contrario no se adiciona la soluci�n candidata a la lista
			//Si fue insertada en la lista entonces la solucion candidata se convierte en solucion actual
			if(dominace.listDominance(stateCandidate, Strategy.getStrategy().getListRefPoblacFinal())){
				//Se pone la soluci�n candidata como soluci�n actual
				accept = true;
			}
			else{
				accept = false;
			}
		}
		return accept;
	}

}
