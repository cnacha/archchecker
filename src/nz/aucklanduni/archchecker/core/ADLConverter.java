package nz.aucklanduni.archchecker.core;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ADLConverter {
	private OWLOntologyManager manager;

	private static Logger logger = Logger.getLogger(ADLConverter.class.getName());

	private static final String IRI_NAME = "http://www.semanticweb.org/user/ontologies/2561/0/arch-library";

	public ADLConverter() {
		manager = OWLManager.createOWLOntologyManager();

	}

	public void process(String modelName, String ontologyFile) throws OWLOntologyCreationException {
		logger.info("processing model: "+modelName);
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile));

		// call reasoner to process once
		OWLReasonerFactory rf = new ReasonerFactory();
		OWLReasoner reasoner = rf.createReasoner(ontology);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		StringBuffer adlCode = new StringBuffer();
		
		// append API connector
		adlCode.append("connector APIConnector {\r\n" + 
				"	role consumer(j) = request->req!j->res?j-> process -> consumer(j);  \r\n" + 
				"	role provider()  = req?j -> invoke -> process -> res!j->provider();\r\n" + 
				"}\n");

		// get list of components individuals
		NodeSet<OWLNamedIndividual> componentSet = reasoner
				.getInstances(factory.getOWLClass(IRI.create(IRI_NAME + "#Component")), false);

		StringBuffer attachmentCode = new StringBuffer();
		StringBuffer systemExeCode = new StringBuffer();
		// convert components
		boolean notfirstPort = false;
		for (Node<OWLNamedIndividual> node : componentSet) {
			String compName = node.getRepresentativeElement().getIRI().getShortForm();
			adlCode.append("component " + compName + "{\n");
			Set<OWLObjectPropertyAssertionAxiom> objProps = ontology
					.getObjectPropertyAssertionAxioms(node.getRepresentativeElement());
			// logger.info(node.getRepresentativeElement().getIRI().getShortForm() +"
			// "+objProps.size());

			for (OWLObjectPropertyAssertionAxiom prop : objProps) {

				// logger.info(prop.getProperty().getNamedProperty().getIRI() +"__" +
				// prop.getObject());
				String portName = prop.getObject().asOWLNamedIndividual().getIRI().getShortForm();
				adlCode.append("   port " + portName + "() = initial -> exec -> " + portName + "(); \n");

				// define attachment
				attachmentCode.append("   attach " + compName + "." + portName + "() = ");
				Set<OWLObjectPropertyAssertionAxiom> attachmentProps = ontology
						.getObjectPropertyAssertionAxioms(prop.getObject().asOWLNamedIndividual());
				boolean notFirstAttr = false;
				
				// attach provider first
				for (OWLObjectPropertyAssertionAxiom attrProp : attachmentProps) {
					String roleName = attrProp.getObject().asOWLNamedIndividual().getIRI().getShortForm();
					String connName = roleName.substring(roleName.lastIndexOf("conn"));
					
					if (roleName.startsWith("prov")) {
						if (notFirstAttr) {
							attachmentCode.append(" <*> ");
						}
						attachmentCode.append(connName + ".provider()");
						notFirstAttr = true;
					}

					
				}
				// attach consumer later
				for (OWLObjectPropertyAssertionAxiom attrProp : attachmentProps) {
					String roleName = attrProp.getObject().asOWLNamedIndividual().getIRI().getShortForm();
					String connName = roleName.substring(roleName.lastIndexOf("conn"));
					
					if (roleName.startsWith("cons")) {
						if (notFirstAttr) {
							attachmentCode.append(" <*> ");
						}
						attachmentCode.append(connName + ".consumer(99)");
						notFirstAttr = true;
					}
				}
				
				
				attachmentCode.append("; \n");
				if (notfirstPort) {
					systemExeCode.append(" || ");
				}
				systemExeCode.append(compName + "." + portName + "()");

				notfirstPort = true;

			}
			adlCode.append("}\n");
		}
		// construct system configuration
		adlCode.append("system " + modelName + " { \n");
		// define connector
		NodeSet<OWLNamedIndividual> connectorSet = reasoner
				.getInstances(factory.getOWLClass(IRI.create(IRI_NAME + "#Connector")), false);
		for (Node<OWLNamedIndividual> node : connectorSet) {
			String connectorName = node.getRepresentativeElement().getIRI().getShortForm();
			adlCode.append("   declare " + connectorName + " = APIConnector; \n");
		}
		// append with attachment process
		adlCode.append(attachmentCode);

		// system execution
		adlCode.append("   execute " + systemExeCode + ";\n");

		adlCode.append("}\n\n");
		
		// append assertion
		adlCode.append("assert "+modelName+" ambiguousinterfacefree;\r\n" + 
				"assert "+modelName+" decompositionfree;\r\n" + 
				"assert "+modelName+" circularfree;\r\n" + 
				"assert "+modelName+" poltergeistfree;");


		String adlFileName = ontologyFile.substring(0, ontologyFile.indexOf(".")) + ".adl";

		File adlFile = new File(adlFileName);

		try (FileWriter fw = new FileWriter(adlFile)) {

			fw.write(adlCode.toString());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		System.out.println(adlCode.toString());
	}
}
