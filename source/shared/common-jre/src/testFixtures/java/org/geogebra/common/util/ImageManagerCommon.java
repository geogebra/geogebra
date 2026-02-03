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

package org.geogebra.common.util;

import java.util.HashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.MyImage;

public class ImageManagerCommon extends ImageManager {

	private final HashMap<String, MyImage> externalImages = new HashMap<>();

	@Override
	public void addExternalImage(String filename0, String urlBase64) {
	}

	@Override
	public void addExternalImage(@Nonnull MyImage image, @Nonnull String path) {
		externalImages.put(path, image);
	}
	
	@Override
	public @CheckForNull MyImage getExternalImage(@Nonnull String path) {
		return externalImages.get(path);
	}
}
