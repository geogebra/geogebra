/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import javax.annotation.CheckForNull;

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
	@CheckForNull ExpressionValue getValue();

}
