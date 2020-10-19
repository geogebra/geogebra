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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.SpreadsheetTraceable;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoCoords4D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * 
 * @author Markus + ggb3D
 */
public abstract class GeoVec4D extends GeoElement3D implements GeoCoords4D,
		Vector3DValue, Traceable, SpreadsheetTraceable {
	/** vector coordinates */
	protected Coords v;

	/**
	 * @param c
	 *            construction
	 */
	public GeoVec4D(Construction c) {
		super(c);
		v = new Coords(4);
		setConstructionDefaults(); // init visual settings
	}

	/**
	 * Creates new GeoVec4D with coordinates (x,y,z,w) and label
	 * 
	 * @param c
	 *            construction
	 * @param x
	 *            homogeneous x-coord
	 * @param y
	 *            homogeneous y-coord
	 * @param z
	 *            homogeneous z-coord
	 * @param w
	 *            homogeneous 4th coord
	 */
	public GeoVec4D(Construction c, double x, double y, double z, double w) {
		this(c);
		setCoords(x, y, z, w);
	}

	@Override
	public void setCoords(double x, double y, double z, double w) {
		setCoords(new double[] { x, y, z, w });
	}

	/**
	 * Set this to (x,y,0,w)
	 * 
	 * @param x
	 *            homogeneous x-coord
	 * @param y
	 *            homogeneous y-coord
	 * @param w
	 *            homogeneous 4th coord
	 */
	public void setCoords(double x, double y, double w) {
		setCoords(x, y, 0, w);
	}

	/**
	 * @return homogeneous x-coord
	 */
	public double getX() {
		return getCoords().get(1);
	}

	/**
	 * @return homogeneous y-coord
	 */
	public double getY() {
		return getCoords().get(2);
	}

	/**
	 * @return homogeneous z-coord
	 */
	public double getZ() {
		return getCoords().get(3);
	}

	/**
	 * @return homogeneous 4th coord
	 */
	public double getW() {
		return getCoords().get(4);
	}

	/**
	 * returns all class-specific xml tags for saveXML Geogebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<coords x=\"").append(getX())
				.append("\" y=\"").append(getY())
				.append("\" z=\"").append(getZ())
				.append("\" w=\"").append(getW()).append("\"/>\n");
	}

	/**
	 * Sets the coord style
	 * 
	 * @param mode
	 *            new coord style
	 */
	@Override
	public void setMode(int mode) {
		toStringMode = mode;
	}

	@Override
	public final boolean hasCoords() {
		return true;
	}

	/**
	 * @param vals
	 *            homogeneous coordinates
	 */
	public void setCoords(Coords vals) {
		setDefinition(null);
		v.set(vals);
	}

	/**
	 * @param vals
	 *            homogeneous coordinates
	 */
	public void setCoords(double[] vals) {
		setDefinition(null);
		v.set(vals);
	}

	/**
	 * Copy coords from other vector
	 * 
	 * @param vec
	 *            other vector
	 */
	public void setCoords(GeoVec4D vec) {
		setCoords(vec.v);
	}

	/**
	 * set coords to point's coords (in 3D)
	 * 
	 * @param p
	 *            point
	 */
	final public void setCoords(GeoPointND p) {
		setCoords(p.getCoordsInD3());
	}

	/**
	 * @return homogeneous coords
	 */
	final public Coords getCoords() {
		return v;
	}

	/**
	 * Add v0 to this
	 * 
	 * @param v0
	 *            translation vector
	 */
	public void translate(Coords v0) {
		v.addInside(v0);
		setCoords(v);
	}

	@Override
	public boolean hasSpecialEditor() {
		return isIndependent()
				|| getDefinition() != null && getDefinition().unwrap() instanceof MyVecNDNode;
	}
}
