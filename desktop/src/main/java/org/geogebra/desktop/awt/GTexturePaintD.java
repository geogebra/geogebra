package org.geogebra.desktop.awt;

import java.awt.TexturePaint;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GTexturePaint;

public class GTexturePaintD implements GTexturePaint {
	public GTexturePaintD(GBufferedImage subimage, GRectangle rect) {
		impl = new TexturePaint(GBufferedImageD.getAwtBufferedImage(subimage),
				GRectangleD.getAWTRectangle(rect));
	}

	public GTexturePaintD(TexturePaint paint) {
		impl = paint;
	}

	private TexturePaint impl;

	public TexturePaint getPaint() {
		return impl;
	}

}
