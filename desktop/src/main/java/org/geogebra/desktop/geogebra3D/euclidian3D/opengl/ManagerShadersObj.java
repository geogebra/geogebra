package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.io.BufferedWriter;
import java.io.IOException;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * manager for shaders + obj files
 * 
 * @author mathieu
 *
 */
public class ManagerShadersObj extends ManagerShadersElementsGlobalBuffer {



	/**
	 * constructor
	 * 
	 * @param renderer
	 *            renderer
	 * @param view3D
	 *            3D view
	 */
	public ManagerShadersObj(Renderer renderer, EuclidianView3D view3D) {
		super(renderer, view3D);

	}

	private int objCurrentIndex;

	private BufferedWriter objBufferedWriter;

	/**
	 * start .obj file (set writer and vertex index)
	 * 
	 * @param writer
	 *            .obj file writer
	 */
	public void startObjFile(BufferedWriter writer) {
		objCurrentIndex = 1;
		objBufferedWriter = writer;
	}

	private Coords center = null;

	@Override
	public void drawInObjFormat(Drawable3D d,
			TypeFor3DPrinter type) {

		try {
			currentGeometriesSet = geometriesSetList.get(d.getGeometryIndex());
			if (type == TypeFor3DPrinter.POINT) {
				center = ((DrawPoint3D) d).getCenter();
			} else {
				center = null;
			}
			if (currentGeometriesSet != null) {
				for (Geometry g : currentGeometriesSet) {
					GeometryElementsGlobalBuffer geometry = (GeometryElementsGlobalBuffer) g;

					printToObjFile("\n##########################\n\no "
							+ d.getGeoElement().getLabelSimple() + "\n");


					// vertices
					GLBuffer fb = geometry.getVertices();
					for (int i = 0; i < geometry.getLength(); i++) {
						double x = fb.get();
						double y = fb.get();
						double z = fb.get();
						printVertexToObjFile(x, y, z);

					}
					fb.rewind();

					// faces
					GLBufferIndices bi = geometry.getCurrentBufferI();
					printToObjFile("\n");
					for (int i = 0; i < geometry.getIndicesLength() / 3; i++) {
						int v1 = objCurrentIndex + bi.get();
						int v2 = objCurrentIndex + bi.get();
						int v3 = objCurrentIndex + bi.get();
						printFaceToObjFile(v1, v2, v3);
					}
					bi.rewind();

					if (type == TypeFor3DPrinter.CURVE) {
						// face for start
						for (int i = 1; i < 7; i++) {
							printFaceToObjFile(objCurrentIndex, objCurrentIndex
									+ i, objCurrentIndex + i + 1);
						}
					}

					// update index
					objCurrentIndex += geometry.getLength();

					if (type == TypeFor3DPrinter.CURVE) {
						// face for end
						for (int i = 2; i < 8; i++) {
							printFaceToObjFile(objCurrentIndex - 1,
									objCurrentIndex - i, objCurrentIndex - i
											- 1);
						}
					}

					printToObjFile("\n##########################\n\n");


				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printVertexToObjFile(double x, double y, double z)
			throws IOException {
		if (center != null) {
			double r = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR
					/ getRenderer().getView().getScale();
			x = center.getX() + x * r;
			y = center.getY() + y * r;
			z = center.getZ() + z * r;
		}
		printToObjFile("\nv " + x + " " + y + " " + z);
	}

	private void printFaceToObjFile(int v1, int v2, int v3) throws IOException {
		printToObjFile("\nf " + v1 + " " + v2 + " " + v3);
	}

	private void printToObjFile(String s) throws IOException {
		// System.out.print(s);
		objBufferedWriter.write(s);
	}

}
