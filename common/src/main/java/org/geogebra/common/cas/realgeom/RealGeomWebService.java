package org.geogebra.common.cas.realgeom;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.main.RealGeomWSSettings;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.common.util.debug.Log;

/**
 * Maintains a RealGeom WebService. For the RealGeom API please see the
 * documentation of RealGeom
 *
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * @see "https://github.com/kovzol/realgeom"
 */
public class RealGeomWebService {

    private final static int GET_REQUEST_MAX_SIZE = 2000;

    private int timeout = RealGeomWSSettings.getTimeout();
    String testConnectionCommand = "euclideansolver";
    String testConnectionParameters = "lhs=a+b-c&rhs=g&polys=(b1-c1)^2+(b2-c2)^2-a^2,(a1-c1)^2+(a2-c2)^2-b^2,(a1-b1)^2+(a2-b2)^2-c^2,(g1-c1)^2+(g2-c2)^2-g^2,(a1+b1)-2g1,(a2+b2)-2g2&vars=a1,a2,b1,b2,c1,c2,g1,g2,a,b,c,g&posvariables=a,b,c,g&triangles=a,b,c&mode=explore";

    private String wsHost = RealGeomWSSettings.getRealGeomWebServiceRemoteURL();
    private Boolean available;

    private String rgwsCommandResult(String command) throws Throwable {
        return rgwsCommandResult(command, "");
    }

    private String rgwsCommandResult(String command, String parameters)
            throws Throwable {
        String url1 = wsHost; // + "/";
        String encodedParameters = "";
        if (parameters != null) {
            URLEncoder urle = UtilFactory.getPrototype().newURLEncoder();
            encodedParameters = urle.encode(parameters);
        }
        HttpRequest httpr = UtilFactory.getPrototype().newHttpRequest();
        httpr.setTimeout(timeout);

        if (encodedParameters.length() + url1.length() + command.length()
                + 6 <= GET_REQUEST_MAX_SIZE) {
            httpr.sendRequestPost("GET",
                    url1 + "/" + command + "?" + encodedParameters, null, null);
        } else {
            httpr.sendRequestPost("POST", url1,
                    "/" + command + "?" + encodedParameters,
                    null);
        }
        String response = httpr.getResponse();
        // callback!
        if (response == null) {
            return null; // avoiding NPE in web
        }
        return response;
    }

    /**
     * Reports if RealGeomWS is available. (It must be initialized by enable()
     * first.)
     *
     * @return true if RealGeomWS is available
     */
    public boolean isAvailable() {
        if (available == null) {
            return false;
        }
        if (available) {
            return true;
        }
        return false;
    }

    /**
     * Create a connection to the RealGeomWS server for testing. Also sets up
     * variables depending on the installed features of RealGeom.
     *
     * @return true if the connection works properly
     */
    public boolean testConnection() {

        String result = null;
        try {
            result = rgwsCommandResult(testConnectionCommand, testConnectionParameters);
        } catch (Throwable e) {
            Log.error("Failure while testing RealGeomWS connection");
        }
        System.out.println("result=" + result);
        if (result == null) {
            return false;
        }
        if ("Inequality[0, Less, m, Less, 2]".equals(result)) {
            return true;
        }
        return false;
    }


    /**
     * Sets the remote server being used for RealGeomWS.
     *
     * @param site The remote http URL for the remote server
     */
    public void setConnectionSite(String site) {
        this.wsHost = site;
    }

    /**
     * Reports what remote server is used for RealGeomWS.
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
        if (!RealGeomWSSettings.isUseRealGeomWebService()) {
            Log.debug("RealGeomWS connection disabled by command line option");
            this.available = false;
            return;
        }
        Log.debug("Trying to enable RealGeomWS connection");
        boolean tc = testConnection();
        if (tc) {
            this.available = true;
        } else {
            this.available = false;
        }
    }

    /**
     * Set the RealGeomWS connection handler to off
     */
    public void disable() {
        this.available = false;
    }

    /**
     * Sets the maximal time spent in RealGeomWS for a program (not yet
     * implemented).
     *
     * @param timeout the timeout in seconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
