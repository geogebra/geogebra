package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;

public class PlotterSurfaceElements extends PlotterSurface {

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

		// App.debug(latitudeMaxBottom+","+latitudeMaxTop);

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
		short[] arrayI = ((ManagerShadersBindBuffers) manager)
				.getCurrentGeometryIndices((latitudeMax - latitudeMin)
						* longitudeLength * 60);

		Coords n = new Coords(4);

		double[] cosSinV = new double[2];

		App.debug("longitude = " + longitude + " , longitudeLength = "
				+ longitudeLength);

		// first latitude
		cosSin(latitudeMin, latitude, cosSinV);
		for (int ui = 0; ui < longitudeLength; ui++) {
			sphericalCoords(ui, longitude, longitudeStart, cosSinV, n);
			drawNCr(n, center, radius);
		}
		
		int arrayIndex = 0;

		
		short lastStartIndex = 0;
		short lastLength = (short) longitudeLength;
		short currentStartIndex = lastStartIndex;
		short currentLength = (short) longitudeLength;
		
		// both = 1 if only drawing up or down, both = 2 if drawing both
		boolean drawTop = true, drawBottom = true;
		short lastBoth = 1, both = 1;

		int vi = latitudeMin + 1;
		int nextJump = (int) (latitude / Math.PI);
		App.debug("latitude : " + latitude + " , latitude-nextJump : "
				+ (latitude - nextJump));
		int next = 0;
		short shift = 1;

		while (next < latitudeMax) {
			
			next = Math.min(latitudeMax, latitude - nextJump);
			App.debug("latitude : " + latitude + " , latitudeMin : "
					+ latitudeMin + " , next : " + next
					+ " , latitudeMax : " + latitudeMax);

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

				App.debug("vi : " + vi + " -- both : " + both);

				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength * lastBoth;


				if (drawTop) {// top triangles
					short currentIndex = currentStartIndex;
					for (short lastIndex = lastStartIndex; lastIndex < currentStartIndex
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

					// if (longitudeLength == longitude) {
					// // close the parallel
					// arrayI[arrayIndex] = (short) (lastStartIndex
					// + lastLength - 1);
					// arrayIndex++;
					// arrayI[arrayIndex] = lastStartIndex;
					// arrayIndex++;
					// arrayI[arrayIndex] = (short) (currentStartIndex
					// + currentLength - 1);
					// arrayIndex++;
					// }

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
					for (short lastIndex = lastStartIndex; lastIndex < currentStartIndex
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

					// if (longitudeLength == longitude) {
					// // close the parallel
					// arrayI[arrayIndex] = (short) (lastStartIndex
					// + lastLength - 1);
					// arrayIndex++;
					// arrayI[arrayIndex] = lastStartIndex;
					// arrayIndex++;
					// arrayI[arrayIndex] = (short) (currentStartIndex
					// + currentLength - 1);
					// arrayIndex++;
					// }
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
					short currentIndex = currentStartIndex;
					for (short lastIndex = lastStartIndex; lastIndex < currentStartIndex
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

					// if (longitudeLength == longitude) {
					// // close the parallel
					// arrayI[arrayIndex] = (short) (lastStartIndex + 2 *
					// (currentLength - 1));
					// arrayIndex++;
					// arrayI[arrayIndex] = (short) (lastStartIndex + 2
					// * (currentLength - 1) + 1);
					// arrayIndex++;
					// arrayI[arrayIndex] = (short) (currentStartIndex +
					// (currentLength - 1));
					// arrayIndex++;
					//
					// arrayI[arrayIndex] = (short) (lastStartIndex + 2
					// * (currentLength - 1) + 1);
					// arrayIndex++;
					// arrayI[arrayIndex] = lastStartIndex;
					// arrayIndex++;
					// arrayI[arrayIndex] = currentStartIndex;
					// arrayIndex++;
					//
					// }

					// shift for maybe draw bottom
					lastStartIndex += 1;
					currentStartIndex += 1;

				}

				if (drawBottom) {// bottom triangles
					short currentIndex = currentStartIndex;
					for (short lastIndex = lastStartIndex; lastIndex < currentStartIndex
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

				drawNCr(Coords.VZ, center, radius);

				for (short ui = 0; ui < lastLength - 1; ui++) {
					arrayI[arrayIndex] = (short) (lastStartIndex + ui
							* lastBoth);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastStartIndex + (ui + 1)
							* lastBoth);
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

				drawNCrm(Coords.VZ, center, radius);

				for (short ui = 0; ui < lastLength - 1; ui++) {
					arrayI[arrayIndex] = (short) (lastStartIndex + ui
							* lastBoth);
					arrayIndex++;
					arrayI[arrayIndex] = currentStartIndex;
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastStartIndex + (ui + 1)
							* lastBoth);
					arrayIndex++;
				}

			}
		}



		((ManagerShadersBindBuffers) manager).endGeometry(arrayIndex,
				TypeElement.SURFACE);

	}
}
