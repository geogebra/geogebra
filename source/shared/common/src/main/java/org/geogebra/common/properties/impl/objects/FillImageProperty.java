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
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractImageProperty;
import org.geogebra.common.properties.impl.objects.delegate.FillableDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;

/**
 * Property responsible for filling an object with an image.
 */
public final class FillImageProperty extends AbstractImageProperty
		implements GeoElementDependentProperty {

	private final FillableDelegate delegate;

	/**
	 * @param loc localization
	 * @param imageManager image manager
	 * @param element element
	 * @throws NotApplicablePropertyException if not filled by image
	 */
	public FillImageProperty(Localization loc, ImageManager imageManager, GeoElement element) throws
			NotApplicablePropertyException {
		super(loc, imageManager, "Image");
		delegate = new FillableDelegate(element);
	}

	@Override
	protected String getImagePath() {
		return delegate.getElement().getImageFileName();
	}

	@Override
	protected void setImagePath(String path) {
		String resolvedPath = path != null ? path : "";
		GeoElement element = delegate.getElement();
		element.setImageFileName(resolvedPath);
		element.setAlphaValue(resolvedPath.isEmpty() ? 0.0f : 1.0f);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public boolean isAvailable() {
		return delegate.getElement().getFillType() == FillType.IMAGE;
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
