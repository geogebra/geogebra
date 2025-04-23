package org.geogebra.common.kernel.geos;

/**
 * Construction element that provides short text for screen reader.
 */
public interface HasAuralText {

	/**
	 * @return short aural text for altText reader (excluding the list of actions)
	 */
	String getAuralText();
}
