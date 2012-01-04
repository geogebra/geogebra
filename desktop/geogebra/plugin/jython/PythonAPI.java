package geogebra.plugin.jython;

import geogebra.common.awt.Color;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.kernel.Kernel;
import geogebra.main.Application;


public class PythonAPI {
	public static Class geoPoint = GeoPoint2.class;
	public static Class geoElement = GeoElement.class;
	
	public static void repaintGeo(GeoElement geo) {
		geo.updateRepaint();
	}
	public static String getGeoLabel(GeoElement geo) {
		return geo.getLabel();
	}
	public static void setGeoLabel(GeoElement geo, String label) {
		geo.setLabel(label);
	}
	public static Color getGeoColor(GeoElement geo) {
		return geo.getObjectColor();
	}
	public static void setGeoColor(GeoElement geo, Color color) {
		geo.setObjColor(color);
	}
	public static String getGeoCaption(GeoElement geo) {
		return geo.getCaption();
	}
	public static void setGeoCaption(GeoElement geo, String caption) {
		geo.setCaption(caption);
	}
	public static int getGeoLabelMode(GeoElement geo) {
		return geo.getLabelMode();
	}
	public static void setGeoLabelMode(GeoElement geo, int mode) {
		geo.setLabelMode(mode);
	}
	public static Color getGeoLabelColor(GeoElement geo) {
		return geo.getLabelColor();
	}
	
	private Application app;
	private Kernel kernel;
	private Construction cons;
	private AlgebraProcessor algProcessor;
	
	public PythonAPI(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.algProcessor = kernel.getAlgebraProcessor();
	}
}
