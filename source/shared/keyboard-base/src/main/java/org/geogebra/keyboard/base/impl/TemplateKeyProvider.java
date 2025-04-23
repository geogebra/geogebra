package org.geogebra.keyboard.base.impl;

/**
 * Provides input sequences for template keys.
 */
public interface TemplateKeyProvider {
	/**
	 * @return input sequence for point key
	 */
	String getPointFunction();

	/**
	 * @return input sequence for vector key
	 */
	String getVectorFunction();
}
