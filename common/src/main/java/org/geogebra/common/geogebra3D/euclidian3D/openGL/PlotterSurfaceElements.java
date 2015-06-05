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

	public PlotterSurfaceElements(Manager manager) {
		super(manager);
	}

	@Override
	public void drawSphere(Coords center, double radius, int longitude,
			double longitudeStart, int longitudeLength) {

		manager.startGeometry(Manager.Type.TRIANGLES);

		// set texture to (0,0)
		manager.setDummyTexture();

		int latitude = longitude / 4;

		// check which parts are visible (latitudes)
		Coords o = manager.getView3D().getCenter();
		double frustumRadius = manager.getView3D().getFrustumRadius();

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

		// debug(latitudeMaxBottom+","+latitudeMaxTop);

		int latitudeMax = Math.max(latitudeMaxTop, latitudeMaxBottom);
		if (latitudeMax > latitude) {
			latitudeMax = latitude;
		}

		int latitudeMin = 0; // start on equator
		if (latitudeMaxTop < 0) { // start below equator
			latitudeMin = -latitudeMaxTop;
		} else if (latitudeMaxBottom < 0) { // start above equator
			latitudeMin = -latitudeMaxBottom;
		}

		// check which parts are visible (longitudes)

		// start drawing

		Coords n = new Coords(4);

		double[] cosSinV = new double[2];

		debug("longitude = " + longitude + " , longitudeLength = "
				+ longitudeLength);

		short lastStartIndex, lastLength, currentStartIndex, currentLength, lastBoth, both;
		boolean drawTop, drawBottom;
		int vi, nextJump, next;
		short shift;

		// ///////////////
		// draw vertices

		// first latitude
		cosSin(latitudeMin, latitude, cosSinV);
		for (int ui = 0; ui < longitudeLength; ui++) {
			sphericalCoords(ui, longitude, longitudeStart, cosSinV, n);
			drawNCr(n, center, radius);
		}
		
		arrayIndex = 0;

		lastStartIndex = 0;
		lastLength = (short) longitudeLength;
		currentStartIndex = lastStartIndex;
		currentLength = (short) longitudeLength;
		
		// both = 1 if only drawing up or down, both = 2 if drawing both
		drawTop = true;
		drawBottom = true;
		lastBoth = 1;
		both = 1;

		vi = latitudeMin + 1;
		nextJump = (int) (latitude / Math.PI);
		debug("latitude : " + latitude + " , latitude-nextJump : "
				+ (latitude - nextJump));
		next = 0;
		shift = 1;

		while (next < latitudeMax) {
			
			next = Math.min(latitudeMax, latitude - nextJump);
			debug("latitude : " + latitude + " , latitudeMin : "
					+ latitudeMin + " , next : " + next + " , latitudeMax : "
					+ latitudeMax);

			// until next jump
			while (vi < next) {

				drawTop = vi < latitudeMaxTop;
				drawBottom = vi < latitudeMaxBottom;

				cosSin(vi, latitude, cosSinV);
				for (int ui = 0; ui < longitudeLength; ui += shift) {
					sphericalCoords(ui, longitude, longitudeStart, cosSinV, n);
					if (drawTop) {// top vertices
						drawNCr(n, center, radius);
					}
					if (drawBottom) {// bottom vertices
						drawNCrm(n, center, radius);
					}
				}


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
			if (next > latitudeMin && next < latitudeMax) {

				shift *= 2;
				cosSin(vi, latitude, cosSinV);
				for (int ui = 0; ui < longitudeLength; ui += shift) {
					sphericalCoords(ui, longitude, longitudeStart, cosSinV, n);
					if (drawTop) {// top vertices
						drawNCr(n, center, radius);
					}
					if (drawBottom) {// bottom vertices
						drawNCrm(n, center, radius);
					}

				}

				lastBoth = both;

				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength * lastBoth;
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

				nextJump /= 2;
			}


		}

		lastBoth = both;

		lastStartIndex = currentStartIndex;
		lastLength = currentLength;
		currentStartIndex += lastLength * lastBoth;


		// north pole
		if (latitudeMax == latitude) {

			if (drawTop) {

				drawNCr(Coords.VZ, center, radius);
				
				if (longitudeLength == longitude) {
					arrayIndex += 3 * lastLength;
				} else {
					arrayIndex += 3 * (lastLength - 1);
				}

			}

			// south pole
			if (drawBottom) {

				drawNCrm(Coords.VZ, center, radius);

				if (longitudeLength == longitude) {
					arrayIndex += 3 * lastLength;
				} else {
					arrayIndex += 3 * (lastLength - 1);
				}

			}
		}

		debug("==== arrayIndex (1) = " + arrayIndex);


		// ///////////////
		// set indices
		arrayI = ((ManagerShadersElements) manager)
				.getCurrentGeometryIndices(arrayIndex);


		arrayIndex = 0;

		lastStartIndex = 0;
		lastLength = (short) longitudeLength;
		currentStartIndex = lastStartIndex;
		currentLength = (short) longitudeLength;

		// both = 1 if only drawing up or down, both = 2 if drawing both
		drawTop = true;
		drawBottom = true;
		lastBoth = 1;
		both = 1;

		vi = latitudeMin + 1;
		nextJump = (int) (latitude / Math.PI);
		debug("latitude : " + latitude + " , latitude-nextJump : "
				+ (latitude - nextJump));
		next = 0;
		shift = 1;

		while (next < latitudeMax) {

			next = Math.min(latitudeMax, latitude - nextJump);
			debug("latitude : " + latitude + " , latitudeMin : "
					+ latitudeMin + " , next : " + next + " , latitudeMax : "
					+ latitudeMax);

			// until next jump
			while (vi < next) {

				drawTop = vi < latitudeMaxTop;
				drawBottom = vi < latitudeMaxBottom;


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

				if (drawTop) {// top triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- lastBoth; lastIndex += lastBoth) {

						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;

						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (currentIndex + both);
						arrayIndex++;

						currentIndex += both;
					}

					if (longitudeLength == longitude) {
						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = lastStartIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;

						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = lastStartIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentStartIndex;
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

				if (drawBottom) {// bottom triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- both; lastIndex += lastBoth) {
						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;

						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (currentIndex + both);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;

						currentIndex += both;
					}

					if (longitudeLength == longitude) {
						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = lastStartIndex;
						arrayIndex++;

						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentStartIndex;
						arrayIndex++;
						arrayI[arrayIndex] = lastStartIndex;
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
			if (next > latitudeMin && next < latitudeMax) {

				shift *= 2;

				lastBoth = both;

				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength * lastBoth;
				currentLength /= 2;

				if (drawTop) {// top triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- 2 * lastBoth; lastIndex += 2 * lastBoth) {

						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + 2 * lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (currentIndex + both);
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (currentIndex + both);
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;

						currentIndex += both;

					}

					if (longitudeLength == longitude) {
						// close the parallel
						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = lastStartIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentStartIndex;
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentStartIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
					}

					// shift for maybe draw bottom
					lastStartIndex += 1;
					currentStartIndex += 1;

				}

				if (drawBottom) {// bottom triangles
					short currentIndex = currentStartIndex;
					short lastIndex;
					for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
							- 2 * lastBoth; lastIndex += 2 * lastBoth) {
						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (currentIndex + both);
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + 2 * lastBoth);
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (currentIndex + both);
						arrayIndex++;

						currentIndex += both;

					}

					if (longitudeLength == longitude) {
						// close the parallel
						arrayI[arrayIndex] = lastIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentStartIndex;
						arrayIndex++;
						arrayI[arrayIndex] = lastStartIndex;
						arrayIndex++;

						arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
						arrayIndex++;
						arrayI[arrayIndex] = currentIndex;
						arrayIndex++;
						arrayI[arrayIndex] = currentStartIndex;
						arrayIndex++;
					}

				}

				if (drawTop) {
					// shift back
					lastStartIndex -= 1;
					currentStartIndex -= 1;
				}

				vi++;

				nextJump /= 2;
			}

		}

		lastBoth = both;

		lastStartIndex = currentStartIndex;
		lastLength = currentLength;
		currentStartIndex += lastLength * lastBoth;

		// north pole
		if (latitudeMax == latitude) {

			if (drawTop) {


				short lastIndex;
				for (lastIndex = lastStartIndex; lastIndex < currentStartIndex
						- lastBoth; lastIndex += lastBoth) {
					arrayI[arrayIndex] = lastIndex;
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
					arrayIndex++;
					arrayI[arrayIndex] = currentStartIndex;
					arrayIndex++;
				}

				if (longitudeLength == longitude) {
					// close the parallel
					arrayI[arrayIndex] = lastIndex;
					arrayIndex++;
					arrayI[arrayIndex] = lastStartIndex;
					arrayIndex++;
					arrayI[arrayIndex] = currentStartIndex;
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
					arrayI[arrayIndex] = lastIndex;
					arrayIndex++;
					arrayI[arrayIndex] = currentStartIndex;
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastIndex + lastBoth);
					arrayIndex++;
				}

				if (longitudeLength == longitude) {
					// close the parallel
					arrayI[arrayIndex] = lastIndex;
					arrayIndex++;
					arrayI[arrayIndex] = currentStartIndex;
					arrayIndex++;
					arrayI[arrayIndex] = lastStartIndex;
					arrayIndex++;
				}

			}
		}

		debug("==== arrayIndex (2) = " + arrayIndex);

		((ManagerShadersElements) manager).endGeometry(arrayIndex,
				TypeElement.SURFACE);

	}

	private int arrayIndex = 0;
	private short[] arrayI;

	@Override
	public void startTriangles(int size) {

		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.setDummyTexture();

		arrayIndex = 0;

		arrayI = ((ManagerShadersElements) manager)
				.getCurrentGeometryIndices(size);

	}

	@Override
	public void vertexDirect(Coords3 p) {
		manager.vertex(p.getXf(), p.getYf(), p.getZf());
	}

	@Override
	public void normalDirect(Coords3 n) {
		manager.normal(n.getXf(), n.getYf(), n.getZf());
	}

	@Override
	public void endGeometryDirect() {
		((ManagerShadersElements) manager).endGeometry(arrayIndex,
				TypeElement.SURFACE);
	}

	/**
	 * @param id
	 *            vertex normal id
	 */
	public void drawIndex(int id) {
		arrayI[arrayIndex] = (short) id;
		arrayIndex++;
	}
}
