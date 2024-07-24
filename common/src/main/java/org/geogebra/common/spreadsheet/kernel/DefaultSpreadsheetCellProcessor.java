package org.geogebra.common.spreadsheet.kernel;

import static com.himamis.retex.editor.share.util.Unicode.ASSIGN_STRING;
import static org.geogebra.common.util.StringUtil.isNumber;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Sends spreadsheet cell editor input towards the AlgebraProcessor.
 *
 * (This class is an adapter between the Spreadsheet core and the Kernel.)
 */
public class DefaultSpreadsheetCellProcessor implements SpreadsheetCellProcessor {

	private final AlgebraProcessor algebraProcessor;
	private final ErrorHandler errorHandler;
	private String cellName;
	private final TabularData tabularData;

	/**
	 * Constructor.
	 * @param algebraProcessor {@link AlgebraProcessor}
	 * @param errorHandler The error handler of the cell.
	 */
	public DefaultSpreadsheetCellProcessor(@Nonnull AlgebraProcessor algebraProcessor,
			TabularData tabularData) {
		this.algebraProcessor = algebraProcessor;
		this.errorHandler = new SpreadsheetErrorHandler(this);
		this.tabularData = tabularData;
	}

	/**
	 * Depending on input, processor makes text or evaluates input.
	 *
	 * @param input The input to process.
	 * @param row Identifies the cell to receive the input.
	 * @param column Identifies the cell to receive the input.
	 */
	@Override
	public void process(String input, int row, int column) {
		String cellName = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
		process(input, cellName);
	}

	/**
	 * Same as {@link #process(String, int, int)}, only with a cell name formed from a row/column
	 * pair.
	 * @param input The input to process.
	 * @param cellName Identifies the cell to receive the input.
	 */
	public void process(String input, String cellName) {
		try {
			this.cellName = cellName;
			processInput(buildProperInput(input, cellName));
		} catch (Exception e) {
			Log.debug("error " + e.getLocalizedMessage());
		}
	}

	private String buildProperInput(String input, String cellName) {
		StringBuilder sb = new StringBuilder();

		appendCellAssign(cellName, sb);

		if (isNumber(input) && input.lastIndexOf('-') < 1) {
			sb.append(input);
		} else if (isCommand(input)) {
			appendAsCommand(input, sb);
		} else {
			appendAsText(input, sb);
		}

		return sb.toString();
	}

	private static void appendCellAssign(String cellName, StringBuilder sb) {
		sb.append(cellName);
		sb.append(ASSIGN_STRING);
	}

	private static void appendAsCommand(String input, StringBuilder sb) {
		sb.append(input.substring(1));
	}

	private static void appendAsText(String input, StringBuilder sb) {
		sb.append("\"");
		sb.append(input.replaceAll("\"", ""));
		sb.append("\"");
	}

	private void processInput(String command) {
		algebraProcessor.processAlgebraCommandNoExceptionHandling(command, true,
				errorHandler, false, null);
	}

	private static boolean isCommand(String input) {
		return input.startsWith("=");
	}

	private String buildError() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(cellName);
		strBuilder.append(ASSIGN_STRING);
		strBuilder.append("\"");
		strBuilder.append("ERROR");
		strBuilder.append("\"");
		return strBuilder.toString();
	}

	@Override
	public void markError() {
		GeoElement geo = algebraProcessor.getKernel().lookupLabel(cellName);
		if (geo != null) {
			geo.remove();
		}
		processInput(buildError());
		SpreadsheetCoords pt = GeoElementSpreadsheet.spreadsheetIndices(cellName);
		if (tabularData != null && pt != null && pt.row != -1) {
			tabularData.markError(pt.row, pt.column, true);
		}
	}
}
