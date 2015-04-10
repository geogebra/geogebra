package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.DrawVector;
import org.geogebra.common.euclidian.plot.PathPlotter;
import org.geogebra.common.euclidian.plot.CurvePlotter.Gap;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoCurveCartesian3DInterface;
import org.geogebra.common.kernel.geos.GeoElement;

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
	private Manager manager;

	/** index */
	private int index;

	/** start and end sections */
	private PlotterBrushSection start = new PlotterBrushSection(),
			end = new PlotterBrushSection();

	private boolean justStarted = false;
	private boolean notStarted = false;

	/** current thickness */
	private float thickness;
	private int lineThickness;

	/** view scale */
	private float scale;

	/** global length of the curve */
	private float length;

	// color
	/** color r, g, b, a */
	private float red, green, blue, alpha;
	/** says if it's colored */
	private boolean hasColor;

	// texture
	/** start and end textures values */
	private float texturePosZero, textureValZero;
	/** textures coords */
	private float[] textureX = new float[2];
	/** type of texture */
	static final public int TEXTURE_CONSTANT_0 = 0;
	static final private int TEXTURE_ID = 1;
	static final private int TEXTURE_AFFINE = 2;
	static final private int TEXTURE_LINEAR = 3;
	private int textureTypeX = TEXTURE_ID;
	private int textureTypeY = TEXTURE_CONSTANT_0;

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

	// /** length of the arrow */
	// static private float ARROW_HANDLE_LENGTH = ARROW_LENGTH;//ARROW_WIDTH;

	/** ticks */
	static public enum Ticks {
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

	// for GeoCartesianCurve
	/** curve */
	GeoCurveCartesian3DInterface curve;

	// level of detail
	/** number of rules */
	final private static int LATITUDES = 8;

	/**
	 * pre-calculated cosinus and sinus
	 */
	final private static double[] COSINUS, SINUS;

	static {
		COSINUS = new double[LATITUDES + 1];
		SINUS = new double[LATITUDES + 1];
		for (int i = 0; i <= LATITUDES; i++) {
			COSINUS[i] = Math.cos(2 * i * Math.PI / LATITUDES);
			SINUS[i] = Math.sin(2 * i * Math.PI / LATITUDES);
		}

	}

	/**
	 * default constructor
	 * 
	 * @param manager
	 */
	public PlotterBrush(Manager manager) {
		this.manager = manager;
	}

	// //////////////////////////////////
	// START AND END
	// //////////////////////////////////

	/**
	 * start new curve
	 */
	public void start(int old) {
		index = manager.startNewList(old);
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
	 */
	private void down(Coords point, Coords clockU, Coords clockV) {

		start.set(point, thickness, clockU, clockV);
		justStarted = true;
	}

	/**
	 * move to point and draw curve part
	 * 
	 * @param point
	 */
	public void moveTo(Coords point) {

		// update start and end sections
		if (justStarted) {
			end.set(start, point, thickness, true);
			justStarted = false;
		} else {
			PlotterBrushSection tmp = start;
			start = end;
			end = tmp;
			end.set(start, point, thickness, false);
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

		if (point.equalsForKernel(start.center, Kernel.STANDARD_PRECISION)) {
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

	private Coords drawNormal = new Coords(3), drawPos = new Coords(3);

	/**
	 * draws a section point
	 * 
	 */
	private void draw(PlotterBrushSection s, double u, double v, int texture) {

		s.getNormalAndPosition(u, v, drawNormal, drawPos);

		// set normal
		manager.normal(drawNormal);

		// set texture
		float pos = textureX[texture];
		switch (textureTypeX) {
		case TEXTURE_ID:
		default:
			manager.texture(pos, 0);
			break;
		case TEXTURE_CONSTANT_0:
			manager.texture(0, 0);
			break;
		case TEXTURE_AFFINE:
			// float factor = (int) (TEXTURE_AFFINE_FACTOR*length*scale); //TODO
			// integer for cycles
			float factor = (TEXTURE_AFFINE_FACTOR * length * scale);
			manager.texture(factor * (pos - texturePosZero) + textureValZero, 0);
			break;
		case TEXTURE_LINEAR:
			manager.texture(TEXTURE_AFFINE_FACTOR * scale * pos, 0);
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
	 * @param p2
	 */
	public void segment(Coords p1, Coords p2) {

		length = (float) p1.distance(p2);
		if (Kernel.isEqual(length, 0, Kernel.STANDARD_PRECISION))
			return;

		down(p1);

		float factor, arrowPos;
		Coords arrowBase;

		switch (arrowType) {
		case ARROW_TYPE_NONE:
		default:
			setTextureX(0, 1);
			moveTo(p2);
			break;
		case ARROW_TYPE_SIMPLE:
			factor = (float) (DrawVector.getFactor(lineThickness) * LINE3D_THICKNESS / scale);
			if (ARROW_LENGTH * factor > 0.9f * length){
				factor = 0.9f * length / ARROW_LENGTH;
			}
			arrowPos = ARROW_LENGTH / length * factor;
			arrowBase = start.getCenter().mul(arrowPos)
					.add(p2.mul(1 - arrowPos));

			setTextureX(0);
			if (ticksDistance > 0) {
				switch (ticks) {
				case MAJOR:
				default:
					Coords d = p2.sub(p1).normalized();
					float thickness = this.thickness;

					float i = ticksOffset * length
							- ((int) (ticksOffset * length / ticksDistance))
							* ticksDistance;
					float ticksDelta = thickness;
					float ticksThickness = 4 * thickness;
					if (i <= ticksDelta)
						i += ticksDistance;

					for (; i <= length * (1 - arrowPos); i += ticksDistance) {

						Coords p1b = p1.add(d.mul(i - ticksDelta));
						Coords p2b = p1.add(d.mul(i + ticksDelta));

						setTextureType(TEXTURE_AFFINE);
						setTextureX(i / length);
						moveTo(p1b);
						setThickness(ticksThickness);
						setTextureType(TEXTURE_CONSTANT_0);
						moveTo(p1b);
						moveTo(p2b);
						setThickness(thickness);
						moveTo(p2b);

					}
					break;
				case MAJOR_AND_MINOR:
					d = p2.sub(p1).normalized();
					thickness = this.thickness;

					i = ticksOffset * length
							- ((int) (ticksOffset * length / ticksDistance))
							* ticksDistance;
					ticksDelta = thickness;
					ticksThickness = 4 * thickness;
					float ticksMinorThickness = 2.5f * thickness;
					boolean minor = false;
					if (i > ticksDistance / 2 + ticksDelta) {
						minor = true;
						i -= ticksDistance / 2;
					} else if (i <= ticksDelta) {
						i += ticksDistance / 2;
						minor = true;
					}

					for (; i <= length * (1 - arrowPos); i += ticksDistance / 2) {

						Coords p1b = p1.add(d.mul(i - ticksDelta));
						Coords p2b = p1.add(d.mul(i + ticksDelta));

						setTextureType(TEXTURE_AFFINE);
						setTextureX(i / length);
						moveTo(p1b);
						if (minor) {
							setThickness(ticksMinorThickness);
						} else {
							setThickness(ticksThickness);
						}
						setTextureType(TEXTURE_CONSTANT_0);
						moveTo(p1b);
						moveTo(p2b);
						setThickness(thickness);
						moveTo(p2b);

						minor = !minor;
					}
					break;
				case NONE:
					break;
				}
			}

			setTextureType(TEXTURE_AFFINE);
			setTextureX(1 - arrowPos);
			moveTo(arrowBase);

			textureTypeX = TEXTURE_ID;
			setTextureX(0, 0);
			setThickness(factor * ARROW_WIDTH);
			moveTo(arrowBase);
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
	 * draws a circle
	 * 
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 * @param longitude
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
					&& longitude < manager.getLongitudeMax()) {// find the
																// correct
																// longitude
																// size
				longitude *= 2;
			}
		}

		// App.debug("circle ==== longitude="+longitude);
		return longitude;
	}

	/**
	 * draw an arc
	 * 
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 * @param start
	 * @param extent
	 */
	public void arc(Coords center, Coords v1, Coords v2, double radius,
			double start, double extent, int longitude) {

		length = (float) (extent * radius);

		Coords vn1;
		Coords vn2 = v2.crossProduct(v1);

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);
		float u, v;
		u = (float) Math.cos(start);
		v = (float) Math.sin(start);

		setTextureX(0, 0);
		vn1 = v1.mul(u).add(v2.mul(v));
		down(center.add(vn1.mul(radius)), vn1, vn2);

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(start + i * da);
			v = (float) Math.sin(start + i * da);

			setTextureX(i * dt);
			vn1 = v1.mul(u).add(v2.mul(v));
			moveTo(center.add(vn1.mul(radius)), vn1, vn2);
		}

	}

	private Coords m = new Coords(3), vn1 = new Coords(3);
	private Coords tmpCoords = new Coords(3), tmpCoords2 = new Coords(3),
			tmpCoords3 = new Coords(3), tmpCoords4 = new Coords(3);

	private Coords f1 = new Coords(4), f2 = new Coords(4), vn2 = new Coords(3);

	/**
	 * draws an ellipse
	 * 
	 * @param center
	 * @param v1
	 *            1st eigenvector
	 * @param v2
	 *            2nd eigenvector
	 * @param a
	 *            1st eigenvalue
	 * @param b
	 *            2nd eigenvalue
	 * */
	public void arcEllipse(Coords center, Coords v1, Coords v2, double a,
			double b, double start, double extent) {

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

		vn2.setCrossProduct(v2, v1);

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);
		float u, v;
		u = (float) Math.cos(start);
		v = (float) Math.sin(start);

		m.setAdd(m.setMul(v1, a * u), tmpCoords.setMul(v2, b * v));

		vn1.setAdd(tmpCoords3.setSub(m, f1).normalize(),
				tmpCoords4.setSub(m, f2).normalize()).normalize();

		tmpCoords.setAdd(center, m);
		down(tmpCoords, vn1, vn2);

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(start + i * da);
			v = (float) Math.sin(start + i * da);

			tmpCoords2.set(m);
			m.setAdd(m.setMul(v1, a * u), tmpCoords.setMul(v2, b * v));
			addCurvePos((float) tmpCoords2.setSub(m, tmpCoords2).norm());

			vn1.setAdd(tmpCoords3.setSub(m, f1).normalize(),
					tmpCoords4.setSub(m, f2).normalize()).normalize();

			tmpCoords.setAdd(center, m);
			moveTo(tmpCoords, vn1, vn2);

		}

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
		Coords f1 = v1.mul(f);
		Coords f2 = v1.mul(-f);

		// dash
		length = 1;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		setCurvePos(0.75f / (TEXTURE_AFFINE_FACTOR * scale)); // midpoint is
																// middle of an
																// empty dash

		int longitude = manager.getLongitudeDefault();

		Coords m, mold, vn1;
		Coords vn2 = v1.crossProduct(v2);

		float dt = (float) (tMax - tMin) / longitude;

		float u, v;
		u = (float) Math.cosh(tMin);
		v = (float) Math.sinh(tMin);

		m = v1.mul(a * u).add(v2.mul(b * v));
		vn1 = (m.sub(f1).normalized()).sub((m.sub(f2).normalized()))
				.normalized(); // bissector
		down(center.add(m), vn1, vn2);

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cosh(tMin + i * dt);
			v = (float) Math.sinh(tMin + i * dt);

			mold = m;
			m = v1.mul(a * u).add(v2.mul(b * v));
			addCurvePos((float) m.sub(mold).norm());

			vn1 = (m.sub(f1).normalized()).sub((m.sub(f2).normalized()))
					.normalized(); // bissector
			moveTo(center.add(m), vn1, vn2);
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
		Coords f1 = v1.mul(p / 2);

		Coords vn2 = v1.crossProduct(v2);

		int longitude = manager.getLongitudeDefault();

		// dash
		length = 1;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		setCurvePos(0.75f / (TEXTURE_AFFINE_FACTOR * scale));

		Coords m, vn1, mold;

		float dt = (float) (tMax - tMin) / longitude;

		float u, v;
		double t;
		t = tMin;
		u = (float) (p * t * t / 2);
		v = (float) (p * t);

		m = v1.mul(u).add(v2.mul(v));
		vn1 = (m.sub(f1).normalized()).sub(v1).normalized(); // bissector
		if (p1 != null) {
			p1.set(center.add(m));
			down(p1, vn1, vn2);
		} else {
			down(center.add(m), vn1, vn2);
		}

		for (int i = 1; i <= longitude; i++) {

			t = tMin + i * dt;
			u = (float) (p * t * t / 2);
			v = (float) (p * t);

			mold = m;
			m = v1.mul(u).add(v2.mul(v));
			addCurvePos((float) m.sub(mold).norm());

			vn1 = (m.sub(f1).normalized()).sub(v1).normalized(); // bissector
			moveTo(center.add(m), vn1, vn2);
		}

		if (p2 != null) {
			p2.set(center.add(m));
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
	 * @param scale
	 * @return real world thickness
	 */
	public float setThickness(int thickness, float scale) {

		this.lineThickness = thickness;
		this.scale = scale;

		float t = lineThickness * LINE3D_THICKNESS / scale;
		setThickness(t);
		return t;

	}

	/**
	 * set the current thickness of the brush
	 * 
	 * @param thickness
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
	 * @param alpha
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
	 */
	private void setTextureType(int type) {
		textureTypeX = type;
	}

	private void setTextureX(float x0, float x1) {
		this.textureX[0] = x0;
		this.textureX[1] = x1;
	}

	private void setTextureX(float x) {
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
	 */
	public void setTicks(Ticks ticks) {
		this.ticks = ticks;
	}

	/**
	 * sets the distance between two ticks
	 * 
	 * @param distance
	 */
	public void setTicksDistance(float distance) {
		this.ticksDistance = distance;
	}

	/**
	 * sets the offset for origin of the ticks (0: start of the curve, 1: end of
	 * the curve)
	 * 
	 * @param offset
	 */
	public void setTicksOffset(float offset) {
		this.ticksOffset = offset;
	}

	// //////////////////////////////
	// PATH PLOTTER
	// //////////////////////////////

	public void drawTo(double[] pos, boolean lineTo) {

		Coords p = new Coords(0, 0, 0, 1);
		p.set(pos);

		// App.debug("\n"+p);

		if (lineTo) {
			curveTo(p);
		} else {
			setCurvePos(0);
			down(p);
		}
	}

	public void lineTo(double[] pos) {
		drawTo(pos, true);
	}

	public void moveTo(double[] pos) {
		drawTo(pos, false);
	}

	public void corner() {
		// TODO Auto-generated method stub
	}

	public void corner(double[] pos) {
		// TODO Auto-generated method stub
	}

	public void firstPoint(double[] pos, Gap moveToAllowed) {
		// TODO only Gap.MOVE_TO implemented
		moveTo(pos);

	}

	public double[] newDoubleArray() {
		return new double[3];
	}

	public boolean copyCoords(MyPoint point, double[] ret) {
		ret[0] = point.x;
		ret[1] = point.y;
		ret[2] = point.getZ(); // maybe 0 if 2D point

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

}
