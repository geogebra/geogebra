package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class EmphasizeRightAngleProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoAngle element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public EmphasizeRightAngleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "EmphasizeRightAngle");
		if (!(element instanceof GeoAngle)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoAngle) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setEmphasizeRightAngle(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return element.isEmphasizeRightAngle();
	}
}
