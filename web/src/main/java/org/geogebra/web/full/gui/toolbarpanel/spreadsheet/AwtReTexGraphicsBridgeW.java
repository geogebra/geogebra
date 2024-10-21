package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.rendering.AwtReTeXGraphicsBridge;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;

public class AwtReTexGraphicsBridgeW implements AwtReTeXGraphicsBridge {
	@Override
	public Graphics2DInterface convert(GGraphics2D gGraphics2D) {
		return new Graphics2DW(((GGraphics2DWI) gGraphics2D).getContext());
	}
}
