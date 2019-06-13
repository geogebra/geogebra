package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.CoordConverter;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

public class RotationConverter implements CoordConverter {

	private GeoLineND axis;
	private Coords rotationCenter = new Coords(3);
	private Coords vStart = new Coords(3);
	private Coords vCurrent = new Coords(3);
	private Coords result = Coords.createInhomCoorsInD3();
	private Coords lastStartPoint = Coords.createInhomCoorsInD3();
	private Coords crossProduct = new Coords(4);
	private Coords vStartNormalized = new Coords(4);

	public RotationConverter(GeoLineND axis) {
		this.axis = axis;
	}

	@Override
	public double translationToValue(Coords direction, Coords rwTransVec, double startValue, EuclidianView view) {
		vCurrent.setAdd3(vStart, rwTransVec);
		double sin = direction.dotCrossProduct(vStart, vCurrent);
		double cos = vStart.dotproduct3(vCurrent);
		Log.debug(Math.atan2(sin, cos) / Kernel.PI_180);

		return (Math.atan2(sin, cos) + startValue + Kernel.PI_2) % Kernel.PI_2;
	}

	@Override
	public void record(ChangeableParent parent, Coords startPoint) {
		startPoint.projectLine(axis.getStartInhomCoords(),
				axis.getDirectionInD3(), rotationCenter);
		vStart.setSub3(startPoint, rotationCenter);
		this.lastStartPoint.set3(startPoint);
	}

	@Override
	public void updateTranslation(Coords startPoint3D, Coords direction,
			Coords rayOrigin, Coords rayDirection, Coords translationVec3D) {
		crossProduct.setCrossProduct3(vStart, direction);
		vStartNormalized.set3(vStart);
		vStartNormalized.normalize();
		rayOrigin.projectPlane(vStartNormalized, crossProduct, rayDirection,
				startPoint3D,
				result);
		// if ray direction is parallel to the plane
		if (DoubleUtil.isZero(result.getW())) {
			translationVec3D.setUndefined();
		} else {
			translationVec3D.setSub3(result, startPoint3D);
		}

	}


}
