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

package org.geogebra.desktop.gui.spreadsheet;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.rendering.AwtReTeXGraphicsBridge;
import org.geogebra.desktop.awt.GGraphics2DD;

import com.himamis.retex.renderer.desktop.graphics.Graphics2DD;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class AwtReTeXGraphicsBridgeD implements AwtReTeXGraphicsBridge {
	@Override
	public Graphics2DInterface convert(GGraphics2D gGraphics2D) {
		return new Graphics2DD(((GGraphics2DD) gGraphics2D).getNativeImplementation());
	}
}
