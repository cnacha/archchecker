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

public class ListStatMain {

	private static final String MODNAME = "xerces";
	
	protected static Logger  logger = Logger.getLogger(ConvertADLMain.class.getName());
	
	
	public static void batchTest() {
		ClassDiagramParser parser = new ClassDiagramParser();
		
		String[] modSets = {"activiti","hibernate","hsqldb","log4j","springbeans","springwebmvc","xerces","xwork"};
		//String[] modSets = {"hibernate"};
		try {
			File dir = new File("input");
			for(String modName: modSets) {
				
				//query only test file with name xxx-test*
				File [] files = dir.listFiles(new FilenameFilter() {
				    @Override
				    public boolean accept(File dir, String name) {
				        return name.contains(modName+".ucls");
				    }
				});
				
				for(File file: files) {
					logger.info("converting Diagram to OWL : "+file.getPath());
					String submodName = file.getName().substring(0,file.getName().indexOf("."));
					// process test file
					ClassDiagram dg = parser.process(file.getPath());
					OntologyBuilder ontBuilder = new OntologyBuilder();
					
					ontBuilder.buildModel(submodName, dg);
					
					System.out.println("		# "+ontBuilder.getNumberOfIndividuals());
					
				//	String owlFileName = "output/"+submodName+".owl";
				//	logger.info("converting OWL to ADL : "+owlFileName);
				//	ADLConverter adlconverter = new ADLConverter();
				//	adlconverter.process(modName, owlFileName);
					
				}
				
				
			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		batchTest();

	}
}
