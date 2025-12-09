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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.kernelND.CoordStyle;

/**
 * Common parent for points and vectors (2D).
 * Contains methods that do not apply to lines.
 */
public abstract class GeoPointVector extends GeoVec3D implements CoordStyle {
	protected int toStringMode = Kernel.COORD_CARTESIAN;

	public GeoPointVector(Construction c) {
		super(c);
	}

	public GeoPointVector(Construction c, double x, double y, double z) {
		super(c, x, y, z);
	}

	/**
	 * Changes coord style to POLAR
	 */
	@Override
	public void setPolar() {
		toStringMode = Kernel.COORD_POLAR;
	}

	/**
	 * Changes coord style to CARTESIAN
	 */
	@Override
	public void setCartesian() {
		toStringMode = Kernel.COORD_CARTESIAN;
	}

	/**
	 * Changes coord style to COMPLEX
	 */
	@Override
	public void setComplex() {
		toStringMode = Kernel.COORD_COMPLEX;
	}

	/**
	 * Changes coord style to CARTESIAN 3D
	 */
	@Override
	public void setCartesian3D() {
		toStringMode = Kernel.COORD_CARTESIAN_3D;
	}

	/**
	 * @return true if using POLAR style
	 */
	final public boolean isPolar() {
		return getToStringMode() == Kernel.COORD_POLAR;
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
	public void setSpherical() {
		setMode(Kernel.COORD_SPHERICAL);
	}

	@Override
	public boolean hasSpecialEditor() {
		return toStringMode != Kernel.COORD_COMPLEX && (isIndependent() && getDefinition() == null
				|| getDefinition() != null && getDefinition().unwrap() instanceof MyVecNDNode);
	}

	/**
	 * @return complex / polar/ cartesian for points an vectors, implicit /
	 *         explicit / parametric / ... for equations
	 */
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
