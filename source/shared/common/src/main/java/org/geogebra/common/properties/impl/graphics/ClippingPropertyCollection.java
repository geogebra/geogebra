package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class ClippingPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs a clipping property collection.
	 * @param localization localization for the title
	 * @param euclidianView EV view
	 */
	public ClippingPropertyCollection(Localization localization,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "Clipping");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new UseClippingBooleanProperty(localization,
				(EuclidianView3D) euclidianView));
		properties.add(new ShowClippingBooleanProperty(localization,
				(EuclidianView3D) euclidianView));
		properties.add(new ClippingBoxSizeProperty(localization,
				((EuclidianView3D) euclidianView).getSettings()));
		setProperties(properties.toArray(new Property[0]));
	}
}
