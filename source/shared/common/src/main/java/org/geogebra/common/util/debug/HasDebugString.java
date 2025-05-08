package org.geogebra.common.util.debug;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Object providing a string for logging purposes (more detailed than toString).
 */
public interface HasDebugString {
	@MissingDoc
	String getDebugString();
}
