package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public interface EquationFormQuadric {

	@CheckForNull
	EquationForm.Quadric getEquationForm();

	default void setEquationForm(@Nullable EquationForm.Quadric equationForm) {
		if (equationForm == null) {
			return;
		}
		setEquationForm(equationForm.rawValue);
	}

	void setEquationForm(int toStringMode);

	default void setToImplicit() {
		setEquationForm(EquationForm.Quadric.IMPLICIT);
	}

	default void setToExplicit() {
		setEquationForm(EquationForm.Quadric.EXPLICIT);
	}

	default void setToSpecific() {
		setEquationForm(EquationForm.Quadric.SPECIFIC);
	}

	void setToParametric(String parameter);

	default void setToUser() {
		setEquationForm(EquationForm.Quadric.USER);
	}

	default void setToVertex() {
		setEquationForm(EquationForm.Quadric.VERTEX);
	}

	default void setToConic() {
		setEquationForm(EquationForm.Quadric.CONICFORM);
	}

	/**
	 * Set the equation form from a string value coming from XML.
	 * @param equationForm
	 *            equation form (e.g., "implicit", "explicit", "user")
	 * @param parameter
	 *            parameter name
	 * @return whether equation form is valid for this objecttype
	 */
	default boolean setEquationFormFromXML(String equationForm, String parameter) {
		if ("implicit".equals(equationForm)) {
			setToImplicit();
		} else if ("specific".equals(equationForm)) {
			setToSpecific();
		} else if ("explicit".equals(equationForm)) {
			setToExplicit();
		} else if ("parametric".equals(equationForm)) {
			setToParametric(parameter);
		} else if ("user".equals(equationForm)) {
			setToUser();
		} else if ("vertex".equals(equationForm)) {
			setToVertex();
		} else if ("conic".equals(equationForm)) {
			setToConic();
		} else {
			return false;
		}
		return true;
	}
}
