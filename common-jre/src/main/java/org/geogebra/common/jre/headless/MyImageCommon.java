package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;

public class MyImageCommon implements MyImage {
	@Override
	public int getWidth() {
		return 64;
	}

	@Override
	public int getHeight() {
		return 64;
	}

	@Override
	public boolean isSVG() {
		return false;
	}

	@Override
	public GGraphics2D createGraphics() {
		return null;
	}

	@Override
	public String toLaTeXStringBase64() {
		return "";
	}
}
