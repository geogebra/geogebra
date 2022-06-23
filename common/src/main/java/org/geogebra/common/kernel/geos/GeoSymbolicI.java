package org.geogebra.common.kernel.geos;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Common interface for CAS cells and symbolic geos in AV
 *
 * @author Zbynek
 *
 */
public interface GeoSymbolicI {

	/**
	 * @param key
	 *            error message translation key
	 */
	void setError(String key);

	/**
	 * @param assignmentType
	 *            the {@link AssignmentType} to set
	 */
	void setAssignmentType(AssignmentType assignmentType);

	/**
	 * Computes the output of this CAS cell based on its current input settings.
	 * Note that this will also change a corresponding twinGeo.
	 */
	void computeOutput();

	/**
	 * @return computed expression
	 */
	@Nullable
	ExpressionValue getValue();

}
