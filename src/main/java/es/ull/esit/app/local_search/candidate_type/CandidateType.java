package es.ull.esit.app.local_search.candidate_type;

/**
 * Enum that represents the different types of candidates that can be used in local search algorithms.
 */
public enum CandidateType{
	
	SmallerCandidate, /** Candidate with smaller objective function value */
  GreaterCandidate, /** Candidate with greater objective function value */
  RandomCandidate,  /** Candidate selected randomly */
  NotDominatedCandidate; /** Candidate that is not dominated */
}
