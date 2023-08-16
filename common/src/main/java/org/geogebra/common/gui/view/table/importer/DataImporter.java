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

	// Note: Since the TableValuesView has to hold all data in memory anyway, we could
	// drop the memory efficiency design goal, collect all successfully validated rows
	// in memory, get rid of parsing everything twice, and replace the 2-step import
	// (validateCSV, loadCSV) with a single `importCSV()` method.

	public boolean validateCSV(Reader reader) {
		LineReader lineReader = new LineReader(reader);
		CSVParser parser = new CSVParser();
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
					parser = new CSVParser(separator);
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
		tableValuesView.startImport(nrOfRows, nrOfColumns);
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
				// TODO add to TableValuesView
				tableValuesView.importRow(values);
				if (!isHeaderRow) {
					if (!shouldContinueImport(hasHeader ? currentRow - 1 : currentRow,
							nrOfRows)) {
						tableValuesView.cancelImport();
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO log
			return false;
		}
		tableValuesView.commitImport();
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
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException e) { }
		try {
			Integer.parseInt(value);
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
}
