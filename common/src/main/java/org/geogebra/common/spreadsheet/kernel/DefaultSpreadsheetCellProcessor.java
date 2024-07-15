package org.geogebra.common.spreadsheet.kernel;

import static org.geogebra.common.util.StringUtil.isNumber;

import java.util.Arrays;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellProcessor;
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

	/**
	 * Constructor.
	 * @param algebraProcessor {@link AlgebraProcessor}
	 * @param errorHandler The error handler of the cell.
	 */
	public DefaultSpreadsheetCellProcessor(@Nonnull AlgebraProcessor algebraProcessor,
			@CheckForNull ErrorHandler errorHandler) {
		this.algebraProcessor = algebraProcessor;
		this.errorHandler = errorHandler;
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
		sb.append(Unicode.ASSIGN_STRING);
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
				errorHandler, false, this::setGeosEuclidianInvisibleAndAuxiliary);
	}

	private void setGeosEuclidianInvisibleAndAuxiliary(GeoElementND[] geos) {
		if (geos != null) {
			Arrays.stream(geos).forEach(geo -> {
				geo.setEuclidianVisible(false);
				geo.setAuxiliaryObject(true);
				geo.updateVisualStyle(GProperty.VISIBLE);
			});
		}
	}

	private static boolean isCommand(String input) {
		return input.startsWith("=");
	}
}