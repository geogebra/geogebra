package org.geogebra.web.geogebra3D.web.euclidian3D.printer3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.http.client.URL;

/**
 * Printer export
 * 
 * @author Mathieu
 *
 */
public class ExportToPrinter3DW extends ExportToPrinter3D {
	
	private StringBuilder sb = new StringBuilder();

	/**
	 * @param view
	 *            3d view
	 * @param renderer
	 *            renderer
	 */
	public ExportToPrinter3DW(EuclidianView3D view, Renderer renderer) {
		super();
		set(view, (ManagerShadersElementsGlobalBuffer) renderer.getGeometryManager());
	}
	
	@Override
	public void start() {
		super.start();
		sb.setLength(0);
		getFormat().getScriptStart(sb);
	}

	@Override
	protected void printToFile(String s) {
		sb.append(s);
	}

	@Override
	public void end() {
		getFormat().getScriptEnd(sb);
		Log.debug(sb.toString());
		String url = "data:text/plain;charset=utf-8,"
				+ URL.encodePathSegment(sb.toString());

		AppW app = (AppW) view.getApplication();
		app.dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, "exportSCAD"));
		app.getFileManager().showExportAsPictureDialog(url,
				app.getExportTitle(), "jscad", "Export", app);
	}

}
