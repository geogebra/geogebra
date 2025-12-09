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
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

public class AlgoImageCorner extends AlgoElement {

	private GeoImage img; // input
	private GeoPoint corner; // output
	private GeoNumberValue number;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param img
	 *            image
	 * @param number
	 *            corner index, see {@link AlgoDrawingPadCorner}
	 */
	public AlgoImageCorner(Construction cons, String label, GeoImage img,
			GeoNumberValue number) {
		super(cons);
		this.img = img;
		this.number = number;

		corner = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
		img.setNeedsBoundingBoxUpdate(true);
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
		input[0] = img;
		input[1] = number.toGeoElement();

		setOnlyOutput(corner);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getCorner() {
		return corner;
	}

	@Override
	public final void compute() {
		img.calculateCornerPoint(corner, (int) number.getDouble());
	}

	@Override
	public boolean euclidianViewUpdate() {

		// update image to update it's bounding box
		kernel.notifyUpdate(img);

		// now compute()
		compute();

		// update corner
		corner.update();

		return true; // update cascade of dependent objects done in Construction
	}

}
