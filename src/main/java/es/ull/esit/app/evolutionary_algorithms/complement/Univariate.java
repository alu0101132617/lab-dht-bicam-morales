package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.HashMap;
import java.util.Map;

import es.ull.esit.app.problem.definition.State;

/**
 * Class that represents the Univariate Distribution method.
 */
public class Univariate extends Distribution {

  /**
   * Calculates the univariate distribution of variable values from a list of father states.
   * @param fathers [List<State>] List of father states.
   * @return [List<Probability>] List of Probability objects representing the distribution.
   */
	@Override
	public List<Probability> distribution(List<State> fathers) {

		List<Probability> listProbability = new ArrayList<>();
		if (fathers == null || fathers.isEmpty()) {
			return listProbability;
		}

		int cantV = fathers.get(0).getCode().size();
		for (int i = 0; i < cantV; i++) {
			Map<Integer, Integer> freq = new HashMap<>();
			for (int j = 0; j < fathers.size(); j++) {
				Integer value = (Integer) fathers.get(j).getCode().get(i);
				if (value == -1) {
					continue;
				}
				freq.put(value, freq.getOrDefault(value, 0) + 1);
			}

			for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
				Probability probability = new Probability();
				float prob = ((float) entry.getValue()) / fathers.size();
				probability.setKey(i);
				probability.setValue(entry.getKey());
				probability.setProbability(prob);
				listProbability.add(probability);
			}
		}
		return listProbability;
	}

  /**
   * Generates a list of keys from a sorted map.
   * @param map [SortedMap<String, Object>] Input sorted map.
   * @return [List<String>] List of keys extracted from the map.
   */
	public List<String> getListKey(SortedMap<String, Object> map){
		List<String> listKey = new ArrayList<>();
		String key = map.keySet().toString();
		String returnString = key.substring(1, key.length() - 1);
		returnString = returnString + ", ";
		int countKey = map.size();
		for (int j = 0; j < countKey; j++) {
			String r = returnString.substring(0, returnString.indexOf(','));
			returnString = returnString.substring(returnString.indexOf(',')+2);
			listKey.add(r);
	   }
	   return listKey;
	}
}
