/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
 */
package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;

/**
 * Stick graph algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoStepGraph extends AlgoBarChart {

	/******************************************************
	 * StepGraph[<x list>, <y list>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoList list2) {

		super(cons, label, list1, list2, null, null,
				null, null, AlgoBarChart.TYPE_STEPGRAPH);
	}

	/******************************************************
	 * StepGraph[<x list>, <y list>] (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 */
	public AlgoStepGraph(Construction cons, GeoList list1, GeoList list2) {
		super(cons, list1, list2, null, null, null,
				null, AlgoBarChart.TYPE_STEPGRAPH);
	}

	/******************************************************
	 * StepGraph[<x list>, <y list>, <boolean showStep>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param showStep
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoBoolean showStep) {
		super(cons, label, list1, list2, null, null, showStep, null,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	/******************************************************
	 * StepGraph[<x list>, <y list>, <boolean showStep>, < point type >]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param showStep
	 */
	public AlgoStepGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoBoolean showStep, GeoText pointType) {
		super(cons, label, list1, list2, null, null, showStep, pointType,
				AlgoBarChart.TYPE_STEPGRAPH);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoStepGraph;
	}

}