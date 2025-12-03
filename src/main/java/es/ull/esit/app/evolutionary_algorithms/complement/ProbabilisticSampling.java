package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ull.esit.app.metaheuristics.generators.GeneratorType;
import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the probabilistic sampling operator.
 */
public class ProbabilisticSampling extends Sampling {

  /** Random number generator used for sampling operations. */
  private Random r = new Random();

  /**
   * Applies the probabilistic sampling operation to generate new states based on
   * a list of parent states.
   * 
   * @param fathers  [List<State>] the list of parent states.
   * @param countInd [int] the number of new states to generate.
   * @return [List<State>] the list of newly generated states.
   * 
   */
  @Override
  public List<State> sampling(List<State> fathers, int countInd) {
    int cantV = fathers.get(0).getCode().size();
    List<State> staList = listState(countInd);
    int[] possibleValues = buildPossibleValues();

    for (int i = 0; i < cantV; i++) {
      int[] valuesPerVariable = collectValuesForVariable(fathers, i);
      int[] occurrences = countOccurrences(valuesPerVariable, possibleValues);
      int sum = sumOccurrences(occurrences);

      sampleForVariable(staList, occurrences, sum, possibleValues, countInd);
    }

    return staList;
  }

  /**
   * Builds an array of possible values for the problem's variables.
   * @return [int[]] an array containing all possible values.
   */
  private int[] buildPossibleValues() {
    int size = Strategy.getStrategy().getProblem().getPossibleValue();
    int[] arr = new int[size];
    for (int j = 0; j < size; j++) {
      arr[j] = j;
    }
    return arr;
  }

  /**
   * Collects the values of a specific variable (by index) from each parent state.
   * @param fathers [List<State>] the list of parent states.
   * @param variableIndex [int] the index of the variable to collect values for.
   * @return [int[]] an array containing the values of the specified variable from each parent.
   */
  private int[] collectValuesForVariable(List<State> fathers, int variableIndex) {
    int[] values = new int[fathers.size()];
    for (int j = 0; j < fathers.size(); j++) {
      Object v = fathers.get(j).getCode().get(variableIndex);
      values[j] = (v.getClass() == Integer.class) ? (Integer) v : -1;
    }
    return values;
  }

  // Cuenta ocurrencias de cada valor posible en la lista de valores de la variable.
  private int[] countOccurrences(int[] values, int[] possibleValues) {
    int[] occurrences = new int[possibleValues.length];
    // Usamos una copia para marcar elementos consumidos sin modificar el original.
    int[] temp = new int[values.length];
    System.arraycopy(values, 0, temp, 0, values.length);

    for (int k = 0; k < possibleValues.length; k++) {
      int target = possibleValues[k];
      int count = 0;
      for (int j = 0; j < temp.length; j++) {
        if (temp[j] != -1 && temp[j] == target) {
          count++;
          temp[j] = -1;
        }
      }
      occurrences[k] = count;
    }
    return occurrences;
  }

  /**
   * Sums the occurrences of all possible values.
   * @param occurrences [int[]] array of occurrences for each possible value.
   * @return [int] the sum of all occurrences.
   */
  private int sumOccurrences(int[] occurrences) {
    int sum = 0;
    for (int occ : occurrences) {
      sum += occ;
    }
    return sum;
  }

  /**
   * Samples values for a specific variable and assigns them to the new states.
   * @param staList [List<State>] the list of new states to assign values to.
   * @param occurrences [int[]] array of occurrences for each possible value.
   * @param sum [int] the sum of all occurrences.
   * @param possibleValues [int[]] array of all possible values for the variable.
   * @param countInd [int] the number of new states to generate.
   */
  private void sampleForVariable(List<State> staList, int[] occurrences, int sum, int[] possibleValues, int countInd) {
    for (int l = 0; l < countInd; l++) {
      boolean find = false;
      int p = 0;
      int random = r.nextInt(Math.max(sum, 1)) + 1; // evitar nextInt(0)

      while (p < occurrences.length && !find) {
        random -= occurrences[p];
        if (random <= 0) {
          staList.get(l).getCode().add(possibleValues[p]);
          find = true;
        } else {
          p++;
        }
      }

      if (!find) {
        // value es un valor aleatorio entre 0 y el numero de valores posibles de la variable
        int value = r.nextInt(Strategy.getStrategy().getProblem().getCodification().getVariableCount() * 10);
        staList.get(l).getCode().add(value);
      }
    }
  }

  /**
   * Generates a list of new State objects.
   * @param countInd [int] the number of State objects to generate.
   * @return [List<State>] the list of generated State objects.
   */
  public List<State> listState(int countInd) {
    List<State> staList = new ArrayList<>(countInd);
    for (int i = 0; i < countInd; i++) {
      State state = new State();
      state.setCode(new ArrayList<>());
      state.setNumber(Strategy.getStrategy().getCountCurrent());
      state.setTypeGenerator(GeneratorType.DistributionEstimationAlgorithm);
      staList.add(state);
    }
    return staList;
  }
}
