/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.table.importer;

import static org.geogebra.common.gui.view.table.importer.DataImporterError.INCONSISTENT_COLUMNS;
import static org.geogebra.common.gui.view.table.importer.DataImporterWarning.DATA_SIZE_LIMIT_EXCEEDED;
import static org.geogebra.common.gui.view.table.importer.DataImporterWarning.NUMBER_FORMAT_WARNING;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.common.util.opencsv.CSVParser;

import com.google.j2objc.annotations.Weak;

/**
 * Imports tabular data into a {@link TableValuesView}.
 *
 * Note: This type is not (designed to be) thread-safe.
 */
public final class DataImporter {

	@Weak
	private DataImporterDelegate delegate;
	private final TableValuesView tableValuesView;
	private int maxRowCount = 1000;
	private int maxColumnCount = 100;
	private boolean discardHeader = true;

	/**
	 * Constructs a new importer for the given TableValuesView.
	 * @param tableValuesView The TableValuesView to import data into.
	 * @param delegate An optional delegate.
	 */
	public DataImporter(TableValuesView tableValuesView, DataImporterDelegate delegate) {
		this.tableValuesView = tableValuesView;
		this.delegate = delegate;
	}

	// Configuration

	/**
	 * Limit the number of rows and/or columns to import.
	 *
	 * @param maxRowCount The maximum number of rows (including header rows) to import.
	 *                  Defaults to 1000.
	 * @param maxColumnCount The maximum number of columns to import.
	 *                     Defaults to 100.
	 */
	public void setDataSizeLimits(int maxRowCount, int maxColumnCount) {
		this.maxRowCount = maxRowCount;
		this.maxColumnCount = maxColumnCount;
	}

	/**
	 * Gets the limit for the number of rows to import.
	 *
	 * @return the maximum number of rows to import. -1 means "no limit".
	 */
	public int getMaxRowCount() {
		return maxRowCount;
	}

	/**
	 * Gets the limit for the number of columns to import.
	 *
	 * @return the maximum number of columns to import. -1 means "no limit".
	 */
	public int getMaxColumnCount() {
		return maxColumnCount;
	}

	/**
	 * Configure whether any header should be discarded during import.
	 *
	 * @param discardHeader Discard any header rows. Defaults to true.
	 */
	public void setsDiscardHeader(boolean discardHeader) {
		this.discardHeader = discardHeader;
	}

	/**
	 * Whether any header should be discarded during import.
	 *
	 * @return true (the default) if any headers should be discarded during import.
	 */
	public boolean getDiscardHeader() {
		return discardHeader;
	}

	// CSV Support

	/**
	 * Imports CSV data into the {@link TableValuesView}.
	 * See {@link #importCSV(Reader, char)} for more details.
	 *
	 * @param csv <a href="https://datatracker.ietf.org/doc/html/rfc4180">Comma-Separated Values (CSV)</a>
	 *            data
	 * @param decimalSeparator The decimal separator to use. This will depend on the user's locale.
	 * @return true if import was successful, false in case of an error.
	 */
	public boolean importCSV(String csv, char decimalSeparator) {
		StringReader reader = new StringReader(csv);
		return importCSV(reader, decimalSeparator);
	}

	/**
	 * Imports CSV data into the {@link TableValuesView}.
	 * <p>
	 * We mostly follow the format as described in
	 * <a href="https://datatracker.ietf.org/doc/html/rfc4180">Comma-Separated Values (CSV)
	 * Files</a>. However, in addition to the standard column separator ',' (comma), we also
	 * support ';' (semicolon) and '\t' (tab) column separators automatically. Also, we
	 * support both '\r\n\' (CRLF) and '\n' (LF) line breaks automatically.
	 * <p>
	 * The decimal separator character must be provided by the user.
	 * The CSV file must not contain thousands separators in decimal numbers. If thousands
	 * separators are present in decimal numbers, parsing will either fail or produce
	 * incorrect results.
	 * <p>
	 * Import is a two-stage process:
	 * <ul>
	 * <li>In the first stage, the data is validated. The delegate will be notified about
	 * validation progress (indeterminate progress feedback), warnings, and errors.
	 * <li>If no validation errors occurred, the rows collected during validation are imported
	 * into the {@link TableValuesView} (row by row) in the second stage. The delegate will
	 * be notified about import progress (determinate progress feedback).
	 * </ul>
	 *
	 * @param reader A reader for the CSV data. The reader does not have to support mark/reset.
	 * @param decimalSeparator The decimal separator to use.
	 * @return false in case of a validation error, if the data is empty, if validation was
	 * canceled by the delegate, or if import was canceled by the delegate; true otherwise.
	 */
	public boolean importCSV(Reader reader, char decimalSeparator) {
		List<Row> rows = validateAndCollectRowsFromCSV(reader, decimalSeparator);
		if (rows == null) {
			return false;
		}
		return importRows(rows);
	}

