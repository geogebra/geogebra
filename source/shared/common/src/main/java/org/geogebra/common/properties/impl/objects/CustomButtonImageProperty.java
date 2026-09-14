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
 * See https://www.geogebra.org/license for full licensing details.
 */

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractImageProperty;
import org.geogebra.common.properties.impl.objects.delegate.IconStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;
import org.jspecify.annotations.Nullable;

/** {@code Property} responsible for selecting a custom image/icon for a button. */
public final class CustomButtonImageProperty extends AbstractImageProperty
		implements GeoElementDependentProperty {
	private final IconStylePropertyDelegate delegate;
	private final ImageManager imageManager;
	private final Kernel kernel;

	/**
	 * Constructs the property.
	 * @param localization localization for property labels
	 * @param imageManager image manager for resolving preset button icons
	 * @param kernel kernel for registering the default button icon
	 * @param element button element to configure
	 * @throws NotApplicablePropertyException if the element does not support an icon
	 */
	public CustomButtonImageProperty(Localization localization, ImageManager imageManager,
			Kernel kernel, GeoElement element) throws NotApplicablePropertyException {
		super(localization, imageManager, "Image");
		this.imageManager = imageManager;
		this.kernel = kernel;
		this.delegate = new IconStylePropertyDelegate(element);
	}

	@Override
	protected @Nullable String getImagePath() {
		String imagePath = delegate.getElement().getImageFileName();
		return ButtonIconProperty.isButtonIconPath(imagePath, imageManager) ? null : imagePath;
	}

	@Override
	protected void setImagePath(@Nullable String imagePath) {
		String resolvedPath = imagePath == null || imagePath.isEmpty()
				? imageManager.applyButtonIcon(ButtonIconProperty.ButtonIcon.PLAY.fileName, kernel)
				: imagePath;
		delegate.getElement().setFillImage(resolvedPath);
		delegate.getElement().setAlphaValue(1.0f);
		delegate.getElement().updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
