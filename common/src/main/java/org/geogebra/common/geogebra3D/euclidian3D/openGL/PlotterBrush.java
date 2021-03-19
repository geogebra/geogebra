package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.DrawVector;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrushSection.TickStep;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * 3D brush, drawing circular-section curves.
 * 
 * @author mathieu
 *
 */
public class PlotterBrush implements PathPlotter {

	/** thickness for drawing 3D lines */
	public static final float LINE3D_THICKNESS = 0.5f;

	/** manager */
	protected Manager manager;

	/** index */
	private int index;

	/** start and end sections */
	protected PlotterBrushSection start;
	/** start and end sections */
	protected PlotterBrushSection end;

	private boolean justStarted = false;
	private boolean notStarted = false;

	/** current thickness */
	private float thickness;
	private float lineThickness;

	/** view scale */
	private float scale;

	/** global length of the curve */
	protected float length;

	// color
	/** color r, g, b, a */
	private float red;
	private float green;
	private float blue;
	private float alpha;
	/** says if it's colored */
	private boolean hasColor;

	// texture
	/** start and end textures values */
	private float texturePosZero;
	private float textureValZero;
	/** textures coords */
	private float[] textureX = new float[2];
	/** type of texture */
	static final public int TEXTURE_CONSTANT_0 = 0;
	static final private int TEXTURE_ID = 1;
	protected static final int TEXTURE_AFFINE = 2;
	static final private int TEXTURE_LINEAR = 3;
	private int textureTypeX = TEXTURE_ID;

	static final private float TEXTURE_AFFINE_FACTOR = 0.05f;

	/** curve position (for texture) */
	private float curvePos;

	// arrows
	/** no arrows */
	static final public int ARROW_TYPE_NONE = 0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE = 1;
	/** closed segment */
	static final public int ARROW_TYPE_CLOSED = 2;

	private int arrowType = ARROW_TYPE_NONE;
	/** length of the arrow */
	static private float ARROW_LENGTH = 3f;
	/** width of the arrow */
	static private float ARROW_WIDTH = ARROW_LENGTH / 4f;

	/** ticks */
	public enum Ticks {
		NONE, MAJOR, MAJOR_AND_MINOR
	}

	/** has ticks ? */
	private Ticks ticks = Ticks.NONE;
	/** distance between two ticks */
	private float ticksDistance;
	/**
	 * offset for origin of the ticks (0: start of the curve, 1: end of the
	 * curve)
	 */
	private float ticksOffset;
	private Coords drawNormal = new Coords(3);
	private Coords drawPos = new Coords(3);

	private float lengthInScene;

	private Coords m = new Coords(3);
	private Coords vn1 = new Coords(3);
	private Coords tmpCoords = new Coords(3);
	private Coords tmpCoords2 = new Coords(3);
	private Coords tmpCoords3 = new Coords(3);
	private Coords tmpCoords4 = new Coords(3);

	private Coords f1 = new Coords(4);
	private Coords f2 = new Coords(4);
	private Coords vn2 = new Coords(3);

	private Coords tmpDrawTo = Coords.createInhomCoorsInD3();

	private Coords tmpCopyCoords;

	// level of detail
	/** number of rules */
	protected final static int LATITUDES = 8;

	/**
	 * pre-calculated cosinus
	 */
	final static double[] COSINUS = new double[LATITUDES + 1];
	/**
	 * pre-calculated sinus
	 */
	final static double[] SINUS = new double[LATITUDES + 1];

	static {
		for (int i = 0; i <= LATITUDES; i++) {
			COSINUS[i] = Math.cos(2 * i * Math.PI / LATITUDES);
			SINUS[i] = Math.sin(2 * i * Math.PI / LATITUDES);
		}
	}

	/**
	 * default constructor
	 * 
	 * @param manager
	 *            manager
	 */
	public PlotterBrush(Manager manager) {
		this.manager = manager;
		start = new PlotterBrushSection(manager);
		end = new PlotterBrushSection(manager);
	}

	// //////////////////////////////////
	// START AND END
	// //////////////////////////////////

