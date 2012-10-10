package geogebra.common.main;
/**
 * Prover settings (see AppD's handleHelpVersionArgs for details)
 */
public class ProverSettings {
	
    public static String proverEngine = "Auto"; // Later: "auto"
    public static int proverTimeout = 5;
    public static int maxTerms = 10000;
    public static String proverMethod = "Wu";
    public static boolean freePointsNeverCollinear = true;
    public static boolean useFixCoordinates = true;
    public static boolean useSingularWebService = true;
    public static String singularWebServiceRemoteURL = "http://singularws.idm.jku.at/";
    public static int singularWebServiceTimeout = 5;
}
