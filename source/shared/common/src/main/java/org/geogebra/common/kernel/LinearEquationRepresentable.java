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

package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;

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
		public static final int CONST_GENERAL = 4;
		public static final int CONST_USER = 5;

		public final int rawValue;

		/**
		 * Map raw values to enum cases.
		 * @param rawValue An integer.
		 * @return The enum case whose rawValue matches the passed-in rawValue, or {@code null}
		 * if no such enum case was found.
		 */
		public static @CheckForNull Form valueOf(int rawValue) {
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
	@CheckForNull Form getEquationForm();

	/**
	 * Set the equation form of this object.
	 * @param equationForm the equation form. If {@code null}, this method has no effect.
	 */
	void setEquationForm(@CheckForNull Form equationForm);

	/**
	 * Set the equation form to {@code IMPLICIT}.
	 */
	default void setToImplicitForm() {
		setEquationForm(Form.IMPLICIT);
	}

	/**
	 * Set the equation form to {@code EXPLICIT}.
	 */
	default void setToExplicitForm() {
		setEquationForm(Form.EXPLICIT);
	}

	/**
	 * Set the equation form to {@code PARAMETRIC}.
	 * @param parameter The parameter name.
	 */
	void setToParametricForm(String parameter);

	/**
	 * Set the equation form to {@code GENERAL}.
	 */
	default void setToGeneralForm() {
		setEquationForm(Form.GENERAL);
	}

	/**
	 * Set the equation form to {@code USER}.
	 */
	default void setToUserForm() {
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
		switch (equationForm) {
			case "implicit":
				setToImplicitForm();
				break;
			case "explicit":
				setToExplicitForm();
				break;
			case "parametric":
				setToParametricForm(parameter);
				break;
			case "general":
				setToGeneralForm();
				break;
			case "user":
				setToUserForm();
				break;
			default:
				return false;
		}
		return true;
	}
}
