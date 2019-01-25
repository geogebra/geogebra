package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.AnimatableValue;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class for drawing a target when placing a point (in AR)
 *
 */
public class Target {

	static final private double ANIMATION_DURATION = 250;

	private TargetType type;
	private CoordMatrix4x4 dotMatrix;
	private CoordMatrix4x4 circleMatrix;
	private Coords hittingOrigin;
	private Coords hittingDirection;
	private Coords tmpNormal;
	private Coords tmpCoords1;
	private Coords tmpCoords2;

	private AnimDouble animDotScale;
	private AnimatableDouble dotScaleGoal;

	private AnimCircleRotation animCircleRotation;
	private AnimPosition animCircleCenter;
	private CoordsAndGeo circleCenterGoal;

	private class CoordsAndGeo implements AnimatableValue<CoordsAndGeo> {
		public Coords coords;
		public GeoElement geo;

		/**
		 * constructor
		 */
		public CoordsAndGeo() {
			coords = new Coords(4);
		}

		@Override
		public boolean equalsForAnimation(CoordsAndGeo other) {
			return (geo != null && geo == other.geo)
					|| coords.equalsForAnimation(other.coords);
		}

		@Override
		public boolean isDefined() {
			return coords.isDefined();
		}

		@Override
		public void setAnimatableValue(CoordsAndGeo other) {
			coords.set3(other.coords);
			geo = other.geo;
		}

		@Override
		public void setUndefined() {
			coords.setUndefined();
		}
	}

	private class AnimatableDouble
			implements AnimatableValue<AnimatableDouble> {

		public double value;
		public boolean isDefined;

		public AnimatableDouble() {
			isDefined = false;
		}

		@Override
		public boolean equalsForAnimation(AnimatableDouble other) {
			return DoubleUtil.isEqual(value, other.value);
		}

		@Override
		public boolean isDefined() {
			return isDefined;
		}

		@Override
		public void setAnimatableValue(AnimatableDouble other) {
			value = other.value;
			isDefined = other.isDefined;
		}

		@Override
		public void setUndefined() {
			isDefined = false;
		}

		/**
		 * set value
		 * 
		 * @param v
		 *            value
		 */
		public void setValue(double v) {
			value = v;
			isDefined = Double.isFinite(value);
		}

	}

	/**
	 * For animating values
	 * 
	 * @param <T>
	 *            value type
	 */
	protected abstract class Anim<T extends AnimatableValue<T>> {

		/** previous value */
		protected T previous;
		/** next value */
		protected T next;
		/** current value */
		protected T current;

		private double firstPrepare;
		private double lastPrepare;
		private double duration;
		private boolean isAnimated;
		private double lastUpdate;

		/**
		 * constructor
		 */
		public Anim() {
			init();
			isAnimated = false;
			previous.setUndefined();
		}

		/**
		 * init values
		 */
		abstract protected void init();

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
			boolean needsNewAnim = !goal.equalsForAnimation(next);
			if (isAnimated || needsNewAnim) {
				previous.setAnimatableValue(current);
				next.setAnimatableValue(goal);
				if (previous.isDefined()) {
					compute();
					if (isAnimated && !needsNewAnim) {
						duration = ANIMATION_DURATION
								- (lastUpdate - firstPrepare);
						lastPrepare = lastUpdate;
					} else {
						duration = ANIMATION_DURATION;
						firstPrepare = UtilFactory.getPrototype()
								.getMillisecondTime();
						lastPrepare = firstPrepare;
						isAnimated = true;
					}
				} else {
					isAnimated = false;
					// next.setAnimatableValue(goal);
				}
			} else if (!isAnimated) {
				previous.setUndefined();
				next.setAnimatableValue(goal);
			}
		}

		/**
		 * calculate current value for remaining time
		 * 
		 * @param remaining
		 *            remaining time
		 */
		abstract protected void calculateCurrent(double remaining);

		/**
		 * set previous value as undefined
		 */
		public void setUndefined() {
			previous.setUndefined();
			current.setUndefined();
		}

		/**
		 * update current value
		 */
		public void updateCurrent() {
			if (!isAnimated) {
				current.setAnimatableValue(next);
			} else if (previous.isDefined()) {
				lastUpdate = UtilFactory.getPrototype().getMillisecondTime();
				double elapsed = lastUpdate - lastPrepare;
				if (elapsed < duration) {
					isAnimated = true;
					calculateCurrent((duration - elapsed) / duration);
				} else {
					isAnimated = false;
					previous.setUndefined();
					current.setAnimatableValue(next);
				}
			} else {
				isAnimated = false;
				current.setAnimatableValue(next);
			}
		}

