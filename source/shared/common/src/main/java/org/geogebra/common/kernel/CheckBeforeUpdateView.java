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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * View that checks if it needs to update visual style for a specific property,
 * and if it will show a specific geo
 */
public interface CheckBeforeUpdateView extends View {

	/**
	 * 
	 * @param property
	 *            visual style property
	 * @return true if changes for this property needs update in AV
	 */
	boolean needsUpdateVisualstyle(GProperty property);

	/**
	 * 
	 * @param geo
	 *            geo
	 * @return true if geo is shown in view
	 */
	boolean show(GeoElement geo);

}
