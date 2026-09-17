/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.ggbjdk.java.awt.geom;

import org.geogebra.common.awt.GDimension;

/**
 * The <code>Dimension</code> class encapsulates the width and
 * height of a component (in integer precision) in a single object.
 * The class is
 * associated with certain properties of components. Several methods
 * defined by the <code>Component</code> class and the
 * <code>LayoutManager</code> interface return a
 * <code>Dimension</code> object.
 * <p>
 * Normally the values of <code>width</code>
 * and <code>height</code> are non-negative integers.
 * The constructors that allow you to create a dimension do
 * not prevent you from setting a negative value for these properties.
 * If the value of <code>width</code> or <code>height</code> is
 * negative, the behavior of some methods defined by other objects is
 * undefined.
 *
 * @author      Sami Shaio
 * @author      Arthur van Hoff
 * @see         java.awt.Component
 * @see         java.awt.LayoutManager
 * @since       1.0
 */
public class Dimension extends GDimension implements java.io.Serializable {

	/**
	 * The width dimension; negative values can be used.
	 *
	 * @see #setSize
	 * @since 1.0
	 */
	public int width;

	/**
	 * The height dimension; negative values can be used.
	 *
	 * @see #setSize
	 * @since 1.0
	 */
	public int height;

	/*
	 * JDK 1.1 serialVersionUID
	 */
	private static final long serialVersionUID = 4723952579491349524L;

	/**
	 * Creates an instance of <code>Dimension</code> with a width
	 * of zero and a height of zero.
	 */
	public Dimension() {
		this(0, 0);
	}

	/**
	 * Constructs a <code>Dimension</code> and initializes
	 * it to the specified width and specified height.
	 *
	 * @param width the specified width
	 * @param height the specified height
	 */
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the size of this <code>Dimension</code> object
	 * to the specified width and height.
	 * This method is included for completeness, to parallel the
	 * <code>setSize</code> method defined by <code>Component</code>.
	 *
	 * @param    width   the new width for this <code>Dimension</code> object
	 * @param    height  the new height for this <code>Dimension</code> object
	 * @see      java.awt.Dimension#getSize
	 * @see      java.awt.Component#setSize
	 * @since    1.1
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Checks whether two dimension objects have equal values.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dimension) {
			Dimension d = (Dimension) obj;
			return (width == d.width) && (height == d.height);
		}
		return false;
	}

	/**
	 * Returns the hash code for this <code>Dimension</code>.
	 *
	 * @return    a hash code for this <code>Dimension</code>
	 */
	@Override
	public int hashCode() {
		int sum = width + height;
		return sum * (sum + 1) / 2 + width;
	}

	/**
	 * Returns a string representation of the values of this
	 * <code>Dimension</code> object's <code>height</code> and
	 * <code>width</code> fields. This method is intended to be used only
	 * for debugging purposes, and the content and format of the returned
	 * string may vary between implementations. The returned string may be
	 * empty but may not be <code>null</code>.
	 *
	 * @return  a string representation of this <code>Dimension</code>
	 *          object
	 */
	@Override
	public String toString() {
		return getClass().getName() + "[width=" + width + ",height=" + height + "]";
	}
}
