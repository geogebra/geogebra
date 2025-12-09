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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.QuadraticEquationFormDelegate;

public class QuadraticEquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

	public static final String NAME_KEY = "QuadraticEquationForm"; // TODO add to ggbTranslate

	private final AbstractGeoElementDelegate delegate;

	/***/
	public QuadraticEquationFormProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, NAME_KEY);
		delegate = new QuadraticEquationFormDelegate(element);

		QuadraticEquationRepresentable firstQuadratic =
				findFirstQuadraticEquationRepresentable(element);
		if (firstQuadratic == null) {
			setNamedValues(List.of());
			return;
		}

		List<Map.Entry<Integer, String>> values = new ArrayList<>();
		if (allMatch(element, QuadraticEquationRepresentable::isSpecificFormPossible)) {
			values.add(entry(QuadraticEquationRepresentable.Form.SPECIFIC.rawValue,
					firstQuadratic.getSpecificEquationLabelKey()));
		}
		if (allMatch(element, QuadraticEquationRepresentable::isExplicitFormPossible)) {
			values.add(entry(QuadraticEquationRepresentable.Form.EXPLICIT.rawValue,
					"ExplicitConicEquation"));
		}
		if (element.getDefinition() != null) {
			values.add(entry(QuadraticEquationRepresentable.Form.USER.rawValue, "InputForm"));
		}
		//if (quadratic.isImplicitFormPossible()) { // TODO always possible?
		values.add(entry(QuadraticEquationRepresentable.Form.IMPLICIT.rawValue,
				firstQuadratic.getImplicitEquationLabelKey()));
		//}
		if (allMatch(element, QuadraticEquationRepresentable::isVertexFormPossible)) {
			values.add(entry(QuadraticEquationRepresentable.Form.VERTEX.rawValue,
					"ParabolaVertexForm"));
		}
		if (allMatch(element, QuadraticEquationRepresentable::isConicFormPossible)) {
			values.add(entry(QuadraticEquationRepresentable.Form.CONICFORM.rawValue,
					"ParabolaConicForm"));
		}
		if (allMatch(element, QuadraticEquationRepresentable::isParametricFormPossible)) {
			values.add(entry(QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue,
					"ParametricForm"));
		}
		setNamedValues(values);
	}

	@Override
	protected void doSetValue(Integer value) {
		QuadraticEquationRepresentable.Form equationForm =
				QuadraticEquationRepresentable.Form.valueOf(value);
		if (equationForm == null) {
			return;
		}
		GeoElement element = delegate.getElement();
		setEquationForm(element, equationForm);
	}

	private void setEquationForm(
			GeoElement geoElement, QuadraticEquationRepresentable.Form equationForm) {
		if (geoElement instanceof QuadraticEquationRepresentable) {
			((QuadraticEquationRepresentable) geoElement).setEquationForm(equationForm);
			geoElement.updateRepaint();
		} else if (geoElement instanceof GeoList) {
			GeoList geoList = (GeoList) geoElement;
			geoList.elements().forEach(element -> setEquationForm(element, equationForm));
		}
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		QuadraticEquationRepresentable firstQuadraticEquationRepresentable =
				findFirstQuadraticEquationRepresentable(element);
		if (firstQuadraticEquationRepresentable == null) {
			return -1;
		}
		QuadraticEquationRepresentable.Form firstQuadraticEquationRepresentableForm =
				firstQuadraticEquationRepresentable.getEquationForm();
		if (firstQuadraticEquationRepresentableForm == null) {
			return -1;
		}
		return firstQuadraticEquationRepresentableForm.rawValue;
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}

	private boolean allMatch(
			GeoElement geoElement, Predicate<QuadraticEquationRepresentable> predicate) {
		if (geoElement instanceof QuadraticEquationRepresentable) {
			QuadraticEquationRepresentable quadratic = (QuadraticEquationRepresentable) geoElement;
			return predicate.test(quadratic);
		} else if (geoElement instanceof GeoList) {
			GeoList geoList = (GeoList) geoElement;
			return geoList.elements().allMatch(element -> allMatch(element, predicate));
		}
		return false;
	}

	private static QuadraticEquationRepresentable findFirstQuadraticEquationRepresentable(
			GeoElement geoElement) {
		if (geoElement instanceof QuadraticEquationRepresentable) {
			return (QuadraticEquationRepresentable) geoElement;
		} else if (geoElement instanceof GeoList) {
			GeoList geoList = (GeoList) geoElement;
			return geoList.elements()
					.map(QuadraticEquationFormProperty::findFirstQuadraticEquationRepresentable)
					.filter(Objects::nonNull)
					.findFirst().orElse(null);
		}
		return null;
	}
}
