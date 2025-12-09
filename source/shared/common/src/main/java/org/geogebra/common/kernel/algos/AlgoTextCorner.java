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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.HasCorners;

/**
 * Algo for Corner(text) command, adapted from AlgoImageCorner.
 */
public class AlgoTextCorner extends AlgoElement {

	private HasCorners txt; // input
	private GeoPoint corner; // output
	private GeoNumberValue number;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param arg
	 *            text
	 * @param number
	 *            corner index (1=SW, 2=SE, 3=NE, 4=NW)
	 */
	public AlgoTextCorner(Construction cons, String label, HasCorners arg,
			GeoNumberValue number) {
		super(cons);
		this.txt = arg;
		this.number = number;

		// make sure bounding box of text is kept up to date
		// so we can use it in compute()
		arg.setNeedsUpdatedBoundingBox(true);
		arg.update();

		corner = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement

		compute();
		corner.setLabel(label);

		cons.registerEuclidianViewCE(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.Corner;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) txt;
		input[1] = number.toGeoElement();

		setOnlyOutput(corner);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getCorner() {
		return corner;
	}

	@Override
	public final void compute() {
		// determine bounding box size here
		txt.calculateCornerPoint(corner, (int) number.getDouble());
	}

	@Override
	public boolean euclidianViewUpdate() {
		// update text to update it's bounding box
		kernel.notifyUpdate((GeoElement) txt);

		// now compute()
		compute();

		return true; // update cascade of dependent objects done in Construction
	}

}
