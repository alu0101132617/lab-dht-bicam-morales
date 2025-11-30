package main.java.es.ull.esit.app.local_search.acceptation_type;

import java.util.List;

import main.java.es.ull.esit.app.metaheuristics.generators.GeneratorType;
import main.java.es.ull.esit.app.metaheuristics.generators.MultiobjectiveHillClimbingDistance;
import main.java.es.ull.esit.app.metaheurictics.strategy.Strategy;
import main.java.es.ull.esit.app.problem.definition.Problem.ProblemType;
import main.java.es.ull.esit.app.problem.definition.State;

public class Dominance {

	//---------------------------------M�todos que se utilizan en los algoritmos multiobjetivo-------------------------------------------------------//
	//Funci�n que determina si la soluci�n X domina a alguna de las soluciones no dominadas de una lista
	//Devuelve la lista actualizada y true si fue adicionada a la lista o false de lo contrario
	public boolean ListDominance(State solutionX, List<State> list){
		boolean domain = false;
		for (int i = 0; i < list.size() && domain == false; i++) {
			//Si la soluci�n X domina a la soluci�n de la lista
			if(dominance(solutionX, list.get(i)) == true){
				//Se elimina el elemento de la lista
				list.remove(i);
				if (i!=0) {
					i--;	
				}
				if (Strategy.getStrategy().generator.getType().equals(GeneratorType.MultiobjectiveHillClimbingDistance)&&list.size()!=0) {
					MultiobjectiveHillClimbingDistance.DistanceCalculateAdd(list);
				}
			}
			if (list.size()>0) {
				if(dominance(list.get(i), solutionX) == true){
					domain = true;
				}
			}

		}
		//Si la soluci�n X no fue dominada
		if(domain == false){
			//Comprobando que la soluci�n no exista
			boolean found = false;
			for (int k = 0; k < list.size() && found == false; k++) {
				State element = list.get(k);
				found = solutionX.Comparator(element);
			}
			//Si la soluci�n no existe
			if(found == false){
				//Se guarda la soluci�n candidata en la lista de soluciones �ptimas de Pareto
				list.add(solutionX.clone());
				if (Strategy.getStrategy().generator.getType().equals(GeneratorType.MultiobjectiveHillClimbingDistance)) {
					MultiobjectiveHillClimbingDistance.DistanceCalculateAdd(list);
				}
				return true;
			}
		}
		return false;

		/*boolean domain = false;
		List<State> deletedSolution = new ArrayList<State>();
		for (int i = 0; i < list.size() && domain == false; i++) {
			State element = list.get(i);
			//Si la soluci�n X domina a la soluci�n de la lista
			if(dominance(solutionX, element) == true){
				//Se elimina el elemento de la lista
				deletedSolution.add(element);
			}
			if(dominance(element, solutionX) == true){
				domain = true;
			}
		}
		//Si la soluci�n X no fue dominada
		if(domain == false){
			//Comprobando que la soluci�n no exista
			boolean found = false;
			for (int k = 0; k < list.size() && found == false; k++) {
				State element = list.get(k);
				found = solutionX.Comparator(element);
			}
			//Si la soluci�n no existe
			if(found == false){

				//Se eliminan de la lista de soluciones optimas de pareto aquellas que fueron dominadas por la soluci�n candidata
				list.removeAll(deletedSolution);
				//Se guarda la soluci�n candidata en la lista de soluciones �ptimas de Pareto
				list.add(solutionX.clone());
				if(Strategy.getStrategy().getProblem()!= null){
					Strategy.getStrategy().listRefPoblacFinal = list;
				}
				return true;
			}
		}

		return false;*/
	}


	//Funci�n que devuelve true si solutionX domina a solutionY
	public boolean dominance(State solutionX,  State solutionY)	{
		boolean dominance = false;
		int countBest = 0;
		int countEquals = 0;
		//Si solutionX domina a solutionY
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)) {
			//Recorriendo las evaluaciones de las funciones objetivo
			for (int i = 0; i < solutionX.getEvaluation().size(); i++) {
				if(solutionX.getEvaluation().get(i).floatValue() > solutionY.getEvaluation().get(i).floatValue()){
					countBest++;
				}
				if(solutionX.getEvaluation().get(i).floatValue() == solutionY.getEvaluation().get(i).floatValue()){
					countEquals++;
				}	
			}
		}
		else{
			//Recorriendo las evaluaciones de las funciones objetivo
			for (int i = 0; i < solutionX.getEvaluation().size(); i++) {
				if(solutionX.getEvaluation().get(i).floatValue() < solutionY.getEvaluation().get(i).floatValue()){
					countBest++;
				}
				if(solutionX.getEvaluation().get(i).floatValue() == solutionY.getEvaluation().get(i).floatValue()){
					countEquals++;
				}	
			}
		}
		if((countBest >= 1) && (countEquals + countBest == solutionX.getEvaluation().size())) {
			dominance = true;
		}
		return dominance;
	} 
}
