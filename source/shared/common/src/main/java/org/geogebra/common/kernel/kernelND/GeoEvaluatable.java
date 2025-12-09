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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.Evaluatable;

/**
 * Geos that can be used as R -&gt; R functions.
 */
public interface GeoEvaluatable extends GeoElementND, Evaluatable {
	/**
	 * @return table index
	 */
	public int getTableColumn();

	/**
	 * @param column
	 *            TV column
	 */
	public void setTableColumn(int column);

	/**
	 * @param pointsVisible
	 *            whether TV points are visible
	 */
	public void setPointsVisible(boolean pointsVisible);

	/**
	 * @return whether TV points are visible
	 */
	public boolean isPointsVisible();

}
