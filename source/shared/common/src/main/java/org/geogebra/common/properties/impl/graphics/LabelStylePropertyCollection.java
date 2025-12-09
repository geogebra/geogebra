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

package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class LabelStylePropertyCollection
		extends AbstractPropertyCollection<IconAssociatedProperty> {

	/**
	 * Constructs a label style property collection (bold, italic, serif).
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 */
	public LabelStylePropertyCollection(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "LabelStyle");

		ArrayList<IconAssociatedProperty> properties = new ArrayList<>();
		properties.add(new AxesLabelBoldProperty(localization, euclidianSettings));
		properties.add(new AxesLabelItalicProperty(localization, euclidianSettings));
		properties.add(new AxesLabelSerifProperty(localization, euclidianSettings));
		setProperties(properties.toArray(new IconAssociatedProperty[0]));
	}
}
