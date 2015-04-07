package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.io.BufferedWriter;
import java.io.IOException;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * manager for shaders + obj files
 * 
 * @author mathieu
 *
 */
public class ManagerShadersObj extends ManagerShaders {

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
		objCurrentIndex = 0;
		objBufferedWriter = writer;
	}

	@Override
	public void drawInObjFormat(GeoElement geo, int index) {

		try {
			currentGeometriesSet = geometriesSetList.get(index);
			if (currentGeometriesSet != null) {
				for (int i = 0; i < currentGeometriesSet.getGeometriesLength(); i++) {
					Geometry geometry = currentGeometriesSet.get(i);

					printToObjFile("\n##########################\n\no "
							+ geo.getLabelSimple() + "\n");

					switch (geometry.getType()) {
					/*
					 * case QUADS:
					 * 
					 * //vertices GLBuffer fb = geometry.getVertices(); for (int
					 * i = 0; i < geometry.getLength(); i++){
					 * printToObjFile("\nv"); for (int j = 0; j < 3; j++){
					 * printToObjFile(" "+fb.get()); } } fb.rewind();
					 * 
					 * //faces printToObjFile("\n"); for (int i = 0; i <
					 * geometry.getLength()/4; i++){ printToObjFile("\nf"); for
					 * (int j = 0; j < 4; j++){ objCurrentIndex++;
					 * //printToObjFile
					 * (" "+objCurrentIndex+"//"+objCurrentIndex);
					 * printToObjFile(" "+objCurrentIndex); } }
					 * 
					 * printToObjFile("\n##########################\n\n");
					 * break;
					 */
					default:
						App.error("geometry type not handled : "
								+ geometry.getType());
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printToObjFile(String s) throws IOException {
		// System.out.print(s);
		objBufferedWriter.write(s);
	}

}
