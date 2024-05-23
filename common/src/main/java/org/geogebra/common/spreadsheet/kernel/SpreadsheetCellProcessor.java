package org.geogebra.common.spreadsheet.kernel;

import java.util.Arrays;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

public class SpreadsheetCellProcessor {

	private final AlgebraProcessor algebraProcessor;
	private final ErrorHandler errorHandler;
	private final String cellName;
	private final StringBuilder sb;

	/**
	 *
	 * @param cellName The name of the cell.
	 * @param algebraProcessor {@link AlgebraProcessor}
	 * @param errorHandler The error handler of the cell.
	 */
	public SpreadsheetCellProcessor(String cellName, AlgebraProcessor algebraProcessor,
			ErrorHandler errorHandler) {
		this.cellName = cellName;
		this.algebraProcessor = algebraProcessor;
		this.errorHandler = errorHandler;
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
				errorHandler, false, this::setGeosEuclidianInvisibleAndAuxiliary);
	}

	private void setGeosEuclidianInvisibleAndAuxiliary(GeoElementND[] geos) {
		Arrays.stream(geos).forEach(geo -> {
			geo.setEuclidianVisible(false);
			geo.setAuxiliaryObject(true);
			geo.updateVisualStyle(GProperty.VISIBLE);
		});
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
		sb.append(Unicode.ASSIGN_STRING);
	}

	private String buildCommandFrom(String input) {
		addCellEquals();
		sb.append(input.substring(1));
		return sb.toString();
	}
}