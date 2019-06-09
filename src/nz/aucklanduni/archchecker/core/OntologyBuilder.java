package nz.aucklanduni.archchecker.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import info.aduna.io.FileUtil;
import nz.aucklanduni.archchecker.object.ClassDiagram;
import nz.aucklanduni.archchecker.object.Dependency;
import nz.aucklanduni.archchecker.util.RandomString;

public class OntologyBuilder {

	public static final String IRI_NAME = "http://www.semanticweb.org/user/ontologies/2561/0/arch-library";
	private static final String DEFAULT_OWL_LIB_FILE = "config/arch-library.owl";

	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;

	public OntologyBuilder() {
		manager = OWLManager.createOWLOntologyManager();

	}
	
	

	public OWLOntologyManager getManager() {
		return manager;
	}



	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}



	public OWLOntology getOntology() {
		return ontology;
	}



	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}



	public OWLDataFactory getFactory() {
		return factory;
	}



	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}

	public void openModel(String modName) throws OWLOntologyCreationException {
		File outputFile = new File("output/" + modName + ".owl");
		// start writing OWL file
		ontology = manager.loadOntologyFromOntologyDocument(outputFile);
		manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().setDefaultPrefix(OntologyBuilder.IRI_NAME + "#");
		factory = manager.getOWLDataFactory();
	}

	public void buildModel(String modName, ClassDiagram dg)
			throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
		// copy file
		File outputFile = new File("output/" + modName + ".owl");
		if (outputFile.exists()) {
			outputFile.delete();
			outputFile.createNewFile();
		}
		FileUtil.copyFile(new File(DEFAULT_OWL_LIB_FILE), outputFile);

		// start writing OWL file
		ontology = manager.loadOntologyFromOntologyDocument(outputFile);
		manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().setDefaultPrefix(OntologyBuilder.IRI_NAME + "#");
		factory = manager.getOWLDataFactory();
		

		HashMap<Integer, OWLIndividual> compHash = new HashMap<Integer, OWLIndividual>();
		// create component
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			System.out.println(pkg.getName());
			OWLIndividual indv = factory.getOWLNamedIndividual(IRI.create(IRI_NAME + "#" + pkg.getShortName()+pkg.getId()));
			System.out.println(indv);
			manager.addAxiom(ontology, factory.getOWLSameIndividualAxiom(indv));
			compHash.put(pkg.getId(), indv);
		}
		// establishSimpleConnectivity(compHash, dg);
		establishConsolidateConnectivity2(compHash, dg);
		generateDummyEA(compHash, dg);
		manager.saveOntology(ontology);
		
	}
	
	HashMap<Integer, OWLIndividual> portDict = new HashMap<Integer, OWLIndividual>();
	
	
	private void generateDummyEA(HashMap<Integer, OWLIndividual> compHash, ClassDiagram dg) {
		// generate num PubSub Connector
		int num = 2;
		// dummy role with PubSub style
		OWLClass publisherClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Publisher"));
		OWLClass subscriberClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Subscriber"));
		
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			OWLIndividual publishCompIndv  = compHash.get(pkg.getId());
			System.out.println("inserting dummy connectors.."+num);
			
			List<Dependency> callers = dg.getDependencyByTarget(pkg.getId());
			if(callers.size()!=0) {
				OWLIndividual subscribeCompIndv  = compHash.get(callers.get(0).getSource().getRefId());
				// create connector for the caller
				String connName = "conn"+ pkg.getId()+"_dummy_" + RandomString.randomString(2);
				OWLIndividual connectorIndv = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#"+connName));
	
				// create provider role
				OWLIndividual publisherRole = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#publisher"+connName));
				OWLClassAssertionAxiom provClassAssertion = factory.getOWLClassAssertionAxiom(publisherClass, publisherRole);
				manager.addAxiom(ontology, provClassAssertion);
	
				// create consumer role
				OWLIndividual subscriberRole = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#subscriber"+connName));
				OWLClassAssertionAxiom consuClassAssertion = factory.getOWLClassAssertionAxiom(subscriberClass, subscriberRole);
				manager.addAxiom(ontology, consuClassAssertion);
				
				// bind connector and roles
				OWLObjectPropertyExpression hasRoleExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasRole"));
				OWLObjectPropertyAssertionAxiom providerHasRoleAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, publisherRole);
				manager.addAxiom(ontology, providerHasRoleAxiom);
				OWLObjectPropertyAssertionAxiom consumerHasRoleAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, subscriberRole);
				manager.addAxiom(ontology, consumerHasRoleAxiom);
				
				//OWLIndividual publishPort = portDict.get(pkg.getId());
				//OWLIndividual subscribPort = portDict.get(callers.get(0).getSource().getRefId());
				// create new port
				OWLIndividual publishPort = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#publish"+pkg.getId()+"_" + RandomString.randomString(4)));
				OWLObjectPropertyExpression hasPortExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasPort"));
				OWLObjectPropertyAssertionAxiom providerHasPortAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasPortExpr, publishCompIndv, publishPort);
				manager.addAxiom(ontology, providerHasPortAxiom);
	
				// attach publisher role to publish port
				OWLObjectPropertyExpression hasAttachmentExpr = factory
						.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasAttachment"));
				OWLObjectPropertyAssertionAxiom publisherhasAttachAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, publishPort, publisherRole);
				manager.addAxiom(ontology, publisherhasAttachAxiom);
				
				
				OWLIndividual subscribePort = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#publish"+pkg.getId()+"_" + RandomString.randomString(4)));
				OWLObjectPropertyAssertionAxiom subscriberHasPortAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasPortExpr, subscribeCompIndv, subscribePort);
				manager.addAxiom(ontology, subscriberHasPortAxiom);
				
				// attach subscriber role to subscribe port
				OWLObjectPropertyAssertionAxiom subscriberhasAttachAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, subscribePort, subscriberRole);
				manager.addAxiom(ontology, subscriberhasAttachAxiom);
				num--;
			} else {
				continue;
			}

			
			if(num ==0)
				break;
			
		}
	}
	
	

	private void establishConsolidateConnectivity(HashMap<Integer, OWLIndividual> compHash, ClassDiagram dg) {
		OWLClass consumerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Consumer"));
		OWLClass providerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Provider"));
		HashMap<Integer, OWLIndividual> consumePortDict = new HashMap<Integer, OWLIndividual>();
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			
			// create provide port
			OWLIndividual providePort = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provide"+pkg.getId()+"_" + RandomString.randomString(4)));
			OWLObjectPropertyExpression hasPortExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasPort"));
			OWLIndividual providerCompIndv = compHash.get(pkg.getId());
			OWLObjectPropertyAssertionAxiom providerHasPortAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasPortExpr, providerCompIndv, providePort);
			manager.addAxiom(ontology, providerHasPortAxiom);
		
			List<Dependency> callers = dg.getDependencyByTarget(pkg.getId());
			if(callers.size()>0) {
				
				// create connector
				String connName = "conn"+ pkg.getId()+"_" + RandomString.randomString(4);
				OWLIndividual connectorIndv = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#"+connName));
	
				// create provider role
				OWLIndividual providerRole = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provider"+connName));
				OWLClassAssertionAxiom provClassAssertion = factory.getOWLClassAssertionAxiom(providerClass, providerRole);
				manager.addAxiom(ontology, provClassAssertion);
	
				// create consumer role
				OWLIndividual consumerRole = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consumer"+connName));
				OWLClassAssertionAxiom consuClassAssertion = factory.getOWLClassAssertionAxiom(consumerClass, consumerRole);
				manager.addAxiom(ontology, consuClassAssertion);
	
				// bind connector and roles
				OWLObjectPropertyExpression hasRoleExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasRole"));
				OWLObjectPropertyAssertionAxiom providerHasRoleAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, providerRole);
				manager.addAxiom(ontology, providerHasRoleAxiom);
				OWLObjectPropertyAssertionAxiom consumerHasRoleAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, consumerRole);
				manager.addAxiom(ontology, consumerHasRoleAxiom);
	
				// attach provider role to provide port
				OWLObjectPropertyExpression hasAttachmentExpr = factory
						.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasAttachment"));
				OWLObjectPropertyAssertionAxiom providerhasAttachAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, providePort, providerRole);
				manager.addAxiom(ontology, providerhasAttachAxiom);
	
				
				
				for (Dependency dep : dg.getDependencyByTarget(pkg.getId())) {
					// create source consume port on consumer component
					OWLIndividual consumerCompIndv = compHash.get(dep.getSource().getRefId());
					OWLIndividual consumePort = null;
					if (consumePortDict.containsKey(dep.getSource().getRefId())) {
						consumePort = consumePortDict.get(dep.getSource().getRefId());
					} else {
						consumePort = factory
								.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consume"+dep.getSource().getRefId()+"_" + RandomString.randomString(4)));
						OWLObjectPropertyAssertionAxiom consumerHasPortAxiom = factory
								.getOWLObjectPropertyAssertionAxiom(hasPortExpr, consumerCompIndv, consumePort);
						manager.addAxiom(ontology, consumerHasPortAxiom);
						consumePortDict.put(dep.getSource().getRefId(), consumePort);
					}
					
					// attach consumer role to consume port
					OWLObjectPropertyAssertionAxiom consumerhasAttachAxiom = factory
							.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, consumePort, consumerRole);
					manager.addAxiom(ontology, consumerhasAttachAxiom);
				}
			}
		}

		// define process
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			for (Dependency dep : dg.getDependencyBySource(pkg.getId())) {
				
			}
		}
	}
	
	private void establishConsolidateConnectivity2(HashMap<Integer, OWLIndividual> compHash, ClassDiagram dg) {
		OWLClass consumerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Consumer"));
		OWLClass providerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Provider"));
		
		
		HashMap<Integer, Integer> callToDict = new HashMap<Integer, Integer>();
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			callToDict.put(pkg.getId(), 0);
		}
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			

			OWLIndividual providePort = null;
			OWLObjectPropertyExpression hasPortExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasPort"));
			if(portDict.containsKey(pkg.getId())) {
				// get provide port
				providePort = portDict.get(pkg.getId());
			} else {
				// create provide port
				providePort = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#port"+pkg.getId()+"_" + RandomString.randomString(4)));
				OWLIndividual providerCompIndv = compHash.get(pkg.getId());
				OWLObjectPropertyAssertionAxiom providerHasPortAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasPortExpr, providerCompIndv, providePort);
				manager.addAxiom(ontology, providerHasPortAxiom);
				
				portDict.put(pkg.getId(),providePort);
			}
			
			
			
			List<Dependency> callers = dg.getDependencyByTarget(pkg.getId());
			
			// define the data properties for number of consumer
			OWLDataPropertyExpression hasProp =  factory.getOWLDataProperty(IRI.create(IRI_NAME+"#consumedByNum"));
			OWLDatatype integerDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI());
			
			OWLLiteral literal = factory.getOWLLiteral(Integer.toString(callers.size()), integerDatatype);
			
			OWLAxiom ax = factory.getOWLDataPropertyAssertionAxiom(hasProp, providePort, literal);
			//System.out.println(pkg.getId()+"==="+callers.size());
			manager.addAxiom(ontology, ax);
			
			if(callers.size()>0) {
								
				// create connector for each caller
				String connName = "conn"+ pkg.getId()+"_" + RandomString.randomString(4);
				OWLIndividual connectorIndv = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#"+connName));
	
				// create provider role
				OWLIndividual providerRole = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provider"+connName));
				OWLClassAssertionAxiom provClassAssertion = factory.getOWLClassAssertionAxiom(providerClass, providerRole);
				manager.addAxiom(ontology, provClassAssertion);
	
				// create consumer role
				OWLIndividual consumerRole = factory
						.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consumer"+connName));
				OWLClassAssertionAxiom consuClassAssertion = factory.getOWLClassAssertionAxiom(consumerClass, consumerRole);
				manager.addAxiom(ontology, consuClassAssertion);
	
				// bind connector and roles
				OWLObjectPropertyExpression hasRoleExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasRole"));
				OWLObjectPropertyAssertionAxiom providerHasRoleAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, providerRole);
				manager.addAxiom(ontology, providerHasRoleAxiom);
				OWLObjectPropertyAssertionAxiom consumerHasRoleAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, consumerRole);
				manager.addAxiom(ontology, consumerHasRoleAxiom);
	
				// attach provider role to provide port
				OWLObjectPropertyExpression hasAttachmentExpr = factory
						.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasAttachment"));
				OWLObjectPropertyAssertionAxiom providerhasAttachAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, providePort, providerRole);
				manager.addAxiom(ontology, providerhasAttachAxiom);
	
				
				
				for (Dependency dep : callers) {
					// create source consume port on consumer component
					OWLIndividual consumerCompIndv = compHash.get(dep.getSource().getRefId());
					//System.out.println("target: "+pkg.getName()+ " caller: "+dep.getSource().getRefId());
					OWLIndividual consumePort = null;
					if (portDict.containsKey(dep.getSource().getRefId())) {
						consumePort = portDict.get(dep.getSource().getRefId());
					} else {
						consumePort = factory
								.getOWLNamedIndividual(IRI.create(IRI_NAME + "#port"+dep.getSource().getRefId()+"_" + RandomString.randomString(4)));
						OWLObjectPropertyAssertionAxiom consumerHasPortAxiom = factory
								.getOWLObjectPropertyAssertionAxiom(hasPortExpr, consumerCompIndv, consumePort);
						manager.addAxiom(ontology, consumerHasPortAxiom);
						portDict.put(dep.getSource().getRefId(), consumePort);
					}
					callToDict.put(dep.getSource().getRefId(), callToDict.get(dep.getSource().getRefId())+1);
					// attach consumer role to consume port
					OWLObjectPropertyAssertionAxiom consumerhasAttachAxiom = factory
							.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, consumePort, consumerRole);
					manager.addAxiom(ontology, consumerhasAttachAxiom);
				}
			}
		}

		// define process
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			OWLDataPropertyExpression hasProp =  factory.getOWLDataProperty(IRI.create(IRI_NAME+"#callToNum"));
			OWLDatatype integerDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI());
			
			OWLLiteral literal = factory.getOWLLiteral(Integer.toString(callToDict.get(pkg.getId())), integerDatatype);
		//	System.out.println("generating callToNum prop: "+portDict.get(pkg.getId()));
			if(portDict.get(pkg.getId())!=null) {
				OWLAxiom ax = factory.getOWLDataPropertyAssertionAxiom(hasProp, portDict.get(pkg.getId()), literal);
				manager.addAxiom(ontology, ax);
			}
			
			
		}
	}

	
