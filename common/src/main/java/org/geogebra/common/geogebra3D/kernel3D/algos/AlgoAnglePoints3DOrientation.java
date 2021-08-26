package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

/**
 * angle for three points, oriented
 * 
 * @author mathieu
 */
public class AlgoAnglePoints3DOrientation extends AlgoAnglePoints3D {

	private GeoDirectionND orientation;
	private boolean isReversed;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @param orientation
	 *            orientation
	 */
	AlgoAnglePoints3DOrientation(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		super(cons, A, B, C, orientation);
	}

	/**
	 * @param cons
	 *            construction
	 * @param orientation
	 *            orientation
	 * @param isReversed
	 *            if orientation is reversed
	 */
	public AlgoAnglePoints3DOrientation(Construction cons,
			GeoDirectionND orientation, boolean isReversed) {
		super(cons);
		this.orientation = orientation;
		this.isReversed = isReversed;
	}

	@Override
	protected void setInput(GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation) {

		super.setInput(A, B, C, orientation);
		this.orientation = orientation;
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = (GeoElement) getA();
		input[1] = (GeoElement) getB();
		input[2] = (GeoElement) getC();
		input[3] = (GeoElement) orientation;

		setOutputLength(1);
		setOutput(0, getAngle());
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {

		super.compute();

		if (orientation == kernel.getSpace()) { // no orientation with space
			return;
		}

		if (!getAngle().isDefined() || DoubleUtil.isZero(getAngle().getValue())) {
			return;
		}

		checkOrientation(vn, orientation, getAngle(), isReversed);
	}

	@Override
	protected void setForceNormalVector() {
		vn = v1.crossProduct4(v2);

		if (vn.isZero()) { // v1 and v2 are dependent
			if (orientation == kernel.getSpace()) { // no orientation with space
				vn = crossXorY(v1);
			} else {
				vn = orientation.getDirectionInD3().copyVector();
			}
		}

		vn.normalize();

	}

	@Override
	public String toString(StringTemplate tpl) {

		return getLoc().getPlain("AngleBetweenABC", getA().getLabel(tpl),
				getB().getLabel(tpl), getC().getLabel(tpl));
	}

}
