package nz.aucklanduni.archchecker.model;

public class ADLResult {

	private String model;
	private String smell ;
	private String result;
	private long visitedStates; 
	private double verificationTime ;
	private String fullResultString ;
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSmell() {
		return smell;
	}
	public void setSmell(String smell) {
		this.smell = smell;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public long getVisitedStates() {
		return visitedStates;
	}
	public void setVisitedStates(long visitedStates) {
		this.visitedStates = visitedStates;
	}
	public double getVerificationTime() {
		return verificationTime;
	}
	public void setVerificationTime(double verificationTime) {
		this.verificationTime = verificationTime;
	}
	public String getFullResultString() {
		return fullResultString;
	}
	public void setFullResultString(String fullResultString) {
		this.fullResultString = fullResultString;
	}
	
	
}
