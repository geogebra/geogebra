package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * An umbrella for objects representable by quadratic equations (e.g., conics, quadrics).
 */
public interface QuadraticEquationRepresentable {

	/**
	 * Equation forms for quadratic equations.
	 */
	enum Form {
		/** ax^2+bxy+cy^2+dx+ey+f=0 */
		IMPLICIT(Form.CONST_IMPLICIT),
		/** y=ax^2+bx+c */
		EXPLICIT(Form.CONST_EXPLICIT),
		/** (x-m)^2/a^2+(y-n)^2/b^2=1 */
		SPECIFIC(Form.CONST_SPECIFIC),
		/** X=(1,1)+(sin(t),cos(t)) */
		PARAMETRIC(Form.CONST_PARAMETRIC),
		/** user input form */
		USER(Form.CONST_USER),
		/** vertex form */
		VERTEX(Form.CONST_VERTEX),
		/** conic form */
		CONICFORM(Form.CONST_CONICFORM);

		// These constants are provided for use in case statements.
		// (values originally defined in GeoConicND).
		public static final int CONST_IMPLICIT = 0;
		public static final int CONST_EXPLICIT = 1;
		public static final int CONST_SPECIFIC = 2;
		public static final int CONST_PARAMETRIC = 3;
		public static final int CONST_USER = 4;
		public static final int CONST_VERTEX = 5;
		public static final int CONST_CONICFORM = 6;

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
			case CONST_SPECIFIC:
				return SPECIFIC;
			case CONST_PARAMETRIC:
				return PARAMETRIC;
			case CONST_USER:
				return USER;
			case CONST_VERTEX:
				return VERTEX;
			case CONST_CONICFORM:
				return CONICFORM;
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
	 * Set the equation form to {@code SPECIFIC}.
	 */
	default void setToSpecific() {
		setEquationForm(Form.SPECIFIC);
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
		setEquationForm(Form.USER);
	}

	/**
	 * Set the equation form to {@code VERTEX}.
	 */
	default void setToVertex() {
		setEquationForm(Form.VERTEX);
	}

	/**
	 * Set the equation form to {@code CONIC}.
	 */
	default void setToConic() {
		setEquationForm(Form.CONICFORM);
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
