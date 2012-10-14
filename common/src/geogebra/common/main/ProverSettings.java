package geogebra.common.main;
/**
 * Prover settings (see AppD's handleHelpVersionArgs for details)
 */
public class ProverSettings {
	
    /**
     * The used prover engine.
     */
    public static String proverEngine = "Auto";
    /**
     * Timeout for the provers to run in seconds.
     */
    public static int proverTimeout = 5;
    /**
     * Maximal number of allowed terms. Used by OpenGeoProver at the moment.
     */
    public static int maxTerms = 10000;
    /**
     * Sub-engine in the defined engine. Used by OpenGeoProver at the moment.
     */
    public static String proverMethod = "Wu";
    /**
     * Assume if the free points are never collinear. Used by Botana's prover at the moment.
     */
    public static boolean freePointsNeverCollinear = true;
    /**
     * Do we need to set fix coordinates to speed up computation?  Used by Botana's prover at the moment.
     */
    public static boolean useFixCoordinates = true;
}
