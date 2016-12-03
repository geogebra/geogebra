package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.util.debug.Log;

public class PlotterSurfaceElements extends PlotterSurface {

	final private static boolean DEBUG = false;

	final private static void debug(String s) {
		if (DEBUG) {
			Log.debug(s);
		}
	}

	private int latitudeMin;
	private int latitude;
	private int latitudeMax;
	private int latitudeMaxTop;
	private int latitudeMaxBottom;
	
	private interface DrawEllipticSurface {
		public void drawNCr(Coords normal);

		public void drawNCrm(Coords normal);

		/**
		 * compute radius and z for given latitude
		 * 
		 * @param v
		 *            current latitude
		 * @param latitudeLength
		 *            latitude length
		 * @param rz
		 *            radius and z return
		 */
		public void computeRadiusAndZ(int v, int latitudeLength, double[] rz);

		/**
		 * 
		 * @return true if we draw poles
		 */
		public boolean drawPoles();

		/**
		 * 
		 * @param latitudeLength
		 *            latitude length
		 * @param longitudeLength TODO
		 * @return next jump for latitude length
		 */
		public int initNextJump(int latitudeLength, int longitudeLength);

		/**
		 * 
		 * @param nextJump
		 *            current value
		 * @param latitudeLength
		 *            latitude length
		 * @return updated value for next jump
		 */
		public int updateNextJump(int nextJump, int latitudeLength);

		/**
		 * 
		 * @return true if we draw equator
		 */
		public boolean drawEquator();

		/**
		 * 
		 * @param vi
		 *            latitude index
		 * @return true if we draw top part at vi (normals and vertices)
		 */
		public boolean drawTop(int vi);

		/**
		 * 
		 * @param vi
		 *            latitude index
		 * @return true if we draw bottom part at vi (normals and vertices)
		 */
		public boolean drawBottom(int vi);

		/**
		 * 
		 * @return north pole
		 */
		public Coords getNorthPole();

	}

	private class DrawSphere implements DrawEllipticSurface {

		private PlotterSurface surface;
		private Coords center;
		private double radius;

		public DrawSphere() {
		}

		public void set(PlotterSurface surface, Coords center, double radius) {
			this.surface = surface;
			this.center = center;
			this.radius = radius;
		}

		@Override
		public void drawNCr(Coords normal) {
			surface.drawNCr(normal, center, radius);
		}

		@Override
		public void drawNCrm(Coords normal) {
			surface.drawNCrm(normal, center, radius);
		}

		@Override
		public void computeRadiusAndZ(int v, int latitudeLength, double[] rz) {
			PlotterSurface.cosSin(v, latitudeLength, rz);
		}

		@Override
		public boolean drawPoles() {
			return true;
		}

		@Override
		public Coords getNorthPole() {
			return Coords.VZ;
		}

		@Override
		public int initNextJump(int latitudeLength, int longitudeLength) {
			return (int) (latitudeLength / Math.PI);
		}

		@Override
		public int updateNextJump(int nextJump, int latitudeLength) {
			return nextJump / 2;
		}

		@Override
		public boolean drawEquator() {
			return true;
		}

		@Override
		public boolean drawTop(int vi) {
			return vi < latitudeMaxTop;
		}

		@Override
		public boolean drawBottom(int vi) {
			return vi < latitudeMaxBottom;
		}


	}

	private class DrawEllipsoid implements DrawEllipticSurface {

		private PlotterSurface surface;
		private Coords center;
		private Coords ev0, ev1, ev2;
		private double r0, r1, r2;

		private Coords c = Coords.createInhomCoorsInD3();
		private Coords n = new Coords(4);
		private Coords tmpCoords = new Coords(4);

		public DrawEllipsoid() {
		}

		public void set(PlotterSurface surface, Coords center, Coords ev0,
				Coords ev1, Coords ev2, double r0, double r1, double r2) {
			this.surface = surface;
			this.center = center;
			this.ev0 = ev0;
			this.ev1 = ev1;
			this.ev2 = ev2;
			this.r0 = r0;
			this.r1 = r1;
			this.r2 = r2;
		}

		@Override
		public void drawNCr(Coords normal) {
			c.setValues(center, 3);
			tmpCoords.setMul(ev0, r0 * normal.getX());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev1, r1 * normal.getY());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev2, r2 * normal.getZ());
			c.addInside(tmpCoords);

			n.setMul(ev0, r1 * r2 * normal.getX());
			tmpCoords.setMul(ev1, r0 * r2 * normal.getY());
			n.addInside(tmpCoords);
			tmpCoords.setMul(ev2, r0 * r1 * normal.getZ());
			n.addInside(tmpCoords);
			n.normalize();

