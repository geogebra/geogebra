package org.geogebra.common.gui.view.table.importer;

import static org.geogebra.common.gui.view.table.importer.DataImporterError.INCONSISTENT_COLUMNS;
import static org.geogebra.common.gui.view.table.importer.DataImporterWarning.DATA_SIZE_LIMIT_EXCEEDED;

import java.io.IOException;
import java.io.Reader;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.util.opencsv.CSVException;
import org.geogebra.common.util.opencsv.CSVParser;

public final class DataImporter {

	public DataImporterDelegate delegate;
	private TableValuesView tableValuesView;
	private boolean hasHeader = false;
	private char separator = 0;
	private int nrOfColumns = -1;
	private int nrOfRows = -1;
	private boolean validatedSuccessfully = false;
	private int maxNrRows = 1000;
	private int maxNrColumns = 100;
	private boolean discardHeader = true;
	public DataImporter(TableValuesView tableValuesView) {
		this.tableValuesView = tableValuesView;
	}

	public void setDataSizeLimits(int maxNrRows, int maxNrColumns) {
		assert maxNrRows > 0;
		assert maxNrColumns > 0;
		this.maxNrRows = maxNrRows;
		this.maxNrColumns = maxNrColumns;
	}

	public void setsDiscardHeader(boolean discardHeader) {
		this.discardHeader = discardHeader;
	}

	// CSV support

	// Note: If we wanted to trade memory for CPU cycles, i.e., keep all successfully
	// validated rows in memory to get rid of parsing everything twice, we could
	// - collect all successfully validated rows in the first phase into a list
	// - loop over this list in the second phase
	// - replace the `validateCSV()` and `loadCSV()` pair of methods with one
	//   `importCSV()` method.

	public boolean validateCSV(Reader reader) {
		LineReader lineReader = new LineReader(reader);
		CSVParser parser = new CSVParser();
		Row previousRow = null;
		String line;
		separator = 0;
		validatedSuccessfully = true;
		nrOfRows = -1;
		nrOfColumns = -1;
		int currentRow = 0;
		try {
			while ((line = lineReader.readLine()) != null) {
				currentRow++;
				// don't consider header for size limits
				if (currentRow - (hasHeader ? 1 : 0) > maxNrRows) {
					notifyAboutWarning(DATA_SIZE_LIMIT_EXCEEDED);
					break;
				}
				if (separator == 0) {
					separator = guessSeparator(line);
				}
				String[] values = parser.parseLine(line);
				if (currentRow == 1) {
					nrOfColumns = values.length;
					if (values.length > 0 && !isNumber(values[0])) {
						hasHeader = true;
					}
				}
				boolean isHeaderRow = currentRow == 1 && hasHeader;
				if (!isHeaderRow) {
					if (!shouldContinueValidation(hasHeader ? currentRow - 1 : currentRow)) {
						break;
					}
				}
				if (values.length != nrOfColumns) {
					validatedSuccessfully = false;
					notifyAboutError(INCONSISTENT_COLUMNS);
					break;
				}
			}
			nrOfRows = hasHeader ? currentRow - 1 : currentRow;
		} catch (Exception e) {
			// TODO log
			validatedSuccessfully = false;
		}
		return validatedSuccessfully;
	}

	public boolean loadCSV(Reader reader) {
		if (!validatedSuccessfully || separator == 0 || nrOfRows == 0 || nrOfColumns == 0) {
			return false;
		}
		LineReader lineReader = new LineReader(reader);
		CSVParser parser = new CSVParser(separator);
		String line;
		int currentRow = 0;
//		tableValuesView.startImport(nrOfRows, nrOfColumns);
		try {
			while ((line = lineReader.readLine()) != null) {
				currentRow++;
				// don't count header for size limit
				if (currentRow - (hasHeader ? 1 : 0) > maxNrRows) {
					break; // stop loading
				}
				if (currentRow == 1 && hasHeader && discardHeader) {
					continue;
				}
				String[] values = parser.parseLine(line);
				boolean isHeaderRow = currentRow == 1 && hasHeader;
				Row row = new Row(values, isHeaderRow);
				// TODO add to TableValuesView
				if (!isHeaderRow) {
					if (!shouldContinueImport(hasHeader ? currentRow - 1 : currentRow,
							nrOfRows)) {
//						tableValuesView.cancelImport();
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO log
			return false;
		}
//		tableValuesView.commitImport();
		return true;
	}

	private char guessSeparator(String line) {
		if (line.contains(";")) {
			return ';';
		}
		if (line.contains(",")) {
			return ',';
		}
		return '\n'; // single-column case
	}

	private boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) { }
		try {
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException e) { }
		return false;
	}

	private void notifyAboutError(DataImporterError error) {
		if (delegate != null) {
			delegate.onImportError(error);
		}
	}

	private void notifyAboutWarning(DataImporterWarning warning) {
		if (delegate != null) {
			delegate.onImportWarning(warning);
		}
	}

	private boolean shouldContinueValidation(int currentRow) {
		if (delegate != null) {
			return delegate.onValidationProgress(currentRow);
		}
		return true;
	}

	private boolean shouldContinueImport(int currentRow, int totalNrOfRows) {
		if (delegate != null) {
			return delegate.onImportProgress(currentRow, totalNrOfRows);
		}
		return true;
	}

	/**
	 * Mostly for testing
	 * @return true if a header has been detected during validation.
	 */
	public boolean getHasHeader() {
		return hasHeader;
	}

	private static class Row {

		boolean isHeader;

		String[] values;

		Row(String[] values, boolean isHeader) {
			this.values = values;
			this.isHeader = isHeader;
		}
	}
}
