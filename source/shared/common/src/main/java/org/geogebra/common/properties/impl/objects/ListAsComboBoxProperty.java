package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class ListAsComboBoxProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoList element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public ListAsComboBoxProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "DrawAsDropDownList");
		if (!(element instanceof GeoList)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoList) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setDrawAsComboBox(value);
		element.updateRepaint();
	}

	@Override
	public Boolean getValue() {
		return element.drawAsComboBox();
	}
}

