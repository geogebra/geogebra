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

package org.geogebra.common.properties.aliases;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.ConstrainedProperty;

/**
 * A property that is represented as a String.
 */
public interface StringProperty extends ConstrainedProperty<String> {
	/**
	 * @return {@code true} if the property should be displayed as a text area (see
	 * {@link org.geogebra.common.properties.PropertyView.TextArea}, {@code false} otherwise (see
	 * {@link org.geogebra.common.properties.PropertyView.TextField} and
     * {@link org.geogebra.common.properties.PropertyView.ComboBox}).
	 */
	default boolean isDisplayedAsTextArea() {
		return false;
	}

	/**
	 * @return {@code true} if the property should be displayed with a math editor,
	 * {@code false} if it should be displayed as plain text.
	 */
	default boolean isDisplayedInMathFormat() {
		return false;
	}

	/**
	 * Returns a label used as aria-title and data-title.
	 * It is defined only in specific cases, null otherwise.
	 * @return translation key of an accessibility label.
	 */
	default @CheckForNull String getAriaLabel() {
		return null;
	}
}
