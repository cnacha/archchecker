package nz.aucklanduni.archchecker.object;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "package")
@XmlAccessorType (XmlAccessType.FIELD)
public class Package implements Serializable{
	
    @XmlAttribute
	private int id;
    
    @XmlAttribute
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public String getShortName() {
		return name.substring(name.lastIndexOf(".")+1);
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "id: "+id +" name: "+name;
	}
	
}
