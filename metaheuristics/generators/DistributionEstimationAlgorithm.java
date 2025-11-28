package metaheuristics.generators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import metaheurictics.strategy.Strategy;

import problem.definition.State;
import problem.definition.Problem.ProblemType;

import evolutionary_algorithms.complement.DistributionType;
import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.Replace;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.Sampling;
import evolutionary_algorithms.complement.SamplingType;
import evolutionary_algorithms.complement.SelectionType;
import factory_interface.IFFSampling;
import factory_interface.IFFactoryFatherSelection;
import factory_interface.IFFactoryReplace;
import factory_method.FactoryFatherSelection;
import factory_method.FactoryReplace;
import factory_method.FactorySampling;

public class DistributionEstimationAlgorithm extends Generator {

	private State stateReferenceDA;
	private List<State> referenceList = new ArrayList<State>(); 
	public static List<State> sonList = new ArrayList<State>(); 
	private IFFactoryFatherSelection iffatherselection;
	private IFFSampling iffsampling;
	private IFFactoryReplace iffreplace;
	private DistributionType distributionType;
	private SamplingType Samplingtype;
	
//	private ReplaceType replaceType;
	public static ReplaceType replaceType;
	public static SelectionType selectionType;
	
	private GeneratorType generatorType;
	//private ProblemState candidate;
	public static int truncation;
	public static int countRef = 0;
	private float weight;
	
	//problemas dinamicos
	public static int countGender = 0;
	public static int countBetterGender = 0;
	private int[] listCountBetterGender = new int[10];
	private int[] listCountGender = new int[10];
	private float[] listTrace = new float[1200000];
	
	
	public DistributionEstimationAlgorithm() {
		super();
		this.referenceList = getListStateRef(); // llamada al método que devuelve la lista. 
//		this.selectionType = SelectionType.Truncation;
		//this.replaceType = ReplaceType.Generational;
//		this.replaceType = ReplaceType.Smallest;
		this.generatorType = GeneratorType.DistributionEstimationAlgorithm;
		this.distributionType = DistributionType.Univariate;
		this.Samplingtype = SamplingType.ProbabilisticSampling;
		this.weight = 50;
		listTrace[0] = weight;
		listCountBetterGender[0] = 0;
		listCountGender[0] = 0;
	}
	
	public State MaxValue (List<State> listInd){
		State state = new State(listInd.get(0));
		double max = state.getEvaluation().get(0);
		for (int i = 1; i < listInd.size(); i++) {
			if(listInd.get(i).getEvaluation().get(0) > max){
				max = listInd.get(i).getEvaluation().get(0);
				state = new State(listInd.get(i));
			}
		}
		return state;
	}
	
	@Override
	public State generate(Integer operatornumber) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {
		//********************selection*****************************
		//ProblemState candidate = new ProblemState();
		/*State candidate = new State();//(State) Strategy.getStrategy().getProblem().getState().clone()
		List<State> fathers = new ArrayList<State>();
		fathers = getfathersList();
		//**************************distribution***********************************
		iffdistribution = new FactoryDistribution();
    	Distribution distribution = iffdistribution.createDistribution(distributionType);
    	List<Probability> probability = distribution.distribution(fathers);
		
    	//***************************muestreo*****************************************
    	iffsampling = new FactorySampling();
    	Sampling samplingG = iffsampling.createSampling(Samplingtype);
    	List<State> ind = samplingG.sampling(probability, referenceList.size());
    	sonList = ind;
    	candidate = MaxValue(ind);
       // this.candidate = candidate;
    	return candidate;*/	
    	
    	//***************************version 1.0
    	//ArrayList<State> listcandidate = new ArrayList<State>();//(State) Strategy.getStrategy().getProblem().getState().clone()
		
    	List<State> fathers = new ArrayList<State>();
		fathers = getfathersList();
		iffsampling = new FactorySampling();
    	Sampling samplingG = iffsampling.createSampling(Samplingtype);
    	List<State> ind = samplingG.sampling(fathers, operatornumber);
    	State candidate = null;
    	if(ind.size() > 1){
    		for (int i = 0; i < ind.size(); i++) {
    			double evaluation = Strategy.getStrategy().getProblem().getFunction().get(0).Evaluation(ind.get(i));
    			ArrayList<Double> listEval = new ArrayList<Double>();
    			listEval.add(evaluation);
    			ind.get(0).setEvaluation(listEval);
    		}
    		candidate = MaxValue(ind);
    	}
    	else{
    		candidate = ind.get(0);
    	}
//    	sonList = ind;
    	//listcandidate.add(candidate);
       // this.candidate = candidate;
    	return candidate;
    	
    }
    		
