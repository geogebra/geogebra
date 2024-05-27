package org.geogebra.common.spreadsheet.kernel;

import static com.himamis.retex.editor.share.util.Unicode.ASSIGN_STRING;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class SpreadsheetCellProcessor {

	private final AlgebraProcessor algebraProcessor;
	private final SpreadsheetEditorListener listener;
	private final ErrorHandler errorHandler;
	private final String cellName;
	private final StringBuilder sb;
	private String input;

	/**
	 * @param cellName The name of the cell.
	 * @param algebraProcessor {@link AlgebraProcessor}
	 */
	public SpreadsheetCellProcessor(String cellName, AlgebraProcessor algebraProcessor,
			SpreadsheetEditorListener listener) {
		this.cellName = cellName;
		this.algebraProcessor = algebraProcessor;
		this.listener = listener;
		errorHandler = new SpreadsheetErrorHandler(this);
		sb = new StringBuilder();
	}

	/**
	 * Depending on input, processor makes text or evaluates input.
	 *
	 * @param input to process
	 */
	public void process(String input) {
		try {
			this.input = input;
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

	private String buildErrorFrom(String input) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(cellName);
		stringBuilder.append(ASSIGN_STRING);
		stringBuilder.append("\"");
		stringBuilder.append(input.replaceAll("=", ""));
		stringBuilder.append("\"");
		return stringBuilder.toString();
	}

	/**
	 * mark error
	 */
	public void markError() {
		Kernel kernel = algebraProcessor.getKernel();
		GeoElement geo = kernel.lookupLabel(cellName);
		if (geo != null) {
			geo.remove();
		}
		processInput(buildErrorFrom(input));
		GPoint pt = GeoElementSpreadsheet.spreadsheetIndices(cellName);
		if (pt != null && pt.x != -1) {
			listener.markError(pt.y, pt.x);
		}
	}
}