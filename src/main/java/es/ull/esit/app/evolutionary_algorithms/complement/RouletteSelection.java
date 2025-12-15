package es.ull.esit.app.evolutionary_algorithms.complement;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import es.ull.esit.app.metaheuristics.generators.LimitRoulette;
import es.ull.esit.app.problem.definition.State;

/**
 * Class implementing the roulette wheel selection method for selecting parents in an evolutionary algorithm.
 */
public class RouletteSelection extends FatherSelection {

  /** Random number generator used for selection operations. */
  private SecureRandom random = new SecureRandom();

  /**
   * Selects parents from the given list of states using the roulette wheel selection method.
   * @param listState [List<State>] The list of candidate states (individuals) to select from.
   * @param truncation [int] The number of individuals to select (not used in this implementation).
   * @return [List<State>] The list of selected parent states.
   */
	@Override
	public List<State> selection(List<State> listState, int truncation) {
    
		float totalWeight = 0;
		for (int i = 0; i < listState.size(); i++) {
			totalWeight = (float) (listState.get(i).getEvaluation().get(0) + totalWeight);
		}
		List<Float> listProb = new ArrayList<>();
		for (int i = 0; i < listState.size(); i++) {
			float probF = (float) (listState.get(i).getEvaluation().get(0) / totalWeight);
			listProb.add(probF);
		}
		List<LimitRoulette> listLimit = new ArrayList<>();
		float limitHigh = 0;
		float limitLow = 0;
		for (int i = 0; i < listProb.size(); i++) {
			LimitRoulette limitRoulette = new LimitRoulette();
			limitHigh = listProb.get(i) + limitHigh;
			limitRoulette.setLimitHigh(limitHigh);
			limitRoulette.setLimitLow(limitLow);
			limitLow = limitHigh;
			listLimit.add(limitRoulette);
		}
		List<State> fatherList = new ArrayList<>();
		for (int j = 0; j < listState.size(); j++) {
			float numbAleatory = (float) (random.nextDouble() * 1.0);
			boolean find = false;
			int i = 0;
			while ((!find) && (i < listLimit.size())){
				if((listLimit.get(i).getLimitLow() <= numbAleatory) && (numbAleatory <= listLimit.get(i).getLimitHigh())){
					find = true;
					fatherList.add(listState.get(i));
				}
				else i++;
			}
		}
		return fatherList;
	}
}
