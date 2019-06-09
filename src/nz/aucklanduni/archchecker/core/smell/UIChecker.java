package nz.aucklanduni.archchecker.core.smell;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import nz.aucklanduni.archchecker.core.AbstractSmellChecker;
import nz.aucklanduni.archchecker.core.OntologyBuilder;
import nz.aucklanduni.archchecker.model.OntologyReasoningResult;
import nz.aucklanduni.archchecker.util.RandomString;

// for checking unused link
public class UIChecker extends AbstractSmellChecker {

	
	private OWLOntologyManager manager;


	public UIChecker(OWLReasonerFactory rsfac, OWLDataFactory datafac, String iri) {
		super(rsfac, datafac, iri);
		manager = OWLManager.createOWLOntologyManager();
	}
	
	
	@Override
	public OntologyReasoningResult check(String modName, OWLOntology ontology) {
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		// logger.info("model is consistent? "+reasoner.isConsistent());
		//logger.info("start checking Unused Link ");
		Instant start = Instant.now();

		// query number of attachment for each port
		
		/*
		
		SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
		try {
			SQWRLResult result = queryEngine.runSQWRLQuery("q1",
					"Port(?p) ^ hasAttachment(?p, ?a) -> sqwrl:select(?p) ^ sqwrl:count(?a)");
			for(String col: result.getColumnNames()) {
				System.out.println(col);
			}
			while (result.next()) {
				int numAttachment = result.getValue(1).asLiteralResult().getInt();
				System.out.println("c: " + result.getValue(0).toString() + "  attachment: " + numAttachment);
				if(numAttachment == 0) {
					OWLIndividual ulindv = dataFactory
							.getOWLNamedIndividual(IRI.create(iri + "#"+ result.getValue(0).toString()));
					OWLClassAssertionAxiom provClassAssertion = dataFactory.getOWLClassAssertionAxiom(ulclass, ulindv);
					manager.addAxiom(ontology, provClassAssertion);
				}
			}
			
		} catch (SQWRLException | SWRLParseException e) {
			e.printStackTrace();
		}
*/
		 List<OWLNamedIndividual> indvResultList = queryIndividualsFromClass(reasoner,ontology, "UnusedLink");

		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		// logger.info("end checking Unused Link: "+result.size()+", Total Time(ms)"+timeElapsed);
		OntologyReasoningResult result = new OntologyReasoningResult(indvResultList, timeElapsed);
		return result;

	}

}
