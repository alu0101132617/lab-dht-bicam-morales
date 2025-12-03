package es.ull.esit.app.evolutionary_algorithms.complement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ull.esit.app.metaheurictics.strategy.Strategy;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements the one-point crossover operator.
 */
public class OnePointCrossover extends Crossover {

  /**
   * Applies the one-point crossover operation between two parent states with a given crossover probability.
   * @param father1 [State] the first parent state.
   * @param father2 [State] the second parent state.
   * @param pc [double] the crossover probability.
   * @return [State] the resulting offspring state after crossover.
   */
	@Override
	public State crossover(State father1, State father2, double pc) {
				
		State newInd = (State) father1.getCopy();
		
	    List<Object> ind1 = new ArrayList<>();
	    List<Object> ind2 = new ArrayList<>();
	    
		double number = random.nextDouble();	
		if(number <= pc){

      int limit = Strategy.getStrategy()
                    .getProblem()
                    .getCodification()
                    .getVariableCount();

      int pos = random.nextInt(limit - 1);
      
      for (int i = 0; i < father1.getCode().size(); i++) {
				if(i <= pos){
					ind1.add(father1.getCode().get(i));
					ind2.add(father2.getCode().get(i));
				}
				else{
					ind1.add(father2.getCode().get(i));
					ind2.add(father1.getCode().get(i));
				}
			}
	
		
			int choice = random.nextInt(2);
			if(choice == 0)
				newInd.setCode((ArrayList<Object>) ind1);
			else newInd.setCode((ArrayList<Object>) ind2); 
		}
		return newInd;			
	}

  /** Random number generator used for crossover operations. */
  private Random random = new Random();
	
}
