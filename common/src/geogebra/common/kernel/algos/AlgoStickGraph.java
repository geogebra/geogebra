/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
 */
package geogebra.common.kernel.algos;

import geogebra.common.euclidian.draw.DrawBarGraph;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Stick graph algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoStickGraph extends AlgoBarChart {

	/******************************************************
	 * StickGraph[<list of points>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1) {

		super(cons, label, list1, null, null, null,
				null, null, AlgoBarChart.TYPE_STICKGRAPH);

	}
	

	/******************************************************
	 * StickGraph[<list of points, <horizontal>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1, GeoBoolean isHorizontal) {

		super(cons, label, list1, null, null, isHorizontal,
				null, null, AlgoBarChart.TYPE_STICKGRAPH);

	}
	
	
	/******************************************************
	 * StickGraph[<x list>, <y list>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1,
			GeoList list2) {

		super(cons, label, list1, list2, null, null,
				null, null, AlgoBarChart.TYPE_STICKGRAPH);

	}

	/******************************************************
	 * StickGraph[<x list>, <y list>] (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 */
	public AlgoStickGraph(Construction cons, GeoList list1, GeoList list2) {
		super(cons, list1, list2, null, null,
				null, null, AlgoBarChart.TYPE_STICKGRAPH);
	}

	/**
	 * /******************************************************
	 * StickGraph[<x list>, <y list>, <Horizontal>] 
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param isHorizontal
	 */
	public AlgoStickGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoBoolean isHorizontal) {
		
		super(cons, label, list1, list2, null,
				isHorizontal, null, null, AlgoBarChart.TYPE_STICKGRAPH);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoStickGraph;
	}

}