package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;

/**
 * Class for drawing surfaces.
 * 
 * @author Mathieu
 *
 */
public class PlotterSurface {

	/** manager */
	protected Manager manager;

	/** index */
	private int index;

	/** 2-var function */
	private Functional2Var functional2Var;

	private GeoFunctionNVar function;

	/** domain for plotting */
	private float uMin, uMax, vMin, vMax;

	/** number of plotting */
	private int uNb, vNb;

	/** delta for plotting */
	private float du, dv;

	/** fading values */
	private float uMinFade, uMaxFade, vMinFade, vMaxFade;
	private float uMinFadeNb, uMaxFadeNb, vMinFadeNb, vMaxFadeNb;

	/** texture coord for out (alpha = 0) */
	static final private float TEXTURE_FADE_OUT = 0.75f;
	/** texture coord for in (alpha = 1) */
	static final private float TEXTURE_FADE_IN = 0f;

	/**
	 * default constructor
	 * 
	 * @param manager
	 */
	public PlotterSurface(Manager manager) {
		this.manager = manager;

	}

	// //////////////////////////////////
	// START AND END
	// //////////////////////////////////

	/**
	 * start new surface
	 */
	public void start(int old) {
		index = manager.startNewList(old);
	}

	/**
	 * start new surface
	 * 
	 * @param fun
	 *            function
	 */
	public void start(Functional2Var fun, int old) {
		index = manager.startNewList(old);
		this.functional2Var = fun;
		uMinFade = 0;
		vMinFade = 0;
		uMaxFade = 0;
		vMaxFade = 0;

	}

	/**
	 * start new surface
	 * 
	 * @param fun
	 *            function
	 */
	public void start(GeoFunctionNVar fun, int old) {
		index = manager.startNewList(old);
		this.function = fun;
		uMinFade = 0;
		vMinFade = 0;
		uMaxFade = 0;
		vMaxFade = 0;

	}

	/**
	 * end surface
	 * 
	 * @return gl index of the surface
	 */
	public int end() {
		manager.endList();
		return index;
	}

	// //////////////////////////////////
	// DRAWING METHODS
	// //////////////////////////////////

	/**
	 * set domain for u parameter
	 * 
	 * @param min
	 * @param max
	 */
	public void setU(float min, float max) {
		this.uMin = min;
		this.uMax = max;
	}

	/**
	 * set domain for v parameter
	 * 
	 * @param min
	 * @param max
	 */
	public void setV(float min, float max) {
		this.vMin = min;
		this.vMax = max;
	}

	/**
	 * set number of plot for u
	 * 
	 * @param n
	 */
	public void setNbU(int n) {
		this.uNb = n;
	}

	/**
	 * set number of plot for v
	 * 
	 * @param n
	 */
	public void setNbV(int n) {
		this.vNb = n;
	}

	/**
	 * set fading frontiers for u parameter
	 * 
	 * @param min
	 * @param max
	 */
	public void setUFading(float min, float max) {
		this.uMinFade = min;
		this.uMaxFade = max;
	}

	/**
	 * set fading frontiers for v parameter
	 * 
	 * @param min
	 * @param max
	 */
	public void setVFading(float min, float max) {
		this.vMinFade = min;
		this.vMaxFade = max;
	}

