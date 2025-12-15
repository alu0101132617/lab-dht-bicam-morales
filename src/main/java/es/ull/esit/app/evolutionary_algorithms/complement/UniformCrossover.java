package es.ull.esit.app.evolutionary_algorithms.complement;

import es.ull.esit.app.problem.definition.State;

import java.security.SecureRandom;

/**
 * Class that represents the Uniform Crossover method.
 */
public class UniformCrossover extends Crossover {

  /** Random number generator for crossover decisions. */
  private SecureRandom r = new SecureRandom();
  
	
	/**
   * Generates a random binary mask of given length.
   * @param length [int] Length of the mask.
   * @return [int[]] Binary mask array.
   */
	public int[] mascara(int length){
		int[] mascara = new int[length];
		for (int i = 0; i < mascara.length; i++) {
			int value = r.nextInt(2);
			mascara[0] = value;
		}
		return mascara;
	}	
  
  /**
   * Performs uniform crossover between two father states.
   * @param father1 [State] First father state.
   * @param father2 [State] Second father state.
   * @param pc [double] Crossover probability.
   * @return [State] New state resulting from the crossover.
   */
	@Override
	public State crossover(State father1, State father2, double pc) {
		Object value;
		State state = (State) father1.getCopy();
		int[] mascara = mascara(father1.getCode().size());
   		for (int k = 0; k < mascara.length; k++) {
   			if(mascara[k] == 1){
   				value = father1.getCode().get(k);
   				state.getCode().set(k, value);
   			}
   			else{
   				if(mascara[k] == 0){
   					value = father2.getCode().get(k);  
   					state.getCode().set(k, value);
   				}
   			}
		}
		return state;
	}
}
