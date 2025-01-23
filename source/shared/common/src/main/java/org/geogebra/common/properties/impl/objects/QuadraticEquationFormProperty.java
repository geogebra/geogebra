package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.QuadraticEquationFormDelegate;

public class QuadraticEquationFormProperty extends AbstractNamedEnumeratedProperty<Integer> {

	public static final String NAME_KEY = "QuadraticEquationForm"; // TODO add to ggbTranslate

	private final GeoElementDelegate delegate;

	/***/
	public QuadraticEquationFormProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, NAME_KEY);
		delegate = new QuadraticEquationFormDelegate(element);

		QuadraticEquationRepresentable quadratic = (QuadraticEquationRepresentable) element;

		List<Map.Entry<Integer, String>> values = new ArrayList<>();
		if (quadratic.isSpecificFormPossible()) {
			values.add(entry(QuadraticEquationRepresentable.Form.SPECIFIC.rawValue,
					quadratic.getSpecificEquationLabelKey()));
		}
		if (quadratic.isExplicitFormPossible()) {
			values.add(entry(QuadraticEquationRepresentable.Form.EXPLICIT.rawValue,
					"ExplicitConicEquation"));
		}
		if (element.getDefinition() != null) {
			values.add(entry(QuadraticEquationRepresentable.Form.USER.rawValue,
					"InputForm"));
		}
		//if (quadratic.isImplicitFormPossible()) { // TODO always possible?
			values.add(entry(QuadraticEquationRepresentable.Form.IMPLICIT.rawValue,
					quadratic.getImplicitEquationLabelKey()));
		//}
		if (quadratic.isVertexFormPossible()) {
			values.add(entry(QuadraticEquationRepresentable.Form.VERTEX.rawValue,
					"ParabolaVertexForm"));
		}
		if (quadratic.isConicFormPossible()) {
			values.add(entry(QuadraticEquationRepresentable.Form.CONICFORM.rawValue,
					"ParabolaConicForm"));
		}
		if (quadratic.isParametricFormPossible()) {
			values.add(entry(QuadraticEquationRepresentable.Form.PARAMETRIC.rawValue,
					"ParametricForm"));
		}
		setNamedValues(values);
	}

	@Override
	protected void doSetValue(Integer value) {
		QuadraticEquationRepresentable.Form equationForm =
				QuadraticEquationRepresentable.Form.valueOf(value);
		GeoElement element = delegate.getElement();
		if (equationForm != null && element instanceof QuadraticEquationRepresentable) {
			((QuadraticEquationRepresentable) element).setEquationForm(equationForm);
			element.updateRepaint();
		}
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof QuadraticEquationRepresentable) {
			QuadraticEquationRepresentable.Form equationForm =
					((QuadraticEquationRepresentable) element).getEquationForm();
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
