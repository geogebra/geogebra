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

package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.GroupedEnumeratedProperty;

/**
 * Base class for enumerated properties that can be grouped. When overriding this class,
 * make sure to call {@link AbstractGroupedEnumeratedProperty#setGroupDividerIndices(int[])}
 * to set the indices of the dividers.
 * @param <V> value type
 */
public abstract class AbstractGroupedEnumeratedProperty<V>
		extends AbstractNamedEnumeratedProperty<V>
		implements GroupedEnumeratedProperty<V> {

	private int[] groupDividerIndices = new int[0];

	/**
	 * Constructs an AbstractGroupedEnumeratedProperty
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractGroupedEnumeratedProperty(Localization localization,
			String name) {
		super(localization, name);
	}

	/**
	 * Set the group divider indices. For the format of this array,
	 * see {@link GroupedEnumeratedProperty#getGroupDividerIndices()}.
	 * @param groupDividerIndices group divider indices
	 */
	protected void setGroupDividerIndices(int[] groupDividerIndices) {
		this.groupDividerIndices = groupDividerIndices;
	}

	@Override
	public int[] getGroupDividerIndices() {
		return groupDividerIndices;
	}
}
