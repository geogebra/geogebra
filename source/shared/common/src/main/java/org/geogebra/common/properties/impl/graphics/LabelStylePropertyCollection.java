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
