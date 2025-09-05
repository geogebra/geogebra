package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class SymbolicValueProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final HasSymbolicMode element;

	/**
	 * @param localization this is used to localize the name
	 * @param element construction element
	 */
	public SymbolicValueProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Symbolic");
		if (!(element instanceof HasSymbolicMode)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (HasSymbolicMode) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setSymbolicMode(value, true);
	}

	@Override
	public Boolean getValue() {
		return element.isSymbolicMode();
	}
}
