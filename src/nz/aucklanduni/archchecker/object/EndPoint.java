package nz.aucklanduni.archchecker.object;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "end")
@XmlAccessorType (XmlAccessType.FIELD)
public class EndPoint {
	
	public static final String SOURCE = "SOURCE";
	public static final String TARGET = "TARGET";

	@XmlAttribute
	private String type;
	
	@XmlAttribute
	private int refId;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

}
