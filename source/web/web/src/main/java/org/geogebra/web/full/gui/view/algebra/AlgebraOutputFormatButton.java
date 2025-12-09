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

package org.geogebra.web.full.gui.view.algebra;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.resources.client.ResourcePrototype;

public class AlgebraOutputFormatButton extends StandardButton {

	/**
	 * Default constructor
	 */
	public AlgebraOutputFormatButton() {
		super(24);
		addStyleName("symbolicButton");
	}

	/**
	 * Selects on the three different button states and updates the icon
	 * @param format Index
	 */
	public void select(@Nonnull AlgebraOutputFormat format) {
		setIcon(getIconFor(format));
		Dom.toggleClass(this, "show-fraction",
				format == AlgebraOutputFormat.FRACTION);
	}

	private ResourcePrototype getIconFor(AlgebraOutputFormat format) {
		MaterialDesignResources resources = MaterialDesignResources.INSTANCE;
		switch (format) {
		case FRACTION:
			return resources.fraction_white();
		case APPROXIMATION:
			return resources.modeToggleSymbolic();
		case ENGINEERING:
			return resources.engineering_notation_white();
		case EXACT:
		default:
			return resources.equal_sign_white();
		}
	}

}
