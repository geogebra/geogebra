package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * An umbrella for objects representable by linear equations (e.g., lines, planes).
 */
public interface LinearEquationRepresentable {

	/**
	 * Equation forms for linear equations.
	 */
	enum Form {
		/** implicit equation a x + b y = c */
		IMPLICIT(Form.CONST_IMPLICIT),
		/** explicit equation y = m x + b */
		EXPLICIT(Form.CONST_EXPLICIT),
		/** parametric equation */
		PARAMETRIC(Form.CONST_PARAMETRIC),
		/** general form a x + b y + c = 0 (GGB-1212) */
		GENERAL(Form.CONST_GENERAL),
		/** user input form */
		USER(Form.CONST_USER);

		// These constants are provided for use in case statements.
		// (values originally defined in GeoLine).
		public static final int CONST_IMPLICIT = 0;
		public static final int CONST_EXPLICIT = 1;
		public static final int CONST_PARAMETRIC = 2;
		// TODO APPS-5867 This constant is only left in here (for now) because some code in GeoLine
		//  (e.g., toValueString) uses it in switch cases. However, the numeric value (=3) in these
		//  use cases is just the *default value* of GeoElement.toStringMode, which is initialized
		//  to Kernel.COORD_CARTESIAN (also 3). So, it doesn't make sense, this is purely
		//  coincidental, but I left it in to not break existing behaviour in this first stage of
		//  equation form cleanup.
		public static final int CONST_IMPLICIT_NON_CANONICAL = 3;
		public static final int CONST_GENERAL = 4;
		public static final int CONST_USER = 5;

		public final int rawValue;

		/**
		 * Map raw values to enum cases.
		 * @param rawValue An integer.
		 * @return The enum case whose rawValue matches the passed-in rawValue, or {@code null}
		 * if no such enum case was found.
		 */
		@CheckForNull
		public static Form valueOf(int rawValue) {
			switch (rawValue) {
			case CONST_IMPLICIT:
				return IMPLICIT;
			case CONST_EXPLICIT:
				return EXPLICIT;
			case CONST_PARAMETRIC:
				return PARAMETRIC;
			case CONST_GENERAL:
				return GENERAL;
			case CONST_USER:
				return USER;
			default:
				return null;
			}
		}

		Form(int rawValue) {
			this.rawValue = rawValue;
		}
	}

	/**
	 * @return The equation form of this object.
	 */
	@CheckForNull
	Form getEquationForm();

	/**
	 * Set the equation form of this object.
	 * @param equationForm the equation form. If {@code null}, this method has no effect.
	 */
	default void setEquationForm(@Nullable Form equationForm) {
		if (equationForm == null) {
			return;
		}
		setEquationForm(equationForm.rawValue);
	}

	/**
	 * Set the equation form.
	 * @param equationForm One of the raw values of the {@link Form} enum cases.
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
		setEquationForm(Form.IMPLICIT);
	}

	/**
	 * Set the equation form to {@code EXPLICIT}.
	 */
	default void setToExplicit() {
		setEquationForm(Form.EXPLICIT);
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
		setEquationForm(Form.GENERAL);
	}

	/**
	 * Set the equation form to {@code USER}.
	 */
	default void setToUser() {
		setEquationForm(Form.USER);
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
