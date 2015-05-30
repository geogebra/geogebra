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
						* longitudeLength * 6);

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
				cosSin(vi, latitude, cosSinV);
				for (int ui = 0; ui < longitudeLength; ui += shift) {
					sphericalCoords(ui, longitude, longitudeStart, cosSinV, n);
					drawNCr(n, center, radius);
				}

				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength;

				for (short ui = 0; ui < lastLength - 1; ui++) {
					arrayI[arrayIndex] = (short) (lastStartIndex + ui);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastStartIndex + ui + 1);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (currentStartIndex + ui);
					arrayIndex++;
				}

				if (longitudeLength == longitude) {
					// close the parallel
					arrayI[arrayIndex] = (short) (lastStartIndex + lastLength - 1);
					arrayIndex++;
					arrayI[arrayIndex] = lastStartIndex;
					arrayIndex++;
					arrayI[arrayIndex] = (short) (currentStartIndex
							+ currentLength - 1);
					arrayIndex++;
				}

				vi++;
			}

			// jump
			if (next > latitudeMin && next < latitudeMax) {

				shift *= 2;
				cosSin(vi, latitude, cosSinV);
				for (int ui = 0; ui < longitudeLength; ui += shift) {
					sphericalCoords(ui, longitude, longitudeStart, cosSinV, n);
					drawNCr(n, center, radius);
				}
				lastStartIndex = currentStartIndex;
				lastLength = currentLength;
				currentStartIndex += lastLength;
				currentLength /= 2;

				for (short ui = 0; ui < currentLength - 1; ui++) {
					arrayI[arrayIndex] = (short) (lastStartIndex + 2 * ui);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastStartIndex + 2 * ui + 1);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (currentStartIndex + ui);
					arrayIndex++;

					arrayI[arrayIndex] = (short) (lastStartIndex + 2 * ui + 1);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastStartIndex + 2 * (ui + 1));
					arrayIndex++;
					arrayI[arrayIndex] = (short) (currentStartIndex + ui + 1);
					arrayIndex++;
				}

				if (longitudeLength == longitude) {
					// close the parallel
					arrayI[arrayIndex] = (short) (lastStartIndex + 2 * (currentLength - 1));
					arrayIndex++;
					arrayI[arrayIndex] = (short) (lastStartIndex + 2
							* (currentLength - 1) + 1);
					arrayIndex++;
					arrayI[arrayIndex] = (short) (currentStartIndex + (currentLength - 1));
					arrayIndex++;

					arrayI[arrayIndex] = (short) (lastStartIndex + 2
							* (currentLength - 1) + 1);
					arrayIndex++;
					arrayI[arrayIndex] = lastStartIndex;
					arrayIndex++;
					arrayI[arrayIndex] = currentStartIndex;
					arrayIndex++;

				}

				vi++;

				nextJump /= 2;
			}


		}

		// pole
		if (latitudeMax == latitude) {

			drawNCr(Coords.VZ, center, radius);

			lastStartIndex = currentStartIndex;
			lastLength = currentLength;
			currentStartIndex += lastLength;

			for (short ui = 0; ui < lastLength; ui++) {
				arrayI[arrayIndex] = (short) (lastStartIndex + ui);
				arrayIndex++;
				arrayI[arrayIndex] = (short) (lastStartIndex + ((ui + 1) % lastLength));
				arrayIndex++;
				arrayI[arrayIndex] = currentStartIndex;
				arrayIndex++;
			}

		}



		((ManagerShadersBindBuffers) manager).endGeometry(arrayIndex,
				TypeElement.SURFACE);

	}
}
