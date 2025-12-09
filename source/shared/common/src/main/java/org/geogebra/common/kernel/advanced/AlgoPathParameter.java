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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Adapted from AlgoPerimeterPoly
 */
public class AlgoPathParameter extends AlgoElement {

	// Take a polygon as input
	private GeoPointND point;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric value;

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            point on path (for other points result is undefined)
	 */
	public AlgoPathParameter(Construction cons, GeoPointND point) {
		super(cons);
		this.point = point;

		value = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.PathParameter;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) point;

		setOnlyOutput(value);
		setDependencies();
	}

	@Override
	public final void compute() {
		if (!point.isDefined() || !point.isPointOnPath()) {
			value.setUndefined();
			return;
		}

		Path p = point.getPath();

		value.setValue(PathNormalizer.toNormalizedPathParameter(
				point.getPathParameter().getT(), p.getMinParameter(),
				p.getMaxParameter()));
	}

	/**
	 * @return path parameter
	 */
	public GeoNumeric getResult() {
		return value;
	}

}
