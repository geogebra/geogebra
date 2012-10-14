package geogebra.common.main;
/**
 * SingularWS settings (see AppD's handleHelpVersionArgs for details)
 */
public class SingularWSSettings {
	
    /**
     * Do we want to use SingularWS for specific computations?
     */
    public static boolean useSingularWebService = true;
    /**
     * The remote machine to be used for outsourced computations.
     */
    public static String singularWebServiceRemoteURL = "http://singularws.idm.jku.at/";
    /**
     * Timeout for a SingularWS session to run in seconds.
     */
    public static int singularWebServiceTimeout = 5;
}