	private List<Row> validateAndCollectRowsFromCSV(Reader reader, char decimalSeparator) {
		char csvSeparator = 0;
		boolean dataHasHeader = false;
		int columnCount = -1;
		int currentRow = 0;
		LineReader lineReader = new LineReader(reader);
		CSVParser parser = new CSVParser();
		List<Row> rows = new ArrayList<>();
		String line;
		try {
			while ((line = lineReader.readLine()) != null) {
				currentRow++;
				if (currentRow > maxRowCount) {
					notifyAboutWarning(DATA_SIZE_LIMIT_EXCEEDED, currentRow);
					break; // skip remaining data
				}
				if (csvSeparator == 0) {
					csvSeparator = guessCSVSeparator(line);
					parser = new CSVParser(csvSeparator);
				}
				String[] rawValues = parser.parseLine(line);
				trim(rawValues);
				if (maxColumnCount > 0 && maxColumnCount < rawValues.length) {
					rawValues = Arrays.copyOf(rawValues, maxColumnCount);
				}
				if (currentRow == 1) {
					columnCount = rawValues.length;
					if (rawValues.length > 0 && !isValidNumber(rawValues[0], decimalSeparator)) {
						dataHasHeader = true; // best-effort guess
					}
				}
				int rowNr = dataHasHeader ? currentRow - 1 : currentRow;
				boolean isHeaderRow = currentRow == 1 && dataHasHeader;
				if (!isHeaderRow && !shouldContinueValidation(rowNr)) {
					return null;
				}
				if (rawValues.length != columnCount) {
					notifyAboutError(INCONSISTENT_COLUMNS, rowNr);
					return null;
				}
				if (isHeaderRow) {
					if (!discardHeader) {
						rows.add(new Row(0, true, null, rawValues, false));
					}
				} else {
					Row row = validateAndParse(rawValues, decimalSeparator, rowNr);
					rows.add(row);
					if (row.hasValidationIssues) {
						notifyAboutWarning(NUMBER_FORMAT_WARNING, rowNr);
					}
				}
			}
		} catch (CSVException e) {
			notifyAboutError(DataImporterError.DATA_FORMAT_ERROR, currentRow);
			return null;
		} catch (IOException e) {
			notifyAboutError(DataImporterError.READ_ERROR, currentRow);
			return null;
		}
		return rows;
	}

	private boolean importRows(List<Row> rows) {
		if (rows == null || rows.size() == 0) {
			return false;
		}
		Row firstRow = rows.get(0);
		String[] columnNames = getColumnNames(firstRow);
		tableValuesView.startImport(rows.size(), firstRow.columnCount, columnNames);
		for (Row row : rows) {
			if (row.isHeader) {
				continue;
			}
			if (!shouldContinueImport(row.rowNr, rows.size())) {
				tableValuesView.cancelImport();
				return false;
			}
			tableValuesView.importRow(row.values, row.rawValues);
		}
		tableValuesView.commitImport();
		return true;
	}

	private char guessCSVSeparator(String line) {
		if (line.contains(";")) {
			return ';';
		}
		if (line.contains(",")) {
			return ',';
		}
		if (line.contains("\t")) {
			return '\t';
		}
		return '\n'; // single-column case
	}

	// Number parsing

	private Row validateAndParse(String[] rawValues, char decimalSeparator, int rowNr) {
		Double[] values = new Double[rawValues.length];
		boolean hasValidationIssue = false;
		for (int index = 0; index < rawValues.length; index++) {
			Double value = parseDouble(rawValues[index], decimalSeparator);
			if (value == null) {
				hasValidationIssue = true;
			} else {
				values[index] = value;
			}
		}
		return new Row(rowNr, false, values, rawValues, hasValidationIssue);
	}

