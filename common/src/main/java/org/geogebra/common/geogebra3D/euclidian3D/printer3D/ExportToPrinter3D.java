package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;

public abstract class ExportToPrinter3D {

	static public enum Type {
		CURVE, CURVE_CLOSED, SURFACE_CLOSED, POINT
	}

	protected Format format;

	abstract public void export(Drawable3D d, Type type);

	/**
	 * 
	 * @return 3D printer format
	 */
	public Format getFormat() {
		return format;
	}


}
