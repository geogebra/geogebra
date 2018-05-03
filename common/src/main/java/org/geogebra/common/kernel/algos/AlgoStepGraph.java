/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Stick graph algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoStepGraph extends AlgoBarChart {

	/**
	 * StepGraph[&lt;list of points&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param list1
	 *            first list
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1) {
		super(cons, label, list1, null, null, null, null, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;list of points&gt;, &lt;boolean hasJoin&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of points
	 * @param hasJoin
	 *            true to join steps
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoBoolean hasJoin) {
		super(cons, label, list1, null, null, null, hasJoin, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;list of points&gt;, &lt;boolean hasJoin&gt;, &lt;point
	 * style&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of points
	 * @param hasJoin
	 *            true to join steps
	 * @param pointStyle
	 *            point style
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoBoolean hasJoin, GeoNumeric pointStyle) {
		super(cons, label, list1, null, null, null, hasJoin, pointStyle,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;x list&gt;, &lt;y list&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of x-coords
	 * @param list2
	 *            list of y-coords
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoList list2) {
		super(cons, label, list1, list2, null, null, null, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;x list&gt;, &lt;y list&gt;] (no label)
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            list of x-coords
	 * @param list2
	 *            list of y-coords
	 */
	public AlgoStepGraph(Construction cons, GeoList list1, GeoList list2) {
		super(cons, list1, list2, null, null, null, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;x list&gt;, &lt;y list&gt;, &lt;boolean hasJoin&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of x-coords
	 * @param list2
	 *            list of y-coords
	 * @param hasJoin
	 *            true to join steps
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoBoolean hasJoin) {
		super(cons, label, list1, list2, null, null, hasJoin, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;x list&gt;, &lt;y list&gt;, &lt;boolean hasJoin&gt;] (no
	 * label)
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            list of x-coords
	 * @param list2
	 *            list of y-coords
	 * @param hasJoin
	 *            true to join steps
	 */
	public AlgoStepGraph(Construction cons, GeoList list1, GeoList list2,
			GeoBoolean hasJoin) {
		super(cons, list1, list2, null, null, hasJoin, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/**
	 * StepGraph[&lt;x list&gt;, &lt;y list&gt;, &lt;boolean hasJoin&gt;,
	 * &lt;point style&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of x-coords
	 * @param list2
	 *            list of y-coords
	 * @param showStep
	 *            true to join steps
	 * @param pointStyle
	 *            point style
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoBoolean showStep, GeoNumeric pointStyle) {
		super(cons, label, list1, list2, null, null, showStep, pointStyle,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	@Override
	public Commands getClassName() {
		return Commands.StepGraph;
	}

}