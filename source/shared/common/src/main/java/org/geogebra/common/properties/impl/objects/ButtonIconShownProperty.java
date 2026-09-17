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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.util.ImageManager;

/** {@code Property} responsible for controlling whether to show an icon for the button. */
public class ButtonIconShownProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, GeoElementDependentProperty {
	private final ImageManager imageManager;
	private final GeoElement geoElement;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for property labels
	 * @param imageManager image manager for resolving preset button icons
	 * @param geoElement button element to configure
	 */
	public ButtonIconShownProperty(
			Localization localization, ImageManager imageManager, GeoElement geoElement) {
		super(localization, "");
		this.imageManager = imageManager;
		this.geoElement = geoElement;
	}

	@Override
	protected void doSetValue(Boolean showIcon) {
		if (showIcon && geoElement.getImageFileName().isEmpty()) {
			String imagePath = imageManager.applyButtonIcon(
					ButtonIconProperty.ButtonIcon.PLAY.fileName, geoElement.getKernel());
			geoElement.setFillImage(imagePath);
		} else if (!showIcon) {
			geoElement.setFillImage("");
		}
		geoElement.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return !geoElement.getImageFileName().isEmpty();
	}

	@Override
	public GeoElement getGeoElement() {
		return geoElement;
	}
}
