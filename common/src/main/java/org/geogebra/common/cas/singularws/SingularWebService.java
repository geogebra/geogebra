package org.geogebra.common.cas.singularws;

import java.util.Date;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SingularWSSettings;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.URLEncoder;

/**
 * Maintains a Singular WebService. For the SingularWS API please see the
 * documentation of SingularWS
 * 
 * @see "https://github.com/kovzol/singularws"
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

	private final String[] SINGULAR_LIB_GROBCOVs = { "grobcovG", "grobcovF2m",
			"grobcovC1", "grobcovC0" };

	private final int CONNECTION_SPEED_NO_TESTS = 3;
	private final int CONNECTION_SPEED_THRESHOLD = 100;

	/**
	 * Creates a Singular webservice connection handler
	 */
	public SingularWebService() {
	}

	private String swsCommandResult(String command) throws Throwable {
		return swsCommandResult(command, "");
	}

	private String swsCommandResult(String command, String parameters)
			throws Throwable {
		String url1 = wsHost + "/";
		String encodedParameters = "";
		String caching = cachingString();
		if (parameters != null) {
			URLEncoder urle = UtilFactory.prototype.newURLEncoder();
			encodedParameters = urle.encode(parameters);
		}
		HttpRequest httpr = UtilFactory.prototype.newHttpRequest();
		httpr.setTimeout(timeout);
		// Varnish currently cannot do caching for POST requests,
		// so we prefer GET for the shorter Singular programs:
		if (encodedParameters.length() + url1.length() + command.length() + 6 <= GET_REQUEST_MAX_SIZE)
			httpr.sendRequest(url1 + "?c=" + command + "&p="
					+ encodedParameters + caching);
		else
			httpr.sendRequestPost(url1, "c=" + command + "&p="
					+ encodedParameters + caching, null);
		// In fact we will not use Varnish after changing SingularWS to version
		// >= 3 (2014-01-03).
		String response = httpr.getResponse(); // will not work in web, TODO:
												// callback!
		if (response == null)
			return null; // avoiding NPE in web
		// Trimming:
		if (response.endsWith("> "))
			response = response.substring(0, response.length() - 2);
		if (response.endsWith("\n"))
			response = response.substring(0, response.length() - 1);
		if (response.contains("error")) {
			// Intuitive detection of error in computation. TODO: be more
			// strict.
			App.error("Computation error in SingularWS: " + response);
			throw new org.geogebra.common.cas.error.ComputationException(
					"Computation error in SingularWS");
		}
		return response;
	}

	private static String cachingString() {
		if (SingularWSSettings.useCaching == null) {
			return "";
		}
		final String prefix = "&l=";
		if (SingularWSSettings.useCaching) {
			return prefix + "1";
		}
		return prefix + "0";
	}

	/**
	 * Reports if SingularWS is available. (It must be initialized by enable()
	 * first.)
	 * 
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
	 * 
	 * @return true if SingularWS connection is fast enough
	 */
	public boolean isFast() {
		return fastConn;
	}

	private String speed() {
		if (isFast())
			return "fast";
		return "slow";
	}

	/**
	 * Create a connection to the SingularWS server for testing. Also sets up
	 * variables depending on the installed features of Singular.
	 * 
	 * @return true if the connection works properly
	 */
	public boolean testConnection() {

		// App.debug("TEST: " +
		// convertFloatsToRationals("((6.56*(x-x1))-(-0.2197*(y-x2)))"));

		String result = null;
		try {
			result = swsCommandResult(testConnectionCommand);
		} catch (Throwable e) {
			App.error("Failure while testing SingularWS connection");
		}
		if (result == null)
			return false;
		if (result.equals("ok")) {
			// Testing connection speed.
			fastConn = true; // be optimistic
			for (int i = 0; i < CONNECTION_SPEED_NO_TESTS && fastConn; ++i) {
				Date date = new Date();
				long startTime = date.getTime();
				try {
					swsCommandResult(testConnectionCommand);
				} catch (Throwable e) {
					App.error("Failure while testing SingularWS connection");
				}
				date = new Date();
				long elapsedTime = date.getTime() - startTime;
				App.debug("Measuring speed to SWS #" + i + ": " + elapsedTime
						+ " ms");
				if (elapsedTime > CONNECTION_SPEED_THRESHOLD)
					fastConn = false;
			}

			// Testing extra features.
			for (String l : SINGULAR_LIB_GROBCOVs) {
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
		String result;
		try {
			result = directCommand("LIB \"" + name + ".lib\";");
			if (result.length() == 0) {
				App.debug("SingularWS supports library " + name);
				return true;
			}
			App.debug("SingularWS doesn't support library " + name + " ("
					+ result + ")");
		} catch (Throwable e) {
			App.error("Failure connecting to SingularWS");
		}
		return false;
	}

	/**
	 * Sends a Singular program to the SingularWS server and returns the answer.
	 * 
	 * @param singularProgram
	 *            The program code to be sent directly to Singular
	 * @return the answer
	 * @throws Throwable
	 *             ComputationException when problem occurs
	 */
	public String directCommand(String singularProgram) throws Throwable {
		return swsCommandResult(singularDirectCommand, singularProgram);
	}

	/**
	 * Sets the remote server being used for SingularWS.
	 * 
	 * @param site
	 *            The remote http URL for the remote server
	 */
	public void setConnectionSite(String site) {
		this.wsHost = site;
	}

	/**
	 * Reports what remote server is used for SingularWS.
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
		} else
			this.available = false;
	}

	/**
	 * Set the SingularWS connection handler to off
	 */
	public void disable() {
		this.available = false;
	}

	/**
	 * Sets the maximal time spent in SingularWS for a program (not yet
	 * implemented).
	 * 
	 * @param timeout
	 *            the timeout in seconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets Singular version.
	 * 
	 * @return version number (int), e.g. 3150
	 */
	public String getVersion() {
		if (isAvailable()) {
			try {
				return directCommand("system(\"version\");");
			} catch (Throwable e) {
				App.error("Failure while getting SingularWS version");
			}
		}
		return null;
	}

	/**
	 * Gets full Singular version string.
	 * 
	 * @return version string
	 */
	public String getSingularVersionString() {
		if (this.available) {
			return "SingularWS " + getVersion() + " " + speed() + " at "
					+ getConnectionSite();
		}
		return null;
	}

	/**
	 * If non-empty, it contains the name of the auxiliary Singular library
	 * "grobcovCx" to compute loci in such a form which does not contain the
	 * degenerate parts of the algebraic curve. See
	 * http://www-ma2.upc.edu/montes/ for more details. Thanks to Antonio Montes
	 * and Francisco Botana for providing this extra library.
	 * 
	 * @return the name of the auxiliary Groebner cover library
	 */
	public static String getLocusLib() {
		return locusLib;
	}

	/**
	 * Decides if we are using a new or an old version of the grobcov library.
	 * In old versions the "locusdg" command was "locus2d".
	 * 
	 * @return
	 */
	public static String getLocusCommand() {
		if (locusLib.endsWith("F2m") || locusLib.endsWith("G")) {
			return "locusdg";
		}
		return "locus2d";
	}

	/**
	 * Helper computations from Singular. Note that the returned string will be
	 * post-processed by GeoGebra and GeoGebraCAS. This is important since we
	 * assume that the factors computed by Singular in CIFactor.1 will be
	 * further simplified by Giac.
	 * 
	 * @param command
	 *            the GeoGebra command pattern
	 * @return its translation to Singular commands
	 */
	public String getTranslatedCASCommand(String command) {
		if (command.equals("CIFactor.1")) {
			StringBuilder sb = new StringBuilder();
			sb.append("LIB \"absfact.lib\";")
					.
					// FIXME: This covers the one-letter variables only, but
					// does nothing for the others
					// (infinitely many).
					append("ring R=0, (x,y,z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w), ds; short=0;")
					.append("poly q=%0;")
					.
					// Warning: the absfact.lib package prints an unwanted line
					// containing "absolute_factors".
					// It must be filtered somehow. Currently it is done in
					// SingularWS (server side)
					// for SingularWS version < 3. TODO: Do it for version >= 3
					// too, or hack the LIB.
					append("def S=absFactorize(q); setring(S); list af=absolute_factors;")
					.append("string Z=\"\";")
					.append("int i; int p=size(af[1]);")
					.
					// quadpolyroot returns the Ith root of poly P
					append("proc quadpolyroot(poly P, int I) {")
					.append("string PS=string(P);")
					.append("string RS=string(\"poly PP=\",PS[2,size(PS)-2]);")
					.
					// @c is the variable name for the rootof-like polynomial.
					append("def RR=basering; ring NR=0,(@c),ds;	execute(RS);")
					.append("matrix L=coeffs(PP,@c); bigint A=bigint(L[3,1]); bigint B=bigint(L[2,1]); bigint C=bigint(L[1,1]);")
					.append("string SC; if (I==1) { SC=\"+\"; } if (I==2) { SC=\"-\"; }")
					.append("string RV=string(\"((\",(-B),SC,\"sqrt(\",(B*B)-(4*A*C),\"))/(\",(2*A),\"))\");")
					.append("setring(RR); return(RV); }")
					.
					// polydeg returns the degree of poly P
					append("proc polydeg(poly P) { string PS=string(P); string RS=string(\"poly PP=\",PS[2,size(PS)-2]);")
					.append("def RR=basering; ring NR=0,(@c),ds; execute(RS); int L=size(coeffs(PP,@c))-1; setring(RR); return(L); }")
					.
					// replace is a standard search-replace string function
					append("proc replace(string HS, string N, string TO) { int found=1; while (found>0) { found=find(HS,N);")
					.append("if (found>0) { string BEF=HS[1,found-1]; string AFT; if (found+size(N)<=size(HS)) {")
					.append("AFT=HS[found+size(N),size(HS)]; } HS=string(BEF,TO,AFT); } } return(HS); }")
					.
					// the main computation: we return the product of the
					// factors as a string in Z
					append("for (i=1; i<=p; i++) { poly s=af[3][i]; if (polydeg(s)>2) { print(\"error\"); }")
					.append("string f=string(\"(\",af[1][i],\")\");")
					.append("if (polydeg(s)==2) { string f1=replace(f,\"@c\",quadpolyroot(s,1));")
					.append("string f2=replace(f,\"@c\",quadpolyroot(s,2)); f=string(\"simplify(\",f1,\")*simplify(\",f2,\")\"); }")
					.append("if (af[2][i]!=1) { f=string(f,\"^\",af[2][i]); }")
					.append("Z=string(Z,f); if (i<p) { Z=string(Z,\"*\"); } } Z;");

			return sb.toString();
		}
		return null;
	}

}
