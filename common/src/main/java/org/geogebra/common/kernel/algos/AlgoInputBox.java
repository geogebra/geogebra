/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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

		inputBox = new GeoInputBox(cons);
		if (linkedGeo != null) {
			inputBox.setLinkedGeo(linkedGeo);
		}
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

		super.setOutputLength(1);
		super.setOutput(0, inputBox);
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
