package org.geogebra.common.kernel.commands.redefinition;

import org.geogebra.common.plugin.GeoClass;

/**
 * Describing redefinition rules for geo element types.
 */
public interface RedefinitionRule {

	/**
	 * Applies the redefinition rule for these types.
	 *
	 * @param fromType redefine from class
	 * @param toType to class
	 * @return true if redefinition is allowed, false otherwise
	 */
	boolean allowed(GeoClass fromType, GeoClass toType);
}
