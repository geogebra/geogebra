package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.ColorProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;

/**
 * Handles a collection of ColorProperty objects as a single ColorProperty.
 */
public class ColorPropertyCollection<T extends ElementColorProperty>
		extends AbstractTypedPropertyCollection<T, GColor> implements ColorProperty {

	/**
	 * @param properties properties to handle
	 */
	public ColorPropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public GColor[] getValues() {
		return getFirstProperty().getValues();
	}
}
