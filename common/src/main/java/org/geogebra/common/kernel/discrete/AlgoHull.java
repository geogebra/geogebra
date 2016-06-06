/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * 
 * @author Michael Borcherds
 * @version
 */

public abstract class AlgoHull extends AlgoElement implements GraphAlgo {

	
	protected GeoList inputList; // input
	private GeoNumeric percentage; // input
	protected GeoLocus locus; // output
	protected ArrayList<MyPoint> al;
	protected int size;

	public AlgoHull(Construction cons, String label, GeoList inputList,
			GeoNumeric percentage) {
		super(cons);
		this.inputList = inputList;
		this.percentage = percentage;

		locus = new GeoLocus(cons);

		setInputOutput();
		compute();
		locus.setLabel(label);
	}

	protected void setInputOutput() {
		input = new GeoElement[percentage == null ? 1 : 2];
		input[0] = inputList;
		if (percentage != null)
			input[1] = percentage;

		setOnlyOutput(locus);
		setDependencies(); // done by AlgoElement
	}

	public GeoLocus getResult() {
		return locus;
	}



}
