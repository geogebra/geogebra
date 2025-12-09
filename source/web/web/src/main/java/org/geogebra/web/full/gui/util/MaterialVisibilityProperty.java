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

package org.geogebra.web.full.gui.util;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.util.debug.Log;

public class MaterialVisibilityProperty
		extends AbstractNamedEnumeratedProperty<MaterialVisibility> {
	private MaterialVisibility materialVisibility;

	/**
	 * Constructs an AbstractEnumerableProperty
	 * @param localization the localization used
	 */
	public MaterialVisibilityProperty(Localization localization) {
		super(localization, "");
		update(MaterialVisibility.Private);
	}

	@Override
	protected void doSetValue(MaterialVisibility value) {
		materialVisibility = value;
	}

	@Override
	public MaterialVisibility getValue() {
		return materialVisibility;
	}

	/**
	 * Update property due to visibility
	 * @param visibility to update on.
	 */
	public void update(MaterialVisibility visibility) {
		Log.debug("Property is updated to " + visibility);
		if (visibility == MaterialVisibility.Public) {
			setNamedValues(List.of(
					entry(MaterialVisibility.Private, "Private"),
					entry(MaterialVisibility.Shared, "Shared"),
					entry(MaterialVisibility.Public, "Public")
			));
		} else {
			setNamedValues(List.of(
					entry(MaterialVisibility.Private, "Private"),
					entry(MaterialVisibility.Shared, "Shared")
			));
		}
		doSetValue(visibility);
	}
}
