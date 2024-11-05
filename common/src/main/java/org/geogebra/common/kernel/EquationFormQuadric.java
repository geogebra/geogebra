package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * Retrieve or set the equation form of objects described by conic/quadric equations.
 */
public interface EquationFormQuadric {

	/**
	 * @return The equation form of this object.
	 */
	@CheckForNull
	EquationForm.Quadric getEquationForm();

	/**
	 * Set the equation form of this object.
	 * @param equationForm the equation form. If {@code null}, this method has no effect.
	 */
	default void setEquationForm(@Nullable EquationForm.Quadric equationForm) {
		if (equationForm == null) {
			return;
		}
		setEquationForm(equationForm.rawValue);
	}

	/**
	 * Set the equation form.
	 * @param equationForm One of the raw values of the {@link EquationForm.Quadric} enum cases.
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
		setEquationForm(EquationForm.Quadric.IMPLICIT);
	}

	/**
	 * Set the equation form to {@code EXPLICIT}.
	 */
	default void setToExplicit() {
		setEquationForm(EquationForm.Quadric.EXPLICIT);
	}

	/**
	 * Set the equation form to {@code SPECIFIC}.
	 */
	default void setToSpecific() {
		setEquationForm(EquationForm.Quadric.SPECIFIC);
	}

	/**
	 * Set the equation form to {@code PARAMETRIC}.
	 * @param parameter The parameter name.
	 */
	void setToParametric(String parameter);

	/**
	 * Set the equation form to {@code USER}.
	 */
	default void setToUser() {
		setEquationForm(EquationForm.Quadric.USER);
	}

	/**
	 * Set the equation form to {@code VERTEX}.
	 */
	default void setToVertex() {
		setEquationForm(EquationForm.Quadric.VERTEX);
	}

	/**
	 * Set the equation form to {@code CONIC}.
	 */
	default void setToConic() {
		setEquationForm(EquationForm.Quadric.CONICFORM);
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
