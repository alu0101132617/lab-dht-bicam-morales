package es.ull.esit.app.local_search.acceptation_type;

/**
 * Enum that defines the different types of acceptation strategies.
 */
public enum AcceptType
{
	AcceptBest,             /** Accept the best candidate solution */
  AcceptAnyone,           /** Accept any candidate solution */
  AcceptNotBadT,          /** Accept not bad candidate solution based on temperature */
  AcceptNotBadU,          /** Accept not bad candidate solution based on utility */
  AcceptNotDominated,     /** Accept not dominated candidate solution */
  AcceptNotDominatedTabu, /** Accept not dominated candidate solution with tabu */
  AcceptNotBad,           /** Accept not bad candidate solution */
  AcceptMulticase;        /** Accept multiple case candidate solution */
	
}