	@Override
	public State getReference() {
		stateReferenceDA = referenceList.get(0);
		if(Strategy.getStrategy().getProblem().getTypeProblem().equals(ProblemType.Maximizar)){
			for (int i = 1; i < referenceList.size(); i++) {
				if(stateReferenceDA.getEvaluation().get(0) < referenceList.get(i).getEvaluation().get(0))
					stateReferenceDA = referenceList.get(i);
			}
		}
		else{
			for (int i = 1; i < referenceList.size(); i++) {
				if(stateReferenceDA.getEvaluation().get(0) > referenceList.get(i).getEvaluation().get(0))
					stateReferenceDA = referenceList.get(i);
			}
		}
		return stateReferenceDA;
	}

	@Override
	public List<State> getReferenceList() {
		List<State> ReferenceList = new ArrayList<State>();
		for (int i = 0; i < referenceList.size(); i++) {
			State value = referenceList.get(i);
			ReferenceList.add( value);
		}
		return ReferenceList;
	}

	@Override
	public GeneratorType getType() {
		return this.generatorType;
	}

	@Override
	public void setInitialReference(State stateInitialRef) {
		this.stateReferenceDA = stateInitialRef;
	}

	@Override
	public void updateReference(State stateCandidate, Integer countIterationsCurrent) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {
		iffreplace = new FactoryReplace();
		Replace replace = iffreplace.createReplace(replaceType);
		referenceList = replace.replace(stateCandidate, referenceList);
	}
	
	public List<State> getListStateRef(){
		Boolean found = false;
		List<String> key = Strategy.getStrategy().getListKey();
		int count = 0;
		//if(Strategy.getStrategy().Statistics.getAllStates().size() == 0){
		/*if(RandomSearch.listStateReference.size() == 0){
			return this.referenceList = new ArrayList<State>();
		}*/
		while((found.equals(false)) && (Strategy.getStrategy().mapGenerators.size() > count)){
			if(key.get(count).equals(GeneratorType.DistributionEstimationAlgorithm.toString())){
				GeneratorType keyGenerator = GeneratorType.valueOf(String.valueOf(key.get(count)));
				DistributionEstimationAlgorithm generator = (DistributionEstimationAlgorithm)Strategy.getStrategy().mapGenerators.get(keyGenerator);
				if(generator.getListReference().isEmpty()){
					referenceList.addAll(RandomSearch.listStateReference);
					//for (int j = 1; j < Strategy.getStrategy().Statistics.getAllStates().size(); j++) {
//					for (int j = 1; j < RandomSearch.listStateReference.size(); j++) {
//						//if((Strategy.getStrategy().Statistics.getAllStates().get(j).getTypeGenerator().equals(GeneratorType.RandomSearch)) && (referenceList.size()!= countRef)){
//						if(referenceList.size() != countRef){
//							//State problemState = Strategy.getStrategy().Statistics.getAllStates().get(j);
//							referenceList.add(RandomSearch.listStateReference.get(j));
//						}
//					}  
				}
				else{
					referenceList = generator.getListReference();
				}
			    found = true;
			}
			count++;
		}
		return referenceList;
	}

	public List<State> getListReference() {
		return referenceList;
	}

	public void setListReference(List<State> listReference) {
		referenceList = listReference;
	}

	public GeneratorType getGeneratorType() {
		return generatorType;
	}

	public void setGeneratorType(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

	public List<State> getfathersList() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<State> refList = new ArrayList<State>(this.referenceList); 
    	iffatherselection = new FactoryFatherSelection();
    	FatherSelection selection = iffatherselection.createSelectFather(selectionType);
    	List<State> fathers = selection.selection(refList, truncation);
    	return fathers;
	}

	@Override
	public List<State> getSonList() {
		return sonList;
	}

	public boolean awardUpdateREF(State stateCandidate) {
		boolean find = false;
		int i = 0;
		while (find == false && i < this.referenceList.size()) {
			if(stateCandidate.equals(this.referenceList.get(i)))
				find = true;
			else i++;
		}
		return find;
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

	public DistributionType getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(DistributionType distributionType) {
		this.distributionType = distributionType;
	}

	@Override
	public int[] getListCountBetterGender() {
		// TODO Auto-generated method stub
		return this.listCountBetterGender;
	}

	@Override
	public int[] getListCountGender() {
		// TODO Auto-generated method stub
		return this.listCountGender;
	}

	@Override
	public float[] getTrace() {
		// TODO Auto-generated method stub
		return this.listTrace;
	}


}