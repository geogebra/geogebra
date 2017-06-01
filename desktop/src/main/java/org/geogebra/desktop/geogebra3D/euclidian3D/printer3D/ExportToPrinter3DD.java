package org.geogebra.desktop.geogebra3D.euclidian3D.printer3D;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.util.debug.Log;

public class ExportToPrinter3DD extends ExportToPrinter3D {

	public ExportToPrinter3DD(EuclidianView3D view, Renderer renderer) {
		super();
		Manager manager = renderer.getGeometryManager();
		if (manager instanceof ManagerShadersElementsGlobalBuffer){
			set(view, (ManagerShadersElementsGlobalBuffer) manager);
		}
	}

	private BufferedWriter objBufferedWriter;

	public void start() {
		
		super.start();
		StringBuilder sb = new StringBuilder();
		sb.append("test");
		getFormat().getExtension(sb);
		try {
			objBufferedWriter = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(sb.toString()),
							"UTF-8"));
			Log.debug("Export to "+sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (objBufferedWriter == null) {
			return;
		}
		sb.setLength(0);
		getFormat().getScriptStart(sb);
		try {
			objBufferedWriter.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void printToFile(String s) {
		if (objBufferedWriter == null) {
			return;
		}
		// System.out.print(s);
		try {
			objBufferedWriter.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void end() {
		if (objBufferedWriter == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		getFormat().getScriptEnd(sb);
		try {
			objBufferedWriter.write(sb.toString());
			objBufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
