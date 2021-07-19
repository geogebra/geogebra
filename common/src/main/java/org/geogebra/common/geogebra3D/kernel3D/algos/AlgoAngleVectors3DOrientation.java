package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.util.DoubleUtil;

/**
 * angle for three points, oriented
 * 
 * @author mathieu
 */
public class AlgoAngleVectors3DOrientation extends AlgoAngleVectors3D {

	private GeoDirectionND orientation;

	/**
	 * @param cons
	 *            construction
	 * @param v
	 *            vector
	 * @param w
	 *            vector
	 * @param orientation
	 *            orientation
	 */
	AlgoAngleVectors3DOrientation(Construction cons,
			GeoVectorND v, GeoVectorND w, GeoDirectionND orientation) {
		super(cons, v, w, orientation);
	}

	@Override
	protected void setInput(GeoVectorND v, GeoVectorND w,
			GeoDirectionND orientation) {

		super.setInput(v, w, orientation);
		this.orientation = orientation;
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) getv();
		input[1] = (GeoElement) getw();
		input[2] = (GeoElement) orientation;

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

		checkOrientation(vn, orientation, getAngle(), false);
	}

	@Override
	public String toString(StringTemplate tpl) {

		// return loc.getPlain("AngleBetweenABOrientedByC",
		// getv().getLabel(tpl),
		// getw().getLabel(tpl), orientation.getLabel(tpl));

		// clearer just as "angle between u and v"
		return getLoc().getPlain("AngleBetweenAB", getv().getLabel(tpl),
				getw().getLabel(tpl));

	}

}
