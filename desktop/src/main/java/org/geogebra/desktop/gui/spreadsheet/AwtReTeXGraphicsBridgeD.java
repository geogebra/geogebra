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
