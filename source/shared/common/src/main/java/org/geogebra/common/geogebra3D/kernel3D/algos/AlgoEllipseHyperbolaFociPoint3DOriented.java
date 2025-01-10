package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public class AlgoEllipseHyperbolaFociPoint3DOriented
		extends AlgoEllipseHyperbolaFociPoint3D {

	private GeoDirectionND orientation;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            first focus
	 * @param B
	 *            second focus
	 * @param C
	 *            point on conic
	 * @param orientation
	 *            orientation
	 * @param type
	 *            conic type
	 */
	public AlgoEllipseHyperbolaFociPoint3DOriented(Construction cons,
			String label, GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation, final int type) {
		super(cons, label, A, B, C, orientation, type);
	}

	@Override
	protected void setOrientation(GeoDirectionND orientation) {
		this.orientation = orientation;
	}

	@Override
	protected void setInput() {
		input = new GeoElement[4];
		input[0] = (GeoElement) getFocus1();
		input[1] = (GeoElement) getFocus2();
		input[2] = (GeoElement) getConicPoint();
		input[3] = (GeoElement) orientation;
	}

	@Override
	protected boolean setCoordSys(CoordSys cs, Coords Ac, Coords Bc,
			Coords Cc) {

		Coords vn = orientation.getDirectionInD3();

		if (vn.isZero()) {
			return false;
		}

		Coords d1 = Bc.sub(Ac);

		// check if plane (ABC) and vn are orthogonal
		if (!DoubleUtil.isZero(d1.dotproduct(vn))) {
			return false;
		}

		if (!DoubleUtil.isZero(Cc.sub(Ac).dotproduct(vn))) {
			return false;
		}

		// set the coord sys
		cs.addPoint(Ac);
		cs.addVector(d1);
		cs.addVector(vn.crossProduct4(d1));

		return cs.makeOrthoMatrix(false, false);
	}

	@Override
	public String toString(StringTemplate tpl) {

		// direction is plane
		if (orientation instanceof GeoCoordSys2D) {
			if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
				return getLoc().getPlain(
						"HyperbolaWithFociABPassingThroughCParallelToD",
						getFocus1().getLabel(tpl), getFocus2().getLabel(tpl),
						getConicPoint().getLabel(tpl),
						orientation.getLabel(tpl));
			}
			return getLoc().getPlain(
					"EllipseWithFociABPassingThroughCParallelToD",
					getFocus1().getLabel(tpl), getFocus2().getLabel(tpl),
					getConicPoint().getLabel(tpl),
					orientation.getLabel(tpl));
		}

		// direction is line
		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return getLoc().getPlain(
					"HyperbolaWithFociABPassingThroughCPerpendicularToD",
					getFocus1().getLabel(tpl), getFocus2().getLabel(tpl),
					getConicPoint().getLabel(tpl),
					orientation.getLabel(tpl));
		}
		return getLoc().getPlain(
				"EllipseWithFociABPassingThroughCPerpendicularToD",
				getFocus1().getLabel(tpl), getFocus2().getLabel(tpl),
				getConicPoint().getLabel(tpl),
				orientation.getLabel(tpl));

	}
}
