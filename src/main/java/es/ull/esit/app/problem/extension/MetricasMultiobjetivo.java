package es.ull.esit.app.problem.extension;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jxl.read.biff.BiffException;
import es.ull.esit.app.problem.definition.State;

/**
 * Class that implements multi-objective metrics.
 */
public class MetricasMultiobjetivo {


  /**
   * Calculates the error rate between current and true Pareto front solutions.
   * 
   * @param solutionsFPcurrent [List<State>] Current Pareto front solutions.
   * @param solutionsFPtrue [List<State>] True Pareto front solutions.
   * @return [double] Error rate.
   * @throws BiffException If an error occurs while reading the Excel file.
   * @throws IOException If an I/O error occurs.
   */
	public double calcularTasaError(List<State> solutionsFPcurrent, List<State> solutionsFPtrue) throws BiffException, IOException{
		float tasaError = 0;
		for (int i = 0; i < solutionsFPcurrent.size() ; i++) { 
			State solutionVO = solutionsFPcurrent.get(i);
			if(!contains(solutionVO, solutionsFPtrue)){ 
				tasaError++;
			}
		}
    return tasaError/solutionsFPcurrent.size();

	}
	
  /**
   * Calculates the generational distance between current and true Pareto front solutions.
   * @param solutionsFPcurrent [List<State>] Current Pareto front solutions.
   * @param solutionsFPtrue [List<State>] True Pareto front solutions.
   * @return [double] Generational distance.
   * @throws BiffException If an error occurs while reading the Excel file.
   * @throws IOException If an I/O error occurs.
   */
	public double calcularDistanciaGeneracional(List<State> solutionsFPcurrent, List<State> solutionsFPtrue) throws BiffException, IOException{
		float min;
		float distancia = 0;
		float distanciaGeneracional = 0;
		for (int i = 0; i < solutionsFPcurrent.size();i++) {
			State solutionVO = solutionsFPcurrent.get(i);
			//Calculando la distancia euclideana entre solutionVO y el miembro m�s cercano del frente de Pareto verdadero
			min = 1000;
			for (int j = 0; j < solutionsFPtrue.size();j++) { 
				for (int j2 = 0; j2 < solutionVO.getEvaluation().size(); j2++) {
					State solutionFPV = solutionsFPtrue.get(j);
					// porq elevar la distancia al cuadrado
					distancia += (solutionVO.getEvaluation().get(j2) - solutionFPV.getEvaluation().get(j2))*  
							(solutionVO.getEvaluation().get(j2) - solutionFPV.getEvaluation().get(j2)); //ceros si el argumento es el cero, 1.0 si el argumento es mayor que el cero, -1.0 si el argumento est� menos del cero
				}
				if(distancia < min){
					min = distancia;
				}
			}
			distanciaGeneracional += min;
		}
		return  Math.sqrt(distanciaGeneracional)/solutionsFPcurrent.size();
	}
  
  /**
   * Calculates the dispersion of solutions in the Pareto front.
   * 
   * @param solutions [List<State>] List of solutions.
   * @return [double] Dispersion value.
   * @throws BiffException If an error occurs while reading the Excel file.
   * @throws IOException If an I/O error occurs.
   */
	public double calcularDispersion(List<State> solutions) throws BiffException, IOException{
		LinkedList<Float> distancias = new LinkedList<>();
		float distancia = 0;
		float min;
		for (Iterator<State> iter = solutions.iterator(); iter.hasNext();) {
			State solutionVO = iter.next();
			min = 1000;
			for (Iterator<State> iterator = solutions.iterator(); iterator.hasNext();) {
				State solVO = iterator.next();
				for (int i = 0; i < solutionVO.getEvaluation().size(); i++) {
					if(!solutionVO.getEvaluation().equals(solVO.getEvaluation())){
						distancia += (solutionVO.getEvaluation().get(i)- solVO.getEvaluation().get(i));
					}}
				if(distancia < min){
					min = distancia;
				}
			}
			distancias.add(Float.valueOf(min));
		}
		float sum = 0;
		for (Iterator<Float> iter = distancias.iterator(); iter.hasNext();) {
			Float dist = iter.next();
			sum += dist;
		}
		float media = sum/distancias.size();
		float sumDistancias = 0;
		for (Iterator<Float> iter = distancias.iterator(); iter.hasNext();) {
			Float dist = iter.next();
			sumDistancias += Math.pow((media - dist),2);
		}
		double dispersion = 0;
		if(solutions.size() > 1){
			dispersion = Math.sqrt((1.0/(solutions.size()-1))*sumDistancias);
		}
		return dispersion;
	}

  /**
   * Checks if a solution is contained in a list of solutions.
   * 
   * @param solA [State] The solution to check.
   * @param solutions [List<State>] The list of solutions.
   * @return [boolean] True if the solution is contained in the list, false otherwise.
   */
	private boolean contains(State solA, List<State> solutions){
		int i = 0;
		boolean result = false;
		while(i<solutions.size()&& !result){
			if(solutions.get(i).getEvaluation().equals(solA.getEvaluation()))
				result=true;
			else
				i++;
		}
		return result;
	}

  /**
   * Calculates the minimum value from a list of metrics.
   * 
   * @param allMetrics [List<Double>] List of metric values.
   * @return [double] Minimum value.
   */
	public double calcularMin(List<Double> allMetrics){
		double min = 1000;
		for (Iterator<Double> iter = allMetrics.iterator(); iter.hasNext();) {
			double element = iter.next();
			if(element < min){
				min = element;
			}
		}
		return min;
	}

  /**
   * Calculates the maximum value from a list of metrics.
   * 
   * @param allMetrics [List<Double>] List of metric values.
   * @return [double] Maximum value.
   */
	public double calcularMax(List<Double> allMetrics){
		double max = 0;
		for (Iterator<Double> iter = allMetrics.iterator(); iter.hasNext();) {
			double element = iter.next();
			if(element > max){
				max = element;
			}
		}
		return max;
	}
  /**
   * Calculates the average value from a list of metrics.
   * 
   * @param allMetrics [List<Double>] List of metric values.
   * @return [double] Average value.
   */
	public double calcularMedia(List<Double> allMetrics){
		double sum = 0;
		for (Iterator<Double> iter = allMetrics.iterator(); iter.hasNext();) {
			double element = iter.next();
			sum = sum + element;
		}
		return sum/allMetrics.size();
	}
}
