package nz.aucklanduni.archchecker.test;


import nz.aucklanduni.archchecker.core.ClassDiagramParser;
import nz.aucklanduni.archchecker.core.OntologyBuilder;
import nz.aucklanduni.archchecker.core.SmellCheckerSuite;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import org.semanticweb.HermiT.ReasonerFactory;

import nz.aucklanduni.archchecker.core.ADLConverter;
import nz.aucklanduni.archchecker.core.AbstractSmellChecker;
import nz.aucklanduni.archchecker.object.ClassDiagram;

public class SmellCheckMain {

	private static final String MODNAME = "activiti";
	
	protected static Logger  logger = Logger.getLogger(SmellCheckMain.class.getName());
	
	public static void  singleTest() {
		System.out.println("singleTest is called...");
		ClassDiagramParser parser = new ClassDiagramParser();
		OntologyBuilder ontBuilder = new OntologyBuilder();
		try {
			ClassDiagram dg = parser.process("input/"+MODNAME+".ucls");
			ontBuilder.buildModel(MODNAME, dg);
			//ontBuilder.openModel(MODNAME);
			
			// smell check
		//	SmellCheckerSuite checkSuite = new SmellCheckerSuite(new ReasonerFactory(), ontBuilder.getFactory(), ontBuilder.IRI_NAME);
		//	checkSuite.run(MODNAME, ontBuilder.getOntology());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void batchTest() {
		ClassDiagramParser parser = new ClassDiagramParser();
		
		String[] modSets = {"activiti","hibernate","hsqldb","log4j","springbeans","springwebmvc","xerces","xwork"};
		try {
			for(String modName: modSets) {
				ClassDiagram dg = parser.process("input/"+modName+".ucls");
				OntologyBuilder ontBuilder = new OntologyBuilder();
				ontBuilder.buildModel(modName, dg);
				
				// smell check
				SmellCheckerSuite checkSuite = new SmellCheckerSuite(new ReasonerFactory(), ontBuilder.getFactory(), ontBuilder.IRI_NAME);
				checkSuite.run(modName, ontBuilder.getOntology());
				
				
			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		batchTest(); 
		//singleTest();

	}
}