	/**
	 * start new curve
	 * 
	 * @param old
	 *            old index
	 */
	public void start(int old) {
		index = manager.startNewList(old, true);
		hasColor = false;
		notStarted = true;
	}

	/**
	 * end curve
	 * 
	 * @return gl index of the curve
	 */
	public int end() {
		manager.endList();
		return index;
	}

	// //////////////////////////////////
	// SIMPLE DRAWING METHODS
	// //////////////////////////////////

	/**
	 * start new curve part
	 * 
	 * @param point
	 *            point
	 */
	public void down(Coords point) {

		start.set(point, thickness);
		justStarted = true;
		notStarted = false;
	}

	/**
	 * start new curve part
	 * 
	 * @param point
	 *            point
	 */
	private void down(Coords point, Coords clockU, Coords clockV) {

		start.set(point, thickness, clockU, clockV);
		justStarted = true;
	}

	/**
	 * move to point and draw curve part
	 * 
	 * @param point
	 *            point
	 */
	public void moveTo(Coords point) {
		moveTo(point, TickStep.NOT);
	}

	/**
	 * move to point and draw curve part
	 * 
	 * @param point
	 *            point
	 * @param tick
	 *            if drawing a tick
	 */
	public void moveTo(Coords point, TickStep tick) {

		// update start and end sections
		if (justStarted) {
			end.set(start, point, thickness, true, tick);
			justStarted = false;
		} else {
			PlotterBrushSection tmp = start;
			start = end;
			end = tmp;
			end.set(start, point, thickness, false, tick);
		}

		join();
	}

	/**
	 * move to new coords only if not equal to last. Set texture pos.
	 * 
	 * @param point
	 *            coords
	 */
	public void curveTo(Coords point) {

		if (notStarted) {
			notStarted = false;
			setCurvePos(0);
			down(point);
			return;
		}

		if (start.centerEqualsForKernel(point)) {
			return;
		}

		// update start and end sections
		if (justStarted) {
			justStarted = false;
		} else {
			PlotterBrushSection tmp = start;
			start = end;
			end = tmp;
		}

		end.set(start, point, thickness);

		// set curve pos
		addCurvePos((float) end.length);

		join();
	}

	/**
	 * move to point and draw curve part
	 * 
	 * @param point
	 *            point
	 */
	private void moveTo(Coords point, Coords clockU, Coords clockV) {

		if (justStarted) {
			justStarted = false;
		} else {
			PlotterBrushSection tmp = start;
			start = end;
			end = tmp;
		}

		end.set(point, thickness, clockU, clockV);

		join();
	}

	/**
	 * join start to end
	 */
	public void join() {

		// draw curve part
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);
		double u, v;
		for (int i = 0; i <= LATITUDES; i++) {
			u = SINUS[i];
			v = COSINUS[i];
			draw(start, u, v, 0); // bottom of the tube rule
			draw(end, u, v, 1); // top of the tube rule
		}

