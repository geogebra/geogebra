package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.regexp.shared.MatchResult;

public final class CellReferenceHandler {

	/**
	 * @param definition The definition of the GeoElement that should be copied <br/>
	 * Note: Use {@link GeoElement#getDefinitionForEditor()}
	 * @return True if the passed GeoElement contains a reference to another cell, false else
	 */
	public static boolean containsDynamicReference(String definition) {
		if (!definition.contains("=")) {
			return false;
		}
		return getPossibleCellReferences(definition).stream().anyMatch(
				CellReferenceHandler::isDynamicReference);
	}

	/**
	 * @param definition The definition of the GeoElement that should be copied <br/>
	 * Note: Use {@link GeoElement#getDefinitionForEditor()}
	 * @param sourceRow Source row index
	 * @param targetRow Target row index
	 * @param sourceColumn Source column index
	 * @param targetColumn Target column index
	 * @return The definition of the GeoElement that should be copied, with all dynamic references
	 * adjusted accordingly
	 */
	public static String getDefinitionWithSubstitutedDynamicReferences(String definition,
			int sourceRow, int targetRow, int sourceColumn, int targetColumn) {
		String newDefinition = definition;
		int rowDifference = targetRow - sourceRow;
		int columnDifference = targetColumn - sourceColumn;
		List<String> dynamicReferences = getPossibleCellReferences(definition).stream().filter(
				CellReferenceHandler::isDynamicReference
		).collect(Collectors.toList());

		MatchResult matcher;
		for (String reference : dynamicReferences) {
			matcher = GeoElementSpreadsheet.spreadsheetPattern.exec(reference);
			newDefinition = newDefinition.replace(reference,
					GeoElementSpreadsheet.getSpreadsheetCellName(
							GeoElementSpreadsheet.getSpreadsheetColumn(matcher) + columnDifference,
							GeoElementSpreadsheet.getSpreadsheetRow(matcher) + rowDifference));
		}
		return newDefinition;
	}

	private static List<String> getPossibleCellReferences(String definition) {
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

	private static boolean isDynamicReference(String possibleReference) {
		return GeoElementSpreadsheet.isSpreadsheetLabel(possibleReference);
	}
}
