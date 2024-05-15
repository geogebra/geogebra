package org.geogebra.common.spreadsheet.kernel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
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
	 * @param input to process
	 */
	public void process(String input) {
		process(input, true);
	}

	/**
	 * Depending on input, processor makes text or evaluates input
	 * @param input to process
	 * @param storeUndo Whether or not to store undo point
	 */
	public void process(String input, boolean storeUndo) {
		try {
			processInput(isCommand(input) ? buildCommandFrom(input) : buildTextFrom(input),
					storeUndo);
		} catch (Exception e) {
			Log.debug("error " + e.getLocalizedMessage());
		}
	}

	/**
	 * @param geoToCopy The GeoElement that should be copied to another cell
	 * @return True if the passed GeoElement contains a reference to another cell, false else
	 */
	public boolean containsDynamicReference(GeoElement geoToCopy) {
		String definition = geoToCopy.getDefinitionForEditor();
		if (!definition.contains("=")) {
			return false;
		}
		return getPossibleCellReferences(definition).stream().anyMatch(
				possibleReference -> GeoElementSpreadsheet.isSpreadsheetLabel(possibleReference));
	}

	private void processInput(String command, boolean storeUndo) {
		algebraProcessor.processAlgebraCommandNoExceptionHandling(command, storeUndo,
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
		sb.append(Unicode.ASSIGN_STRING);
	}

	private String buildCommandFrom(String input) {
		addCellEquals();
		sb.append(input.substring(1));
		return sb.toString();
	}

	private List<String> getPossibleCellReferences(String definition) {
		List<String> possibleCellReferences = new ArrayList<>();
		boolean possibleReference;
		final int length = definition.length();

		for (int i = definition.indexOf('='); i < length; i++) {
			possibleReference = false;
			if (Character.isLetter(definition.charAt(i))) {
				for (int j = i + 1; j < length; j++) {
					if (Character.isDigit(definition.charAt(j))) {
						possibleReference = true;
					} else {
						if (possibleReference) {
							possibleCellReferences.add(definition.substring(i, j));
						}
						possibleReference = false;
						break;
					}
					if (possibleReference && j + 1 == length) {
						possibleCellReferences.add(definition.substring(i, j + 1));
					}
				}
			}
		}
		return possibleCellReferences;
	}
}