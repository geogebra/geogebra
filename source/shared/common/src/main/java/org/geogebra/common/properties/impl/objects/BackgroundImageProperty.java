package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class BackgroundImageProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoImage element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public BackgroundImageProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "BackgroundImage");
		if (!(element instanceof GeoImage)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoImage) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setInBackground(value);
		element.updateRepaint();
	}

	@Override
	public Boolean getValue() {
		return element.isInBackground();
	}
}
