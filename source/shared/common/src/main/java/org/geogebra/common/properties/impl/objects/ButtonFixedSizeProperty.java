package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class ButtonFixedSizeProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {

	private final GeoButton element;

	/**
	 * @param loc localization
	 * @param element construction element
	 * @throws NotApplicablePropertyException if element is not a button
	 */
	public ButtonFixedSizeProperty(Localization loc, GeoElement element)
			throws NotApplicablePropertyException {
		super(loc, "FixedSize");
		if (!(element instanceof GeoButton) || element instanceof GeoInputBox) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoButton) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setFixedSize(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return element.isFixedSize();
	}
}
