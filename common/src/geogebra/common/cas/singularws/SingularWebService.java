package geogebra.common.cas.singularws;

import geogebra.common.factories.UtilFactory;
import geogebra.common.main.AbstractApplication;

public class SingularWebService {

	private int timeout = 10;
	private String wsHostDefault = "http://ws.geogebra.org/Singular";
	private String testConnectionCommand = "t";
	
	private String wsHost = wsHostDefault;
	private Boolean available; 
	
	public SingularWebService() {}
	
	public boolean testConnection() {
		String command = wsHost + "/" + testConnectionCommand;
		String response = UtilFactory.prototype.newHttpRequestResponse(command);
		if (response.equals("ok"))
			return true;
		return false;
	}

	public void setConnectionSite(String site) {
		this.wsHost = site;
	}

	public String getConnectionSite() {
		return this.wsHost;
	}
	
	/**
	 * If the test connection is working, then set the webservice "available".
	 */
	public void enable() {
		if (testConnection()) {
			this.available = true;
		}
		else this.available = false;
		System.err.println("SingularWS="+this.available);
	}
	
	public void disable() {
		this.available = false;
	}
	
	/**
	 * Sets the maximal time spent in the Prover for the given proof.
	 * @param timeout The timeout in seconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}		

}
