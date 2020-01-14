package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCursor3D;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.AnimatableValue;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class for drawing a target when placing a point (in AR)
 *
 */
public class Target {

	static final private double CIRCLE_ANIMATION_DURATION = 250;
	static final private double DOT_ANIMATION_DURATION = 100;

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
	private AnimPosition animDotCenter;
	private PositionAndGeo dotCenterGoal;

	private AnimCircleRotation animCircleRotation;
	private NormalAndGeo circleNormalGoal;
	private AnimPosition animCircleCenter;
	private PositionAndGeo circleCenterGoal;

	static abstract private class CoordsAndGeo<T extends CoordsAndGeo<T>>
			implements AnimatableValue<T> {
		public Coords coords;
		public long geo1;
		public long geo2;

		/**
		 * constructor
		 */
		public CoordsAndGeo() {
			coords = new Coords(4);
		}

		@Override
		public boolean equalsForAnimation(T other) {
			return (geo1 == other.geo1 && geo2 == other.geo2) || coordsEquals(other);
		}

		abstract protected boolean coordsEquals(T other);

		@Override
		public boolean isDefined() {
			return coords.isDefined();
		}

		@Override
		public void setAnimatableValue(T other) {
			coords.set3(other.coords);
			geo1 = other.geo1;
            geo2 = other.geo2;
		}

		@Override
		public void setUndefined() {
			coords.setUndefined();
		}
	}

	static private class PositionAndGeo extends CoordsAndGeo<PositionAndGeo> {

		public PositionAndGeo() {
			super();
		}

		@Override
		protected boolean coordsEquals(PositionAndGeo other) {
			return coords.equalsForKernel(other.coords);
		}

	}

	static private class NormalAndGeo extends CoordsAndGeo<NormalAndGeo> {

		public NormalAndGeo() {
			super();
		}

		@Override
		protected boolean coordsEquals(NormalAndGeo other) {
			return coords.equalsForAnimation(other.coords);
		}

	}

