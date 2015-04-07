package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDrawingPadCorner3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVertexConic3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVertexPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.algos.AlgoVertexPolygon;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdVertex;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class CmdVertex3D extends CmdVertex {

	public CmdVertex3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoPointND cornerOfDrawingPad(String label, NumberValue number,
			NumberValue ev) {

		// Corner[ev, n] : if ev==3, check if loading - then do as <5.0 version
		// (with 2D points)
		if (!kernelA.getLoadingMode() && ev != null
				&& AlgoDrawingPadCorner3D.is3D(ev)) {
			return cornerOfDrawingPad3D(label, number, ev);
		}

		return super.cornerOfDrawingPad(label, number, ev);
	}

	protected GeoPointND cornerOfDrawingPad3D(String label, NumberValue number,
			NumberValue ev) {

		AlgoDrawingPadCorner3D algo = new AlgoDrawingPadCorner3D(cons, label,
				number, ev);
		return algo.getCorner();
	}

	@Override
	protected AlgoVertexPolygon newAlgoVertexPolygon(Construction cons,
			String label, GeoPoly p, GeoNumberValue v) {

		if (p.isGeoElement3D()) {
			return new AlgoVertexPolygon3D(cons, label, p, v);
		}

		return super.newAlgoVertexPolygon(cons, label, p, v);
	}

	@Override
	protected AlgoVertexConic newAlgoVertexConic(Construction cons,
			String[] labels, GeoConicND conic) {

		if (conic.isGeoElement3D()) {
			return new AlgoVertexConic3D(cons, labels, conic);
		}

		return super.newAlgoVertexConic(cons, labels, conic);
	}
}
