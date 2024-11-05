package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * Retrieve or set the equation form of objects described by linear equations.
 */
public interface EquationFormLinear {

	/**
	 * @return The equation form of this object.
	 */
	@CheckForNull
	EquationForm.Linear getEquationForm();

	/**
	 * Set the equation form of this object.
	 * @param equationForm the equation form. If {@code null}, this method has no effect.
	 */
	default void setEquationForm(@Nullable EquationForm.Linear equationForm) {
		if (equationForm == null) {
			return;
		}
		setEquationForm(equationForm.rawValue);
	}

	/**
	 * Set the equation form.
	 * @param equationForm One of the raw values of the {@link EquationForm.Linear} enum cases.
	 * @apiNote Any {@link org.geogebra.common.kernel.geos.GeoElement GeoElement} subclass
	 * implementing this interface is expected to store the passed-in value in
	 * {@link org.geogebra.common.kernel.geos.GeoElement GeoElement}'s {@code toStringMode}
	 * field.
	 */
	void setEquationForm(int equationForm);

	/**
	 * Set the equation form to {@code IMPLICIT}.
	 */
	default void setToImplicit() {
		setEquationForm(EquationForm.Linear.IMPLICIT);
	}

	/**
	 * Set the equation form to {@code EXPLICIT}.
	 */
	default void setToExplicit() {
		setEquationForm(EquationForm.Linear.EXPLICIT);
	}

	/**
	 * Set the equation form to {@code PARAMETRIC}.
	 * @param parameter The parameter name.
	 */
	void setToParametric(String parameter);

	/**
	 * Set the equation form to {@code GENERAL}.
	 */
	default void setToGeneral() {
		setEquationForm(EquationForm.Linear.GENERAL);
	}

	/**
	 * Set the equation form to {@code USER}.
	 */
	default void setToUser() {
		setEquationForm(EquationForm.Linear.USER);
	}

	/**
	 * Set the equation form from a string value coming from XML.
	 * @param equationForm
	 *            equation form (e.g., "implicit", "explicit", "user")
	 * @param parameter
	 *            parameter name
	 * @return whether equation form is valid for this object type
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
