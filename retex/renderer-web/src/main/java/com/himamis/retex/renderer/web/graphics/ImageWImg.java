package com.himamis.retex.renderer.web.graphics;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;

import elemental2.dom.HTMLImageElement;

public class ImageWImg implements Image {
	private final int width;
	private final int height;
	private final HTMLImageElement image;

	/**
	 * @param img image
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	public ImageWImg(HTMLImageElement img, int width, int height) {
		this.image = img;
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
	public Graphics2DInterface createGraphics2D() {
		return null;
	}

	public HTMLImageElement getImage() {
		return image;
	}
}
