package nz.aucklanduni.archchecker.core.smell;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.sqwrl.SQWRLQueryEngine;

import nz.aucklanduni.archchecker.core.AbstractSmellChecker;
import nz.aucklanduni.archchecker.model.OntologyReasoningResult;

public class EAChecker extends AbstractSmellChecker {
	

	public EAChecker(OWLReasonerFactory rsfac, OWLDataFactory datafac, String iri) {
		super(rsfac, datafac, iri);
		// TODO Auto-generated constructor stub
	}

	@Override
	public OntologyReasoningResult check(String modName, OWLOntology ontology) {
		
		//SWRLRuleEngine ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
		//ruleEngine.run();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//logger.info("start checking EA Connector ");
		Instant start = Instant.now();
		
		
		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);

		List<OWLNamedIndividual> indvResultList =  queryIndividualsFromClass(reasoner, ontology, "EAConnector");
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		//logger.info("end checking EA Connector: "+result.size()+", Total Time(ms) "+timeElapsed);
		OntologyReasoningResult result = new OntologyReasoningResult(indvResultList, timeElapsed);
		return result;
	}
	

}
