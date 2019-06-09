package nz.aucklanduni.archchecker.core.smell;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import nz.aucklanduni.archchecker.core.AbstractSmellChecker;
import nz.aucklanduni.archchecker.model.OntologyReasoningResult;

public class LOChecker extends AbstractSmellChecker{
	


	public LOChecker(OWLReasonerFactory rsfac, OWLDataFactory datafac, String iri) {
		super(rsfac, datafac, iri);
	}

	@Override
	public OntologyReasoningResult check(String modName, OWLOntology ontology) {
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	//	logger.info("start checking Link Overload ");
		Instant start = Instant.now();
		
		List<OWLNamedIndividual> indvResultList =  queryIndividualsFromClass(reasoner, ontology, "LinkOverload");
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		//logger.info("end checking Link Overload: "+result.size()+", Total Time(ms) "+timeElapsed);
		OntologyReasoningResult result = new OntologyReasoningResult(indvResultList, timeElapsed);
		return result;
	}
	
	

}
