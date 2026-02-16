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

package org.geogebra.common.properties.impl;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ImageProperty;
import org.geogebra.common.util.ImageManager;

/**
 * Base class for properties extending ImageProperty.
 */
public abstract class AbstractImageProperty extends AbstractValuedProperty<ImageProperty.Value>
		implements ImageProperty {

	private final ImageManager imageManager;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param imageManager image manager
	 * @param name the name to be localized
	 */
	public AbstractImageProperty(Localization localization, ImageManager imageManager,
			String name) {
		super(localization, name);
		this.imageManager = imageManager;
	}

	@Override
	protected final void doSetValue(Value value) {
		if (value == null) {
			setImagePath("");
		} else {
			MyImage image = imageManager.getExternalImage(value.path());
			if (image != value.image()) {
				imageManager.addExternalImage(value.image(), value.path());
			}
			setImagePath(value.path());
		}
	}

	@Override
	public final Value getValue() {
		String path = getImagePath();
		if (path == null) {
			return null;
		}
		MyImage image = imageManager.getExternalImage(path);
		if (image == null) {
			return null;
		}
		return new Value(image, path);
	}

	@Override
	public final String getChooseFromFileLabel() {
		return getLocalization().getMenu("ChooseFromFile");
	}

	protected abstract @CheckForNull String getImagePath();
	
	protected abstract void setImagePath(@CheckForNull String path);
}