	/**
	 * Checks if a string represents a valid decimal number.
	 * <p/>
	 * We use the same regex that GWT uses for validating floats and doubles:
	 * <code>"^\\s*[+-]?(NaN|Infinity|((\\d+\\.?\\d*)|(\\.\\d+))([eE][+-]?\\d+)?[dDfF]?)
	 * \\s*$"</code>
	 * <p/>
	 * See <a href="https://github.com/gwtproject/gwt/blob/main/user/super/com/google/gwt/emul/java/lang/Number.java">
	 *     GWT's number parsing</a>
	 * Also see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseFloat#specifications">
	 *     JavaScript parseFloat documentation</a>
	 * @param value A String representing a decimal number.
	 * @param decimalSeparator The decimal separator character.
	 * @return True if the (canonicalized) value matches the above-mentioned regex.
	 */
	private boolean isValidNumber(String value, char decimalSeparator) {
		String canonicalized = canonicalizeNumber(value, decimalSeparator);
		// See <a href="https://github.com/gwtproject/gwt/blob/main/user/super/com/google/gwt/emul/java/lang/Number.java#__isValidDouble">Number.java</a>
		String floatRegex = "^\\s*[+-]?(NaN|Infinity|((\\d+\\.?\\d*)|(\\.\\d+))([eE][+-]?\\d+)?[dDfF]?)\\s*$";
		return canonicalized.matches(floatRegex);
	}

	/**
	 * Parses a string into a Double.
	 *
	 * @implNote Normally, we'd use NumberFormat/DecimalFormat for locale-specific number parsing,
	 * but this is not supported by GWT's
	 * <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">JRE emulation</a>.
	 * @param value A String representing a decimal number.
	 * @param decimalSeparator The decimal separator character.
	 * @return The double result if parsing was successful, or null in case of a parsing error.
	 */
	private Double parseDouble(String value, char decimalSeparator) {
		String canonicalized = canonicalizeNumber(value, decimalSeparator);
		try {
			return Double.parseDouble(canonicalized);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Replaces the decimal separator char with a '.' (dot), to make the value match
	 * the number format expected by the number parsing code.
	 *
	 * @param value A String representing a decimal number.
	 * @param decimalSeparator The decimal separator character.
	 * @return A canonicalized version of the input value.
	 */
	private String canonicalizeNumber(String value, char decimalSeparator) {
		// replace decimal separator with '.'
		return value.replace(decimalSeparator, '.');
	}

	private void trim(String[] rawValues) {
		for (int i = 0; i < rawValues.length; i++) {
			rawValues[i] = rawValues[i] != null ? rawValues[i].trim() : "";
		}
	}

	private String[] getColumnNames(Row firstRow) {
		if (firstRow == null || !firstRow.isHeader) {
			return null;
		}
		String[] columnNames = new String[firstRow.columnCount];
		for (int columnIndex = 0; columnIndex < firstRow.columnCount; columnIndex++) {
			columnNames[columnIndex] = firstRow.rawValues[columnIndex];
		}
		return columnNames;
	}

	// Delegate notifications

	private void notifyAboutError(DataImporterError error, int rowNr) {
		if (delegate != null) {
			delegate.onImportError(error, rowNr);
		}
	}

	private void notifyAboutWarning(DataImporterWarning warning, int rowNr) {
		if (delegate != null) {
			delegate.onImportWarning(warning, rowNr);
		}
	}

	private boolean shouldContinueValidation(int rowNr) {
		if (delegate != null) {
			return delegate.onValidationProgress(rowNr);
		}
		return true;
	}

	private boolean shouldContinueImport(int rowNr, int totalRowCount) {
		if (delegate != null) {
			return delegate.onImportProgress(rowNr, totalRowCount);
		}
		return true;
	}

	private static class Row {

		int rowNr; // note: 1-based (optional header is row 0)
		int columnCount;
		boolean isHeader;
		Double[] values;
		String[] rawValues;
		boolean hasValidationIssues;

		Row(int rowNr, boolean isHeader, Double[] values, String[] rawValues,
				boolean hasValidationIssues) {
			this.rowNr = rowNr;
			this.columnCount = rawValues.length;
			this.isHeader = isHeader;
			this.values = values;
			this.rawValues = rawValues;
			this.hasValidationIssues = hasValidationIssues;
		}
	}
}
