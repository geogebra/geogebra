package org.geogebra.common.util.shape;

import java.util.Objects;

/**
 * Size object.
 */
public final class Size {

	public final double width;
	public final double height;

	/**
	 * Create a size object.
	 * @param width width
	 * @param height height
	 */
	public Size(double width, double height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Return the width
	 * @return width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Return the height
	 * @return height
	 */
	public double getHeight() {
		return height;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Size)) {
			return false;
		}
		Size other = (Size) object;
		return Double.compare(width, other.width) == 0
				&& Double.compare(height, other.height) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height);
	}
}
