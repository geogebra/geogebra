package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.jre.gui.MyImageJre;

public class MyImageCommon implements MyImageJre {
	private final int height;
	private final int width;

	/**
	 * @param width pixel width
	 * @param height pixel height
	 */
	public MyImageCommon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
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

	@Override
	public String getSVG() {
		return "<svg/>";
	}

	@Override
	public boolean hasNonNullImplementation() {
		return false;
	}
}
