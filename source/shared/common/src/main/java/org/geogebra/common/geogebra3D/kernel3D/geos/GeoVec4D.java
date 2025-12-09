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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoElement;
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
	protected int toStringMode = Kernel.COORD_CARTESIAN;

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
	protected void getXMLTags(XMLStringBuilder sb) {
		super.getXMLTags(sb);
		sb.startTag("coords").attr("x", getX())
				.attr("y", getY())
				.attr("z", getZ())
				.attr("w", getW()).endTag();
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

	@Override
	public final int getToStringMode() {
		return toStringMode;
	}

	@Override
	public void applyToStringModeFrom(GeoElement other) {
		if (other instanceof VectorNDValue) {
			toStringMode = ((VectorNDValue) other).getToStringMode();
		}
	}

}
