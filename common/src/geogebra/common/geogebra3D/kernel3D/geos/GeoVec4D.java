/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.kernel.Construction;

/**
 * 
 * @author Markus + ggb3D
 * @version
 */
public abstract class GeoVec4D extends GeoVec implements GeoCoords4D {

	public GeoVec4D(Construction c) {
		this(c, 4);
	}

	public GeoVec4D(Construction c, int n) {
		super(c, n);
	}

	/** Creates new GeoVec4D with coordinates (x,y,z,w) and label */
	public GeoVec4D(Construction c, double x, double y, double z, double w) {
		super(c, new double[] { x, y, z, w });

	}

	/** Copy constructor */
	public GeoVec4D(Construction c, GeoVec4D v) {
		super(c);
		set(v);
	}

	public void setCoords(double x, double y, double z, double w) {
		setCoords(new double[] { x, y, z, w });
	}

	public void setCoords(double x, double y, double w) {
		setCoords(x, y, 0, w);
	}

	public double getX() {
		return getCoords().get(1);
	}

	public double getY() {
		return getCoords().get(2);
	}

	public double getZ() {
		return getCoords().get(3);
	}

	public double getW() {
		return getCoords().get(4);
	}

	/**
	 * returns all class-specific xml tags for saveXML Geogebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		sb.append("\t<coords");
		sb.append(" x=\"" + getX() + "\"");
		sb.append(" y=\"" + getY() + "\"");
		sb.append(" z=\"" + getZ() + "\"");
		sb.append(" w=\"" + getW() + "\"");
		sb.append("/>\n");

	}

	/**
	 * Sets the coord style
	 * 
	 * @param mode
	 *            new coord style
	 */
	public void setMode(int mode) {
		toStringMode = mode;
	}

	/**
	 * get the coord style
	 * 
	 * @return coord style
	 */
	public int getMode() {
		return toStringMode;
	}

	@Override
	public final boolean hasCoords() {
		return true;
	}

}
