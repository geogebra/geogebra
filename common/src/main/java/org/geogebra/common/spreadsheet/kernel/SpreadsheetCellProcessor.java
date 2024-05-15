package org.geogebra.common.spreadsheet.kernel;

import static com.himamis.retex.editor.share.util.Unicode.ASSIGN_STRING;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class SpreadsheetCellProcessor {

	private final AlgebraProcessor algebraProcessor;
	private final ErrorHandler errorHandler;
	private final String cellName;
	private final StringBuilder sb;

	/**
	 * @param cellName The name of the cell.
	 * @param algebraProcessor {@link AlgebraProcessor}
	 */
	public SpreadsheetCellProcessor(String cellName, AlgebraProcessor algebraProcessor,
			MathFieldInternal mathField) {
		this.cellName = cellName;
		this.algebraProcessor = algebraProcessor;
		errorHandler = new SpreadsheetErrorHandler(this, mathField);
		sb = new StringBuilder();
	}

	/**
	 * Depending on input, processor makes text or evaluates input.
	 *
	 * @param input to process
	 */
	public void process(String input) {
		try {
			processInput(isCommand(input) ? buildCommandFrom(input) : buildTextFrom(input));
		} catch (Exception e) {
			Log.debug("error " + e.getLocalizedMessage());
		}
	}

	private void processInput(String command) {
		algebraProcessor.processAlgebraCommandNoExceptionHandling(command, true,
				errorHandler, false, null);
	}

	private static boolean isCommand(String input) {
		return input.startsWith("=");
	}

	private String buildTextFrom(String input) {
		addCellEquals();
		sb.append("\"");
		sb.append(input.replaceAll("\"", ""));
		sb.append("\"");
		return sb.toString();
	}

	private void addCellEquals() {
		sb.append(cellName);
		sb.append(ASSIGN_STRING);
	}

	private String buildCommandFrom(String input) {
		addCellEquals();
		sb.append(input.substring(1));
		return sb.toString();
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

	/**
	 * show error in cell
	 */
	public void showError() {
		GeoElement geo = algebraProcessor.getKernel().lookupLabel(cellName);
		if (geo != null) {
			geo.remove();
		}
		processInput(buildError());
	}
}