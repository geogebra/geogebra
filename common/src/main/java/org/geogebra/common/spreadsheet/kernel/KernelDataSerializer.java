package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.geos.GeoElement;

public final class KernelDataSerializer {

	/**
	 * @param data spreadsheet cell content
	 * @return string representation for editor
	 */
	public String getStringForEditor(Object data) {
		if (data instanceof String) {
			return (String) data;
		}
		return data == null ? ""
				: ((GeoElement) data).getRedefineString(true, false);
	}
}
