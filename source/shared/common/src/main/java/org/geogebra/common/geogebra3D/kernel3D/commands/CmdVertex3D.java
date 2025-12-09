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

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDrawingPadCorner3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVertexConic3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVertexPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.algos.AlgoVertexPolygon;
import org.geogebra.common.kernel.commands.CmdVertex;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Vertex, Corner commands
 */
public class CmdVertex3D extends CmdVertex {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdVertex3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoPointND cornerOfDrawingPad(String label, GeoNumberValue number,
			GeoNumberValue ev) {

		// Corner[ev, n] : if ev==3, check if loading - then do as <5.0 version
		// (with 2D points)
		if (!kernel.getLoadingMode() && ev != null
				&& AlgoDrawingPadCorner3D.is3D(ev)) {
			return cornerOfDrawingPad3D(label, number, ev);
		}

		return super.cornerOfDrawingPad(label, number, ev);
	}

	/**
	 * @param label
	 *            label
	 * @param number
	 *            index
	 * @param ev
	 *            view
	 * @return corner
	 */
	protected GeoPointND cornerOfDrawingPad3D(String label,
			GeoNumberValue number, GeoNumberValue ev) {

		AlgoDrawingPadCorner3D algo = new AlgoDrawingPadCorner3D(cons, label,
				number, ev);
		return algo.getCorner();
	}

	@Override
	protected AlgoVertexPolygon newAlgoVertexPolygon(Construction cons1,
			String label, GeoPoly p, GeoNumberValue v) {

		if (p.isGeoElement3D()) {
			return new AlgoVertexPolygon3D(cons1, label, p, v);
		}

		return super.newAlgoVertexPolygon(cons1, label, p, v);
	}

	@Override
	protected AlgoVertexConic newAlgoVertexConic(Construction cons1,
			String[] labels, GeoConicND conic) {

		if (conic.isGeoElement3D()) {
			return new AlgoVertexConic3D(cons1, labels, conic);
		}

		return super.newAlgoVertexConic(cons1, labels, conic);
	}
}
