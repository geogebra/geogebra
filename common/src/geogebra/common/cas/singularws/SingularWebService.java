package geogebra.common.cas.singularws;

import geogebra.common.factories.UtilFactory;
import geogebra.common.util.DebugPrinter;
import geogebra.common.util.HttpRequest;
import geogebra.common.util.URLEncoder;

/**
 * Maintains a Singular WebService.
 * For the SingularWS API please see the documentation of SingularWS
 * @see "http://code.google.com/p/singularws/source/browse/inc/commands.php"
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class SingularWebService {

	private int timeout = 1;
	//private String wsHostDefault = "http://ws.geogebra.org/Singular";
	private final String wsHostDefault = "http://140.78.116.130:8085";
	private final String testConnectionCommand = "t";
	private final String singularDirectCommand = "s";
	
	private String wsHost = wsHostDefault;
	private Boolean available; 
	
	/**
	 * Creates a Singular webservice connection handler
	 */
	public SingularWebService() {}
	
	private String swsCommandResult(String command) {
		return swsCommandResult(command, "");
	}
	
	private String swsCommandResult(String command, String parameters) {
		String getRequest = wsHost + "/?c=" + command;
		if (parameters != null) {
			URLEncoder urle = UtilFactory.prototype.newURLEncoder();
			String encodedParameters = urle.encode(parameters);
			getRequest += "&p=" + encodedParameters;
		}
		HttpRequest httpr = UtilFactory.prototype.newHttpRequest();
		httpr.setTimeout(timeout);
		httpr.sendRequest(getRequest);
		String response = httpr.getResponse(); // will not work in web
		return response;
	}
	
	/**
	 * Reports if SingularWS is available. (It must be initialized by enable() first.) 
	 * @return true if SingularWS is available
	 */
	public boolean isAvailable() {
		if (available == null)
			return false;
		if (available)
			return true;
		return false;
	}
	
	/**
	 * Create a connection to the SingularWS server for testing.

	 * @return true if the connection works properly
	 */
	public boolean testConnection() {
		String result = swsCommandResult(testConnectionCommand); 
		if (result == null)
			return false;
		if (result.equals("ok"))
			return true;
		return false;
	}
	
	/**
	 * Sends a Singular program to the SingularWS server and returns the answer.

	 * @param singularProgram The program code to be sent directly to Singular
	 * @return the answer
	 */
	public String directCommand(String singularProgram) {
		return swsCommandResult(singularDirectCommand, singularProgram);
	}

	/** Sets the remote server being used for SingularWS.
	 * 
	 * @param site The remote http URL for the remote server 
	 */
	public void setConnectionSite(String site) {
		this.wsHost = site;
	}

	/** Reports what remote server is used for SingularWS.
	 * 
	 * @return the URL of the remote server
	 */
	public String getConnectionSite() {
		return this.wsHost;
	}
	
	/**
	 * If the test connection is working, then set the webservice "available".
	 */
	public void enable() {
		DebugPrinter dp = UtilFactory.prototype.newDebugPrinter();
		dp.print("Trying to enable SingularWS connection");
		Boolean tc = testConnection();
		if (tc != null && tc) {
			this.available = true;
		}
		else this.available = false;
	}
	
	/**
	 * Set the SingularWS connection handler to off 
	 */
	public void disable() {
		this.available = false;
	}
	
	/**
	 * Sets the maximal time spent in SingularWS for a program (not yet implemented).
	 * 
	 * @param timeout the timeout in seconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
}
