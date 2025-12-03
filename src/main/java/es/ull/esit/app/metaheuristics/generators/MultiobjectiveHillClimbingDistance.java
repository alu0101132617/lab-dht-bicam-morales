package es.ull.esit.app.metaheuristics.generators;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import es.ull.esit.app.factory_interface.IFFactoryAcceptCandidate;
import es.ull.esit.app.factory_method.FactoryAcceptCandidate;

import es.ull.esit.app.problem.definition.State;
import es.ull.esit.app.local_search.acceptation_type.AcceptType;
import es.ull.esit.app.local_search.acceptation_type.AcceptableCandidate;
import es.ull.esit.app.local_search.candidate_type.CandidateType;
import es.ull.esit.app.local_search.candidate_type.CandidateValue;
import es.ull.esit.app.local_search.complement.StrategyType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;

public class MultiobjectiveHillClimbingDistance extends Generator{

	protected CandidateValue candidatevalue;
	protected AcceptType typeAcceptation;
	protected StrategyType strategy;
	protected CandidateType typeCandidate;
	protected State stateReferenceHC;
	protected IFFactoryAcceptCandidate ifacceptCandidate;
	protected GeneratorType generatorType;
	protected List<State> listStateReference = new ArrayList<State>(); 
	protected float weight;
	protected List<Float> listTrace = new ArrayList<Float>();
	private List<State> visitedState = new ArrayList<State>();
	public static int sizeNeighbors;
	//Lista que contiene las distancias de cada soluci�n del frente de Pareto estimado
	public static List<Double> distanceSolution = new ArrayList<Double>();


	public MultiobjectiveHillClimbingDistance() {
		super();
		this.typeAcceptation = AcceptType.AcceptNotDominated;
		this.strategy = StrategyType.NORMAL;
		this.typeCandidate = CandidateType.NotDominatedCandidate;
		this.candidatevalue = new CandidateValue();
		this.generatorType = GeneratorType.MultiobjectiveHillClimbingDistance;
		this.weight = 50;
		listTrace.add(weight);
	}

	@Override
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<State> neighborhood = new ArrayList<State>();
		neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceHC, operatornumber);
		State statecandidate = candidatevalue.stateCandidate(stateReferenceHC, typeCandidate, strategy, operatornumber, neighborhood);
		return statecandidate;
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, 
	InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		//Agregando la primera soluci�n a la lista de soluciones no dominadas
		if(Strategy.getStrategy().getListRefPoblacFinal().size() == 0){
			Strategy.getStrategy().getListRefPoblacFinal().add(stateReferenceHC.copy());
			distanceSolution.add(new Double(0));
		}
		ifacceptCandidate = new FactoryAcceptCandidate();
		AcceptableCandidate candidate = ifacceptCandidate.createAcceptCandidate(typeAcceptation);
		State lastState = Strategy.getStrategy().getListRefPoblacFinal().get(Strategy.getStrategy().getListRefPoblacFinal().size()-1);
		List<State> neighborhood = new ArrayList<State>();
		neighborhood = Strategy.getStrategy().getProblem().getOperator().generatedNewState(stateReferenceHC, sizeNeighbors);
		int i= 0;
//		Boolean restart= true;

//		while (restart==true) {
			Boolean accept = candidate.acceptCandidate(lastState, stateCandidate.copy());
			if(accept.equals(true)){
				stateReferenceHC = stateCandidate.copy();
				visitedState = new ArrayList<State>();
				lastState=stateReferenceHC.copy();
//				restart=false;
			}

			else{

				boolean stop = false;
				while (i < neighborhood.size()&& stop==false) {
					if (Contain(neighborhood.get(i))==false) {
						stateReferenceHC = SolutionMoreDistance(Strategy.getStrategy().getListRefPoblacFinal(), distanceSolution);
						visitedState.add(stateReferenceHC);
						stop=true;
						lastState=stateReferenceHC.copy();
//						restart=false;
					}
					i++;
				}
				int coutrestart=0;
				while (stop == false && coutrestart < sizeNeighbors && accept==false) {
					stateCandidate = Strategy.getStrategy().getProblem().getOperator().generateRandomState(1).get(0);
					if (Contain(stateCandidate)==false) {
						Strategy.getStrategy().getProblem().evaluate(stateCandidate);  
						visitedState.add(stateCandidate);
						stop=true;
						coutrestart++;
						accept = candidate.acceptCandidate(lastState, stateCandidate.copy());
					}
				}
				if(accept.equals(true)){
					stateReferenceHC = stateCandidate.copy();
					visitedState = new ArrayList<State>();
					lastState = stateReferenceHC.copy();
					//tomar xc q pertenesca a la vecindad de xa
				}
			}

		getReferenceList();
	}

	private State SolutionMoreDistance(List<State> state, List<Double> distanceSolution) {
		Double max = (double) -1;
		int pos = -1;
		Double[] distance = distanceSolution.toArray(new Double[distanceSolution.size()]);
		State[] solutions = state.toArray(new State[state.size()]);
		for (int i = 0; i < distance.length; i++) {
			Double dist = distance[i];
			if(dist > max){
				max = dist;
				pos = i;
			}
		}
		if(pos != -1)
			return solutions[pos];
		else
			return null;
	}

	@Override
	public List<State> getReferenceList() {
		listStateReference.add(stateReferenceHC.copy());
		return listStateReference;
	}

	@Override
	public State getReference() {
		return stateReferenceHC;
	}

	public void setStateRef(State stateRef) {
		this.stateReferenceHC = stateRef;
	}

	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceHC = stateInitialRef;
	}

	public GeneratorType getGeneratorType() {
		return generatorType;
	}

	public void setGeneratorType(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

	@Override
	public GeneratorType getType() {
		return this.generatorType;
	}

	@Override
	public List<State> getSonList() {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Double> distanceCalculateAdd(List<State> solution) {
		State[] solutions = solution.toArray(new State[solution.size()]);
		Double distance = 0.0;
		List<Double>listDist=new ArrayList<Double>();
		State lastSolution = solution.get(solution.size()-1);
		//Actualizando las distancias de todos los elmentos excepto el nuevo insertando
		for (int k = 0; k < solutions.length-1; k++) {
			State solA = solutions[k];
			distance = solA.distance(lastSolution);
			listDist.add(distanceSolution.get(k)+distance);
//			distanceSolution.set(k, distanceSolution.get(k) + distance);
		}
		distance = 0.0;
		//Calculando la distancia del �ltimo elemento (elemento insertado) respecto al resto de los elementos
		if (solutions.length==1) {
			return distanceSolution;
		
		}else {
		
			for (int l = 0; l < solutions.length-1; l++) {
				State solB = solutions[l];
				distance += lastSolution.distance(solB);
			}
			listDist.add(distance);
//			distanceSolution.add(distance);
			distanceSolution=listDist;
			
			return distanceSolution;
		}

	}


	private boolean Contain(State state){
		boolean found = false;
		for (Iterator<State> iter = visitedState.iterator(); iter.hasNext();) {
			State element = (State) iter.next();
			if(element.comparator(state)){
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean awardUpdateREF(State stateCandidate) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public float getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWeight(float weight) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getListCountBetterGender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getListCountGender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getTrace() {
		// TODO Auto-generated method stub
		return null;
	}
}
