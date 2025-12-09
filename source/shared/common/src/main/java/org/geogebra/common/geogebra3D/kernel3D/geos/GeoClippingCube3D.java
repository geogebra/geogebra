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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;

/**
 * Simple geo class for clipping cube
 * 
 * @author mathieu
 *
 */
public class GeoClippingCube3D extends GeoElement3D {
	/** cube reduction: small */
	final static public int REDUCTION_SMALL = 0;
	/** cube reduction: medium */
	final static public int REDUCTION_MEDIUM = 1;
	/** cube reduction: large */
	final static public int REDUCTION_LARGE = 2;
	/** cube reduction: min */
	final static public int REDUCTION_MIN = 0;
	/** cube reduction: max */
	final static public int REDUCTION_MAX = 2;

	private int reduction = REDUCTION_MEDIUM;

	/**
	 * @param c
	 *            construction
	 */
	public GeoClippingCube3D(Construction c) {
		super(c);
	}

	@Override
	public Coords getLabelPosition() {
		return null;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CLIPPINGCUBE3D;
	}

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(GeoElementND geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() {
		// always defined
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	/**
	 * sets the reduction of the cube
	 * 
	 * @param value
	 *            reduction
	 */
	public void setReduction(int value) {
		reduction = value;
	}

	/**
	 * 
	 * @return the reduction of the cube
	 */
	public int getReduction() {
		return reduction;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.NONE;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}
}
