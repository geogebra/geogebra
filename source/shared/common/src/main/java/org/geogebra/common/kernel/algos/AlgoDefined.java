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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Returns whether an object is defined
 * 
 * @author Michael Borcherds
 * @version 2008-03-06
 */

public class AlgoDefined extends AlgoElement {

	private GeoElement inputGeo; // input
	private GeoBoolean outputBoolean; // output

	/**
	 * @param cons
	 *            construction
	 * @param inputGeo
	 *            element to be checked
	 */
	public AlgoDefined(Construction cons, GeoElement inputGeo) {
		super(cons);
		this.inputGeo = inputGeo;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Defined;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputGeo;

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {

		if (inputGeo.isGeoPoint()) {
			GeoPointND p = (GeoPointND) inputGeo;
			outputBoolean.setValue(inputGeo.isDefined() && !p.isInfinite());
			return;
		} else if (inputGeo.isGeoVector()) {
			GeoVectorND v = (GeoVectorND) inputGeo;
			outputBoolean.setValue(inputGeo.isDefined() && !v.isInfinite());
			return;
		} else if (inputGeo.isGeoFunction()) {
			if (inputGeo.toValueString(StringTemplate.defaultTemplate)
					.equals("?")) {
				outputBoolean.setValue(false);
				return;
			}
		}

		outputBoolean.setValue(inputGeo.isDefined());
	}

}
