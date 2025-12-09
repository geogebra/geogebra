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

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;

/**
 * Creates textfield linked with geo
 * 
 * @author Zbynek Konecny
 */

public class AlgoInputBox extends AlgoElement {

	private @CheckForNull GeoElement linkedGeo; // input
	private GeoInputBox inputBox; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param linkedGeo
	 *            linked object
	 */
	public AlgoInputBox(Construction cons, String label, GeoElement linkedGeo) {
		super(cons);
		this.linkedGeo = linkedGeo;

		inputBox = new GeoInputBox(cons, linkedGeo);
		inputBox.setAbsoluteScreenLoc(30, 30);
		inputBox.resetScreenLocation();
		setInputOutput();
		compute();
		inputBox.setLabel(label);
		inputBox.setLabelVisible(true);
		inputBox.setEuclidianVisible(true);
		inputBox.update();
	}

	@Override
	public Commands getClassName() {
		return Commands.Textfield;
	}

	@Override
	protected void setInputOutput() {
		if (linkedGeo == null) {
			input = new GeoElement[0];
		} else {
			input = new GeoElement[1];
			input[0] = linkedGeo;
		}

		setOnlyOutput(inputBox);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return the box
	 */
	public GeoInputBox getResult() {
		return inputBox;
	}

	@Override
	public final void compute() {
		if (linkedGeo != null && linkedGeo.isDefined()) {
			inputBox.clearTempUserInput();
		}
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TEXTFIELD_ACTION;
	}

}
