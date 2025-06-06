package org.geogebra.common.properties.impl.collections;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;

/**
 * Handles a collection of ColorProperty objects as a single ColorProperty.
 */
public class ColorPropertyCollection<T extends ElementColorProperty>
		extends EnumeratedPropertyCollection<T, GColor> implements ColorProperty {

	/**
	 * @param properties properties to handle
	 */
	public ColorPropertyCollection(List<T> properties) {
		super(properties);
	}

	@Override
	public @Nonnull List<GColor> getValues() {
		return getFirstProperty().getValues();
	}
}
