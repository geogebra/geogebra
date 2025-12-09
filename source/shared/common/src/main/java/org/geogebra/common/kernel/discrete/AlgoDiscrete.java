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

package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;

/**
 * 
 * @author Michael Borcherds
 */

public abstract class AlgoDiscrete extends AlgoElement implements GraphAlgo {
	/** input points */
	protected GeoList inputList;
	/** output locus */
	protected GeoLocus locus;
	/** locus points */
	protected ArrayList<MyPoint> al;
	/** number of points */
	protected int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            points
	 */
	public AlgoDiscrete(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		locus = new GeoLocus(cons);

		setInputOutput();
		compute();
		locus.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return locus
	 */
	public GeoLocus getResult() {
		return locus;
	}

}
