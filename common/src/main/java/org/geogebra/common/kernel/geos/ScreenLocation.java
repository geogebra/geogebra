package org.geogebra.common.kernel.geos;

/**
 * Class to store original screen location of a geo that comes from ggb.
 * 
 * @author Laszlo Gal
 *
 */
public class ScreenLocation {
	private Integer x = null;
	private Integer y = null;
	private Integer height = null;
	private Integer width = null;

	/**
	 * Constructor from (x, y)
	 * 
	 * @param x
	 *            to set
	 * @param y
	 *            to set
	 */
	public ScreenLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return screen x from file
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 *            to set
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * @return screen y from file
	 */
	public Integer getY() {
		return y;
	}

	/**
	 * 
	 * @param y
	 *            to set
	 */
	public void setY(Integer y) {
		this.y = y;
	}

	/**
	 * @return height from file
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * Sets height if is not initialized yet
	 * 
	 * @param h
	 *            to set
	 */
	public void initHeight(Integer h) {
		if (height == null) {
			height = h;
		}
	}

	/**
	 * @return width from file
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * Sets width if is not initialized yet
	 * 
	 * @param w
	 *            to set
	 */
	public void initWidth(Integer w) {
		if (width == null) {
			width = w;
		}
	}

}