	static private class AnimatableDouble
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
			isDefined = MyDouble.isFinite(value);
		}

	}

	/**
	 * For animating values
	 * 
	 * @param <T>
	 *            value type
	 */
	static protected abstract class Anim<T extends AnimatableValue<T>> {

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
		final private double totalDuration;

		/**
		 * constructor
		 * 
		 * @param totalDuration
		 *            total duration for one animation loop
		 */
		public Anim(double totalDuration) {
			this.totalDuration = totalDuration;
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
						duration = totalDuration - (lastUpdate - firstPrepare);
						lastPrepare = lastUpdate;
					} else {
						duration = totalDuration;
						firstPrepare = UtilFactory.getPrototype()
								.getMillisecondTime();
						lastPrepare = firstPrepare;
						// in case updateCurrent() is not called between two
						// prepareAnimation() calls
						lastUpdate = firstPrepare;
						isAnimated = true;
					}
				} else {
					isAnimated = false;
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

	static private class AnimCircleRotation extends Anim<NormalAndGeo> {

		private Coords axis;
		private double angle;
		private Coords tmpCoords;

		public AnimCircleRotation(double totalDuration) {
			super(totalDuration);
		}

		@Override
		protected void init() {
			previous = new NormalAndGeo();
			next = new NormalAndGeo();
			current = new NormalAndGeo();
			axis = new Coords(4);
			tmpCoords = new Coords(4);
		}

		@Override
		protected void compute() {
			axis.setCrossProduct3(previous.coords, next.coords);
			double l = axis.calcNorm();
			double cos = previous.coords.dotproduct3(next.coords);
			axis.mulInside3(1 / l);
			previous.coords.setCrossProduct3(next.coords, axis);
			angle = l > 1 ? Math.PI / 2 : Math.asin(l);
			if (cos < 0) {
				next.coords.mulInside3(-1);
			}
		}

		@Override
		protected void calculateCurrent(double remaining) {
			double a = angle * remaining;
			current.coords.setMul3(previous.coords, Math.sin(a));
			tmpCoords.setMul3(next.coords, Math.cos(a));
			current.coords.setAdd3(current.coords, tmpCoords);
		}

	}

	static private class AnimPosition extends Anim<PositionAndGeo> {

		private Coords tmpCoords;

		public AnimPosition(double totalDuration) {
			super(totalDuration);
		}

		@Override
		protected void init() {
			previous = new PositionAndGeo();
			next = new PositionAndGeo();
			current = new PositionAndGeo();
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

	static private class AnimDouble extends Anim<AnimatableDouble> {

		public AnimDouble(double totalDuration) {
			super(totalDuration);
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

		animDotScale = new AnimDouble(DOT_ANIMATION_DURATION);
		dotScaleGoal = new AnimatableDouble();

		animDotCenter = new AnimPosition(DOT_ANIMATION_DURATION);
		dotCenterGoal = new PositionAndGeo();

		animCircleRotation = new AnimCircleRotation(CIRCLE_ANIMATION_DURATION);
		circleNormalGoal = new NormalAndGeo();
		animCircleCenter = new AnimPosition(CIRCLE_ANIMATION_DURATION);
		circleCenterGoal = new PositionAndGeo();
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

	private void setCentersGoal(GeoElement geo) {
        setCentersGoal(geo == null ? GeoCursor3D.NO_SOURCE : geo.getID());
    }

    private void setCentersGoal(long id) {
	    setCentersGoal(id, GeoCursor3D.NO_SOURCE);
    }

    synchronized private void setCentersGoal(long geo1, long geo2) {
        dotCenterGoal.geo1 = geo1;
        dotCenterGoal.geo2 = geo2;
        circleCenterGoal.geo1 = geo1;
        circleCenterGoal.geo2 = geo2;
        circleNormalGoal.geo1 = geo1;
        circleNormalGoal.geo2 = geo2;
    }

	synchronized private void setAnimationsUndefined() {
		animCircleCenter.setUndefined();
		animCircleRotation.setUndefined();
		animDotScale.setUndefined();
		animDotCenter.setUndefined();
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
			if (view.getCursor3D().getIsCaptured()) {
				setCentersGoal(GeoCursor3D.CAPTURED);
			} else {
				setCentersGoal((GeoElement) view.getCursor3D().getRegion());
			}
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
			if (view.getCursor3D().getIsCaptured()) {
				setCentersGoal(GeoCursor3D.CAPTURED);
			} else {
				setCentersGoal(view.getCursorPath());
			}
			setMatrices(view, view.getCursorPath().getLineThickness()
					+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_ON_PATH,
					tmpNormal);
			break;
		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			setCentersGoal(view.getCursor3D().getSource1(), view.getCursor3D().getSource2());
			setMatrices(view,
					view.getIntersectionThickness());
			break;
		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			if (type == TargetType.POINT_ALREADY_NO_ARROW) {
				setCentersGoal(view.getCursor3D().getSource1());
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

		// dot center
		dotCenterGoal.coords
				.set3(view.getCursor3D().getDrawingMatrix().getOrigin());
		view.scaleXYZ(dotCenterGoal.coords);
		animDotCenter.prepareAnimation(dotCenterGoal);

		// dot scale
		dotScaleGoal.setValue(dotScale);
		animDotScale.prepareAnimation(dotScaleGoal);

		// set hitting
		// WARNING: circleNormal can be hittingDirection which must be updated
		// first
		view.getHittingOrigin(view.getEuclidianController().getMouseLoc(),
				hittingOrigin);
		view.getHittingDirection(hittingDirection);

		// circle center (aligned with hittingDirection)
		circleNormal.completeOrthonormal(tmpCoords1, tmpCoords2);
		hittingOrigin.projectPlaneThruVIfPossible(tmpCoords1, tmpCoords2,
				circleNormal,
				view.getCursor3D().getDrawingMatrix().getOrigin(),
				hittingDirection, circleCenterGoal.coords);
		view.scaleXYZ(circleCenterGoal.coords);
		animCircleCenter.prepareAnimation(circleCenterGoal);

		// circle orientation
		circleNormalGoal.coords.set3(circleNormal);
		animCircleRotation.prepareAnimation(circleNormalGoal);

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

		animDotCenter.updateCurrent();
		dotMatrix.setOrigin(animDotCenter.getCurrent().coords);

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
		animCircleRotation.getCurrent().coords.completeOrthonormal(tmpCoords1,
				tmpCoords2);
		circleMatrix.setVx(tmpCoords1);
		circleMatrix.setVy(tmpCoords2);
		circleMatrix.setVz(animCircleRotation.getCurrent().coords);

		return circleMatrix;
	}
}
