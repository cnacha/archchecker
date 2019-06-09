package nz.aucklanduni.archchecker.core;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nz.aucklanduni.archchecker.object.ClassDiagram;

public class ClassDiagramParser {
	
	public ClassDiagram process(String fileName) throws JAXBException, IOException{
		File xmlFile = new File(fileName);
		 
		JAXBContext jaxbContext = JAXBContext.newInstance(ClassDiagram.class);             
		 
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	 
	    ClassDiagram cd = (ClassDiagram) jaxbUnmarshaller.unmarshal(xmlFile);
	    
	    return cd;
	   
	}
	/*	
	public static void main(String args[])  {
		
		try {
			ClassDiagram cd = (new ClassDiagramParser()).process("input/lucene.ucls");

			for (nz.aucklanduni.archchecker.object.Package p : cd.getPackageList()) {
				System.out.println(p);
			}

			for (nz.aucklanduni.archchecker.object.Dependency d : cd.getDependencyList()) {
				System.out.println(d);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
}