/*
	private void establishConsolidateConnectivity2(HashMap<Integer, OWLIndividual> compHash, ClassDiagram dg) {
		OWLClass consumerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Consumer"));
		OWLClass providerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Provider"));
		HashMap<Integer, OWLIndividual> providePortDict = new HashMap<Integer, OWLIndividual>();
		for (nz.aucklanduni.archchecker.object.Package pkg : dg.getPackageList()) {
			// create consume port
			OWLIndividual consumePort = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consume" + RandomString.randomString(8)));
			OWLObjectPropertyExpression hasPortExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasPort"));
			OWLIndividual consumerCompIndv = compHash.get(pkg.getId());
			OWLObjectPropertyAssertionAxiom consumerHasPortAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasPortExpr, consumerCompIndv, consumePort);
			manager.addAxiom(ontology, consumerHasPortAxiom);

			// create connector
			OWLIndividual connectorIndv = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#call" + RandomString.randomString(8)));

			// create provider role
			OWLIndividual providerRole = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provider" + RandomString.randomString(8)));
			OWLClassAssertionAxiom provClassAssertion = factory.getOWLClassAssertionAxiom(providerClass, providerRole);
			manager.addAxiom(ontology, provClassAssertion);

			// create consumer role
			OWLIndividual consumerRole = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consumer" + RandomString.randomString(8)));
			OWLClassAssertionAxiom consuClassAssertion = factory.getOWLClassAssertionAxiom(consumerClass, consumerRole);
			manager.addAxiom(ontology, consuClassAssertion);

			// bind connector and roles
			OWLObjectPropertyExpression hasRoleExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasRole"));
			OWLObjectPropertyAssertionAxiom providerHasRoleAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, providerRole);
			manager.addAxiom(ontology, providerHasRoleAxiom);
			OWLObjectPropertyAssertionAxiom consumerHasRoleAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, consumerRole);
			manager.addAxiom(ontology, consumerHasRoleAxiom);

			// attach consumer role to consume port
			OWLObjectPropertyExpression hasAttachmentExpr = factory
					.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasAttachment"));
			OWLObjectPropertyAssertionAxiom consumerhasAttachAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, consumePort, consumerRole);
			manager.addAxiom(ontology, consumerhasAttachAxiom);
			
			for (Dependency dep : dg.getDependencyBySource(pkg.getId())) {
				// create provide port on target component
				OWLIndividual providerCompIndv = compHash.get(dep.getTarget().getRefId());
				OWLIndividual providePort = null;
				if (providePortDict.containsKey(dep.getTarget().getRefId())) {
					providePort = providePortDict.get(dep.getTarget().getRefId());
				} else {
					providePort = factory
							.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provide" + RandomString.randomString(8)));
					OWLObjectPropertyAssertionAxiom providerHasPortAxiom = factory
							.getOWLObjectPropertyAssertionAxiom(hasPortExpr, providerCompIndv, providePort);
					manager.addAxiom(ontology, providerHasPortAxiom);
					
					providePortDict.put(dep.getTarget().getRefId(), providePort);
				}
				

				// attach provider role to provide port
				OWLObjectPropertyAssertionAxiom providerhasAttachAxiom = factory
						.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, providePort, providerRole);
				manager.addAxiom(ontology, providerhasAttachAxiom);
			}
		}
	}
*/
	// 1 connector for a provider
	
	// 1 connector per dependency
