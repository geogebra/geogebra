package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public interface EquationFormLinear {

	@CheckForNull
	EquationForm.Linear getEquationForm();

	default void setEquationForm(@Nullable EquationForm.Linear equationForm) {
		if (equationForm == null) {
			return;
		}
		setEquationForm(equationForm.rawValue);
	}

	void setEquationForm(int toStringMode);

	default void setToImplicit() {
		setEquationForm(EquationForm.Linear.IMPLICIT);
	}

	default void setToExplicit() {
		setEquationForm(EquationForm.Linear.EXPLICIT);
	}

	void setToParametric(String parameter);

	default void setToGeneral() {
		setEquationForm(EquationForm.Linear.GENERAL);
	}

	default void setToUser() {
		setEquationForm(EquationForm.Linear.USER);
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
		} else if ("explicit".equals(equationForm)) {
			setToExplicit();
		} else if ("parametric".equals(equationForm)) {
			setToParametric(parameter);
		} else if ("general".equals(equationForm)) {
			setToGeneral();
		} else if ("user".equals(equationForm)) {
			setToUser();
		} else {
			return false;
		}
		return true;
	}
}
