package geogebra.awt;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GTexturePaint;

import java.awt.TexturePaint;

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
