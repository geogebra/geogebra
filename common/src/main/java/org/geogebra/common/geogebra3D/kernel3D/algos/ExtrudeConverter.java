package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.CoordConverter;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;

public class ExtrudeConverter implements CoordConverter {

	private Coords project1;
	private Coords project2;
	private double[] lineCoords;
	private double[] tmp;

	@Override
	public double translationToValue(Coords direction, Coords rwTransVec,
			double startValue, EuclidianView view) {
		return snap(direction.dotproduct3(rwTransVec) + startValue, view);
	}

	@Override
	public double snap(double val, EuclidianView view) {
		double g = view.getGridDistances(0);
		double valRound = Kernel.roundToScale(val, g);
		if (view.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
				|| (Math.abs(valRound - val) < g * view.getEuclidianController()
						.getPointCapturingPercentage())) {
			return valRound;
		}
		return val;
	}

	@Override
	public void record(ChangeableParent parent, Coords startPoint) {
		// not needed
	}

	@Override
	public void updateTranslation(Coords startPoint3D, Coords direction,
			Coords rayOrigin, Coords rayDirection, Coords translationVec3D) {
		if (project1 == null) {
			project1 = new Coords(4);
			project2 = new Coords(4);
			lineCoords = new double[2];
			tmp = new double[4];
		}
		CoordMatrixUtil.nearestPointsFromTwoLines(startPoint3D,
				direction, rayOrigin, rayDirection, project1.val, project2.val,
				lineCoords,
				tmp);

		// if two lines are parallel, it will return NaN
		if (Double.isNaN(lineCoords[0])) {
			translationVec3D.setUndefined();
		} else {
			translationVec3D.setSub3(project1, startPoint3D);
		}
	}

}
