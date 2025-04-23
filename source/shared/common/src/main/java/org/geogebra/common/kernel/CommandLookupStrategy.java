package org.geogebra.common.kernel;

/**
 * Strategy for mapping command names to commands.
 */
public enum CommandLookupStrategy {
	/**
	 * Accept localized, English or internal.
	 */
	USER,
	/** Accept only internal */
	XML,
	/**
	 * Accept English or internal.
	 */
	SCRIPT
}
