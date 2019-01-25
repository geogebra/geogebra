package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.factories.UtilFactory;
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

	static final private double CIRCLE_ANIMATION_DURATION = 250;

	private TargetType type;
	private CoordMatrix4x4 dotMatrix;
	private CoordMatrix4x4 circleMatrix;
	private Coords hittingOrigin;
	private Coords hittingDirection;
	private Coords tmpNormal;
	private Coords tmpCoords1;
	private Coords tmpCoords2;

	private AnimCircleRotation animCircleRotation;

	/**
	 * For animating values
	 * 
	 * @param <T>
	 *            value type
	 */
	protected abstract class Anim<T> {

		private double lastChange;

		/**
		 * 
		 * @return true if previous value is defined
		 */
		abstract protected boolean isPreviousDefined();

		/**
		 * 
		 * @param goal
		 *            goal value
		 * @return true if it needs an animation to reach goal value
		 */
		abstract protected boolean needsAnim(T goal);

		/**
		 * set previous value to current value
		 */
		abstract protected void setPreviousToCurrent();

		/**
		 * set next value to goal value
		 * 
		 * @param goal
		 *            goal value
		 */
		abstract protected void setNext(T goal);

		/**
		 * compute animation
		 */
		abstract protected void compute();

		/**
		 * prepare animation
		 * 
		 * @param goal
		 *            goal value
		 */
		public void prepareAnimation(T goal) {
			if (needsAnim(goal)) {
				setPreviousToCurrent();
				setNext(goal);
				if (isPreviousDefined()) {
					compute();
					lastChange = UtilFactory.getPrototype()
							.getMillisecondTime();
				}
			}
		}

		/**
		 * calculate current value for elapsed time
		 * 
		 * @param elapsed
		 *            elapsed time
		 */
		abstract protected void calculateCurrent(double elapsed);

		/**
		 * set current value to next value
		 */
		abstract protected void setCurrentToNext();

		/**
		 * set previous value as undefined
		 */
		abstract protected void setPreviousUndefined();

		/**
		 * update current value
		 */
		public void updateCurrent() {
			if (isPreviousDefined()) {
				double elapsed = UtilFactory.getPrototype().getMillisecondTime()
						- lastChange;
				if (elapsed < CIRCLE_ANIMATION_DURATION) {
					calculateCurrent(elapsed);
				} else {
					setPreviousUndefined();
					setCurrentToNext();
				}
			} else {
				setCurrentToNext();
			}
		}

		/**
		 * 
		 * @return current value
		 */
		abstract public T getCurrent();
	}

	private class AnimCircleRotation extends Anim<Coords> {

		private Coords previous;
		private Coords next;
		private Coords current;
		private Coords axis;
		private double angle;

		private Coords tmpCoords;

		public AnimCircleRotation() {
			previous = new Coords(4);
			next = new Coords(4);
			current = new Coords(4);
			axis = new Coords(4);
			tmpCoords = new Coords(4);
		}

		@Override
		protected boolean isPreviousDefined() {
			return previous.isDefined();
		}

		@Override
		protected boolean needsAnim(Coords goal) {
			return !goal.equalsForKernel(next);
		}

		@Override
		protected void setPreviousToCurrent() {
			previous.set3(current);
		}

		@Override
		protected void setNext(Coords goal) {
			next.set3(goal);
		}

		@Override
		protected void compute() {
			axis.setCrossProduct3(previous, next);
			double l = axis.calcNorm();
			double cos = previous.dotproduct3(next);
			axis.mulInside3(1 / l);
			previous.setCrossProduct3(next, axis);
			angle = l > 1 ? Math.PI / 2 : Math.asin(l);
			if (cos < 0) {
				next.mulInside3(-1);
			}

		}

		@Override
		protected void setPreviousUndefined() {
			previous.setUndefined();
		}

		@Override
		protected void calculateCurrent(double elapsed) {
			double a = angle * (1 - elapsed / CIRCLE_ANIMATION_DURATION);
			current.setMul3(previous, Math.sin(a));
			tmpCoords.setMul3(next, Math.cos(a));
			current.setAdd3(current, tmpCoords);

		}

		@Override
		protected void setCurrentToNext() {
			current.set3(next);
		}

		@Override
		public Coords getCurrent() {
			return current;
		}

	}

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
	    if (dotMatrix == null) {
            dotMatrix = CoordMatrix4x4.identity();
            circleMatrix = CoordMatrix4x4.identity();
            hittingOrigin = new Coords(4);
            hittingDirection = new Coords(4);
            tmpNormal = new Coords(3);
            tmpCoords1 = new Coords(4);
            tmpCoords2 = new Coords(4);
			animCircleRotation = new AnimCircleRotation();
        }
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
			tmpNormal.set3(view.getCursor3D().getMoveNormalDirection());
			view.scaleNormalXYZ(tmpNormal);
			tmpNormal.normalize();
			setMatrices(view,
					EuclidianStyleConstants.PREVIEW_POINT_SIZE_WHEN_FREE
							* DrawPoint3D.DRAW_POINT_FACTOR,
					tmpNormal);
			break;
		case EuclidianView3D.PREVIEW_POINT_PATH:
		case EuclidianView3D.PREVIEW_POINT_REGION_AS_PATH:
			tmpNormal.set3(view.getCursorPath().getMainDirection());
			view.scaleXYZ(tmpNormal);
			tmpNormal.normalize();
			setMatrices(view, view.getCursorPath().getLineThickness()
					+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_ON_PATH,
					tmpNormal);
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

	synchronized private void setMatrices(EuclidianView3D view, double dotScale,
			Coords circleNormal) {

		dotMatrix.setOrigin(view.getCursor3D().getDrawingMatrix().getOrigin());
		view.scaleXYZ(dotMatrix.getOrigin());

		dotMatrix.getVx().setMul3(Coords.VX, dotScale);
		dotMatrix.getVy().setMul3(Coords.VY, dotScale);
		dotMatrix.getVz().setMul3(Coords.VZ, dotScale);

		view.getHittingOrigin(view.getEuclidianController().getMouseLoc(),
				hittingOrigin);
		view.getHittingDirection(hittingDirection);
		view.getCursor3D().getDrawingMatrix().getOrigin()
				.projectLine(hittingOrigin, hittingDirection, tmpCoords2);
		circleMatrix.setOrigin(tmpCoords2);
		view.scaleXYZ(circleMatrix.getOrigin());

		// WARNING: circleNormal can be hittingDirection which must be updated
		// first
		animCircleRotation.prepareAnimation(circleNormal);

	}

	/**
	 * 
	 * @return current dot matrix
	 */
	synchronized public CoordMatrix4x4 getDotMatrix() {
		return dotMatrix;
	}

	/**
	 * 
	 * @return current circle matrix
	 */
	synchronized public CoordMatrix4x4 getCircleMatrix() {
		animCircleRotation.updateCurrent();
		animCircleRotation.getCurrent().completeOrthonormal(tmpCoords1, tmpCoords2);
		circleMatrix.setVx(tmpCoords1);
		circleMatrix.setVy(tmpCoords2);
		circleMatrix.setVz(animCircleRotation.getCurrent());
		return circleMatrix;
	}
}
