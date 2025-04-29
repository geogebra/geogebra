package org.geogebra.common.spreadsheet.kernel;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;

/**
 * Spreadsheet data conversion.
 * <p>
 * (This class is an adapter between the Spreadsheet core and the Kernel.)
 * </p>
 */
public final class DefaultSpreadsheetCellDataSerializer implements SpreadsheetCellDataSerializer {

	private static final StringTemplate NO_POINT_EDITOR_TEMPLATE =
			StringTemplate.editorTemplate.deriveWithoutPointTemplate();

	/**
	 * @param data Spreadsheet cell content.
	 * @return A string representation of the content suitable for a cell editor.
	 */
	@Override
	public @Nonnull String getStringForEditor(Object data) {
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
				NO_POINT_EDITOR_TEMPLATE);
		return geo.isGeoText() && parentAlgorithm == null
				? redefineString
				: "=" + redefineString;
	}
}
