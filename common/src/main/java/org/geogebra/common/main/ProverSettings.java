package org.geogebra.common.main;

/**
 * Prover settings (see AppD's handleHelpVersionArgs for details)
 */
public final class ProverSettings {

	private static ProverSettings INSTANCE;
	/**
	 * The used prover engine.
	 */
	public String proverEngine = "Auto";
	/**
	 * Timeout for the provers to run in seconds.
	 */
	public int proverTimeout = 5;
	/**
	 * Maximal number of allowed terms. Used by OpenGeoProver at the moment.
	 */
	private int maxTerms = 10000;
	/**
	 * Sub-engine in the defined engine. Used by OpenGeoProver/Recio at the
	 * moment.
	 */
	public String proverMethod = "Wu";
	/**
	 * Assume if the free points are never collinear. Used by Botana's prover at
	 * the moment.
	 */
	public Boolean freePointsNeverCollinear = null;
	/**
	 * How many coordinates are to be fixed speed up computation for "Prove"?
	 * Used by Botana's prover at the moment.
	 */
	public int useFixCoordinatesProve = 4;
	/**
	 * How many coordinates are to be fixed speed up computation for
	 * "ProveDetails"? Used by Botana's prover at the moment.
	 */
	public int useFixCoordinatesProveDetails = 4;
	/**
	 * If possible, should the polynomial ring with coefficients from
	 * transcendental extension used? Singular normally supports that by using a
	 * good enough (and fast) implementation.
	 */
	public boolean transcext = true;

	/**
	 * Show debug information in GeoElement captions. Useful for creating
	 * mathematically precise documentation of the applied algorithms. Used in
	 * Botana's method.
	 */
	public boolean captionAlgebra = false;

	private ProverSettings() {
		// singleton constructor
	}

	/**
	 * @return singleton instance
	 */
	public static ProverSettings get() {
		if (INSTANCE == null) {
			INSTANCE = new ProverSettings();
		}
		return INSTANCE;
	}

	/**
	 * @param max
	 *            max terms
	 */
	public void setMaxTerms(int max) {
		maxTerms = max;
	}

	/**
	 * @return max terms
	 */
	public int getMaxTerms() {
		return maxTerms;
	}

}
