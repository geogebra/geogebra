package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * {@code Property} responsible for setting if an object is auxiliary.
 */
public class AuxiliaryObjectProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final GeoElement geoElement;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public AuxiliaryObjectProperty(Localization localization, GeoElement geoElement) {
		super(localization, "AuxiliaryObject");
		this.geoElement = geoElement;
	}

	@Override
	public Boolean getValue() {
		return geoElement.isAuxiliaryObject();
	}

	@Override
	protected void doSetValue(Boolean value) {
		geoElement.setAuxiliaryObject(value);
		geoElement.getApp().updateGuiForShowAuxiliaryObjects();
	}
}
