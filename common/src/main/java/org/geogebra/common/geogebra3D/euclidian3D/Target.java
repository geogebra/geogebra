package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Class for drawing a target when placing a point (in AR)
 *
 */
public class Target {

	private TargetType type;
	private CoordMatrix4x4 dotMatrix = CoordMatrix4x4.identity();
	private CoordMatrix4x4 circleMatrix = CoordMatrix4x4.identity();
	private Coords hittingOrigin = new Coords(4);
	private Coords hittingDirection = new Coords(4);
	private Coords circleNormal = new Coords(3);
	private Coords tmpCoords1 = new Coords(4);
	private Coords tmpCoords2 = new Coords(4);

	/**
	 * Constructor
	 */
	public Target() {
		type = TargetType.NOT_USED;
	}

	/**
	 * 
	 * @return target type
	 */
	public TargetType getType() {
		return type;
	}

	/**
	 * update type for view
	 * 
	 * @param view
	 *            3D view
	 */
	public void updateType(EuclidianView3D view) {
		type = TargetType.getCurrentTargetType(view,
				(EuclidianController3D) view.getEuclidianController());
	}

	/**
	 * draw target
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param view
	 *            3D view
	 */
	public void draw(Renderer renderer, EuclidianView3D view) {
		type.drawTarget(renderer, view, this);
	}

	/**
	 * update matrices regarding view
	 * 
	 * @param view
	 *            3D view
	 */
	public void updateMatrices(EuclidianView3D view) {
		switch (view.getCursor3DType()) {
		case EuclidianView3D.PREVIEW_POINT_FREE:
			// assume free points are on horizontal plane
			setMatrices(view, EuclidianStyleConstants.PREVIEW_POINT_SIZE_WHEN_FREE
					* DrawPoint3D.DRAW_POINT_FACTOR, Coords.VZ);
			break;
		case EuclidianView3D.PREVIEW_POINT_REGION:
			circleNormal.set3(view.getCursor3D().getMoveNormalDirection());
			view.scaleNormalXYZ(circleNormal);
			circleNormal.normalize();
			setMatrices(view,
					EuclidianStyleConstants.PREVIEW_POINT_SIZE_WHEN_FREE
							* DrawPoint3D.DRAW_POINT_FACTOR,
					circleNormal);
			break;
		case EuclidianView3D.PREVIEW_POINT_PATH:
		case EuclidianView3D.PREVIEW_POINT_REGION_AS_PATH:
			circleNormal.set3(view.getCursorPath().getMainDirection());
			view.scaleXYZ(circleNormal);
			circleNormal.normalize();
			setMatrices(view, view.getCursorPath().getLineThickness()
					+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_ON_PATH,
					circleNormal);
			break;
		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			setMatrices(view,
					view.getIntersectionThickness(),
					hittingDirection);
			break;
		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			if (type == TargetType.POINT_ALREADY_NO_ARROW) {
				setMatrices(view, (view.getCursor3D().getPointSize()
						+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_WHEN_ALREADY)
						* DrawPoint3D.DRAW_POINT_FACTOR, hittingDirection);
			}
			break;
		default:
			// do nothing
			break;
		}
	}

	private void updateHitting(EuclidianView3D view) {
		view.getHittingOrigin(view.getEuclidianController().getMouseLoc(),
				hittingOrigin);
		view.getHittingDirection(hittingDirection);
	}

	private void setMatrices(EuclidianView3D view, double dotScale,
			Coords circleNormal) {

		dotMatrix.setOrigin(view.getCursor3D().getDrawingMatrix().getOrigin());
		view.scaleXYZ(dotMatrix.getOrigin());

		dotMatrix.getVx().setMul3(Coords.VX, dotScale);
		dotMatrix.getVy().setMul3(Coords.VY, dotScale);
		dotMatrix.getVz().setMul3(Coords.VZ, dotScale);

		updateHitting(view);
		updateTargetCircleMatrixOrigin(view);

		// WARNING: circleNormal can be hittingDirection, must be updated first
		circleNormal.completeOrthonormal(tmpCoords1, tmpCoords2);
		circleMatrix.setVx(tmpCoords1);
		circleMatrix.setVy(tmpCoords2);
		circleMatrix.setVz(circleNormal);
	}

	private void updateTargetCircleMatrixOrigin(EuclidianView3D view) {
		view.getCursor3D().getDrawingMatrix().getOrigin()
				.projectLine(hittingOrigin, hittingDirection, tmpCoords2);
		circleMatrix.setOrigin(tmpCoords2);
		view.scaleXYZ(circleMatrix.getOrigin());
	}

	/**
	 * 
	 * @return current dot matrix
	 */
	public CoordMatrix4x4 getDotMatrix() {
		return dotMatrix;
	}

	/**
	 * 
	 * @return current circle matrix
	 */
	public CoordMatrix4x4 getCircleMatrix() {
		return circleMatrix;
	}
}
