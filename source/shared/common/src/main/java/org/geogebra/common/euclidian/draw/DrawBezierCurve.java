package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.ControlPointHandler;
import org.geogebra.common.euclidian.CurveBoundingBox;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoBezierCurve;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

public class DrawBezierCurve extends DrawParametricCurve {

	private CurveBoundingBox boundingBox;
	private final AlgoBezierCurve algo;

	/**
	 * @param view graphics view
	 * @param geo curve
	 * @param parentAlgorithm curve's parent algorithm
	 */
	public DrawBezierCurve(EuclidianView view, ParametricCurve geo,
			AlgoBezierCurve parentAlgorithm) {
		super(view, geo);
		algo = parentAlgorithm;
	}

	@Override
	public CurveBoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new CurveBoundingBox();
			boundingBox.setColor(view.getApplication().getPrimaryColor());
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	@Override
	public void updateBoundingBox() {
		getBoundingBox().setRectangle(AwtFactory.getPrototype().newRectangle(gp.getBounds()));
		for (int i = 0; i < 4; i ++) {
			boundingBox.setHandlerFromCenter(i,
					view.toScreenCoordXd(algo.getPoints()[i].getInhomX()),
					view.toScreenCoordYd(algo.getPoints()[i].getInhomY()));
		}
	}

	@Override
	public void updateByControlPointMovement(GPoint2D point,
			ControlPointHandler handler) {
		GeoPointND updated = algo.getPoints()[handler.id];
		GeoPointND control = null;
		double realX = view.toRealWorldCoordX(point.getX());
		double realY = view.toRealWorldCoordY(point.getY());
		if (handler.id == 0) {
			control = algo.getPoints()[1];
		}
		if (handler.id == 3) {
			control = algo.getPoints()[2];
		}
		double dx = realX - updated.getInhomX();
		double dy = realY - updated.getInhomY();
		updated.setCoords(realX, realY, 1);
		if (control != null) {
			control.translate(new Coords(dx, dy));
		}
		algo.update();
		view.getKernel().notifyRepaint();
	}

	@Override
	public ArrayList<GPoint2D> toPoints() {
		ArrayList<GPoint2D> points = new ArrayList<>();
		for (GeoPointND pt : algo.getPoints()) {
			points.add(
					new MyPoint(view.toScreenCoordXd(pt.getInhomX()),
							view.toScreenCoordYd(pt.getInhomY())));
		}
		return points;
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		int i = 0;
		for (GeoPointND pt : algo.getPoints()) {
			pt.setCoords(view.toRealWorldCoordX(points.get(i).getX()),
					view.toRealWorldCoordY(points.get(i).getY()), 1);
			i++;
		}
		algo.update();
	}
}
