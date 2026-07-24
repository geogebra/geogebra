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

package org.geogebra.web.full.gui.components;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SVGResourcePrototype;

public class ComponentChip extends StandardButton {

	/**
	 * Creates a chip component.
	 * @param text of chips
	 * @param icon of chips
	 * @param primaryStyle whether neutral or primary style
	 * @param handler on click handler
	 */
	public ComponentChip(String text, SVGResource icon, boolean primaryStyle,
			Runnable handler) {
		super(icon != null ? icon.withFill(GeoGebraColorConstants.NEUTRAL_800.toString()) : null,
				text, 16);
		addFastClickHandler(source -> handler.run());
		styleButton(icon != null, primaryStyle);
	}

	private void styleButton(boolean hasIcon, boolean primaryStyle) {
		addStyleName("componentChips");
		addStyleName("ripple");
		Dom.toggleClass(this, "primary", primaryStyle);
		Dom.toggleClass(this, "withIcon", hasIcon);
	}

	@Override
	public void setEnabled(boolean enabled) {
		Dom.toggleClass(this, "disabled", !enabled);
		AriaHelper.setDisabled(this, !enabled);
		if (getIcon() instanceof SVGResourcePrototype svg) {
			setResource(svg.withFill(enabled ? GeoGebraColorConstants.NEUTRAL_800.toString()
					: GeoGebraColorConstants.NEUTRAL_500.toString()));
		}
	}
}
