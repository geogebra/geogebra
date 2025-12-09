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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.rendering.AwtReTeXGraphicsBridge;
import org.geogebra.web.awt.GGraphics2DWI;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;

public class AwtReTexGraphicsBridgeW implements AwtReTeXGraphicsBridge {
	@Override
	public Graphics2DInterface convert(GGraphics2D gGraphics2D) {
		return new Graphics2DW(((GGraphics2DWI) gGraphics2D).getContext());
	}
}
