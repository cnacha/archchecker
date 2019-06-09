package nz.aucklanduni.archchecker.core;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import nz.aucklanduni.archchecker.core.smell.EAChecker;
import nz.aucklanduni.archchecker.core.smell.LFChecker;
import nz.aucklanduni.archchecker.core.smell.LOChecker;
import nz.aucklanduni.archchecker.core.smell.UIChecker;
import nz.aucklanduni.archchecker.model.OntologyReasoningResult;

public class SmellCheckerSuite {
	
	protected static Logger  logger = LogManager.getLogger(AbstractSmellChecker.class.getName());
	
	protected OWLReasonerFactory reasonerFactory;
	protected OWLDataFactory dataFactory;
	protected String iri;
	
	public SmellCheckerSuite(OWLReasonerFactory rsfac, OWLDataFactory datafac, String iri) {
		this.reasonerFactory = rsfac;
		this.dataFactory = datafac;
		this.iri = iri;
		
	} 

	public void run(String modName, OWLOntology ontology) {
		System.out.println("Start checking all type of smells: "+modName);
		
		
		UIChecker uiChecker = new UIChecker(reasonerFactory, dataFactory, iri);
	    OntologyReasoningResult result1 =  uiChecker.check(modName,ontology);
		
		LFChecker lfChecker = new LFChecker(reasonerFactory, dataFactory, iri); 
		OntologyReasoningResult result2 =lfChecker.check(modName,ontology);
		

	    LOChecker loChecker = new LOChecker(reasonerFactory, dataFactory, iri);
	    OntologyReasoningResult result3 = loChecker.check(modName,ontology);
	    
		EAChecker eaChecker = new EAChecker(reasonerFactory, dataFactory, iri); 
		OntologyReasoningResult result4 = eaChecker.check(modName,ontology);
		 
		
		logger.info("result out", modName, result1.getResults().size(), result1.getTimeElapsed()
				, result2.getResults().size(), result2.getTimeElapsed()
				, result3.getResults().size(), result3.getTimeElapsed()
				, result4.getResults().size(), result4.getTimeElapsed());
		
		System.out.println("End checking all type of smells: "+modName);
	}
}
