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

package org.geogebra.common.awt;

public interface RenderingHints {

	public static final int KEY_ANTIALIASING = 1;
	public static final int VALUE_ANTIALIAS_ON = 1;

	public static final int KEY_RENDERING = 2;
	public static final int VALUE_RENDER_QUALITY = 2;

	public static final int KEY_TEXT_ANTIALIASING = 3;
	public static final int VALUE_TEXT_ANTIALIAS_ON = 3;

	public static final int KEY_INTERPOLATION = 4;
	public static final int VALUE_INTERPOLATION_BILINEAR = 4;
	public static final int VALUE_INTERPOLATION_NEAREST_NEIGHBOR = 5;
	public static final int VALUE_INTERPOLATION_BICUBIC = 6;
}