	public void drawTriangle(Coords p1, Coords p2, Coords p3) {
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);

		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);
		manager.texture(uT, vT);

		manager.vertex(p1);
		manager.vertex(p3);
		manager.vertex(p2);
		manager.endGeometry();
	}

	/**
	 * draw a quadrilateral
	 * 
	 * @param p1
	 *            point 1
	 * @param p2
	 *            point 2
	 * @param p3
	 *            point 3
	 * @param p4
	 *            point 4
	 */
	public void drawQuad(Coords p1, Coords p2, Coords p3, Coords p4) {

		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);

		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);
		manager.texture(uT, vT);

		manager.vertex(p1);
		manager.vertex(p2);
		manager.vertex(p4);
		manager.vertex(p3);
		manager.endGeometry();
	}

	public void drawQuadNoTexture(Coords p1, Coords p2, Coords p3, Coords p4) {

		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);

		manager.setDummyTexture();

		manager.vertex(p1);
		manager.vertex(p2);
		manager.vertex(p4);
		manager.vertex(p3);
		manager.endGeometry();
	}

	public void drawQuadWireFrame(Coords p1, Coords p2, Coords p3, Coords p4) {

		// lines
		manager.startGeometry(Manager.Type.LINE_LOOP);

		manager.setDummyTexture();

		manager.color(0f, 0f, 0f, 1f);

		manager.vertex(p1);
		manager.vertex(p2);
		manager.vertex(p3);
		manager.vertex(p4);
		manager.endGeometry();

		// surface
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);

		manager.setDummyTexture();

		manager.color(1f, 0f, 0f, 0.5f);

		manager.vertex(p1);
		manager.vertex(p2);
		manager.vertex(p4);
		manager.vertex(p3);
		manager.endGeometry();
	}

	public void startTrianglesWireFrame() {
		// lines
		manager.startGeometry(Manager.Type.LINE_LOOP);

		manager.setDummyTexture();

		manager.color(0f, 0f, 0f, 1f);
	}

	public void startTrianglesWireFrameSurface() {

		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.setDummyTexture();

		manager.color(1f, 0f, 0f, 0.25f);
	}


	public void startTrianglesWireFrameSurfaceBoundary() {

		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.setDummyTexture();

		manager.color(0f, 0f, 1f, 0.25f);
	}

	public void startTriangles() {

		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.setDummyTexture();

	}
	

	/**
	 * 
	 * draw triangle
	 * 
	 * @param p1
	 *            first vertex
	 * @param p2
	 *            second vertex
	 * @param p3
	 *            last vertex
	 * @param norm1
	 *            first normal
	 * @param norm2
	 *            second normal
	 * @param norm3
	 *            third normal
	 */
	public void triangle(Coords3 p1, Coords3 p2, Coords3 p3, Coords3 norm1,
			Coords3 norm2, Coords3 norm3) {
		manager.normal(norm1.getXd(), norm1.getYd(), norm1.getZd());
		manager.vertexToScale(p1.getXd(), p1.getYd(), p1.getZd());
		manager.normal(norm2.getXd(), norm2.getYd(), norm2.getZd());
		manager.vertexToScale(p2.getXd(), p2.getYd(), p2.getZd());
		manager.normal(norm3.getXd(), norm3.getYd(), norm3.getZd());
		manager.vertexToScale(p3.getXd(), p3.getYd(), p3.getZd());
	}

	/**
	 * start triangles
	 * @param size vertices size
	 */
	public void startTriangles(int size) {

		manager.startGeometryDirect(Manager.Type.TRIANGLES, size);

		manager.setDummyTexture();

	}

	public void vertexDirect(Coords3 p) {
		manager.vertexDirect(p);
	}

	public void normalDirect(Coords3 n0) {
		manager.normalDirect(n0);
	}

	public void endGeometryDirect() {
		manager.endGeometryDirect();
	}

	public void endGeometry() {
		manager.endGeometry();
	}

	/**
	 * 
	 * @param radius
	 *            radius of the sphere
	 * @param viewScale
	 *            view scale
	 * @return longitude length needed to render the sphere
	 */
	public int calcSphereLongitudesNeeded(double radius, double viewScale) {

		int longitude = 8;
		double size = radius * viewScale;
		// App.error(""+size);
		while (longitude * longitude <= 16 * size
				&& longitude < manager.getLongitudeMax()) {// find the correct
															// longitude size
			longitude *= 2;
		}

		// Log.debug("sphere ==== longitude="+longitude);
		return longitude;
	}
	
	

	/**
	 * draw a sphere with center and radius. view scaling is used to know how
	 * many triangles are needed
	 * 
	 * @param center
	 *            center of the sphere
	 * @param radius
	 *            radius of the sphere
	 * @param longitude
	 *            longitude length for rendering
	 */
	public void drawSphere(Coords center, double radius, int longitude) {

		drawSphere(center, radius, longitude, 0, longitude);
	}

	/**
	 * draw an ellipsoid
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r0
	 *            first half axis
	 * @param r1
	 *            second half axis
	 * @param r2
	 *            third half axis
	 * @param longitude
	 *            longitude length for rendering
	 */
	public void drawEllipsoid(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double r2, int longitude) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawEllipsoid(center, ev0, ev1, ev2, r0, r1, r2,
				longitude);

	}

	private ManagerElementForGLList managerElements;
	private PlotterSurfaceElements plotterElements;

	/**
	 * draw an hyperboloid (one sheet)
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r0
	 *            first half axis
	 * @param r1
	 *            second half axis
	 * @param r2
	 *            third half axis
	 * @param longitude
	 *            longitude length for rendering
	 * @param min
	 *            minimum parameter for axis
	 * @param max
	 *            maximum parameter for axis
	 * @param fading
	 *            if we need fading or not
	 * 
	 */
	public void drawHyperboloidOneSheet(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double r2, int longitude,
			double min, double max, boolean fading) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawHyperboloidOneSheet(center, ev0, ev1, ev2, r0, r1,
				r2, longitude, min, max, fading);
	}

	/**
	 * draw an hyperboloid (two sheets)
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r0
	 *            first half axis
	 * @param r1
	 *            second half axis
	 * @param r2
	 *            third half axis
	 * @param longitude
	 *            longitude length for rendering
	 * @param min
	 *            minimum parameter for axis
	 * @param max
	 *            maximum parameter for axis
	 * @param fading
	 *            if we need fading or not
	 * 
	 */
	public void drawHyperboloidTwoSheets(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double r2, int longitude,
			double min, double max, boolean fading) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawHyperboloidTwoSheets(center, ev0, ev1, ev2, r0, r1,
				r2, longitude, min, max, fading);
	}

	/**
	 * draw a paraboloid
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r0
	 *            first half axis
	 * @param r1
	 *            second half axis
	 * @param longitude
	 *            longitude length for rendering
	 * @param min
	 *            minimum parameter for axis
	 * @param max
	 *            maximum parameter for axis
	 * @param fading
	 *            if we need fading or not
	 * 
	 */
	public void drawParaboloid(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, int longitude,
			double min, double max, boolean fading) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawParaboloid(center, ev0, ev1, ev2, r0, r1,
				longitude, min, max, fading);
	}

	/**
	 * draw an hyperbolic paraboloid
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r0
	 *            first half axis
	 * @param r1
	 *            second half axis
	 * @param min0
	 *            minimum parameter for first axis
	 * @param max0
	 *            maximum parameter for first axis
	 * @param min1
	 *            minimum parameter for second axis
	 * @param max1
	 *            maximum parameter for second axis
	 * @param fading
	 *            if we need fading or not
	 * 
	 */
	public void drawHyperbolicParaboloid(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double min0, double max0,
			double min1, double max1, boolean fading) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawHyperbolicParaboloid(center, ev0, ev1, ev2, r0, r1,
				min0, max0, min1, max1, fading);
	}

	/**
	 * draw a parabolic cylinder
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r
	 *            half axis
	 * @param min
	 *            minimum parameter for quadric axis
	 * @param max
	 *            maximum parameter for quadric axis
	 * @param lineMin
	 *            minimum parameter for line axis
	 * @param lineMax
	 *            maximum parameter for line axis
	 * @param fading
	 *            if we need fading or not
	 * 
	 */
	public void drawParabolicCylinder(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r, double min, double max,
			double lineMin, double lineMax, boolean fading) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawParabolicCylinder(center, ev0, ev1, ev2, r, min,
				max, lineMin, lineMax, fading);
	}

	/**
	 * draw an hyperbolic cylinder
	 * 
	 * @param center
	 *            center
	 * @param ev0
	 *            first eigenvector
	 * @param ev1
	 *            second eigenvector
	 * @param ev2
	 *            third eigenvector
	 * @param r1
	 *            half axis
	 * @param r2
	 *            half axis
	 * @param min
	 *            minimum parameter for quadric axis
	 * @param max
	 *            maximum parameter for quadric axis
	 * @param lineMin
	 *            minimum parameter for line axis
	 * @param lineMax
	 *            maximum parameter for line axis
	 * @param fading
	 *            if we need fading or not
	 * 
	 */
	public void drawHyperbolicCylinder(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r1, double r2, double min, double max,
			double lineMin, double lineMax, boolean fading) {

		if (managerElements == null) {
			managerElements = new ManagerElementForGLList(
					manager.getRenderer(), manager.getView3D(), manager);
			plotterElements = new PlotterSurfaceElements(managerElements);
		}

		plotterElements.drawHyperbolicCylinder(center, ev0, ev1, ev2, r1, r2,
				min, max, lineMin, lineMax, fading);
	}

	private Coords[] coordsArray = new Coords[0];


	/**
	 * draw a sphere with center and radius. view scaling is used to know how
	 * many triangles are needed
	 * 
	 * @param center
	 *            center of the sphere
	 * @param radius
	 *            radius of the sphere
	 * @param longitude
	 *            longitude length for rendering, corresponding to 2*PI (must be
	 *            power of 2)
	 * @param longitudeStart
	 *            for sphere parts, first longitude to draw
	 * @param longitudeLength
	 *            for sphere parts, longitude width (must be power of 2)
	 */
	public void drawSphere(Coords center, double radius, int longitude,
			double longitudeStart, int longitudeLength) {
		drawSphere(center, radius, longitude, longitudeStart, longitudeLength,
				manager.getView3D().getFrustumRadius());
	}

	public void drawSphere(Coords center, double radius, int longitude,
			double longitudeStart, int longitudeLength, double frustumRadius) {

		manager.startGeometry(Manager.Type.TRIANGLES);

		// set texture to (0,0)
		manager.setDummyTexture();

		int latitude = longitude / 4;

		// check which parts are visible (latitudes)
		Coords o = manager.getView3D().getCenter();

		double z = center.getZ();
		double zMin = o.getZ() - frustumRadius;
		double zMax = o.getZ() + frustumRadius;

		int latitudeMaxTop = 0;
		latitudeMaxTop = latitude;
		if (Kernel.isGreater(z + radius, zMax)) {
			double angle = Math.asin((zMax - z) / radius);
			latitudeMaxTop = (int) (latitude * 2 * angle / Math.PI) + 2;
		}

		int latitudeMaxBottom = 0;
		latitudeMaxBottom = latitude;
		if (Kernel.isGreater(zMin, z - radius)) {
			double angle = Math.asin((z - zMin) / radius);
			latitudeMaxBottom = (int) (latitude * 2 * angle / Math.PI) + 2;
		}

		// Log.debug(latitudeMaxBottom+","+latitudeMaxTop);

		int latitudeMax = Math.max(latitudeMaxTop, latitudeMaxBottom);

		int latitudeMin = 0; // start on equator
		if (latitudeMaxTop < 0) { // start below equator
			latitudeMin = -latitudeMaxTop;
		} else if (latitudeMaxBottom < 0) { // start above equator
			latitudeMin = -latitudeMaxBottom;
		}

		// check which parts are visible (longitudes)

		// start drawing
		if (coordsArray.length <= longitudeLength) {
			coordsArray = new Coords[longitudeLength + 1];
			for (int ui = 0; ui <= longitudeLength; ui++) {
				coordsArray[ui] = new Coords(4);
			}
		}

		Coords norm1 = new Coords(4), norm2 = new Coords(4), n1b = new Coords(4), n2b = new Coords(
				4);

		double[] cosSinV = new double[2];

		// equator
		// cosSinV[0] = 1; // cos(0)
		// cosSinV[1] = 0; // sin(0)
		cosSin(latitudeMin, latitude, cosSinV);
		double lastCos = 1;
		for (int ui = 0; ui <= longitudeLength; ui++) {
			sphericalCoords(ui, longitude, longitudeStart, cosSinV,
					coordsArray[ui]);
		}

		// shift for longitude
		int shift = 1;

		boolean jumpNeeded = false;

		for (int vi = latitudeMin + 1; vi < latitudeMax; vi++) {

			cosSin(vi, latitude, cosSinV);

			// check if parallel is small enough to make jumps
			if (2 * cosSinV[0] < lastCos) {
				lastCos = lastCos / 2;
				jumpNeeded = true;
			} else {
				jumpNeeded = false;
			}

			// first values
			norm2.set(coordsArray[0]);
			sphericalCoords(0, longitude, longitudeStart, cosSinV, n2b);

			// first : no jump
			boolean jump = jumpNeeded;

			for (int ui = shift; ui <= longitudeLength; ui += shift) {

				// last latitude values
				norm1.set(norm2);
				norm2.set(coordsArray[ui]);

				// new latitude values and draw triangles
				n1b.set(n2b);
				if (jumpNeeded) {
					if (jump) { // draw edge triangle and center triangle

						sphericalCoords(ui + shift, longitude, longitudeStart,
								cosSinV, n2b);

						if (vi < latitudeMaxTop) {// top triangles
							drawNCr(norm1, center, radius);
							drawNCr(norm2, center, radius);
							drawNCr(n1b, center, radius);

							drawNCr(n1b, center, radius);
							drawNCr(norm2, center, radius);
							drawNCr(n2b, center, radius);
						}

						if (vi < latitudeMaxBottom) {// bottom triangles
							drawNCrm(norm1, center, radius);
							drawNCrm(n1b, center, radius);
							drawNCrm(norm2, center, radius);

							drawNCrm(n1b, center, radius);
							drawNCrm(n2b, center, radius);
							drawNCrm(norm2, center, radius);
						}

					} else { // draw edge triangle

						sphericalCoords(ui, longitude, longitudeStart, cosSinV,
								n2b);

						if (vi < latitudeMaxTop) {// top triangles
							drawNCr(norm1, center, radius);
							drawNCr(norm2, center, radius);
							drawNCr(n1b, center, radius);
						}

						if (vi < latitudeMaxBottom) {// bottom triangles
							drawNCrm(norm1, center, radius);
							drawNCrm(n1b, center, radius);
							drawNCrm(norm2, center, radius);
						}

					}
				} else { // no jump : draw two triangles

					sphericalCoords(ui, longitude, longitudeStart, cosSinV, n2b);

					if (vi < latitudeMaxTop) {// top triangles
						drawNCr(norm1, center, radius);
						drawNCr(norm2, center, radius);
						drawNCr(n1b, center, radius);

						drawNCr(norm2, center, radius);
						drawNCr(n2b, center, radius);
						drawNCr(n1b, center, radius);
					}

					if (vi < latitudeMaxBottom) {// bottom triangles
						drawNCrm(norm1, center, radius);
						drawNCrm(n1b, center, radius);
						drawNCrm(norm2, center, radius);

						drawNCrm(norm2, center, radius);
						drawNCrm(n1b, center, radius);
						drawNCrm(n2b, center, radius);
					}

				}

				coordsArray[ui].set(n2b);

				if (jumpNeeded) {
					jump = !jump;
				}

			}

			// if just jumps done, next shift is twice
			if (jumpNeeded) {
				shift = shift * 2;
			}

			sphericalCoords(0, longitude, longitudeStart, cosSinV,
					coordsArray[0]);

		}

		if (latitudeMax == latitude) {
			// pole
			norm2.set(coordsArray[0]);
			for (int ui = shift; ui <= longitudeLength; ui += shift) {
				norm1.set(norm2);
				norm2.set(coordsArray[ui]);

				if (latitudeMaxTop == latitude) {// top triangles
					drawNCr(norm1, center, radius);
					drawNCr(norm2, center, radius);
					drawNCr(Coords.VZ, center, radius);
				}

				if (latitudeMaxBottom == latitude) {// bottom triangles
					drawNCrm(norm1, center, radius);
					drawNCrm(Coords.VZ, center, radius);
					drawNCrm(norm2, center, radius);
				}

			}
		}

		manager.endGeometry();

	}

	/**
	 * draw part of the surface
	 */
	public void draw() {
		manager.startGeometry(Manager.Type.TRIANGLES);

		du = (uMax - uMin) / uNb;
		dv = (vMax - vMin) / vNb;

		/*
		 * uMinFadeNb = uNb*uMinFade/(uMax-uMin); uMaxFadeNb =
		 * uNb*uMaxFade/(uMax-uMin); vMinFadeNb = vNb*vMinFade/(vMax-vMin);
		 * vMaxFadeNb = vNb*vMaxFade/(vMax-vMin);
		 */
		uMinFadeNb = uMinFade / du;
		uMaxFadeNb = uMaxFade / du;
		vMinFadeNb = vMinFade / dv;
		vMaxFadeNb = vMaxFade / dv;

		// Application.debug("vMin, vMax, dv="+vMin+", "+vMax+", "+dv);

		for (int ui = 0; ui < uNb; ui++) {

			for (int vi = 0; vi < vNb; vi++) {

				drawQuad(ui, vi);

			}

		}

		manager.endGeometry();
	}

	// private Coords n1, n2, n3, n4, v1, v2, v3, v4;

	public void drawSphere(int size, Coords center, double radius) {

		int longitude = 8;
		size += 3;
		while (longitude * 6 <= size * size) {// find the correct longitude size
												// (size=3 <-> longitude=12 and
												// size=9 <-> longitude=48)
			longitude *= 2;
		}

		drawSphere(center, radius, longitude, 0, longitude,
				Double.POSITIVE_INFINITY);
	}

	protected static void cosSin(int vi, int latitude, double[] ret) {
		double v = ((double) vi / latitude) * Math.PI / 2;
		ret[0] = Math.cos(v);
		ret[1] = Math.sin(v);
	}

	protected static final void sphericalCoords(int ui, int longitude,
			double longitudeStart, double[] cosSinV, Coords n) {

		double u = ((double) ui / longitude) * 2 * Math.PI + longitudeStart;

		n.setX(Math.cos(u) * cosSinV[0]);
		n.setY(Math.sin(u) * cosSinV[0]);
		n.setZ(cosSinV[1]);

	}

	// private static final void sphericalCoords(int ui, int vi, int longitude,
	// int latitude, Coords n) {
	//
	// double u = ((double) ui / longitude) * 2 * Math.PI;
	// double v = ((double) vi / latitude) * Math.PI / 2;
	//
	// n.setX(Math.cos(u) * Math.cos(v));
	// n.setY(Math.sin(u) * Math.cos(v));
	// n.setZ(Math.sin(v));
	//
	// }


	/**
	 * draws a disc
	 * 
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 */
	public void disc(Coords center, Coords v1, Coords v2, double radius) {
		manager.startGeometry(Manager.Type.TRIANGLE_FAN);

		int longitude = manager.getLongitudeDefault();

		Coords vn;

		float dt = (float) 1 / longitude;
		float da = (float) (2 * Math.PI * dt);

		manager.texture(0, 0);
		manager.normal(v1.crossProduct(v2));
		manager.triangleFanApex(center);

		float u = 1, v = 0;
		vn = v1.mul(u).add(v2.mul(v));
		manager.triangleFanVertex(center.add(vn.mul(radius)));

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(i * da);
			v = (float) Math.sin(i * da);

			vn = v1.mul(u).add(v2.mul(v));
			manager.triangleFanVertex(center.add(vn.mul(radius)));
		}

		manager.endGeometry();
	}

	/**
	 * draws a parallelogram
	 * 
	 * @param center
	 * @param v1
	 * @param v2
	 * @param l1
	 * @param l2
	 */
	public void parallelogram(Coords center, Coords v1, Coords v2, double l1,
			double l2) {
		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.setDummyTexture();
		tmpCoords.setCrossProduct(v1, v2);
		manager.normal(tmpCoords);

		tmpCoords.setAdd(center, tmpCoords.setMul(v1, l1));
		tmpCoords2.setAdd(tmpCoords, tmpCoords2.setMul(v2, l2));
		tmpCoords3.setAdd(center, tmpCoords3.setMul(v2, l2));

		manager.vertexToScale(center);
		manager.vertexToScale(tmpCoords);
		manager.vertexToScale(tmpCoords2);

		manager.vertexToScale(center);
		manager.vertexToScale(tmpCoords2);
		manager.vertexToScale(tmpCoords3);

		manager.endGeometry();
	}

	/**
	 * draws an ellipse
	 * 
	 * @param center
	 * @param v1
	 * @param v2
	 * @param a
	 * @param b
	 * @param start
	 * @param extent
	 */
	public void ellipsePart(Coords center, Coords v1, Coords v2, double a,
			double b, double start, double extent) {

		ellipsePart(center, v1, v2, a, b, start, extent, true);

	}

	protected Coords m = new Coords(4);
	protected Coords tmpCoords = new Coords(4);

	protected Coords tmpCoords2 = new Coords(4);

	protected Coords tmpCoords3 = new Coords(4);

	protected Coords center1 = new Coords(4);

	protected Coords center2 = new Coords(4);

	private Coords n = new Coords(4);

	/**
	 * @param center
	 * @param v1
	 * @param v2
	 * @param a
	 * @param b
	 * @param start
	 * @param extent
	 * @param fromEllipseCenter
	 *            says if the surface is drawn from center of the ellipse
	 */
	public void ellipsePart(Coords center, Coords v1, Coords v2, double a,
			double b, double start, double extent, boolean fromEllipseCenter) {

		manager.startGeometry(Manager.Type.TRIANGLE_FAN);

		int longitude = manager.getLongitudeDefault();

		float u, v;

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);

		manager.setDummyTexture();
		manager.normal(v1.crossProduct(v2));

		u = (float) Math.cos(start);
		v = (float) Math.sin(start);
		v1.mul(a * u, m);
		v2.mul(b * v, tmpCoords);
		m.add(tmpCoords, m);

		// center of the triangle fan
		if (fromEllipseCenter) { // center of the ellipse
			manager.triangleFanApex(center);
		} else { // mid point of the ellipse start and end
			u = (float) Math.cos(start + extent);
			v = (float) Math.sin(start + extent);

			v1.mul(a * u, tmpCoords2);
			v2.mul(b * v, tmpCoords);
			tmpCoords2.add(tmpCoords, tmpCoords2);

			tmpCoords2.add(m, tmpCoords2);
			tmpCoords2.mul(0.5, tmpCoords2);
			center.add(tmpCoords2, tmpCoords2);
			manager.triangleFanApex(tmpCoords2);
		}

		// first point
		manager.triangleFanVertex(center.add(m));

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cos(start + i * da);
			v = (float) Math.sin(start + i * da);

			v1.mul(a * u, m);
			v2.mul(b * v, tmpCoords);
			m.add(tmpCoords, m);
			center.add(m, m);
			manager.triangleFanVertex(m);
		}

		manager.endGeometry();

	}

	/**
	 * @param center
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param radius
	 * @param start
	 * @param extent
	 * @param height
	 * @param fading
	 * @return center of the bottom
	 */
	public Coords cone(Coords center, Coords vx, Coords vy, Coords vz,
			double radius, double start, double extent, double height,
			float fading) {
		return cone(center, vx, vy, vz, radius, radius, start, extent, height,
				fading);
	}

	/**
	 * @param center
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param r1
	 * @param r2
	 * @param start
	 * @param extent
	 * @param height
	 * @param fading
	 * @return center of the bottom
	 */
	public Coords cone(Coords center, Coords vx, Coords vy, Coords vz,
			double r1, double r2, double start, double extent, double height,
			float fading) {
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);

		int longitude = manager.getLongitudeDefault();

		double u, v;

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);
		// if (height > 0){ // ensure correct back/front face culling
		// da *= -1;
		// }

		if (fading == 1) { // no fading
			manager.setDummyTexture();
		}

		center2.set(vz);
		center2.mulInside3(height);
		center2.addInside(center);

		double r1h = r1 * -height;
		double r2h = r2 * -height;
		double rr = r1 * r2;

		for (int i = 0; i <= longitude; i++) {
			u = Math.cos(start + i * da);
			v = Math.sin(start + i * da);

			m.setAdd(tmpCoords2.setMul(vx, u * r1h),
					tmpCoords3.setMul(vy, v * r2h));

			n.setMul(vx, r2 * u);
			tmpCoords.setMul(vy, r1 * v);
			n.addInside(tmpCoords);
			tmpCoords.setMul(vz, rr);
			n.addInside(tmpCoords);
			n.normalize();

			// center of the triangle fan
			if (fading < 1) {
				manager.texture(0, fading);
			}
			manager.normal(n);
			manager.vertexToScale(center);

			// point on circle
			if (fading < 1) {
				manager.texture(0, 1);
			}
			manager.normal(n);
			manager.vertexToScale(tmpCoords2.setAdd(center2, m));
		}

		manager.endGeometry();
		
		return center2;

	}

	public void cone(Coords center, Coords vx, Coords vy, Coords vz,
			double radius, double start, double extent, double min, double max,
			boolean minFading, boolean maxFading) {
		cone(center, vx, vy, vz, radius, radius, start, extent, min, max,
				minFading, maxFading);
	}

	public void cone(Coords center, Coords vx, Coords vy, Coords vz,
			double r1, double r2, double start, double extent, double min,
			double max,
			boolean minFading, boolean maxFading) {
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);

		int longitude = manager.getLongitudeDefault();

		float u, v;

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);

		center1.set(vz);
		center1.mulInside3(min);
		center1.addInside(center);
		
		center2.set(vz);
		center2.mulInside3(max);
		center2.addInside(center);

		double rmin = r1 * min;
		double rmax = r1 * max;
		double ratio = r2 / r1;

		// ensure radius are positive and normals go outside
		int sgn = 1;
		if (max > 0) {
			sgn = -1;
		} else {
			rmin *= -1;
			rmax *= -1;
		}

		double rr = r1 * r2 * sgn;

		boolean fading = minFading || maxFading;
		if (!fading) {
			manager.setDummyTexture();
		}

		for (int i = 0; i <= longitude; i++) {
			u = (float) Math.cos(start + i * da);
			v = (float) Math.sin(start + i * da);

			m.setAdd(tmpCoords2.setMul(vx, u), tmpCoords3.setMul(vy, v * ratio));

			n.setMul(vx, r2 * u);
			tmpCoords.setMul(vy, r1 * v);
			n.addInside(tmpCoords);
			tmpCoords.setMul(vz, rr);
			n.addInside(tmpCoords);
			n.normalize();


			// point on top circle
			if (fading) {
				if (maxFading) {
					manager.texture(0, 1);
				} else {
					manager.texture(0, 0);
				}
			}
			manager.normal(n);
			manager.vertexToScale(tmpCoords2.setAdd(center2,
					tmpCoords3.setMul(m, rmax)));

			// point on bottom circle
			if (fading) {
				if (minFading) {
					manager.texture(0, 1);
				} else {
					manager.texture(0, 0);
				}
			}
			manager.normal(n);
			manager.vertexToScale(tmpCoords2.setAdd(center1,
					tmpCoords3.setMul(m, rmin)));

		}

		manager.endGeometry();

	}

	public Coords cylinder(Coords center, Coords vx, Coords vy, Coords vz,
			double radius, double start, double extent, double min, double max,
			boolean minFading, boolean maxFading, int longitude) {

		return cylinder(center, vx, vy, vz, radius, radius, start, extent, min,
				max, minFading, maxFading, longitude);
	}

	public Coords cylinder(Coords center, Coords vx, Coords vy, Coords vz,
			double r1, double r2, double start, double extent, double min,
			double max,
			boolean minFading, boolean maxFading, int longitude) {
		manager.startGeometry(Manager.Type.TRIANGLE_STRIP);


		float c, s;

		float dt = (float) 1 / longitude;
		float da = (float) (extent * dt);

		center1.set(vz);
		center1.mulInside3(min);
		center1.addInside(center);
		
		center2.set(vz);
		center2.mulInside3(max);
		center2.addInside(center);

		boolean fading = minFading || maxFading;
		if (!fading) {
			manager.setDummyTexture();
		}

		for (int i = 0; i <= longitude; i++) {
			c = (float) Math.cos(start + i * da);
			s = (float) Math.sin(start + i * da);

			n.setAdd(tmpCoords.setMul(vx, r2 * c),
					tmpCoords2.setMul(vy, r1 * s));
			n.normalize();

			// point on top circle
			if (fading) {
				if (maxFading) {
					manager.texture(0, 1);
				} else {
					manager.texture(0, 0);
				}
			}

			tmpCoords3.setAdd(tmpCoords.setMul(vx, r1 * c),
					tmpCoords2.setMul(vy, r2 * s));

			manager.normal(n);
			manager.vertexToScale(tmpCoords.setAdd(center2, tmpCoords3));
			// point on bottom circle
			if (fading) {
				if (minFading) {
					manager.texture(0, 1);
				} else {
					manager.texture(0, 0);
				}
			}
			manager.normal(n);
			manager.vertexToScale(tmpCoords.setAdd(center1, tmpCoords3));

		}

		manager.endGeometry();
		
		return center2;

	}

	/**
	 * draws the inside of the hyperobola part
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
	public void hyperbolaPart(Coords center, Coords v1, Coords v2, double a,
			double b, double tMin, double tMax) {

		manager.startGeometry(Manager.Type.TRIANGLE_FAN);

		manager.texture(0, 0);
		manager.normal(v1.crossProduct(v2));

		int longitude = manager.getLongitudeDefault();

		Coords m;

		float dt = (float) (tMax - tMin) / longitude;

		float u, v;

		// first point on the branch
		u = (float) Math.cosh(tMin);
		v = (float) Math.sinh(tMin);
		m = v1.mul(a * u).add(v2.mul(b * v));

		// center of the fan is midpoint of branch ends
		u = (float) Math.cosh(tMax);
		v = (float) Math.sinh(tMax);
		manager.triangleFanApex(center.add((m.add(v1.mul(a * u).add(
				v2.mul(b * v)))).mul(0.5)));

		// first point
		manager.triangleFanVertex(center.add(m));

		for (int i = 1; i <= longitude; i++) {
			u = (float) Math.cosh(tMin + i * dt);
			v = (float) Math.sinh(tMin + i * dt);

			m = v1.mul(a * u).add(v2.mul(b * v));
			manager.triangleFanVertex(center.add(m));
		}

		manager.endGeometry();

	}

	/**
	 * fill a parabola
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
	 */
	public void parabola(Coords center, Coords v1, Coords v2, double p,
			double tMin, double tMax) {

		manager.startGeometry(Manager.Type.TRIANGLE_FAN);

		manager.texture(0, 0);
		manager.normal(v1.crossProduct(v2));

		int longitude = manager.getLongitudeDefault();

		Coords m;

		float dt = (float) (tMax - tMin) / longitude;

		float u, v;
		double t;

		// first point
		t = tMin;
		u = (float) (p * t * t / 2);
		v = (float) (p * t);
		m = v1.mul(u).add(v2.mul(v));

		// center of the fan is midpoint of branch ends
		t = tMax;
		u = (float) (p * t * t / 2);
		v = (float) (p * t);
		manager.triangleFanApex(center.add((m.add(v1.mul(u).add(v2.mul(v))))
				.mul(0.5)));

		// first point
		manager.triangleFanVertex(center.add(m));

		for (int i = 1; i <= longitude; i++) {
			t = tMin + i * dt;
			u = (float) (p * t * t / 2);
			v = (float) (p * t);

			m = v1.mul(u).add(v2.mul(v));
			manager.triangleFanVertex(center.add(m));
		}

		manager.endGeometry();

	}

	private Coords calcNormal(float x, float y, float z) {
		Coords v0 = new Coords(x, y, z, 0);
		Coords v1 = function.evaluatePoint(x + 1e-9, y);
		Coords v2 = function.evaluatePoint(x, y + 1e-9);

		return v1.sub(v0).crossProduct(v2.sub(v0)).normalized();
	}


	private void drawQuad(int ui, int vi) {

		drawTNV(ui, vi);
		drawTNV(ui + 1, vi);
		drawTNV(ui + 1, vi + 1);

		drawTNV(ui, vi);
		drawTNV(ui + 1, vi + 1);
		drawTNV(ui, vi + 1);

	}

	private void drawTNV(int ui, int vi) {

		float uT = getTextureCoord(ui, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(vi, vNb, vMinFadeNb, vMaxFadeNb);
		manager.texture(uT, vT);

		float u = uMin + ui * du;
		float v = vMin + vi * dv;
		drawNV(functional2Var.evaluateNormal(u, v),
				functional2Var.evaluatePoint(u, v));
	}

	private Coords coords1 = new Coords(4);

	/**
	 * draws normal and point at center + normal * radius
	 * 
	 * @param normal
	 * @param center
	 * @param radius
	 */
	protected void drawNCr(Coords normal, Coords center, double radius) {
		normal.mul(radius, coords1);
		center.add(coords1, coords1);
		// drawNV(normal, center.add(normal.mul(radius)));
		drawNV(normal, coords1);
	}

	private Coords normal2 = new Coords(4);

	/**
	 * draws normal and point at center - normal * radius
	 * 
	 * @param normal
	 * @param center
	 * @param radius
	 */
	protected void drawNCrm(Coords normal, Coords center, double radius) {
		normal2.setX(normal.getX());
		normal2.setY(normal.getY());
		normal2.setZ(-normal.getZ());
		drawNCr(normal2, center, radius);
	}

	public void drawNV(Coords normal, Coords point) {
		manager.normal(normal);
		manager.vertexToScale(point);
	}

	private static float getTextureCoord(int i, int n, float fadeMin,
			float fadeMax) {

		float t;

		if (fadeMin != 0) {
			if (i <= n / 2) {
				t = i / fadeMin;
				return TEXTURE_FADE_OUT * (1 - t) + TEXTURE_FADE_IN * t;
			}
		}

		if (fadeMax != 0) {
			if (i >= n / 2) {
				t = (n - i) / fadeMax;
				return TEXTURE_FADE_OUT * (1 - t) + TEXTURE_FADE_IN * t;
			}
		}

		return TEXTURE_FADE_IN;
	}

}
