package org.geogebra.common.main;

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
	 * Assume if the free points are never collinear. Used by Botana's prover at
	 * the moment.
	 */
	public static Boolean freePointsNeverCollinear = null;
	/**
	 * How many coordinates are to be fixed speed up computation for "Prove"?
	 * Used by Botana's prover at the moment.
	 */
	public static int useFixCoordinatesProve = 4;
	/**
	 * How many coordinates are to be fixed speed up computation for
	 * "ProveDetails"? Used by Botana's prover at the moment.
	 */
	public static int useFixCoordinatesProveDetails = 2;
	/**
	 * If possible, should the polynomial ring with coefficients from
	 * transcendental extension used? Singular normally supports that by using a
	 * good enough (and fast) implementation.
	 */
	public static boolean transcext = true;

	/**
	 * Show debug information in GeoElement captions. Useful for creating
	 * mathematically precise documentation of the applied algorithms. Used in
	 * Botana's method.
	 */
	public static boolean captionAlgebra = false;

}
