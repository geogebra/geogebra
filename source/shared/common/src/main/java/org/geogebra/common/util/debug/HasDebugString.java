package org.geogebra.common.util.debug;

/**
 * Object providing a string for logging purposes (more detailed than toString).
 */
public interface HasDebugString {

	/**
	 * @return string representation for debugging purposes
	 */
	String getDebugString();
}
