package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.EquationFormDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Equation form
 */
public class EquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private final GeoElementDelegate delegate;

	/***/
	public EquationFormProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Equation");
		delegate = new EquationFormDelegate(element);
		setNamedValues(
				List.of(
						entry(LinearEquationRepresentable.Form.IMPLICIT.rawValue,
								"ImplicitLineEquation"),
						entry(LinearEquationRepresentable.Form.EXPLICIT.rawValue,
								"ExplicitLineEquation"),
						entry(LinearEquationRepresentable.Form.PARAMETRIC.rawValue,
								"ParametricForm"),
						entry(LinearEquationRepresentable.Form.GENERAL.rawValue,
								"GeneralLineEquation"),
                               entry(LinearEquationRepresentable.Form.USER.rawValue, "InputForm")
		));
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (element instanceof GeoVec3D) {
			GeoVec3D vec3d = (GeoVec3D) element;
			vec3d.setMode(value);
			vec3d.updateRepaint();
		}
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getToStringMode();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
