/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LinearEquationFormDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Equation form property for objects described by linear equations (lines, planes).
 *
 * TODO there is a PlaneEqnModel, so it looks like planes need different handling
 *  &#8594; add code in this class, or even introduce a new property?
 */
public class LinearEquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

	public static final String NAME_KEY = "LinearEquationForm"; // TODO add to ggbTranslate

	private final AbstractGeoElementDelegate delegate;

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
		return delegate.getElement().isEuclidianVisible();
	}
}
