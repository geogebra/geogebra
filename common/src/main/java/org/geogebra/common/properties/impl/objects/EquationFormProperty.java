package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.properties.impl.objects.delegate.EquationFormDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Equation form
 */
public class EquationFormProperty extends AbstractEnumerableProperty {

	private static final String[] equationFormNames = {
			"ImplicitLineEquation",
			"ExplicitLineEquation",
			"ParametricForm",
			"GeneralLineEquation",
			"InputForm"
	};

	private final GeoElementDelegate delegate;

	/***/
	public EquationFormProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Equation");
		delegate = new EquationFormDelegate(element);
		setValuesAndLocalize(equationFormNames);
	}

	@Override
	protected void setValueSafe(String value, int index) {
		GeoElement element = delegate.getElement();
		if (element instanceof GeoVec3D) {
			GeoVec3D vec3d = (GeoVec3D) element;
			vec3d.setMode(index);
			vec3d.updateRepaint();
		}
	}

	@Override
	public int getIndex() {
		return delegate.getElement().getToStringMode();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
