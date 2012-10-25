package geogebra.common.cas.singularws;

import java.util.Date;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.MatchResult;

import geogebra.common.factories.UtilFactory;
import geogebra.common.main.App;
import geogebra.common.main.SingularWSSettings;
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

	private final int GET_REQUEST_MAX_SIZE = 2000;
	
	private int timeout = SingularWSSettings.singularWebServiceTimeout;
	private final String testConnectionCommand = "t";
	private final String singularDirectCommand = "s";
	
	private String wsHost = SingularWSSettings.singularWebServiceRemoteURL;
	private Boolean available;
	
	private static String locusLib = ""; 
    private static boolean fastConn;
	
	private final String[] SINGULAR_LIB_GROBCOVCx = {"grobcovC1", "grobcovC0"};
	
	private final int CONNECTION_SPEED_NO_TESTS = 3;
	private final int CONNECTION_SPEED_THRESHOLD = 100;
		
	/**
	 * Creates a Singular webservice connection handler
	 */
	public SingularWebService() {}
	
	private String swsCommandResult(String command) {
		return swsCommandResult(command, "");
	}
	
	private String swsCommandResult(String command, String parameters) {
		String url1 = wsHost + "/";
		String encodedParameters = "";
		if (parameters != null) {
			URLEncoder urle = UtilFactory.prototype.newURLEncoder();
			encodedParameters = urle.encode(parameters);
		}
		HttpRequest httpr = UtilFactory.prototype.newHttpRequest();
		httpr.setTimeout(timeout);
		// Varnish currently cannot do caching for POST requests,
		// so we prefer GET for the shorter Singular programs:
		if (encodedParameters.length() + url1.length() + command.length() + 6 <= GET_REQUEST_MAX_SIZE)
			httpr.sendRequest(url1 + "?c=" + command + "&p=" + encodedParameters);
		else
			httpr.sendRequestPost(url1,"c=" + command + "&p=" + encodedParameters);
		String response = httpr.getResponse(); // will not work in web, TODO: callback!
		if (response == null)
			return null; // avoiding NPE in web
		// Trimming:
		if (response.endsWith("> "))
			response = response.substring(0, response.length()-2);
		if (response.endsWith("\n"))
			response = response.substring(0, response.length()-1);
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
	 * Reports if SingularWS has a fast connection available. 
	 * @return true if SingularWS connection is fast enough
	 */
	public boolean isFast() {
		return fastConn;
	}
	
	/**
	 * Create a connection to the SingularWS server for testing.
	 * Also sets up variables depending on the installed features of Singular. 

	 * @return true if the connection works properly
	 */
	public boolean testConnection() {
			
		// App.debug("TEST: " + convertFloatsToRationals("((6.56*(x-x1))-(-0.2197*(y-x2)))"));
		
		String result = swsCommandResult(testConnectionCommand); 
		if (result == null)
			return false;
		if (result.equals("ok")) {
			// Testing connection speed.
			fastConn = true; // be optimistic
			for (int i = 0; i < CONNECTION_SPEED_NO_TESTS && fastConn; ++i) {
		    	Date date = new Date();
		        long startTime = date.getTime();
		    	swsCommandResult(testConnectionCommand);
		    	date = new Date();
		    	long elapsedTime = date.getTime() - startTime;
		    	App.debug("Measuring speed to SWS #" + i + ": " + elapsedTime + " ms");
		    	if (elapsedTime > CONNECTION_SPEED_THRESHOLD)
		    		fastConn = false;
			}

			// Testing extra features.
			for (String l: SINGULAR_LIB_GROBCOVCx) {
				if (testLib(l)) {
					locusLib = l;
					break;
					}
				}
			return true;
			}
		return false;
	}
	
	private boolean testLib(String name) {
		String result = directCommand("LIB \"" + name + ".lib\";");
		if (result.length() == 0) {
			App.debug("SingularWS supports library " + name);
			return true;
		}
		App.debug("SingularWS doesn't support library " + name + " (" + result + ")");
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
	 * If the test connection is working, then set the webservice "available",
	 * unless it is disabled by a command line option.
	 */
	public void enable() {
		if (!SingularWSSettings.useSingularWebService) {
			App.debug("SingularWS connection disabled by command line option");
			this.available = false;
			return;
		}
		App.debug("Trying to enable SingularWS connection");
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

	/**
	 * If non-empty, it contains the name of the auxiliary Singular library "grobcovCx"
	 * to compute loci in such a form which does not contain the degenerate parts
	 * of the algebraic curve.
	 * See http://www-ma2.upc.edu/montes/ for more details.
	 * Thanks to Antonio Montes and Francisco Botana for providing this extra library.
	 * 
	 * @return the name of the auxiliary Groebner cover library
	 */
	public static String getLocusLib() {
		return locusLib;
	}
	
	/**
	 * Converts floats to rationals. Uses some kind of heuristics
	 * since it simply replaces e.g. ".2346" to "2346/10000".
	 * This will also work e.g. for "89.2346" since it will be
	 * changed to "892346/10000". The "." character should not be use
	 * for other purposes, so we naively assume that this holds.

	 * @param input the input expression in floating point format
	 * @return the output expression in rational divisions
	 */
	public static String convertFloatsToRationals(String input) {
		
		/* It was a pain to convert this code to a GWT compliant one.
		 * See http://stackoverflow.com/questions/6323024/gwt-2-1-regex-class-to-parse-freetext
		 * for details.
		 */
		
		StringBuffer output = new StringBuffer();
			
		RegExp re = RegExp.compile("\\.[\\d]+", "g");
		
		int from = 0;
		
		for (MatchResult mr = re.exec(input); mr != null; mr = re.exec(input)) {
			String divisor = "1";
			int length = mr.getGroup(0).length();
			for (int j = 1; j < length; ++j) {
				divisor += "0";
			}
			// Adding the non-matching part from the previous match (or from the start):
			if (from <= mr.getIndex() - 1)
				output.append(input.substring(from, mr.getIndex()));
			// Adding the matching part in replaced form (removing the first "." character):
			output.append(input.substring(mr.getIndex() + 1, mr.getIndex()
					+ length) + "/" + divisor);
			from = mr.getIndex() + length; // Preparing then next "from".
		}
		// Adding tail:
		output.append(input.substring(from, input.length()));
		
		return output.toString();
	}
	
}
