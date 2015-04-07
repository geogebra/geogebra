/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoLengthVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.util.MyMath;

/**
 * Length of a vector or point.
 * 
 * @author Markus
 * @version
 */
public class AlgoLengthVector extends AlgoElement {

	private GeoVec3D v; // input
	private GeoNumeric num; // output

	private double[] coords = new double[2];

	public AlgoLengthVector(Construction cons, String label, GeoVec3D v) {
		super(cons);
		this.v = v;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = v;

		super.setOutputLength(1);
		super.setOutput(0, num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
		return num;
	}

	GeoVec3D getv() {
		return v;
	}

	// calc length of vector v
	@Override
	public final void compute() {
		v.getInhomCoords(coords);
		num.setValue(MyMath.length(coords[0], coords[1]));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("LengthOfA", v.getLabel(tpl));

	}

	// TODO Consider locusequability
}
