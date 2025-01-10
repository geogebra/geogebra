package org.geogebra.common.kernel;

/**
 * Used to distinguish mode change events triggered by human and code
 */
public enum ModeSetter {
	/** resetting to first mode */
	DOCK_PANEL,
	/** human action in toolbar */
	TOOLBAR,
	/** CAS view switching to 1st mode automatically */
	CAS_VIEW,
	/**
	 * exit mode set temporarily
	 */
	EXIT_TEMPORARY_MODE,
	/**
	 * Cas focus lost triggers CAS evaluation (and mode change)
	 */
	CAS_BLUR

}
