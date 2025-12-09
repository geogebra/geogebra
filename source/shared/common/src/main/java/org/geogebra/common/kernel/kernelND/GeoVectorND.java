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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Simple common interface for GeoVector and GeoVector3D
 * 
 * @author ggb3D
 *
 */
public interface GeoVectorND
		extends GeoDirectionND, Locateable, CoordStyle, VectorNDValue {

	/**
	 * @param c
	 *            coordinates as array
	 */
	public void setCoords(double[] c);

	/**
	 * @return the coords of the vector in 2D
	 */
	public Coords getCoordsInD2();

	/**
	 * @return the coords of the vector in 3D
	 */
	public Coords getCoordsInD3();

	/**
	 * UPdates start point
	 */
	void updateStartPointPosition();

	/**
	 * @return true if all coords are finite
	 */
	boolean isFinite();

	/**
	 * @param coords
	 *            array to store inhomogeneous coords
	 */
	void getInhomCoords(double[] coords);

	/**
	 * @return inhomogeneous coords
	 */
	double[] getInhomCoords();

	/**
	 * @return true if tracing
	 */
	public boolean getTrace();

	/**
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void setCoords(double x, double y, double z);

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param w
	 *            inhomogeneous w for 3D vectors
	 */
	public void setCoords(double x, double y, double z, double w);

	/**
	 * 
	 * @return x component
	 */
	public double getX();

	/**
	 * 
	 * @return y component
	 */
	public double getY();

	/**
	 * 
	 * @return z component
	 */
	public double getZ();

	/**
	 * Get string as column vector for editing.
	 *
	 * @param tpl StringTemplate
	 * @return the column vector that can be rendered by editor.
	 */
	String toValueStringAsColumnVector(StringTemplate tpl);

	/**
	 * @param rwTransVec
	 *            translation vector
	 * @param endPosition
	 *            end position
	 * @return true if successful
	 */
	boolean moveVector(Coords rwTransVec, Coords endPosition);
}
