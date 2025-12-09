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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Stick graph algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoStickGraph extends AlgoBarChart {

	/**
	 * StickGraph[&lt;list of points&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of points
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1) {
		super(cons, label, list1, null, null, null, null, null,
				AlgoBarChart.TYPE_STICKGRAPH);
	}

	/**
	 * StickGraph[&lt;list of points, &lt;horizontal&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of points
	 * @param isHorizontal
	 *            horizontal sticks?
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1,
			GeoBoolean isHorizontal) {
		super(cons, label, list1, null, null, isHorizontal, null, null,
				AlgoBarChart.TYPE_STICKGRAPH);
	}

	/**
	 * StickGraph[&lt;x list&gt;, &lt;y list&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            x-coords
	 * @param list2
	 *            y-coords
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1,
			GeoList list2) {
		super(cons, label, list1, list2, null, null, null, null,
				AlgoBarChart.TYPE_STICKGRAPH);
	}

	/**
	 * StickGraph[&lt;x list&gt;, &lt;y list&gt;] (no label)
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            x-coords
	 * @param list2
	 *            y-coords
	 */
	public AlgoStickGraph(Construction cons, GeoList list1, GeoList list2) {
		super(cons, list1, list2, null, null, null, null,
				AlgoBarChart.TYPE_STICKGRAPH);
	}

	/**
	 * StickGraph[&lt;x * list&gt;, &lt;y list&gt;, &lt;Horizontal&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            x-coords
	 * @param list2
	 *            y-coords
	 * @param isHorizontal
	 *            horizontal sticks?
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoBoolean isHorizontal) {
		super(cons, label, list1, list2, null, isHorizontal, null, null,
				AlgoBarChart.TYPE_STICKGRAPH);
	}

	/**
	 * StickGraph[&lt;x list&gt;, &lt;y list&gt;, &lt;Horizontal&gt;] (no label)
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            x-coords
	 * @param list2
	 *            y-coords
	 * @param isHorizontal
	 *            horizontal sticks?
	 */
	public AlgoStickGraph(Construction cons, GeoList list1, GeoList list2,
			GeoBoolean isHorizontal) {
		super(cons, list1, list2, null, isHorizontal, null, null,
				AlgoBarChart.TYPE_STICKGRAPH);
	}

	@Override
	public Commands getClassName() {
		return Commands.StickGraph;
	}

}