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

package org.geogebra.web.full.gui.layout;

import elemental2.dom.CanvasRenderingContext2D;

/**
 * Component that can be painted on canvas.
 */
public interface PaintToCanvas {
	/**
	 * Paint content of this component to a context.
	 * @param context2d context
	 * @param counter view counter, should be increased when painting done
	 * @param x horizontal offset
	 * @param y vertical offset
	 */
	void paintToCanvas(CanvasRenderingContext2D context2d, ViewCounter counter, int x, int y);
}
