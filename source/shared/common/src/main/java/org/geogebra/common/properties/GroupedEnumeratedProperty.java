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

package org.geogebra.common.properties;

/**
 * Enumerated property where groups of values might be separated by dividers.
 */
public interface GroupedEnumeratedProperty<V> extends EnumeratedProperty<V> {

	/**
	 * Gets the array of indices where a divider must be inserted.
	 * Please note that indices refer to the unmodified values array.
	 * The array is sorted in ascending order.
	 * <p>
	 * For example, with values {"a", "b", "c", "d", "e"}, if this method returns
	 * {1, 3}, it must be understood as the following {"a", |, "b", "c", |, "d", "e"}
	 * @return a sorted array of indices where a divider must be inserted
	 */
	int[] getGroupDividerIndices();
}
