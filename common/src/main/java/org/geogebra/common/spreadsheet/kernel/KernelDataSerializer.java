package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.geos.GeoElement;

public final class KernelDataSerializer {

	/**
	 * @param data spreadsheet cell content
	 * @return string representation for editor
	 */
	public String getStringForEditor(Object data, boolean hasError) {
		if (data instanceof String) {
			return (String) data;
		}
		if (data == null) {
			return "";
		} else {
			GeoElement geo = (GeoElement) data;
			String redefineString = geo.getRedefineString(true, false);
			return geo.isGeoText() && !hasError ? redefineString : "=" + redefineString;
		}
	}
}
