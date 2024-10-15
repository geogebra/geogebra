package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
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
		AlgoElement parentAlgorithm = geo.getParentAlgorithm();
		if (Algos.isUsedFor(Commands.ParseToNumber, geo) && parentAlgorithm != null) {
			return "=" + parentAlgorithm.getInput(0).toValueString(
					StringTemplate.defaultTemplate);
		}
		String redefineString = geo.getRedefineString(true, false,
				StringTemplate.editorTemplate);
		return geo.isGeoText() && parentAlgorithm == null
				? redefineString
				: "=" + redefineString;
	}
}
