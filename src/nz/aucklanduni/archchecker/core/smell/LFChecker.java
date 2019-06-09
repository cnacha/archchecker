package nz.aucklanduni.archchecker.core.smell;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import nz.aucklanduni.archchecker.core.AbstractSmellChecker;
import nz.aucklanduni.archchecker.model.OntologyReasoningResult;

public class LFChecker extends AbstractSmellChecker{
	

	public LFChecker(OWLReasonerFactory rsfac, OWLDataFactory datafac, String iri) {
		super(rsfac, datafac, iri);
	}

	@Override
	public OntologyReasoningResult check(String modName, OWLOntology ontology) {
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//logger.info("start checking Lava Flow ");
		Instant start = Instant.now();
		
		List<OWLNamedIndividual> indvResultList =  queryIndividualsFromClass(reasoner, ontology, "LavaFlow");
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
	//	logger.info("end checking Lava Flow: "+result.size()+", Total Time(ms) "+timeElapsed);
		OntologyReasoningResult result = new OntologyReasoningResult(indvResultList, timeElapsed);
		//logger.info("write result",modName,"lf", ""+result.size(), ""+timeElapsed );
		return result;
	}
	
	
	
	

}