		/**
		 * 
		 * @return current value
		 */
		final public T getCurrent() {
			return current;
		}
	}

	private class AnimCircleRotation extends Anim<Coords> {

		private Coords axis;
		private double angle;
		private Coords tmpCoords;

		public AnimCircleRotation() {
			super();
		}

		@Override
		protected void init() {
			previous = new Coords(4);
			next = new Coords(4);
			current = new Coords(4);
			axis = new Coords(4);
			tmpCoords = new Coords(4);
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
		protected void calculateCurrent(double remaining) {
			double a = angle * remaining;
			current.setMul3(previous, Math.sin(a));
			tmpCoords.setMul3(next, Math.cos(a));
			current.setAdd3(current, tmpCoords);
		}

	}

	private class AnimPosition extends Anim<CoordsAndGeo> {

		private Coords tmpCoords;

		public AnimPosition() {
			super();
		}

		@Override
		protected void init() {
			previous = new CoordsAndGeo();
			next = new CoordsAndGeo();
			current = new CoordsAndGeo();
			current.coords.setW(1);
			tmpCoords = new Coords(4);
		}

		@Override
		protected void compute() {
			// nothing to do
		}

		@Override
		protected void calculateCurrent(double remaining) {
			current.coords.setMul3(previous.coords, remaining);
			tmpCoords.setMul3(next.coords, 1 - remaining);
			current.coords.setAdd3(current.coords, tmpCoords);
		}

	}

	private class AnimDouble extends Anim<AnimatableDouble> {

		public AnimDouble() {
			super();
		}

		@Override
		protected void init() {
			previous = new AnimatableDouble();
			next = new AnimatableDouble();
			current = new AnimatableDouble();
		}

		@Override
		protected void compute() {
			// nothing to do
		}

		@Override
		protected void calculateCurrent(double remaining) {
			current.value = previous.value * remaining
					+ next.value * (1 - remaining);
			current.isDefined = true;
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
		if (tmpNormal == null) {
			tmpNormal = new Coords(3);
			initSynced();
		}
		type = TargetType.getCurrentTargetType(view,
				(EuclidianController3D) view.getEuclidianController());
	}

	synchronized private void initSynced() {
		dotMatrix = CoordMatrix4x4.identity();
		circleMatrix = CoordMatrix4x4.identity();
		hittingOrigin = new Coords(4);
		hittingDirection = new Coords(4);

		tmpCoords1 = new Coords(4);
		tmpCoords2 = new Coords(4);

		animDotScale = new AnimDouble();
		dotScaleGoal = new AnimatableDouble();

		animCircleRotation = new AnimCircleRotation();
		animCircleCenter = new AnimPosition();
		circleCenterGoal = new CoordsAndGeo();
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

	synchronized private void setCentersGoal(GeoElement geo) {
		circleCenterGoal.geo = geo;
	}

	synchronized private void setAnimationsUndefined() {
		animCircleCenter.setUndefined();
		animCircleRotation.setUndefined();
		animDotScale.setUndefined();
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
			setCentersGoal(null);
			// assume free points are on horizontal plane
			setMatrices(view, EuclidianStyleConstants.PREVIEW_POINT_SIZE_WHEN_FREE
					* DrawPoint3D.DRAW_POINT_FACTOR, Coords.VZ);
			break;
		case EuclidianView3D.PREVIEW_POINT_REGION:
			tmpNormal.set3(view.getCursor3D().getMoveNormalDirection());
			view.scaleNormalXYZ(tmpNormal);
			tmpNormal.normalize();
			setCentersGoal((GeoElement) view.getCursor3D().getRegion());
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
			setCentersGoal(view.getCursorPath());
			setMatrices(view, view.getCursorPath().getLineThickness()
					+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_ON_PATH,
					tmpNormal);
			break;
		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			setCentersGoal(null);
			setMatrices(view,
					view.getIntersectionThickness());
			break;
		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			if (type == TargetType.POINT_ALREADY_NO_ARROW) {
				setCentersGoal(null);
				setMatrices(view, (view.getCursor3D().getPointSize()
						+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_WHEN_ALREADY)
						* DrawPoint3D.DRAW_POINT_FACTOR);
			}
			break;
		default:
			setCentersGoal(null);
			setAnimationsUndefined();
			// do nothing
			break;
		}
	}

	synchronized private void setMatrices(EuclidianView3D view,
			double dotScale) {
		setMatrices(view, dotScale, hittingDirection);
	}

	synchronized private void setMatrices(EuclidianView3D view, double dotScale,
			Coords circleNormal) {

		dotMatrix.setOrigin(view.getCursor3D().getDrawingMatrix().getOrigin());
		view.scaleXYZ(dotMatrix.getOrigin());

		// dot scale
		dotScaleGoal.setValue(dotScale);
		animDotScale.prepareAnimation(dotScaleGoal);

		// set hitting
		view.getHittingOrigin(view.getEuclidianController().getMouseLoc(),
				hittingOrigin);
		view.getHittingDirection(hittingDirection);

		// circle center
		view.getCursor3D().getDrawingMatrix().getOrigin().projectLine(
				hittingOrigin, hittingDirection, circleCenterGoal.coords);
		view.scaleXYZ(circleCenterGoal.coords);
		animCircleCenter.prepareAnimation(circleCenterGoal);

		// circle orientation
		// WARNING: circleNormal can be hittingDirection which must be updated
		// first
		animCircleRotation.prepareAnimation(circleNormal);

	}

	/**
	 * 
	 * @return current dot matrix
	 */
	synchronized public CoordMatrix4x4 getDotMatrix() {

		animDotScale.updateCurrent();
		double scale = animDotScale.getCurrent().value;
		dotMatrix.getVx().setMul3(Coords.VX, scale);
		dotMatrix.getVy().setMul3(Coords.VY, scale);
		dotMatrix.getVz().setMul3(Coords.VZ, scale);

		return dotMatrix;
	}

	/**
	 * 
	 * @return current circle matrix
	 */
	synchronized public CoordMatrix4x4 getCircleMatrix() {
		animCircleCenter.updateCurrent();
		circleMatrix.setOrigin(animCircleCenter.getCurrent().coords);

		animCircleRotation.updateCurrent();
		animCircleRotation.getCurrent().completeOrthonormal(tmpCoords1, tmpCoords2);
		circleMatrix.setVx(tmpCoords1);
		circleMatrix.setVy(tmpCoords2);
		circleMatrix.setVz(animCircleRotation.getCurrent());

		return circleMatrix;
	}
}
