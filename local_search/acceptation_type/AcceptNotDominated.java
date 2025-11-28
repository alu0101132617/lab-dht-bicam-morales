package local_search.acceptation_type;


import local_search.acceptation_type.Dominance;
import metaheurictics.strategy.Strategy;

import problem.definition.State;

public class AcceptNotDominated extends AcceptableCandidate {
	
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		Boolean accept = false;
		Dominance dominace = new Dominance();
		
		if(Strategy.getStrategy().listRefPoblacFinal.size() == 0){
			Strategy.getStrategy().listRefPoblacFinal.add(stateCurrent.clone());
		}
		if(dominace.dominance(stateCurrent, stateCandidate)== false)
		{
			//Verificando si la solución candidata domina a alguna de las soluciones de la lista de soluciones no dominadas
			//De ser así se eliminan de la lista y se adiciona la nueva solución en la lista
			//De lo contrario no se adiciona la solución candidata a la lista
			//Si fue insertada en la lista entonces la solucion candidata se convierte en solucion actual
			if(dominace.ListDominance(stateCandidate, Strategy.getStrategy().listRefPoblacFinal) == true){
				//Se pone la solución candidata como solución actual
				accept = true;
			}
			else{
				accept = false;
			}
		}
		return accept;
	}

}
