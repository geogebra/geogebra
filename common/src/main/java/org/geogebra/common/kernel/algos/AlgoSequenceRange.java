/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * Algorithm for the Sequence[ expression of var, var, from-value, to-value,
 * step ] command.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoSequenceRange extends AlgoElement {

	private enum SequenceType {
		SIMPLE, RANGE, FULL
	}

	private GeoNumberValue var_from;
	private GeoNumberValue var_to;
	private GeoNumberValue var_step;
	private GeoList list; // output

	private SequenceType type;

	private double last_to = Double.MIN_VALUE;

	/**
	 * Creates simple sequence start..upTo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param from
	 *            lower bound
	 * @param upTo
	 *            upper bound
	 * @param step
	 *            step
	 */
	public AlgoSequenceRange(Construction cons, String label,
			GeoNumberValue from,
			GeoNumberValue upTo, GeoNumberValue step) {
		super(cons);

		type = SequenceType.RANGE;
		var_from = from;
		var_to = upTo;
		var_step = step;

		list = new GeoList(cons);
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	/**
	 * Creates simple sequence 1..upTo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param upTo
	 *            upper bound
	 */
	public AlgoSequenceRange(Construction cons, String label,
			GeoNumberValue upTo) {
		super(cons);
		type = SequenceType.SIMPLE;
		var_from = new GeoNumeric(cons, 1);

		var_to = upTo;
		list = new GeoList(cons);
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Sequence;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		if (type == SequenceType.SIMPLE) {
			input = new GeoElement[1];
			input[0] = var_to.toGeoElement();
		} else {
			if (var_step == null) {
				input = new GeoElement[] { var_from.toGeoElement(),
						var_to.toGeoElement() };
			} else {
				input = new GeoElement[] { var_from.toGeoElement(),
						var_to.toGeoElement(),
						var_step.toGeoElement() };
			}
		}

		list.setTypeStringForXML(var_to
				.getGeoClassType().xmlName);
		setOutputLength(1);
		setOutput(0, list);

		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns list of all contained elements.
	 * 
	 * @return list of elements
	 */
	GeoList getList() {
		return list;
	}

	@Override
	public final void compute() {
		if (type == SequenceType.SIMPLE) {
			computeSimple();
		} else {
			computeRange();
		}
	}

	// use doubles
	// ef Sequence[9007199254000027, 9007199254000187]
	private void computeRange() {
		double from = var_from.getDouble();
		double to = var_to.getDouble();
		if (var_step == null) {
			from = Math.round(from);
			to = Math.round(to);
		}
		if (from > MyMath.LARGEST_INTEGER || from < -MyMath.LARGEST_INTEGER
				|| to > MyMath.LARGEST_INTEGER
				|| to < -MyMath.LARGEST_INTEGER) {
			list.setUndefined();
			return;
		}
		
		list.clear();
		double step = 1;
		if (var_step != null) {
			step = var_step.evaluateDouble();
			if (to < from) {
				step = -step;
			}
			if (DoubleUtil.isZero(step) || step < 0) {
				list.setUndefined();
				return;
			}
		}
		// also see Operation.java case Sequence:
		if (from < to) {

			// Kernel.MIN_PRECISION and isInteger() check for eg
			// Sequence(1, 2, 0.1)

			// increasing list
			for (double k = from; k <= to + Kernel.MIN_PRECISION; k += step) {
				if (DoubleUtil.isInteger(k)) {
					k = Math.round(k);
				}
				list.addNumber(k, null);
			}

		} else {

			// decreasing list
			for (double k = from; k >= to - Kernel.MIN_PRECISION; k -= step) {
				if (DoubleUtil.isInteger(k)) {
					k = Math.round(k);
				}
				list.addNumber(k, null);
			}

		}
	}

	private void computeSimple() {
		int to = (int) Math.round(var_to.getDouble());

		if (last_to < to) {
			for (int k = (int) last_to; k < to; k++) {
				if (k >= 0) {
					list.addNumber(k + 1, null);
				}
			}
		}
		if (last_to > to) {
			for (int k = (int) last_to; k > to; k--) {
				if (k >= 1) {
					GeoElement ge = list.get(k - 1);
					ge.remove();
					list.remove(k - 1);
				}
			}
		}
		last_to = to;
	}

}
