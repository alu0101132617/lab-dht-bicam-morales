package local_search.acceptation_type;

import metaheuristics.generators.*;
import metaheurictics.strategy.*;

import java.util.List;
import java.util.Random;

import problem.definition.State;

public class AcceptMulticase extends AcceptableCandidate {

	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		// TODO Auto-generated method stub
		Boolean accept = false;
		List<State> list = Strategy.getStrategy().listRefPoblacFinal;
		
		if(list.size() == 0){
			list.add(stateCurrent.clone());
		}
		Double T = MultiCaseSimulatedAnnealing.tinitial;
		double pAccept = 0;
		Random rdm = new Random();
		Dominance dominance= new Dominance();
		//Verificando si la solución candidata domina a la solución actual
		//Si la solución candidata domina a la solución actual
		if(dominance.dominance(stateCandidate, stateCurrent) == true){
			//Se asigna como solución actual la solución candidata con probabilidad 1
			pAccept = 1; 
		}
		else if(dominance.dominance(stateCandidate, stateCurrent)== false){	
			if(DominanceCounter(stateCandidate, list) > 0){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) == 0){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) < DominanceRank(stateCurrent, list)){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) == DominanceRank(stateCurrent, list)){
				//Calculando la probabilidad de aceptación
				List<Double> evaluations = stateCurrent.getEvaluation();
				double total = 0;
				for (int i = 0; i < evaluations.size()-1; i++) {
					Double evalA = evaluations.get(i);
					Double evalB = stateCandidate.getEvaluation().get(i);
					if (evalA != 0 && evalB != 0) {
						total += (evalA - evalB)/((evalA + evalB)/2);
					}
				}	
				pAccept = Math.exp(-(1-total)/T);
			}
			else if (DominanceRank(stateCandidate, list) > DominanceRank(stateCurrent, list) && DominanceRank(stateCurrent, list)!= 0){
				float value = DominanceRank(stateCandidate, list)/DominanceRank(stateCurrent, list);
				pAccept = Math.exp(-(value+1)/T);
			}
			else{
				//Calculando la probabilidad de aceptación
				List<Double> evaluations = stateCurrent.getEvaluation();
				double total = 0;
				for (int i = 0; i < evaluations.size()-1; i++) {
					Double evalA = evaluations.get(i);
					Double evalB = stateCandidate.getEvaluation().get(i);
					if (evalA != 0 && evalB != 0) {
						total += (evalA - evalB)/((evalA + evalB)/2);
					}
				}
				pAccept = Math.exp(-(1-total)/T);
			}
		}
		//Generar un número aleatorio
		if((rdm.nextFloat()) < pAccept){
			stateCurrent = stateCandidate.clone();
			//Verificando que la solución candidata domina a alguna de las soluciones
			accept = dominance.ListDominance(stateCandidate, list);
		}
		return accept;
	}


	private int DominanceCounter(State stateCandidate, List<State> list) { //chequea el número de soluciones de Pareto que son dominados por la nueva solución
		int counter = 0;
		for (int i = 0; i < list.size(); i++) {
			State solution = list.get(i);
			Dominance dominance = new Dominance();
			if(dominance.dominance(stateCandidate, solution) == true)
				counter++;
		}
		return counter;
	}

	private int DominanceRank(State stateCandidate, List<State> list) { //calculando el número de soluciones en el conjunto de Pareto que dominan a la solución
		int rank = 0;
		for (int i = 0; i < list.size(); i++) {
			State solution = list.get(i);
			Dominance dominance = new Dominance();
			if(dominance.dominance(solution, stateCandidate) == true){
				rank++;
			}
		}
		
		return rank;
	}

}
