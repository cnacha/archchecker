package nz.aucklanduni.archchecker.object;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dependency")
@XmlAccessorType (XmlAccessType.FIELD)
public class Dependency implements Serializable{

	 @XmlElement(name = "end")
	 private ArrayList<EndPoint> endList;

	public ArrayList<EndPoint> getEndList() {
		return endList;
	}

	public void setEndList(ArrayList<EndPoint> endList) {
		this.endList = endList;
	}
	 
	public EndPoint getSource() {
		for(EndPoint end: endList) {
			if(EndPoint.SOURCE.equals(end.getType()))
				return end;
		}
		return null;
	}
	
	public EndPoint getTarget() {
		for(EndPoint end: endList) {
			if(EndPoint.TARGET.equals(end.getType()))
				return end;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "source: "+getSource().getRefId()+" target: "+getTarget().getRefId();
	}
}
