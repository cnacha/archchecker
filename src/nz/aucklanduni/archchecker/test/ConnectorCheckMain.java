package nz.aucklanduni.archchecker.test;

import nz.aucklanduni.archchecker.core.ClassDiagramParser;
import nz.aucklanduni.archchecker.core.OntologyBuilder;
import nz.aucklanduni.archchecker.object.ClassDiagram;

public class ConnectorCheckMain {
	
	private static final String MODNAME = "activiti";

	public static void main(String[] args) {
		ClassDiagramParser parser = new ClassDiagramParser();
		OntologyBuilder ontBuilder = new OntologyBuilder();
		try {
			ClassDiagram dg = parser.process("input/"+MODNAME+".ucls");
			int totalLO = 0;
			for(nz.aucklanduni.archchecker.object.Package pkg: dg.getPackageList()) {
				int number =0;
				int outgoing = dg.getDependencyBySource(pkg.getId()).size();
				int incoming = dg.getDependencyByTarget(pkg.getId()).size();
				number = outgoing + incoming;
				System.out.print(pkg.getName()+"  out:"+outgoing+" in:"+incoming+" total:"+number);
			//	if(dg.getDependencyBySource(pkg.getId()).size() >= 3 && dg.getDependencyByTarget(pkg.getId()).size()>=4) {
					totalLO ++;
			//		System.out.println(" ****");
			//	}else
			//		System.out.println();
			}
			System.out.println("total LO "+totalLO);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
