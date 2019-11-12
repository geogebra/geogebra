package org.geogebra.common.awt;

public class GDimensionCommon extends GDimension {

	private int width;
	private int height;

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public GDimensionCommon(int width, int height) {
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
}
