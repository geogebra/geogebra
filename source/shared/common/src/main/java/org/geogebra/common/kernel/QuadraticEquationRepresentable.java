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
		public static @CheckForNull Form valueOf(int rawValue) {
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
	 * @return the localization key (e.g., "ImplicitConicEquation") for the implicit equation
	 * label for this object (the labels may be different depending on the object type).
	 */
	String getImplicitEquationLabelKey();

	/**
	 * @return {@code true} if this object has an explicit form (at this moment; may depend
	 * on some properties at run-time).
	 */
	boolean isExplicitFormPossible();

	/**
	 * Set the equation form to {@code EXPLICIT}.
	 */
	default void setToExplicit() {
		setEquationForm(Form.EXPLICIT);
	}

	/**
	 * @return {@code true} if this object has a specific form (at this moment; may depend
	 * on some properties at run-time).
	 */
	boolean isSpecificFormPossible();

	/**
	 * Set the equation form to {@code SPECIFIC}.
	 */
	default void setToSpecificForm() {
		setEquationForm(Form.SPECIFIC);
	}

	/**
	 * @return the localization key (e.g., "SphereEquation") for the implicit equation
	 * label for this object (the labels may be different depending on the object type).
	 */
	String getSpecificEquationLabelKey();

	/**
	 * @return {@code true} if this object has a parametric form (at this moment; may depend
	 * on some properties at run-time).
	 */
	boolean isParametricFormPossible();

	/**
	 * Set the equation form to {@code PARAMETRIC}.
	 * @param parameter The parameter name.
	 */
	void setToParametricForm(String parameter);

	/**
	 * Set the equation form to {@code USER} (user / input form).
	 */
	default void setToUserForm() {
		setEquationForm(Form.USER);
	}

	/**
	 * @return {@code true} if this object has a vertex form (at this moment; may depend
	 * on some properties at run-time).
	 */
	boolean isVertexFormPossible();

	/**
	 * Set the equation form to {@code VERTEX}.
	 */
	default void setToVertexForm() {
		setEquationForm(Form.VERTEX);
	}

	/**
	 * @return {@code true} if this object has a conic form (at this moment; may depend
	 * on some properties at run-time).
	 */
	boolean isConicFormPossible();

	/**
	 * Set the equation form to {@code CONIC}.
	 */
	default void setToConicForm() {
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
		switch (equationForm) {
			case "implicit":
				setToImplicitForm();
				break;
			case "specific":
				setToSpecificForm();
				break;
			case "explicit":
				setToExplicit();
				break;
			case "parametric":
				setToParametricForm(parameter);
				break;
			case "user":
				setToUserForm();
				break;
			case "vertex":
				setToVertexForm();
				break;
			case "conic":
				setToConicForm();
				break;
			default:
				return false;
		}
		return true;
	}
}
