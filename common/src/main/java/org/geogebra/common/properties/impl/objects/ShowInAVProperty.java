package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * Show in AV
 */
public class ShowInAVProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {

	private final GeoElement element;

	/***/
	public ShowInAVProperty(Localization localization, GeoElement element) {
		super(localization, "ShowInAlgebraView");
		this.element = element;
	}

	@Override
	public Boolean getValue() {
		return !element.isAuxiliaryObject();
	}

	@Override
	public void doSetValue(Boolean show) {
		element.setAuxiliaryObject(!show);
		element.updateRepaint();

		App app = element.getApp();
		app.updateGuiForShowAuxiliaryObjects();
	}
}
