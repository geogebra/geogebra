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
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Length of a segment.
 * 
 * @author mathieu
 */
public class AlgoLengthSegment extends AlgoElement {

	private GeoSegmentND seg; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param seg
	 *            segment
	 */
	public AlgoLengthSegment(Construction cons, String label,
			GeoSegmentND seg) {
		super(cons);
		this.seg = seg;
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
		input[0] = (GeoElement) seg;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return segment length
	 */
	public GeoNumeric getLength() {
		return num;
	}

	// calc length of vector v
	@Override
	public final void compute() {

		num.setValue(seg.getLength());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("LengthOfA", "Length of %0",
				seg.getLabel(tpl));

	}

}