		manager.endGeometry();
	}

	/**
	 * draws a section point
	 * 
	 */
	protected void draw(PlotterBrushSection s, double u, double v,
			int texture) {

		s.getNormalAndPosition(u, v, drawNormal, drawPos);

		// set normal
		manager.normalToScale(drawNormal);

		// set texture
		float pos = textureX[texture];
		switch (textureTypeX) {
		case TEXTURE_ID:
		default:
			manager.texture(pos);
			break;
		case TEXTURE_CONSTANT_0:
			manager.texture(0);
			break;
		case TEXTURE_AFFINE:
			manager.texture(
					TEXTURE_AFFINE_FACTOR * length * (pos - texturePosZero) + textureValZero);
			break;
		case TEXTURE_LINEAR:
			manager.texture(TEXTURE_AFFINE_FACTOR * pos);
			break;

		}

		// set vertex
		vertex(drawPos);
	}

	private void vertex(Coords v) {
		// set color
		if (hasColor) {
			manager.color(red, green, blue, alpha);
		}

		// set vertex
		manager.vertex(v);
	}

	// //////////////////////////////////
	// GEOMETRY DRAWING METHODS
	// //////////////////////////////////

	/**
	 * segment curve
	 * 
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 */
	public void segment(Coords p1, Coords p2) {
		tmpCoords.setSub(p2, p1);
		length = getNormInScreenCoords(tmpCoords);
		lengthInScene = (float) p1.distance3(p2);

		if (DoubleUtil.isEqual(length, 0, Kernel.STANDARD_PRECISION)) {
			return;
		}

		down(p1);

		float factor, arrowPos;

		switch (arrowType) {
		case ARROW_TYPE_NONE:
		default:
			setTextureX(0, 1);
			moveTo(p2);
			break;
		case ARROW_TYPE_SIMPLE:
			factor = (float) (DrawVector.getFactor(lineThickness)
						* LINE3D_THICKNESS * lengthInScene / length);
			if (ARROW_LENGTH * factor > 0.9f * lengthInScene) {
				factor = 0.9f * lengthInScene / ARROW_LENGTH;
			}
			arrowPos = ARROW_LENGTH / lengthInScene * factor;
			tmpCoords3.setAdd(tmpCoords4.setMul(p1, arrowPos),
					tmpCoords3.setMul(p2, 1 - arrowPos));

			setTextureX(0);
			if (ticksDistance > 0) {
				switch (ticks) {
				case MAJOR:
				default:
					tmpCoords4.setSub(p2, p1);
					tmpCoords4.normalize();
					float thicknessOld = this.thickness;
					float ticksDistanceNormed = ticksDistance / lengthInScene;

					float i = ticksOffset
							- ((int) (ticksOffset / ticksDistanceNormed))
									* ticksDistanceNormed;
					float ticksDelta = manager.getView3D().getTicksDeltaFactor()
							* thicknessOld * lengthInScene / length;
					float ticksThickness = manager.getView3D()
							.getTicksThicknessFactor() * thicknessOld;
					if (i * lengthInScene <= ticksDelta) {
						i += ticksDistanceNormed;
					}

					for (; i <= 1 - arrowPos; i += ticksDistanceNormed) {
						double x = i * lengthInScene;
						tmpCoords.setAdd(p1,
								tmpCoords.setMul(tmpCoords4, x - ticksDelta));
						tmpCoords2.setAdd(p1,
								tmpCoords2.setMul(tmpCoords4, x + ticksDelta));

						drawTick(tmpCoords, tmpCoords2, i, ticksThickness,
								thicknessOld);
					}
					break;
				case MAJOR_AND_MINOR:
					tmpCoords4.setSub(p2, p1);
					tmpCoords4.normalize();
					thicknessOld = this.thickness;
					ticksDistanceNormed = ticksDistance / lengthInScene;

					i = ticksOffset
							- ((int) (ticksOffset / ticksDistanceNormed))
									* ticksDistanceNormed;
					if (i < 0) {
						i += ticksDistanceNormed;
					}
					ticksDelta = manager.getView3D().getTicksDeltaFactor()
							* thicknessOld * lengthInScene / length;
					ticksThickness = manager.getView3D()
							.getTicksThicknessFactor() * thicknessOld;
					float ticksMinorThickness = manager.getView3D()
							.getTicksMinorThicknessFactor() * thicknessOld;
					boolean minor = false;
					if (i > ticksDistanceNormed / 2
							+ ticksDelta / lengthInScene) {
						minor = true;
						i -= ticksDistanceNormed / 2;
					} else if (i * lengthInScene <= ticksDelta) {
						i += ticksDistanceNormed / 2;
						minor = true;
					}

					for (; i <= 1 - arrowPos; i += ticksDistanceNormed / 2) {
						double x = i * lengthInScene;
						tmpCoords.setAdd(p1,
								tmpCoords.setMul(tmpCoords4, x - ticksDelta));
						tmpCoords2.setAdd(p1,
								tmpCoords2.setMul(tmpCoords4, x + ticksDelta));

						drawTick(tmpCoords, tmpCoords2, i,
								minor ? ticksMinorThickness : ticksThickness,
								thicknessOld);

						minor = !minor;
					}
					break;
				case NONE:
					break;
				}
			}

			drawArrowBase(arrowPos, tmpCoords3);

			textureTypeX = TEXTURE_ID;
			setTextureX(0, 0);
			setThickness(factor * ARROW_WIDTH * length / lengthInScene);
			drawArrowBaseOuter(tmpCoords3);
			setThickness(0);
			moveTo(p2);
			break;
		}

		if (arrowType == ARROW_TYPE_CLOSED) {
			setThickness(0);
			moveTo(p2);
		}
	}

	/**
	 * draw a tick
	 * 
	 */
	protected void drawTick(Coords p1b, Coords p2b, float i,
			float ticksThickness, float thicknessOld) {
		setTextureType(TEXTURE_AFFINE);
		setTextureX(i);
		moveTo(p1b);
		setThickness(ticksThickness);
		setTextureType(TEXTURE_CONSTANT_0);
		moveTo(p1b);
		moveTo(p2b);
		setThickness(thicknessOld);
		moveTo(p2b);
	}

	/**
	 * draw arrow base
	 * 
	 */
	protected void drawArrowBase(float arrowPos, Coords arrowBase) {
		setTextureType(TEXTURE_AFFINE);
		setTextureX(1 - arrowPos);
		moveTo(arrowBase);
	}

	/**
	 * draw arrow base (outer)
	 * 
	 * @param arrowBase
	 *            arrow base coords
	 */
	protected void drawArrowBaseOuter(Coords arrowBase) {
		moveTo(arrowBase);
	}

	/**
	 * draws a circle
	 * 
	 * @param center
	 *            center
	 * @param v1
	 *            first eigenvector
	 * @param v2
	 *            second eigenvector
	 * @param radius
	 *            radius
	 * @param longitude
	 *            longitude
	 */
	public void circle(Coords center, Coords v1, Coords v2, double radius,
			int longitude) {

		arc(center, v1, v2, radius, 0, 2 * Math.PI, longitude);
	}

	/**
	 * 
	 * @param radius
	 *            radius of the arc
	 * @param halfExtent
	 *            arc half extent
	 * @param viewScale
	 *            view scale
	 * @return longitude length needed to render the arc
	 */
	public int calcArcLongitudesNeeded(double radius, double halfExtent,
			double viewScale) {

		int longitude;
		double size = radius * halfExtent * viewScale;
		if (size > 262144) { // longitude would be > 1024
			longitude = manager.getLongitudeMax();
		} else {
			longitude = 8;
			while (longitude * longitude <= 4 * size
					&& longitude < manager.getLongitudeMax()) {
				// find the correct longitude size
				longitude *= 2;
			}
		}

		// Log.debug("circle ==== longitude="+longitude);
		return longitude;
	}

	/**
	 * draw an arc
	 * 
	 * @param center
	 *            center
	 * @param v1
	 *            1st eigenvector
	 * @param v2
	 *            2nd eigenvector
	 * @param radius
	 *            radius
	 * @param arcStart
	 *            arc start
	 * @param extent
	 *            arc extent
	 * @param longitude
	 *            longitude
	 */
	public void arc(Coords center, Coords v1, Coords v2, double radius,
			double arcStart, double extent, int longitude) {

		length = (float) (extent * radius);

		vn2.setCrossProduct3(v2, v1);

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);
		float u, v;
		u = (float) Math.cos(arcStart);
		v = (float) Math.sin(arcStart);

		setTextureX(0, 0);
		vn1.setAdd(tmpCoords.setMul(v1, u), vn1.setMul(v2, v));
		tmpCoords.setAdd(center, tmpCoords.setMul(vn1, radius));
		down(tmpCoords, vn1, vn2);

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(arcStart + i * da);
			v = (float) Math.sin(arcStart + i * da);

			setTextureX(i * dt);
			vn1.setAdd(tmpCoords.setMul(v1, u), vn1.setMul(v2, v));
			tmpCoords.setAdd(center, tmpCoords.setMul(vn1, radius));
			moveTo(tmpCoords, vn1, vn2);
		}

	}

	/**
	 * draw an arc extended with arrows
	 * 
	 * @param center
	 *            center
	 * @param v1
	 *            1st eigenvector
	 * @param v2
	 *            2nd eigenvector
	 * @param radius
	 *            radius
	 * @param arcStart
	 *            arc start
	 * @param extent
	 *            arc extent
	 * @param longitude
	 *            longitude
	 */
	public void arcExtendedWithArrows(Coords center, Coords v1, Coords v2,
			double radius, double arcStart, double extent, int longitude) {

		length = (float) (extent * radius);

		float oldThickness = getThickness();

		double arrowLength = oldThickness * 5;

		vn2.setCrossProduct3(v2, v1);

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);
		float u, v;

		// start arrow
		u = (float) Math.cos(arcStart);
		v = (float) Math.sin(arcStart);

		vn1.setAdd(tmpCoords.setMul(v1, u), vn1.setMul(v2, v));

		tmpCoords.setAdd(center, tmpCoords.setMul(vn1, radius));

		tmpCoords3.setCrossProduct3(vn2, vn1);
		tmpCoords2.setAdd(tmpCoords, tmpCoords3.mulInside(arrowLength));
		setThickness(0);
		setTextureX(0, 0);
		down(tmpCoords2, vn1, vn2);

		setThickness(2 * oldThickness);
		setTextureX(0, 0);
		moveTo(tmpCoords, vn1, vn2);

		setThickness(oldThickness);
		setTextureX(0, 0);
		moveTo(tmpCoords, vn1, vn2);

		// arc
		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(arcStart + i * da);
			v = (float) Math.sin(arcStart + i * da);

			setTextureX(i * dt);
			vn1.setAdd(tmpCoords.setMul(v1, u), vn1.setMul(v2, v));
			tmpCoords.setAdd(center, tmpCoords.setMul(vn1, radius));
			moveTo(tmpCoords, vn1, vn2);
		}

		// end arrow
		setThickness(2 * oldThickness);
		setTextureX(0, 0);
		moveTo(tmpCoords, vn1, vn2);

		tmpCoords3.setCrossProduct3(vn1, vn2);
		tmpCoords2.setAdd(tmpCoords, tmpCoords3.mulInside(arrowLength));
		setThickness(0);
		setTextureX(0, 0);
		moveTo(tmpCoords2, vn1, vn2);

		// back to old thickness
		setThickness(oldThickness);

	}

	/**
	 * draws an ellipse
	 * 
	 * @param center
	 *            center
	 * @param v1
	 *            1st eigenvector
	 * @param v2
	 *            2nd eigenvector
	 * @param a
	 *            1st eigenvalue
	 * @param b
	 *            2nd eigenvalue
	 * @param arcStart
	 *            arc start
	 * @param extent
	 *            extent
	 */
	public void arcEllipse(Coords center, Coords v1, Coords v2, double a,
			double b, double arcStart, double extent) {

		// Ramanujan approximation
		// length=(float) (Math.PI*(3*(a+b)-Math.sqrt((3*a+b)*(a+3*b)))); //TODO
		// use integer to avoid bad dash cycle connection
		length = 1;

		setCurvePos(0);
		setTextureType(PlotterBrush.TEXTURE_LINEAR);

		// foci
		double f = Math.sqrt(a * a - b * b);
		f1.setMul(v1, f);
		f2.setMul(v1, -f);

		int longitude = manager.getLongitudeDefault();

		vn2.setCrossProduct3(v2, v1);

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);
		float u, v;
		u = (float) Math.cos(arcStart);
		v = (float) Math.sin(arcStart);

		m.setAdd(m.setMul(v1, a * u), tmpCoords.setMul(v2, b * v));

		vn1.setAdd(tmpCoords3.setSub(m, f1).normalize(),
				tmpCoords4.setSub(m, f2).normalize()).normalize();

		tmpCoords.setAdd(center, m);
		down(tmpCoords, vn1, vn2);

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(arcStart + i * da);
			v = (float) Math.sin(arcStart + i * da);

			tmpCoords2.set(m);
			m.setAdd(m.setMul(v1, a * u), tmpCoords.setMul(v2, b * v));
			addCurvePos(tmpCoords2.setSub(m, tmpCoords2));

			vn1.setAdd(tmpCoords3.setSub(m, f1).normalize(),
					tmpCoords4.setSub(m, f2).normalize()).normalize();

			tmpCoords.setAdd(center, m);
			moveTo(tmpCoords, vn1, vn2);

		}

	}

	private void addCurvePos(Coords coords) {
		addCurvePos(getNormInScreenCoords(coords));
	}

	private float getNormInScreenCoords(Coords coords) {
		manager.scaleXYZ(coords);
		coords.calcNorm();
		return (float) coords.getNorm();
	}

	/**
	 * draws quarter of an hyperbola
	 * 
	 * @param center
	 *            center
	 * @param v1
	 *            1st eigenvector
	 * @param v2
	 *            2nd eigenvector
	 * @param a
	 *            1st eigenvalue
	 * @param b
	 *            2nd eigenvalue
	 * @param tMin
	 *            t min
	 * @param tMax
	 *            t max
	 */
	public void hyperbolaBranch(Coords center, Coords v1, Coords v2, double a,
			double b, double tMin, double tMax) {

		// foci
		double f = Math.sqrt(a * a + b * b);
		f1.setMul(v1, f);
		f2.setMul(v1, -f);

		// dash
		length = 1;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		setCurvePos(0.75f / (TEXTURE_AFFINE_FACTOR * scale)); // midpoint is
																// middle of an
																// empty dash

		int longitude = manager.getLongitudeDefault();

		vn2.setCrossProduct3(v1, v2);

		float dt = (float) (tMax - tMin) / longitude;

		float u, v;
		u = (float) Math.cosh(tMin);
		v = (float) Math.sinh(tMin);

		m.setAdd(m.setMul(v1, a * u), tmpCoords.setMul(v2, b * v));

		vn1.setSub(tmpCoords3.setSub(m, f1).normalize(),
				tmpCoords4.setSub(m, f2).normalize()).normalize();

		tmpCoords.setAdd(center, m);
		down(tmpCoords, vn1, vn2);

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cosh(tMin + i * dt);
			v = (float) Math.sinh(tMin + i * dt);

			tmpCoords2.set(m);
			m.setAdd(m.setMul(v1, a * u), tmpCoords.setMul(v2, b * v));
			addCurvePos(tmpCoords2.setSub(m, tmpCoords2));

			vn1.setSub(tmpCoords3.setSub(m, f1).normalize(),
					tmpCoords4.setSub(m, f2).normalize()).normalize();

			tmpCoords.setAdd(center, m);
			moveTo(tmpCoords, vn1, vn2);
		}

	}

	/**
	 * draws a parabola, and save ends coords in p1, p2 (if not null)
	 * 
	 * @param center
	 *            center
	 * @param v1
	 *            1st eigenvector
	 * @param v2
	 *            2nd eigenvector
	 * @param p
	 *            eigenvalue
	 * @param tMin
	 *            t min
	 * @param tMax
	 *            t max
	 * @param p1
	 *            to store start point
	 * @param p2
	 *            to store end point
	 */
	public void parabola(Coords center, Coords v1, Coords v2, double p,
			double tMin, double tMax, Coords p1, Coords p2) {

		// focus
		f1.setMul(v1, p / 2);

		vn2.setCrossProduct3(v1, v2);

		int longitude = manager.getLongitudeDefault();

		// dash
		length = 1;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		setCurvePos(0.75f / (TEXTURE_AFFINE_FACTOR * scale));

		float dt = (float) (tMax - tMin) / longitude;

		float u, v;
		double t;
		t = tMin;
		u = (float) (p * t * t / 2);
		v = (float) (p * t);

		m.setAdd(m.setMul(v1, u), tmpCoords.setMul(v2, v));

		vn1.setSub(tmpCoords3.setSub(m, f1).normalize(), v1).normalize();

		tmpCoords.setAdd(center, m);
		down(tmpCoords, vn1, vn2);

		if (p1 != null) {
			p1.set(tmpCoords);
		}

		for (int i = 1; i <= longitude; i++) {

			t = tMin + i * dt;
			u = (float) (p * t * t / 2);
			v = (float) (p * t);

			tmpCoords2.set(m);
			m.setAdd(m.setMul(v1, u), tmpCoords.setMul(v2, v));
			addCurvePos(tmpCoords2.setSub(m, tmpCoords2));

			vn1.setSub(tmpCoords3.setSub(m, f1).normalize(), v1).normalize();

			tmpCoords.setAdd(center, m);
			moveTo(tmpCoords, vn1, vn2);

		}

		if (p2 != null) {
			p2.set(tmpCoords);
		}

	}

	// //////////////////////////////////
	// THICKNESS
	// //////////////////////////////////

	/**
	 * set the current thickness of the brush, using integer for thickness (see
	 * {@link GeoElement#getLineThickness()}
	 * 
	 * @param thickness
	 *            thickness
	 * @param scale
	 *            scale
	 * @return real world thickness
	 */
	public float setThickness(int thickness, float scale) {
		return setThickness(thickness, false, scale);
	}

	/**
	 * 
	 * @param thickness
	 *            thickness
	 * @param isAxis
	 *            says if we want the thickness to be a little bit different for
	 *            axes to avoid z-fighting
	 * @param scale
	 *            scale
	 * @return real world thickness
	 */
	public float setThickness(int thickness, boolean isAxis, float scale) {

		this.lineThickness = manager.getView3D().getThicknessForLine(thickness);
		this.scale = scale;

		float t = lineThickness;
		if (isAxis) {
			t -= 0.97f;
		}
		t *= LINE3D_THICKNESS;
		setThickness(t);
		return t;
	}

	/**
	 * set the current thickness of the brush
	 * 
	 * @param thickness
	 *            thickness
	 */
	public void setThickness(float thickness) {
		this.thickness = thickness;
	}

	/**
	 * 
	 * @return current thickness of the brush
	 */
	public float getThickness() {
		return thickness;
	}

	// //////////////////////////////////
	// COLOR
	// //////////////////////////////////

	/**
	 * sets the current color
	 * 
	 * @param color
	 *            color
	 * @param alpha
	 *            opacity
	 */
	public void setColor(GColor color, float alpha) {
		this.red = color.getRed() / 255f;
		this.green = color.getGreen() / 255f;
		this.blue = color.getBlue() / 255f;
		this.alpha = alpha;
		hasColor = true;
	}

	/**
	 * sets the current color (alpha set to 1)
	 * 
	 * @param color
	 *            color
	 */
	public void setColor(GColor color) {
		setColor(color, 1);
	}

	// //////////////////////////////////
	// TEXTURE
	// //////////////////////////////////

	/**
	 * sets the position of the point on the curve and sets the texture x
	 * 
	 * @param pos
	 *            position
	 * 
	 */
	public void setCurvePos(float pos) {
		curvePos = pos;
		setTextureX(pos);
	}

	/**
	 * add the distance to the position on the curve (used for texture)
	 * 
	 * @param distance
	 *            distance
	 * 
	 */
	public void addCurvePos(float distance) {
		setCurvePos(curvePos + distance);
	}

	/**
	 * set affine texture zero position
	 * 
	 * @param posZero
	 *            position of the "center" of the cylinder
	 * @param valZero
	 *            texture coord for the "center"
	 */
	public void setAffineTexture(float posZero, float valZero) {

		texturePosZero = posZero;
		textureValZero = valZero;
		setTextureType(TEXTURE_AFFINE);
	}

	/**
	 * 
	 */
	public void setPlainTexture() {
		setTextureType(TEXTURE_CONSTANT_0);
	}

	/**
	 * sets the type of texture
	 * 
	 * @param type
	 *            texture type
	 */
	protected void setTextureType(int type) {
		textureTypeX = type;
	}

	private void setTextureX(float x0, float x1) {
		this.textureX[0] = x0;
		this.textureX[1] = x1;
	}

	protected void setTextureX(float x) {
		setTextureX(textureX[1], x);
	}

	// //////////////////////////////////
	// ARROWS
	// //////////////////////////////////

	/**
	 * sets the type of arrow used by the pencil.
	 * 
	 * @param arrowType
	 *            type of arrow, see {@link #ARROW_TYPE_NONE},
	 *            {@link #ARROW_TYPE_SIMPLE}, ...
	 */
	public void setArrowType(int arrowType) {
		this.arrowType = arrowType;
	}

	// //////////////////////////////////
	// TICKS
	// //////////////////////////////////

	/**
	 * sets the type of arrow used by the pencil.
	 * 
	 * @param ticks
	 *            ticks
	 */
	public void setTicks(Ticks ticks) {
		this.ticks = ticks;
	}

	/**
	 * sets the distance between two ticks
	 * 
	 * @param distance
	 *            distance
	 */
	public void setTicksDistance(float distance) {
		this.ticksDistance = distance;
	}

	/**
	 * sets the offset for origin of the ticks (0: start of the curve, 1: end of
	 * the curve)
	 * 
	 * @param offset
	 *            offset
	 */
	public void setTicksOffset(float offset) {
		this.ticksOffset = offset;
	}

	// //////////////////////////////
	// PATH PLOTTER
	// //////////////////////////////

	@Override
	public void drawTo(double[] pos, SegmentType lineTo) {

		tmpDrawTo.set(pos);

		drawTo(lineTo == SegmentType.LINE_TO);
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param lineTo
	 *            whether lineto or moveto
	 */
	public void drawTo(double x, double y, double z, boolean lineTo) {

		tmpDrawTo.setX(x);
		tmpDrawTo.setY(y);
		tmpDrawTo.setZ(z);

		drawTo(lineTo);
	}

	private void drawTo(boolean lineTo) {

		// Log.debug("\n"+p);

		if (lineTo) {
			curveTo(tmpDrawTo);
		} else {
			setCurvePos(0);
			down(tmpDrawTo);
		}
	}

	@Override
	public void lineTo(double[] pos) {
		drawTo(pos, SegmentType.LINE_TO);
	}

	@Override
	public void moveTo(double[] pos) {

		tmpDrawTo.set(pos);
		if (pos.length == 2) {
			// in case of 2D point
			tmpDrawTo.setZ(0);
		}

		drawTo(false);
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void moveTo(double x, double y, double z) {
		drawTo(x, y, z, false);
	}

	@Override
	public void corner() {
		// TODO Auto-generated method stub
	}

	@Override
	public void corner(double[] pos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void firstPoint(double[] pos, Gap moveToAllowed) {
		// TODO only Gap.MOVE_TO implemented
		moveTo(pos);

	}

	@Override
	public double[] newDoubleArray() {
		return new double[3];
	}

	@Override
	public boolean copyCoords(MyPoint point, double[] ret,
			CoordSys transformSys) {

		ret[0] = point.x;
		ret[1] = point.y;
		ret[2] = point.getZ(); // maybe 0 if 2D point

		if (transformSys != CoordSys.XOY) {
			if (tmpCopyCoords == null) {
				tmpCopyCoords = Coords.createInhomCoorsInD3();
			}

			transformSys.getPointFromOriginVectors(ret[0], ret[1],
					tmpCopyCoords);
			ret[0] = tmpCopyCoords.getX();
			ret[1] = tmpCopyCoords.getY();
			ret[2] = tmpCopyCoords.getZ();
		}

		return true;
	}

	/**
	 * set the length for texture pos
	 * 
	 * @param length
	 *            length
	 */
	public void setLength(float length) {
		this.length = length;
	}

	@Override
	public void endPlot() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean supports(CoordSys transformSys) {
		return true;
	}

}
