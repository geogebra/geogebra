package org.geogebra.common.main;

/**
 * RealGeomWS settings (see AppD's handleHelpVersionArgs for details)
 */
public class RealGeomWSSettings {

    /**
     * Do we want to use RealGeomWS for specific computations?
     */
    private static volatile boolean useRealGeomWebService = true;
    /**
     * The remote machine to be used for outsourced computations.
     */
    private static volatile String realGeomWebServiceRemoteURL = "http://roadrunner.risc.jku.at:8765";
    private static Object lock = new Object();
    /**
     * Timeout for a RealGeomWS session to run in seconds.
     */
    private static volatile int realGeomWebServiceTimeout = 5;
    /**
     * Above this value there is no detailed logging, only the size of the
     * program code will be printed as a debug message. This can help avoiding
     * too noisy debug.
     */
    final public static int debugMaxProgramSize = 2000;

    public static void setRealGeomWebServiceRemoteURL(String url) {
        synchronized (lock) {
            realGeomWebServiceRemoteURL = url;
        }
    }

    /**
     * @param t timeout
     */
    public static void setTimeout(int t) {
        synchronized (lock) {
            realGeomWebServiceTimeout = t;
        }
    }

    public static String getRealGeomWebServiceRemoteURL() {
        return realGeomWebServiceRemoteURL;
    }

    public static int getTimeout() {
        return realGeomWebServiceTimeout;
    }

    public static boolean isUseRealGeomWebService() {
        return useRealGeomWebService;
    }

    /**
     * @param b flag for using remote RealGeom
     */
    public static void setUseRealGeomWebService(boolean b) {
        synchronized (lock) {
            useRealGeomWebService = b;
        }
    }

}
