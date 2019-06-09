package nz.aucklanduni.archchecker.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import nz.aucklanduni.archchecker.core.smell.EAChecker;
import nz.aucklanduni.archchecker.model.OntologyReasoningResult;

public abstract class AbstractSmellChecker {
	
	protected OWLReasonerFactory reasonerFactory;
	protected OWLDataFactory dataFactory;
	protected String iri;
	
	protected static Logger  logger = LogManager.getLogger(AbstractSmellChecker.class.getName());
	
	public AbstractSmellChecker(OWLReasonerFactory rsfac, OWLDataFactory datafac, String iri) {
		this.reasonerFactory = rsfac;
		this.dataFactory = datafac;
		this.iri = iri;
		
	}
	public abstract OntologyReasoningResult check(String modName, OWLOntology ontology);
	
	protected List<OWLNamedIndividual> queryIndividualsFromClass(OWLReasoner reasoner, OWLOntology ontology, String className){
		
		OWLClass classObj = dataFactory.getOWLClass(IRI.create(iri + "#"+className));
		NodeSet<OWLNamedIndividual> set = reasoner.getInstances(classObj, false);
		
		List<OWLNamedIndividual> result = new ArrayList<OWLNamedIndividual>();
		for(Node<OWLNamedIndividual> node: set) {
			result.add(node.getRepresentativeElement());
		}
		
		return result;
	}
}
