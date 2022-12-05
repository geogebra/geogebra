package org.geogebra.common.kernel.geos;

/**
 * Class to store original screen location of a geo that comes from ggb.
 * 
 * @author Laszlo Gal
 *
 */
public class ScreenLocation {
	private final int x;
	private final int y;

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
	 * @return screen y from file
	 */
	public Integer getY() {
		return y;
	}

}
