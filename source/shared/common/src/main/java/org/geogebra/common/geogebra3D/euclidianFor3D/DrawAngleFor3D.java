package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author mathieu
 *
 */
public class DrawAngleFor3D extends DrawAngle {

	/**
	 * @param view
	 *            view where the drawable is created
	 * @param angle
	 *            angle
	 */
	public DrawAngleFor3D(EuclidianView view, GeoAngle angle) {
		super(view, angle);
	}

	@Override
	public boolean inView(Coords point) {
		// Coords p = view.getCoordsForView(point);
		return DoubleUtil.isZero(point.getZ());
	}

	@Override
	public Coords getCoordsInView(Coords point) {
		return view.getCoordsForView(point);
	}

	@Override
	protected double getAngleStart(double start, double extent) {

		if (view.getCompanion().goToZPlus(
				((AlgoAngle) getGeoElement().getDrawAlgorithm()).getVn())) {
			return super.getAngleStart(start, extent);
		}

		// reverse orientation
		return start - extent;

	}

}