			surface.drawNV(n, c);
		}

		@Override
		public void drawNCrm(Coords normal) {
			c.setValues(center, 3);
			tmpCoords.setMul(ev0, r0 * normal.getX());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev1, r1 * normal.getY());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev2, -r2 * normal.getZ());
			c.addInside(tmpCoords);

			n.setMul(ev0, r1 * r2 * normal.getX());
			tmpCoords.setMul(ev1, r0 * r2 * normal.getY());
			n.addInside(tmpCoords);
			tmpCoords.setMul(ev2, -r0 * r1 * normal.getZ());
			n.addInside(tmpCoords);
			n.normalize();

			surface.drawNV(n, c);
		}

		@Override
		public void computeRadiusAndZ(int v, int latitudeLength, double[] rz) {
			PlotterSurface.cosSin(v, latitudeLength, rz);
		}

		@Override
		public boolean drawPoles() {
			return true;
		}

		@Override
		public Coords getNorthPole() {
			return Coords.VZ;
		}

		@Override
		public int initNextJump(int latitudeLength, int longitudeLength) {
			return (int) (latitudeLength / Math.PI);
		}

		@Override
		public int updateNextJump(int nextJump, int latitudeLength) {
			return nextJump / 2;
		}

		@Override
		public boolean drawEquator() {
			return true;
		}

		@Override
		public boolean drawTop(int vi) {
			return vi < latitudeMaxTop;
		}

		@Override
		public boolean drawBottom(int vi) {
			return vi < latitudeMaxBottom;
		}


	}

	private class DrawHyperboloidOneSheet implements DrawEllipticSurface {

		protected PlotterSurface surface;
		protected Coords center;
		protected Coords ev0, ev1, ev2;
		protected double r0, r1, r2;

		protected double min;
		protected double max;

		protected double jump;

		private int longitudeJumps;

		protected boolean fading;

		protected Coords c = Coords.createInhomCoorsInD3();
		protected Coords n = new Coords(4);
		protected Coords tmpCoords = new Coords(4);

		public DrawHyperboloidOneSheet() {
		}

		public void set(PlotterSurface surface, Coords center, Coords ev0,
				Coords ev1, Coords ev2, double r0, double r1, double r2,
				boolean fading) {
			this.surface = surface;
			this.center = center;
			this.ev0 = ev0;
			this.ev1 = ev1;
			this.ev2 = ev2;
			this.r0 = r0;
			this.r1 = r1;
			this.r2 = r2;

			this.fading = fading;


		}
		
		protected double maxFadingStartTop, maxFadingEndTop, middleFading,
				maxFadingStartBottom, maxFadingEndBottom;


		public void setMinMax(double min, double max) {
			if (min < 0) {
				if (max > 0) {
					this.min = 0;
					this.max = Math.max(-min, max);
				} else {
					this.min = -max;
					this.max = -min;
				}
			} else {
				this.min = min;
				this.max = max;
			}

			if (fading) {
				double shMin = Math.sinh(min);
				double shMax = Math.sinh(max);
				middleFading = (shMax + shMin) / 2;
				maxFadingStartTop = shMax * 0.9 + shMin * 0.1;
				maxFadingEndTop = shMax;
				maxFadingStartBottom = shMax * 0.1 + shMin * 0.9;
				maxFadingEndBottom = shMin;
			}

			// Log.debug(maxFading + "," + max);

			// use asymptotic behavior of cosh() for (un)refine radius
			jump = Math.log(2) / max;

		}

		@Override
		public void drawNCr(Coords normal) {
			
			double z = normal.getZ();
			
			c.setValues(center, 3);
			tmpCoords.setMul(ev0, -r0 * normal.getX());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev1, r1 * normal.getY());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev2, r2 * z);
			c.addInside(tmpCoords);

			n.setMul(ev0, -r1 * r2 * normal.getX());
			tmpCoords.setMul(ev1, r0 * r2 * normal.getY());
			n.addInside(tmpCoords);
			tmpCoords.setMul(ev2, -r0 * r1 * z);
			n.addInside(tmpCoords);
			n.normalize();

			if (fading) {
				if (z > middleFading) {
					manager.texture(0, (z - maxFadingStartTop)
							/ (maxFadingEndTop - maxFadingStartTop));
				} else {
					manager.texture(0, (z - maxFadingStartBottom)
							/ (maxFadingEndBottom - maxFadingStartBottom));
				}
			}
			surface.drawNV(n, c);
		}

		@Override
		public void drawNCrm(Coords normal) {

			double z = -normal.getZ();

			c.setValues(center, 3);
			tmpCoords.setMul(ev0, -r0 * normal.getX());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev1, r1 * normal.getY());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev2, r2 * z);
			c.addInside(tmpCoords);

			n.setMul(ev0, -r1 * r2 * normal.getX());
			tmpCoords.setMul(ev1, r0 * r2 * normal.getY());
			n.addInside(tmpCoords);
			tmpCoords.setMul(ev2, -r0 * r1 * z);
			n.addInside(tmpCoords);
			n.normalize();

			if (fading) {
				if (z < middleFading) {
					manager.texture(0, (z - maxFadingStartBottom)
							/ (maxFadingEndBottom - maxFadingStartBottom));
				} else {
					manager.texture(0, (z - maxFadingStartTop)
							/ (maxFadingEndTop - maxFadingStartTop));
				}
			}
			surface.drawNV(n, c);
		}

		@Override
		public void computeRadiusAndZ(int vi, int latitudeLength, double[] rz) {
			double v = computeV(vi, latitudeLength);
			rz[0] = Math.cosh(v);
			rz[1] = Math.sinh(v);
		}

		private double computeV(int vi, int latitudeLength) {
			return min
					+ ((double) (latitudeLength - vi - 1) / (latitudeLength - 2))
					* (max - min);
		}

		@Override
		public boolean drawPoles() {
			return false;
		}

		@Override
		public Coords getNorthPole() {
			return null; // no pole here
		}

		@Override
		public int initNextJump(int latitudeLength, int longitudeLength) {
			// if (jump > 1) {
			// return 0;
			// }
			// int ret = (int) (jump * latitudeLength);
			// if (ret < 2) { // avoid jump after first step
			// return 0;
			// }
			// ret = latitudeLength - ret;
			// // asymptotic (un)refine works when x > 2
			// if (ret * max < 2 * latitudeLength) {
			// return 0;
			// }
			// longitudeJumps = longitudeLength;
			// Log.debug(longitudeLength + "," + latitudeLength + "," + ret);
			// return ret;
			return 0;
		}

		@Override
		public int updateNextJump(int nextJump, int latitudeLength) {
			if (longitudeJumps <= 8) { // avoid not enough longitudes
				return 0;
			}
			longitudeJumps /= 2;
			int ret = ((int) (nextJump - jump * latitudeLength));
			if (ret < 0) {
				return 0;
			}
			// asymptotic (un)refine works when x > 2
			if (ret * max < 2 * latitudeLength) {
				return 0;
			}
			return ret;
		}

		@Override
		public boolean drawEquator() {
			return false;
		}

		@Override
		public boolean drawTop(int vi) {
			return vi >= latitudeMaxTop;
		}

		@Override
		public boolean drawBottom(int vi) {
			return vi >= latitudeMaxBottom;
		}


	}

	private class DrawHyperboloidTwoSheets extends DrawHyperboloidOneSheet {


		public DrawHyperboloidTwoSheets() {
		}



		@Override
		public void setMinMax(double min, double max) {
			if (min < 0) {
				if (max > 0) {
					this.min = 0;
					this.max = Math.max(-min, max);
				} else {
					this.min = -max;
					this.max = -min;
				}
			} else {
				this.min = min;
				this.max = max;
			}

			if (fading) {
				double chMin;
				if (Kernel.isZero(min)) {
					chMin = 0;
				} else {
					chMin = Math.cosh(min);
					if (min < 0) {
						chMin = -chMin;
					}
				}
				double chMax;
				if (Kernel.isZero(max)) {
					chMax = 0;
				} else {
					chMax = Math.cosh(max);
					if (max < 0) {
						chMax = -chMax;
					}
				}
				middleFading = (chMax + chMin) / 2;
				maxFadingStartTop = chMax * 0.9 + chMin * 0.1;
				maxFadingEndTop = chMax;
				maxFadingStartBottom = chMax * 0.1 + chMin * 0.9;
				maxFadingEndBottom = chMin;
			}


			// use asymptotic behavior of cosh() for (un)refine radius
			jump = Math.log(2) / max;

		}


		@Override
		public void computeRadiusAndZ(int vi, int latitudeLength, double[] rz) {
			double v = computeV(vi, latitudeLength);
			rz[0] = Math.sinh(v);
			rz[1] = Math.cosh(v);
		}

		private double computeV(int vi, int latitudeLength) {
			return min
					+ ((double) (latitudeLength - vi) / (latitudeLength - 2))
					* (max - min);
		}

		@Override
		public boolean drawPoles() {
			return true;
		}


		@Override
		public Coords getNorthPole() {
			return Coords.VZ;
		}


	}

	private class DrawParaboloid extends DrawHyperboloidOneSheet {

		public DrawParaboloid() {
		}

		@Override
		public void setMinMax(double min, double max) {
			this.min = min;
			this.max = max;

			if (fading) {
				if (Kernel.isZero(min)) {
					middleFading = 0;
					maxFadingEndTop = max * max;
					maxFadingStartTop = maxFadingEndTop * 0.9;
					maxFadingStartBottom = 0;
					maxFadingEndBottom = 0;
				} else {
					double min2 = min * min;
					double max2 = max * max;
					middleFading = (min2 + max2) / 2;
					maxFadingStartTop = max2 * 0.9 + min2 * 0.1;
					maxFadingEndTop = max2;
					maxFadingStartBottom = max2 * 0.1 + min2 * 0.9;
					maxFadingEndBottom = min2;
				}
			}

			// use asymptotic behavior of cosh() for (un)refine radius
			jump = Math.log(2) / max;

		}

		@Override
		public void drawNCr(Coords normal) {

			double z = normal.getZ();

			c.setValues(center, 3);
			tmpCoords.setMul(ev0, -r0 * normal.getX());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev1, r1 * normal.getY());
			c.addInside(tmpCoords);
			tmpCoords.setMul(ev2, r2 * z);
			c.addInside(tmpCoords);

			n.setMul(ev0, -2 * r1 * r2 * normal.getX());
			tmpCoords.setMul(ev1, 2 * r0 * r2 * normal.getY());
			n.addInside(tmpCoords);
			tmpCoords.setMul(ev2, -r0 * r1);
			n.addInside(tmpCoords);
			n.normalize();

			if (fading) {
				if (z > middleFading) {
					manager.texture(0, (z - maxFadingStartTop)
							/ (maxFadingEndTop - maxFadingStartTop));
				} else {
					manager.texture(0, (z - maxFadingStartBottom)
							/ (maxFadingEndBottom - maxFadingStartBottom));
				}
			}
			surface.drawNV(n, c);
		}

		@Override
		public void computeRadiusAndZ(int vi, int latitudeLength, double[] rz) {
			double v = computeV(vi, latitudeLength);
			rz[0] = v;
			rz[1] = v * v;
		}

		private double computeV(int vi, int latitudeLength) {
			return min
					+ ((double) (latitudeLength - vi) / (latitudeLength - 2))
					* (max - min);
		}

		@Override
		public boolean drawPoles() {
			return min == 0;
		}

		@Override
		public Coords getNorthPole() {
			return Coords.O;
		}

		@Override
		public boolean drawTop(int vi) {
			return vi >= latitudeMaxTop;
		}

		@Override
		public boolean drawBottom(int vi) {
			return false;
		}

	}

	public PlotterSurfaceElements(Manager manager) {
		super(manager);
	}



	@Override
	public void drawSphere(Coords center, double radius, int longitude,
			double longitudeStart, int longitudeLength, double frustumRadius) {

		startGeometry();

		// set texture to (0,0)
		manager.setDummyTexture();

		setLatitudeMinMaxForEllipsoid(center, radius, longitude, frustumRadius);

		if (drawSphere == null) {
			drawSphere = new DrawSphere();
		}
		drawSphere.set(this, center, radius);

		drawNV(drawSphere, longitude, longitudeStart,
				longitudeLength);

		setIndices(longitude, longitudeLength, drawSphere);

	}

	private void startGeometry() {
		manager.startGeometry(Manager.Type.TRIANGLES);
		// manager.getRenderer().setLineWidth(1);
		// manager.startGeometry(Manager.Type.LINE_STRIP);

	}

	private void setLatitudeMinMaxForEllipsoid(Coords center, double radius,
			int longitude, double frustumRadius) {

		latitude = longitude / 4;

		// check which parts are visible (latitudes)
		Coords o = manager.getView3D().getCenter();

		double z = center.getZ();
		double zMin = o.getZ() - frustumRadius;
		double zMax = o.getZ() + frustumRadius;

		latitudeMaxTop = latitude;
		if (Kernel.isGreater(z + radius, zMax)) {
			double angle = Math.asin((zMax - z) / radius);
			latitudeMaxTop = (int) (latitude * 2 * angle / Math.PI) + 2;
		}

		latitudeMaxBottom = latitude;
		if (Kernel.isGreater(zMin, z - radius)) {
			double angle = Math.asin((z - zMin) / radius);
			latitudeMaxBottom = (int) (latitude * 2 * angle / Math.PI) + 2;
		}

		// debug(latitudeMaxBottom+","+latitudeMaxTop);

		latitudeMax = Math.max(latitudeMaxTop, latitudeMaxBottom);
		if (latitudeMax > latitude) {
			latitudeMax = latitude;
		}

		latitudeMin = 0; // start on equator
		if (latitudeMaxTop < 0) { // start below equator
			latitudeMin = -latitudeMaxTop;
		} else if (latitudeMaxBottom < 0) { // start above equator
			latitudeMin = -latitudeMaxBottom;
		}

		// prevent too close values
		if (latitudeMin + 1 >= latitudeMax) {
			if (latitudeMax >= 2) {
				latitudeMin = latitudeMax - 2;
			} else {
				latitudeMax = latitudeMin + 2;
			}
		}

		// Log.debug(latitudeMin + "," + latitudeMax + "," + latitudeMaxBottom
		// + "," + latitudeMaxTop + "," + latitude);

	}

	private void setLatitudeMinMaxForHyperboloid(double min, double max,
			DrawHyperboloidOneSheet dhos) {

		if (min < 0) {
			if (max > 0) {
				if (-min > max) {// more bottom than top
					latitudeMaxTop = (int) (latitude * (1 - max / (-min)));
					if (latitudeMaxTop == 1) {
						latitudeMaxTop = 2; // ensure at least a strip is drawn
					}
					latitudeMaxBottom = 0;
				} else { // more top than bottom
					latitudeMaxTop = 0;
					latitudeMaxBottom = (int) (latitude * (1 - (-min) / max));
					if (latitudeMaxBottom == 1) {
						latitudeMaxBottom = 2; // ensure at least a strip is
												// drawn
					}
				}
				latitudeMax = latitude;
				latitudeMin = 0;
				dhos.setMinMax(min, max);
			} else {
				// only bottom
				latitudeMaxTop = latitude;
				latitudeMaxBottom = 0;
				latitudeMax = latitude;
				latitudeMin = 0;
				dhos.setMinMax(min, max);
			}
		} else {
			// only top
			latitudeMaxTop = 0;
			latitudeMaxBottom = latitude;
			latitudeMax = latitude;
			latitudeMin = 0;
			dhos.setMinMax(min, max);
		}

		// Log.debug("min=" + min + ", max=" + max + "," + latitudeMin + ","
		// + latitudeMax + "," + latitudeMaxBottom + "," + latitudeMaxTop
		// + "," + latitude);

	}

	private void setLatitudeMinMaxForParaboloid(double min, double max,
			DrawParaboloid dp) {

		// only top
		latitudeMaxTop = 0;
		latitudeMaxBottom = latitude;
		latitudeMax = latitude;
		latitudeMin = 0;
		dp.setMinMax(min, max);

	}


	private Coords n;

	private DrawSphere drawSphere;


	private void drawNV(DrawEllipticSurface dse, int longitude,
			double longitudeStart, int longitudeLength) {

		// start drawing
		if (n == null) {
			n = new Coords(4);
		}

		// values for radius and z at each latitude
		double[] rz = new double[2];

		debug("longitude = " + longitude + " , longitudeLength = "
				+ longitudeLength);

		short lastLength, currentLength;
		boolean drawTop, drawBottom;
		int vi, nextJump, next;
		short shift;

		// ///////////////
		// draw vertices

		// first latitude
		if (dse.drawEquator()) {
			dse.computeRadiusAndZ(latitudeMin, latitude, rz);
			for (int ui = 0; ui < longitudeLength; ui++) {
				sphericalCoords(ui, longitude, longitudeStart, rz, n);
				dse.drawNCr(n);
			}
		}

		arrayIndex = 0;

		lastLength = (short) longitudeLength;
		currentLength = (short) longitudeLength;
		
		// both = 1 if only drawing up or down, both = 2 if drawing both
		drawTop = true;
		drawBottom = true;

		vi = latitudeMin + 1;
		nextJump = dse.initNextJump(latitude, longitude);
		debug("latitude : " + latitude + " , latitude-nextJump : "
				+ (latitude - nextJump));
		next = 0;
		shift = 1;

		while (next < latitudeMax) {
			
			next = Math.min(latitudeMax, latitude - nextJump);
			debug("latitude : " + latitude + " , latitudeMin : " + latitudeMin
					+ " , next : " + next + " , latitudeMax : " + latitudeMax);
			while (next < latitudeMin + 2) {
				nextJump = dse.updateNextJump(nextJump, latitude);
				next = Math.min(latitudeMax, latitude - nextJump);
				debug(">> next : " + next);
			}

			// until next jump
			while (vi < next) {

				drawTop = dse.drawTop(vi);
				drawBottom = dse.drawBottom(vi);

				dse.computeRadiusAndZ(vi, latitude, rz);
				for (int ui = 0; ui < longitudeLength; ui += shift) {
					sphericalCoords(ui, longitude, longitudeStart, rz, n);
					if (drawTop) {// top vertices
						dse.drawNCr(n);
					}
					if (drawBottom) {// bottom vertices
						dse.drawNCrm(n);
					}
				}


				debug("vi : " + vi);

				lastLength = currentLength;


				if (drawTop) {// top triangles
					if (longitudeLength == longitude) {
						arrayIndex += 6 * lastLength;
					} else {
						arrayIndex += 6 * (lastLength - 1);
					}

				}

				if (drawBottom) {// bottom triangles
					if (longitudeLength == longitude) {
						arrayIndex += 6 * lastLength;
					} else {
						arrayIndex += 6 * (lastLength - 1);
					}
				}

				vi++;
			}

			// jump
			if (next < latitudeMax) {

				shift *= 2;
				dse.computeRadiusAndZ(vi, latitude, rz);
				for (int ui = 0; ui < longitudeLength; ui += shift) {
					sphericalCoords(ui, longitude, longitudeStart, rz, n);
					if (drawTop) {// top vertices
						dse.drawNCr(n);
					}
					if (drawBottom) {// bottom vertices
						dse.drawNCrm(n);
					}

				}

				lastLength = currentLength;
				currentLength /= 2;

				if (drawTop) {// top triangles
					if (longitudeLength == longitude) {
						arrayIndex += 9 * currentLength;
					} else {
						arrayIndex += 9 * (currentLength - 1);
					}
				}

				if (drawBottom) {// bottom triangles
					if (longitudeLength == longitude) {
						arrayIndex += 9 * currentLength;
					} else {
						arrayIndex += 9 * (currentLength - 1);
					}
				}

				vi++;

				nextJump = dse.updateNextJump(nextJump, latitude);
			}


		}


		lastLength = currentLength;


		if (dse.drawPoles()) {
			// north pole
			if (latitudeMax == latitude) {

				if (drawTop) {

					dse.drawNCr(dse.getNorthPole());

					if (longitudeLength == longitude) {
						arrayIndex += 3 * lastLength;
					} else {
						arrayIndex += 3 * (lastLength - 1);
					}

				}

				// south pole
				if (drawBottom) {

					dse.drawNCrm(dse.getNorthPole());

					if (longitudeLength == longitude) {
						arrayIndex += 3 * lastLength;
					} else {
						arrayIndex += 3 * (lastLength - 1);
					}

				}
			}
		}

		debug("==== arrayIndex (1) = " + arrayIndex);

	}

	private void setIndices(int longitude, int longitudeLength, DrawEllipticSurface dse) {


		// ///////////////
		// set indices
		arrayI = manager.getCurrentGeometryIndices(arrayIndex);


		arrayIndex = 0;

		short lastStartIndex = 0;
		short lastLength = (short) longitudeLength;
		short currentStartIndex = lastStartIndex;
		short currentLength = (short) longitudeLength;

		// both = 1 if only drawing up or down, both = 2 if drawing both
		boolean drawTop = true;
		boolean drawBottom = true;
		boolean lastDrawTop = true;
		boolean lastDrawBottom = true;
		short lastBoth = 1;
		short both = 2;
		int vi = latitudeMin + 1;
		if (dse.drawEquator()) {
			both = 1; // we use the same vertices
		} else {
			vi++; // we start after equator
			if (latitudeMaxTop > 0) {
				drawTop = false;
				both = 1;
			} else if (latitudeMaxBottom > 0) {
				drawBottom = false;
				both = 1;
			}
		}
		int nextJump = dse.initNextJump(latitude, longitude);
		debug("latitude : " + latitude + " , latitude-nextJump : "
				+ (latitude - nextJump));
		int next = 0;

		while (next < latitudeMax) {

			next = Math.min(latitudeMax, latitude - nextJump);
			debug("latitude : " + latitude + " , latitudeMin : "
					+ latitudeMin + " , next : " + next + " , latitudeMax : "
					+ latitudeMax);
			while (next < latitudeMin + 2) {
				nextJump = dse.updateNextJump(nextJump, latitude);
				next = Math.min(latitudeMax, latitude - nextJump);
				debug(">> next : " + next);
			}

			// until next jump
			while (vi < next) {

				lastDrawTop = drawTop;
				lastDrawBottom = drawBottom;
				drawTop = dse.drawTop(vi);
				drawBottom = dse.drawBottom(vi);

				lastBoth = both;
				both = 0;
				if (drawTop) {// top vertices
					both++;
				}
				if (drawBottom) {// bottom vertices
					both++;
				}

				debug("vi : " + vi + " -- both : " + both);

				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength * lastBoth;

				if (lastDrawTop && drawTop) {// top triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- lastBoth; lastIndex += lastBoth) {

						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;

						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put((short) (currentIndex + both));
						arrayIndex++;

						currentIndex += both;
					}

					if (longitudeLength == longitude) {
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;

						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
					}

				}

				// shift to draw also bottom
				if (lastBoth == 2) {
					lastStartIndex += 1;
				}
				if (both == 2) {
					currentStartIndex += 1;
				}

				if (lastDrawBottom && drawBottom) {// bottom triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- both; lastIndex += lastBoth) {
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;

						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put((short) (currentIndex + both));
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;

						currentIndex += both;
					}

					if (longitudeLength == longitude) {
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;

						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;
					}
				}

				// shift back
				if (lastBoth == 2) {
					lastStartIndex -= 1;
				}
				if (both == 2) {
					currentStartIndex -= 1;
				}
				vi++;
			}

			// jump
			if (next < latitudeMax) {

				lastBoth = both;

				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength * lastBoth;
				currentLength /= 2;

				if (lastDrawTop && drawTop) {// top triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- 2 * lastBoth; lastIndex += 2 * lastBoth) {

						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put((short) (lastIndex + 2 * lastBoth));
						arrayIndex++;
						arrayI.put((short) (currentIndex + both));
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put((short) (currentIndex + both));
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;

						currentIndex += both;

					}

					if (longitudeLength == longitude) {
						// close the parallel
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
					}

					// shift for maybe draw bottom
					lastStartIndex += 1;
					currentStartIndex += 1;

				}

				if (lastDrawBottom && drawBottom) {// bottom triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- 2 * lastBoth; lastIndex += 2 * lastBoth) {
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put((short) (currentIndex + both));
						arrayIndex++;
						arrayI.put((short) (lastIndex + 2 * lastBoth));
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put((short) (currentIndex + both));
						arrayIndex++;

						currentIndex += both;

					}

					if (longitudeLength == longitude) {
						// close the parallel
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;

						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
					}

				}

				if (drawTop) {
					// shift back
					lastStartIndex -= 1;
					currentStartIndex -= 1;
				}

				vi++;

				nextJump = dse.updateNextJump(nextJump, latitude);
			}

		}

		lastBoth = both;

		lastStartIndex = currentStartIndex;
		lastLength = currentLength;
		currentStartIndex += lastLength * lastBoth;

		if (dse.drawPoles()) {
			// north pole
			if (latitudeMax == latitude) {

				if (drawTop) {


					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- lastBoth; lastIndex += lastBoth) {
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
					}

					if (longitudeLength == longitude) {
						// close the parallel
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
					}

					// shift for maybe south pole
					lastStartIndex += 1;
					currentStartIndex += 1;
				}

				// south pole
				if (drawBottom) {


					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- lastBoth; lastIndex += lastBoth) {
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
						arrayI.put((short) (lastIndex + lastBoth));
						arrayIndex++;
					}

					if (longitudeLength == longitude) {
						// close the parallel
						arrayI.put(lastIndex);
						arrayIndex++;
						arrayI.put(currentStartIndex);
						arrayIndex++;
						arrayI.put(lastStartIndex);
						arrayIndex++;
					}

				}
			}
		}

		debug("==== arrayIndex (2) = " + arrayIndex);

		arrayI.rewind();

		manager.endGeometry(arrayIndex,
				TypeElement.SURFACE);

	}

	private DrawEllipsoid drawEllipsoid;

	@Override
	public void drawEllipsoid(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double r2, int longitude) {

		startGeometry();

		// set texture to (0,0)
		manager.setDummyTexture();

		double r = Math.max(r0, Math.max(r1, r2));
		setLatitudeMinMaxForEllipsoid(center, r, longitude, manager.getView3D()
				.getFrustumRadius());

		if (drawEllipsoid == null) {
			drawEllipsoid = new DrawEllipsoid();
		}
		drawEllipsoid.set(this, center, ev0, ev1, ev2, r0, r1, r2);

		drawNV(drawEllipsoid, longitude, 0, longitude);

		setIndices(longitude, longitude, drawEllipsoid);
	}

	private DrawHyperboloidOneSheet drawHyperboloidOneSheet;

	@Override
	public void drawHyperboloidOneSheet(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double r2, int longitude,
			double min, double max, boolean fading) {

		startGeometry();

		if (!fading) {
			manager.setDummyTexture();
		}


		if (drawHyperboloidOneSheet == null) {
			drawHyperboloidOneSheet = new DrawHyperboloidOneSheet();
		}
		drawHyperboloidOneSheet.set(this, center, ev0, ev1, ev2, r0, r1, r2,
				fading);

		latitude = 32; // 32 seems to be ok in any case

		setLatitudeMinMaxForHyperboloid(min, max, drawHyperboloidOneSheet);

		drawNV(drawHyperboloidOneSheet, longitude, 0, longitude);

		setIndices(longitude, longitude, drawHyperboloidOneSheet);
	}

	private DrawHyperboloidTwoSheets drawHyperboloidTwoSheets;

	@Override
	public void drawHyperboloidTwoSheets(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double r2, int longitude,
			double min, double max, boolean fading) {

		startGeometry();

		if (!fading) {
			manager.setDummyTexture();
		}

		if (drawHyperboloidTwoSheets == null) {
			drawHyperboloidTwoSheets = new DrawHyperboloidTwoSheets();
		}
		drawHyperboloidTwoSheets.set(this, center, ev0, ev1, ev2, r0, r1, r2,
				fading);

		latitude = 16; // 16 seems to be ok in any case

		setLatitudeMinMaxForHyperboloid(min, max, drawHyperboloidTwoSheets);

		drawNV(drawHyperboloidTwoSheets, longitude, 0, longitude);

		setIndices(longitude, longitude, drawHyperboloidTwoSheets);
	}

	private DrawParaboloid drawParaboloid;

	@Override
	public void drawParaboloid(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, int longitude, double min,
			double max, boolean fading) {

		startGeometry();

		if (!fading) {
			manager.setDummyTexture();
		}

		if (drawParaboloid == null) {
			drawParaboloid = new DrawParaboloid();
		}
		drawParaboloid.set(this, center, ev0, ev1, ev2, r0, r1, 1, fading);

		latitude = 32; // 32 seems to be ok in any case
		setLatitudeMinMaxForParaboloid(min, max, drawParaboloid);

		drawNV(drawParaboloid, longitude, 0, longitude);

		setIndices(longitude, longitude, drawParaboloid);
	}

	private double maxFadingStartTop, maxFadingEndTop, middleFading,
			maxFadingStartBottom, maxFadingEndBottom;

	@Override
	public void drawParabolicCylinder(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r, double min, double max,
			double lineMin, double lineMax, boolean fading) {

		center1.setAdd(center, tmpCoords.setMul(ev1, lineMin));
		center2.setAdd(center, tmpCoords.setMul(ev1, lineMax));
		
		if (n == null) {
			n = new Coords(4);
		}

		double lineFading = 0;
		if (fading) {
			if (Kernel.isZero(min)) {
				maxFadingEndTop = max * max;
				maxFadingStartTop = maxFadingEndTop * 0.9;
				middleFading = -1;
				maxFadingStartBottom = 0;
				maxFadingEndBottom = 0;
			} else {
				double min2 = min * min;
				double max2 = max * max;
				middleFading = (min2 + max2) / 2;
				maxFadingStartTop = max2 * 0.9 + min2 * 0.1;
				maxFadingEndTop = max2;
				maxFadingStartBottom = max2 * 0.1 + min2 * 0.9;
				maxFadingEndBottom = min2;
			}

			lineFading = -8; // TODO preferences
		}

		latitude = 16;
		
		// set geometry
		startGeometry();

		if (min == 0) {
			n.setMul(ev0, -1);

			if (fading) {
				manager.texture(1, 0);
			}
			drawNV(n, center1);

			if (fading) { // we need an intermediate vertex for texture
				manager.texture(lineFading, 0);
				drawNV(n, center);
			}

			if (fading) {
				manager.texture(1, 0);
			}
			drawNV(n, center2);
		}

		for (int vi = 1; vi <= latitude; vi++) {
			double v = min + ((max - min) * vi) / latitude;
			double z = v * v;
			double zFading = 0;
			if (fading) {
				if (z > middleFading) {
					zFading = (z - maxFadingStartTop)
							/ (maxFadingEndTop - maxFadingStartTop);
				} else {
					zFading = (z - maxFadingStartBottom)
							/ (maxFadingEndBottom - maxFadingStartBottom);
				}
			}

			tmpCoords2.setMul(ev2, r * v);
			tmpCoords3.setMul(ev0, z);

			// v > 0
			tmpCoords.setMul(ev0, -r);
			n.setMul(ev2, 2 * v);
			n.setAdd(tmpCoords, n);
			n.normalize();

			m.setAdd(center1, tmpCoords2);
			m.setAdd(m, tmpCoords3);
			if (fading) {
				manager.texture(1, zFading);
			}
			drawNV(n, m);

			if (fading) {
				m.setAdd(center, tmpCoords2);
				m.setAdd(m, tmpCoords3);
				manager.texture(lineFading, zFading);
				drawNV(n, m);
			}

			m.setAdd(center2, tmpCoords2);
			m.setAdd(m, tmpCoords3);
			if (fading) {
				manager.texture(1, zFading);
			}
			drawNV(n, m);

			// v < 0
			n.setMul(ev2, -2 * v);
			n.setAdd(tmpCoords, n);
			n.normalize();

			m.setSub(center1, tmpCoords2);
			m.setAdd(m, tmpCoords3);
			if (fading) {
				manager.texture(1, zFading);
			}
			drawNV(n, m);

			if (fading) {
				m.setSub(center, tmpCoords2);
				m.setAdd(m, tmpCoords3);
				manager.texture(lineFading, zFading);
				drawNV(n, m);
			}

			m.setSub(center2, tmpCoords2);
			m.setAdd(m, tmpCoords3);
			if (fading) {
				manager.texture(1, zFading);
			}
			drawNV(n, m);

		}


		// set indices
		if (min == 0) {
			arrayIndex = latitude * 12;
		} else {
			arrayIndex = (latitude - 1) * 12;
		}
		if (fading) {
			arrayIndex *= 2;
		}
		arrayI = manager.getCurrentGeometryIndices(arrayIndex);

		short index = 0;

		if (fading) {
			if (min == 0) {
				arrayI.put(index);
				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 1));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 4));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 2));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 5));


				arrayI.put(index);
				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 6));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 6));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 7));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 8));
				arrayI.put((short) (index + 7));

				index += 3;
			}
			for (int vi = 1; vi < latitude; vi++) {
				arrayI.put(index);
				arrayI.put((short) (index + 6));
				arrayI.put((short) (index + 1));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 6));
				arrayI.put((short) (index + 7));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 2));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 8));

				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 9));

				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 10));
				arrayI.put((short) (index + 9));

				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 5));
				arrayI.put((short) (index + 10));

				arrayI.put((short) (index + 5));
				arrayI.put((short) (index + 11));
				arrayI.put((short) (index + 10));

				index += 6;

			}
		} else {
			if (min == 0) {
				arrayI.put(index);
				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 1));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 3));

				arrayI.put(index);
				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 4));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 5));
				arrayI.put((short) (index + 4));

				index += 2;
			}
			for (int vi = 1; vi < latitude; vi++) {
				arrayI.put(index);
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 1));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 5));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 6));

				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 6));

				index += 4;

			}
		}

		arrayI.rewind();


		manager.endGeometry(arrayIndex, TypeElement.SURFACE);

	}

	@Override
	public void drawHyperbolicCylinder(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r1, double r2, double min0, double max,
			double lineMin,
			double lineMax, boolean fading) {

		double min;
		if (min0 < 0) {
			min = 0;
		} else {
			min = min0;
		}

		center1.setAdd(center, tmpCoords.setMul(ev2, lineMin));
		center2.setAdd(center, tmpCoords.setMul(ev2, lineMax));

		if (n == null) {
			n = new Coords(4);
		}

		double lineFading = 0;
		if (fading) {
			if (min > 0) {
				double shMin = Math.sinh(min0);
				double shMax = Math.sinh(max);
				middleFading = (shMax + shMin) / 2;
				maxFadingStartTop = shMax * 0.9 + shMin * 0.1;
				maxFadingEndTop = shMax;
				maxFadingStartBottom = shMax * 0.1 + shMin * 0.9;
				maxFadingEndBottom = shMin;
			} else {
				double shMax = Math.sinh(max);
				middleFading = -1;
				maxFadingStartTop = shMax * 0.9;
				maxFadingEndTop = shMax;
				maxFadingStartBottom = -1;
				maxFadingEndBottom = -1;
			}

			lineFading = -8; // TODO preferences
		}

		latitude = 16;

		// set geometry
		startGeometry();

		if (!fading) {
			manager.setDummyTexture();
		}

		if (min <= 0) {
			n.setMul(ev0, -1);

			tmpCoords2.setMul(ev0, r1);

			if (fading) {
				manager.texture(1, 0);
			}
			m.setAdd(center1, tmpCoords2);
			drawNV(n, m);

			if (fading) { // we need an intermediate vertex for texture
				manager.texture(lineFading, 0);
				m.setAdd(center, tmpCoords2);
				drawNV(n, m);
			}

			if (fading) {
				manager.texture(1, 0);
			}
			m.setAdd(center2, tmpCoords2);
			drawNV(n, m);
		}

		for (int vi = 1; vi <= latitude; vi++) {
			double v = min + ((max - min) * vi) / latitude;
			double c = Math.cosh(v);
			double s = Math.sinh(v);
			double sFading = 0;
			if (fading) {
				if (s > middleFading) {
					sFading = (s - maxFadingStartTop)
							/ (maxFadingEndTop - maxFadingStartTop);
				} else {
					sFading = (s - maxFadingStartBottom)
							/ (maxFadingEndBottom - maxFadingStartBottom);
				}
			}

			tmpCoords2.setMul(ev0, r1 * c);
			tmpCoords3.setMul(ev1, r2 * s);

			// > 0
			tmpCoords.setMul(ev0, -r2 * c);
			n.setMul(ev1, r1 * s);
			n.setAdd(tmpCoords, n);
			n.normalize();

			m.setAdd(center1, tmpCoords2);
			m.setAdd(m, tmpCoords3);
			if (fading) {
				manager.texture(1, sFading);
			}
			drawNV(n, m);

			if (fading) {
				m.setAdd(center, tmpCoords2);
				m.setAdd(m, tmpCoords3);
				manager.texture(lineFading, sFading);
				drawNV(n, m);
			}

			m.setAdd(center2, tmpCoords2);
			m.setAdd(m, tmpCoords3);
			if (fading) {
				manager.texture(1, sFading);
			}
			drawNV(n, m);

			// < 0
			n.setMul(ev1, -r1 * s);
			n.setAdd(tmpCoords, n);
			n.normalize();

			m.setAdd(center1, tmpCoords2);
			m.setSub(m, tmpCoords3);
			if (fading) {
				manager.texture(1, sFading);
			}
			drawNV(n, m);

			if (fading) {
				m.setAdd(center, tmpCoords2);
				m.setSub(m, tmpCoords3);
				manager.texture(lineFading, sFading);
				drawNV(n, m);
			}

			m.setAdd(center2, tmpCoords2);
			m.setSub(m, tmpCoords3);
			if (fading) {
				manager.texture(1, sFading);
			}
			drawNV(n, m);

		}

		// set indices
		if (min <= 0) {
			arrayIndex = latitude * 12;
		} else {
			arrayIndex = (latitude - 1) * 12;
		}
		if (fading) {
			arrayIndex *= 2;
		}
		arrayI = manager.getCurrentGeometryIndices(arrayIndex);

		short index = 0;

		if (fading) {
			if (min <= 0) {
				arrayI.put(index);
				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 3));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 3));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 4));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 5));
				arrayI.put((short) (index + 4));

				arrayI.put(index);
				arrayI.put((short) (index + 6));
				arrayI.put((short) (index + 1));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 6));
				arrayI.put((short) (index + 7));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 2));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 8));

				index += 3;
			}
			for (int vi = 1; vi < latitude; vi++) {
				arrayI.put(index);
				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 6));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 7));
				arrayI.put((short) (index + 6));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 7));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 8));
				arrayI.put((short) (index + 7));

				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 9));
				arrayI.put((short) (index + 4));

				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 9));
				arrayI.put((short) (index + 10));

				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 10));
				arrayI.put((short) (index + 5));

				arrayI.put((short) (index + 5));
				arrayI.put((short) (index + 10));
				arrayI.put((short) (index + 11));

				index += 6;

			}
		} else {
			if (min <= 0) {
				arrayI.put(index);
				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 2));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 2));

				arrayI.put(index);
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 1));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 4));
				arrayI.put((short) (index + 5));

				index += 2;
			}
			for (int vi = 1; vi < latitude; vi++) {
				arrayI.put(index);
				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 4));

				arrayI.put((short) (index + 1));
				arrayI.put((short) (index + 5));
				arrayI.put((short) (index + 4));

				arrayI.put((short) (index + 2));
				arrayI.put((short) (index + 6));
				arrayI.put((short) (index + 3));

				arrayI.put((short) (index + 3));
				arrayI.put((short) (index + 6));
				arrayI.put((short) (index + 7));

				index += 4;

			}
		}

		arrayI.rewind();

		manager.endGeometry(arrayIndex, TypeElement.SURFACE);

	}

	@Override
	public void drawHyperbolicParaboloid(Coords center, Coords ev0, Coords ev1,
			Coords ev2, double r0, double r1, double min0, double max0,
			double min1, double max1, boolean fading) {

		latitude = 64;

		startGeometry();

		if (!fading) {
			manager.setDummyTexture();
		}

		if (n == null) {
			n = new Coords(4);
		}

		for (int ui = 0; ui <= latitude; ui++) {
			double u = min0 + ui * (max0 - min0) / latitude;
			tmpCoords.setMul(ev0, u);
			tmpCoords.setAdd(center, tmpCoords);
			tmpCoords3.setMul(ev0, 2 * r0 * u);
			double uFading = 0;
			if (fading) {
				if (ui < latitude / 2) {
					uFading = 1 - ui * 20.0 / latitude;
				} else {
					uFading = 1 - (latitude - ui) * 20.0 / latitude;
				}
			}
			for (int vi = 0; vi <= latitude; vi++) {
				double v = min1 + vi * (max1 - min1) / latitude;
				tmpCoords2.setMul(ev1, v);
				m.setAdd(tmpCoords, tmpCoords2);
				tmpCoords2.setMul(ev2, r0 * u * u + r1 * v * v);
				m.setAdd(m, tmpCoords2);

				n.setMul(ev1, 2 * r0 * u);
				n.setAdd(n, tmpCoords3);
				n.setAdd(n, Coords.VZm);
				n.normalize();

				if (fading) {
					double vFading;
					if (vi < latitude / 2) {
						vFading = 1 - vi * 20.0 / latitude;
					} else {
						vFading = 1 - (latitude - vi) * 20.0 / latitude;
					}
					manager.texture(uFading, vFading);
				}

				drawNV(n, m);
			}

		}

		// set indices
		arrayIndex = latitude * latitude * 6;
		arrayI = manager.getCurrentGeometryIndices(arrayIndex);

		short uIndex = 0;
		for (int ui = 0; ui < latitude; ui++) {
			for (int vi = 0; vi < latitude; vi++) {
				arrayI.put((short) (uIndex + vi));
				arrayI.put((short) (uIndex + vi + 1));
				arrayI.put((short) (uIndex + latitude + 1 + vi));

				arrayI.put((short) (uIndex + latitude + 1 + vi));
				arrayI.put((short) (uIndex + vi + 1));
				arrayI.put((short) (uIndex + latitude + 1 + vi + 1));
			}
			uIndex += latitude + 1;

		}

		arrayI.rewind();

		manager.endGeometry(arrayIndex, TypeElement.SURFACE);

	}

	private int arrayIndex = 0;
	private GLBufferIndices arrayI;

	@Override
	public void startTriangles(int size) {

		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.setDummyTexture();

		arrayIndex = 0;

		arrayI = manager.getCurrentGeometryIndices(size);

	}

	@Override
	public void vertexDirect(Coords3 p) {
		manager.vertexToScale(p.getXf(), p.getYf(), p.getZf());
	}

	@Override
	public void normalDirect(Coords3 n) {
		manager.normal(n.getXf(), n.getYf(), n.getZf());
	}

	@Override
	public void endGeometryDirect() {
		arrayI.rewind();
		manager.endGeometry(arrayIndex, TypeElement.SURFACE);
	}

	/**
	 * @param id
	 *            vertex normal id
	 */
	public void drawIndex(int id) {
		arrayI.put((short) id);
		arrayIndex++;
	}
}
