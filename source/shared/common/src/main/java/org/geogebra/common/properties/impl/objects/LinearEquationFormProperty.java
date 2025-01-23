package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LinearEquationFormDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Equation form property for objects described by linear equations (lines, planes).
 *
 * TODO there is a PlaneEqnModel, so it looks like planes need different handling
 *  -> add code in this class, or even introduce a new property?
 */
public class LinearEquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

	public static final String NAME_KEY = "LinearEquationForm"; // TODO add to ggbTranslate

	private final GeoElementDelegate delegate;

	/***/
	public LinearEquationFormProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, NAME_KEY);
		delegate = new LinearEquationFormDelegate(element);
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
						entry(LinearEquationRepresentable.Form.USER.rawValue,
								"InputForm")
				));
	}

	@Override
	protected void doSetValue(Integer value) {
		LinearEquationRepresentable.Form equationForm =
				LinearEquationRepresentable.Form.valueOf(value);
		GeoElement element = delegate.getElement();
		if (equationForm != null && element instanceof LinearEquationRepresentable) {
			((LinearEquationRepresentable) element).setEquationForm(equationForm);
			element.updateRepaint();
		}
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof LinearEquationRepresentable) {
			LinearEquationRepresentable.Form equationForm =
					((LinearEquationRepresentable) element).getEquationForm();
			if (equationForm != null) {
				return equationForm.rawValue;
			}
		}
		return -1;
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
