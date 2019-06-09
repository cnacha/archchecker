package nz.aucklanduni.archchecker.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "class-diagram")
@XmlAccessorType (XmlAccessType.FIELD)
public class ClassDiagram implements Serializable{
	
	    @XmlElement(name = "package")
	    private ArrayList<Package> packageList;
	    
	    @XmlElement(name = "dependency")
	    private ArrayList<Dependency> dependencyList;

		public ArrayList<Package> getPackageList() {
			return packageList;
		}

		public void setPackageList(ArrayList<Package> packageList) {
			this.packageList = packageList;
		}

		public ArrayList<Dependency> getDependencyList() {
			return dependencyList;
		}

		public void setDependencyList(ArrayList<Dependency> dependencyList) {
			this.dependencyList = dependencyList;
		}
		
		public List<Dependency> getDependencyByTarget(int target) {
			List<Dependency> result = new ArrayList<Dependency>();
			for(Dependency dep: dependencyList) {
				if(dep.getTarget().getRefId()  == target ) {
					result.add(dep);
				}
			}
			return result;
		}
		
		public List<Dependency> getDependencyBySource(int source) {
			List<Dependency> result = new ArrayList<Dependency>();
			for(Dependency dep: dependencyList) {
				if(dep.getSource().getRefId()  == source ) {
					result.add(dep);
				}
			}
			return result;
		}
		
	    
	    
}
