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

package org.geogebra.common.gui.toolcategorization;

/**
 * Filters ToolCollections.
 */
public interface ToolCollectionFilter {

	/**
	 * Filter tools by ID.
	 *
	 * @param tool the id of the tool (aka mode). See the constants in
	 * {@link org.geogebra.common.euclidian.EuclidianConstants EuclidianConstants} starting
	 * with "MODE_" for a list of valid values.
	 * @return true if the tool should be included.
	 */
	boolean isIncluded(int tool);
}
