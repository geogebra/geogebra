package org.geogebra.desktop.geogebra3D.euclidian3D.printer3D;

import java.io.BufferedWriter;
import java.io.IOException;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;

public class ExportToPrinter3DD extends ExportToPrinter3D {

	private BufferedWriter objBufferedWriter;



	/**
	 * start file
	 * 
	 * @param writer
	 *            file writer
	 * @param view
	 *            3D view
	 * @param manager
	 *            geometries manager
	 */
	public void startFile(BufferedWriter writer, EuclidianView3D view,
			ManagerShadersElementsGlobalBuffer manager) {
		objBufferedWriter = writer;
		set(view, manager);
	}



	protected void printToFile(String s) {
		// System.out.print(s);
		try {
			objBufferedWriter.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
