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

package org.geogebra.editor.share.util;

import org.geogebra.editor.share.tree.FunctionNode;
import org.jspecify.annotations.NonNull;

/**
 * Part of a piecewise function that reacts to the pointer.
 * @param piecewise piecewise function
 * @param type kind of control
 * @param caseIndex index of the case that the control belongs to; unused for
 * {@link Type#ADD_CASE}
 */
public record PiecewiseControl(
		@NonNull FunctionNode piecewise, @NonNull Type type, int caseIndex) {

	/** Translation key and English text for the label of the button that adds a case. */
	public static final String ADD_CASE_KEY = "AddCase";

	public static final String ADD_CASE_DEFAULT = "Add case";

	/** Kinds of controls. */
	public enum Type {
		/** Row of a case; hovering it reveals the button that removes the case. */
		CASE,
		/** Button that removes a case. */
		REMOVE_CASE,
		/** Button that appends a case. */
		ADD_CASE
	}

	/**
	 * @return whether this is the button that appends a case
	 */
	public boolean isAddCase() {
		return type == Type.ADD_CASE;
	}
}
