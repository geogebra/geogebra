package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;

/**
 * Spreadsheet data conversion.
 *
 *  (This class is an adapter between the Spreadsheet core and the Kernel.)
 */
public final class DefaultSpreadsheetCellDataSerializer implements SpreadsheetCellDataSerializer {

	/**
	 * @param data Spreadsheet cell content.
	 * @return A string representation of the content suitable for a cell editor.
	 */
	@Override
	public String getStringForEditor(Object data) {
		if (data instanceof String) {
			return (String) data;
		}
		if (data == null) {
			return "";
		}
		GeoElement geo = (GeoElement) data;
		String redefineString = geo.getRedefineString(true, false, StringTemplate.editorTemplate);
		return geo.isGeoText() && !((GeoText) geo).hasSpreadsheetError() ? redefineString
				: "=" + redefineString;
	}
}