/*
	private void establishSimpleConnectivity(HashMap<Integer, OWLIndividual> compHash, ClassDiagram dg) {
		// create connectivity
		OWLClass consumerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Consumer"));
		OWLClass providerClass = factory.getOWLClass(IRI.create(IRI_NAME + "#Provider"));
		for (Dependency dep : dg.getDependencyList()) {

			// create provide port
			OWLIndividual providePort = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provide_" + RandomString.randomString(8)));
			OWLObjectPropertyExpression hasPortExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasPort"));
			OWLIndividual providerCompIndv = compHash.get(dep.getTarget().getRefId());
			OWLObjectPropertyAssertionAxiom providerHasPortAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasPortExpr, providerCompIndv, providePort);
			manager.addAxiom(ontology, providerHasPortAxiom);

			// create connector
			OWLIndividual connectorIndv = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#call_" + RandomString.randomString(8)));

			// create provider role
			OWLIndividual providerRole = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#provider_" + RandomString.randomString(8)));
			OWLClassAssertionAxiom provClassAssertion = factory.getOWLClassAssertionAxiom(providerClass, providerRole);
			manager.addAxiom(ontology, provClassAssertion);

			// create consumer role
			OWLIndividual consumerRole = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consumer_" + RandomString.randomString(8)));
			OWLClassAssertionAxiom consuClassAssertion = factory.getOWLClassAssertionAxiom(consumerClass, consumerRole);
			manager.addAxiom(ontology, consuClassAssertion);

			// bind connector and roles
			OWLObjectPropertyExpression hasRoleExpr = factory.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasRole"));
			OWLObjectPropertyAssertionAxiom providerHasRoleAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, providerRole);
			manager.addAxiom(ontology, providerHasRoleAxiom);
			OWLObjectPropertyAssertionAxiom consumerHasRoleAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasRoleExpr, connectorIndv, consumerRole);
			manager.addAxiom(ontology, consumerHasRoleAxiom);

			// attach provider role to provide port
			OWLObjectPropertyExpression hasAttachmentExpr = factory
					.getOWLObjectProperty(IRI.create(IRI_NAME + "#hasAttachment"));
			OWLObjectPropertyAssertionAxiom providerhasAttachAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, providePort, providerRole);
			manager.addAxiom(ontology, providerhasAttachAxiom);

			// create source consume port on consumer component
			OWLIndividual consumerCompIndv = compHash.get(dep.getSource().getRefId());
			OWLIndividual consumePort = factory
					.getOWLNamedIndividual(IRI.create(IRI_NAME + "#consume_" + RandomString.randomString(8)));
			OWLObjectPropertyAssertionAxiom consumerHasPortAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasPortExpr, consumerCompIndv, consumePort);
			manager.addAxiom(ontology, consumerHasPortAxiom);

			// attach consumer role to consume port
			OWLObjectPropertyAssertionAxiom consumerhasAttachAxiom = factory
					.getOWLObjectPropertyAssertionAxiom(hasAttachmentExpr, consumePort, consumerRole);
			manager.addAxiom(ontology, consumerhasAttachAxiom);
		}
	}
*/
}
