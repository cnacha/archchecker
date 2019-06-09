package nz.aucklanduni.archchecker.model;

import java.util.List;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class OntologyReasoningResult {

	private List<OWLNamedIndividual>  results;
	private long timeElapsed;
	
	
	public OntologyReasoningResult(List<OWLNamedIndividual> results, long timeElapsed) {
		super();
		this.results = results;
		this.timeElapsed = timeElapsed;
	}
	public List<OWLNamedIndividual> getResults() {
		return results;
	}
	public void setResults(List<OWLNamedIndividual> results) {
		this.results = results;
	}
	public long getTimeElapsed() {
		return timeElapsed;
	}
	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	
}
