package org.geogebra.common.gui.view.table.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesPointsImpl;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class DataImporterTests extends BaseUnitTest implements DataImporterDelegate {

	private TableValuesView tableValuesView;
	private TableValuesPointsImpl tableValuesPoints;
	private DataImporter dataImporter;
	private int currentRow;
	private int totalRowCount;
	private DataImporterError error;
	private DataImporterWarning warning;
	private int warningOrErrorRow;
	private int cancelValidationAfterRow;
	private int cancelImportAfterRow;

	@Override
	public void setup() {
		super.setup();

		Kernel kernel = getKernel();
		tableValuesView = new TableValuesView(kernel);
		kernel.attach(tableValuesView);
		TableValuesModel model = tableValuesView.getTableValuesModel();
		tableValuesPoints = TableValuesPointsImpl.create(kernel.getConstruction(),
				tableValuesView, model);
		kernel.notifyAddAll(tableValuesView);

		dataImporter = new DataImporter(tableValuesView, this);
		currentRow = -1;
		totalRowCount = -1;
		error = null;
		warning = null;
		warningOrErrorRow = -1;
		cancelValidationAfterRow = -1;
		cancelImportAfterRow = -1;
	}

	@Override
	public void teardown() {
		Kernel kernel = getKernel();
		kernel.detach(tableValuesView);
		tableValuesView = null;

		super.teardown();
	}

	@Test
	public void testImportCSVHeader() {
		Reader reader = loadSample("integers-comma-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount); // header row should be discarded
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals("x", tableValuesView.getTableValuesModel().getHeaderAt(0));
		assertEquals("y1", tableValuesView.getTableValuesModel().getHeaderAt(1));
		// no points should be created during import
		assertFalse(tableValuesPoints.arePointsVisible(0));
		assertFalse(tableValuesPoints.arePointsVisible(1));
	}

	@Test
	public void testImportCSVNotDiscardingHeader() {
		Reader reader = loadSample("integers-comma-header.csv");
		dataImporter.setsDiscardHeader(false);
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(11, totalRowCount); // header row should not be discarded
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals("X", tableValuesView.getTableValuesModel().getHeaderAt(0));
		assertEquals("Y", tableValuesView.getTableValuesModel().getHeaderAt(1));
		// no points should be created during import
		assertFalse(tableValuesPoints.arePointsVisible(0));
		assertFalse(tableValuesPoints.arePointsVisible(1));
	}

	@Test
	public void testImportCSVNoHeader() {
		Reader reader = loadSample("integers-comma-noheader.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals("x", tableValuesView.getTableValuesModel().getHeaderAt(0));
		assertEquals("y1", tableValuesView.getTableValuesModel().getHeaderAt(1));
		// no points should be created during import
		assertFalse(tableValuesPoints.arePointsVisible(0));
		assertFalse(tableValuesPoints.arePointsVisible(1));
	}

	@Test
	public void testImportCSVSingleColumn() {
		Reader reader = loadSample("integers-noheader.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testImportCSVWithInconsistentSeparator() {
		Reader reader = loadSample("inconsistent-separator.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertFalse(success);
		assertNotNull(error);
		assertEquals(3, warningOrErrorRow);
		assertEquals(3, currentRow);
		assertEquals(-1, totalRowCount);
	}

	@Test
	public void testImportCSVEmptyValues() {
		Reader reader = loadSample("integers-empty-comma-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertEquals(DataImporterWarning.NUMBER_FORMAT_WARNING, warning);
		assertEquals(10, warningOrErrorRow);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals(1.0, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(3.0, tableValuesView.getTableValuesModel().getValueAt(2, 1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(3, 1), 0.0);
	}

	@Test
	public void testImportCSVWithCorrectDecimalSeparator1() {
		Reader reader = loadSample("dotdecimals-comma-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(1.1, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(2.2, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(3.3, tableValuesView.getTableValuesModel().getValueAt(2, 1), 0.0);
	}

	@Test
	public void testImportCSVWithCorrectDecimalSeparator2() {
		Reader reader = loadSample("dotdecimals-semicolon-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(1.1, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(2.2, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(3.3, tableValuesView.getTableValuesModel().getValueAt(2, 1), 0.0);
	}

	@Test
	public void testImportCSVWithWrongSeparator1() {
		Reader reader = loadSample("dotdecimals-semicolon-header.csv"); // e.g. "1.1"
		// using wrong decimal separator, but (default) dot separator should still
		// give correct results
		boolean success = dataImporter.importCSV(reader, ',');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(1.1, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(2.2, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(3.3, tableValuesView.getTableValuesModel().getValueAt(2, 1), 0.0);
	}

	@Test
	public void testImportCSVWithWrongSeparator2() {
		Reader reader = loadSample("commadecimals-semicolon-header.csv"); // e.g. "1,1"
		// using wrong decimal separator, decimal parsing should fail
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertEquals(DataImporterWarning.NUMBER_FORMAT_WARNING, warning);
		assertEquals(10, warningOrErrorRow);
		assertEquals(10, currentRow);
		assertEquals(10, totalRowCount);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(2, 1), 0.0);
	}

	@Test
	public void testImportCSVWithHeaderWithDataSizeLimits() {
		Reader reader = loadSample("integers-strings-comma-noheader.csv");
		dataImporter.setDataSizeLimits(3, 2);
		dataImporter.setsDiscardHeader(false);
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertEquals(DataImporterWarning.DATA_SIZE_LIMIT_EXCEEDED, warning);
		assertEquals(3, currentRow);
		assertEquals(3, totalRowCount);
		assertEquals(3, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testImportCSVWithDataSizeLimits() {
		Reader reader = loadSample("integers-comma-noheader.csv");
		dataImporter.setDataSizeLimits(2, 10);
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNotNull(warning);
		assertEquals(2, currentRow);
		assertEquals(2, totalRowCount);
	}

	@Test
	public void testImportCSVStrings() {
		Reader reader = loadSample("strings-comma-noheader.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertEquals(DataImporterWarning.NUMBER_FORMAT_WARNING, warning);
		assertEquals(5, warningOrErrorRow);
		assertEquals(5, currentRow);
		assertEquals(5, totalRowCount);
		assertEquals(5, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(4, 1), 0.0);
		assertEquals("a", tableValuesView.getTableValuesModel().getCellAt(0, 1).getInput());
		assertEquals("b", tableValuesView.getTableValuesModel().getCellAt(1, 1).getInput());
		assertEquals("e", tableValuesView.getTableValuesModel().getCellAt(4, 1).getInput());
	}

	@Test
	public void testImportCSVIntegersStrings() {
		Reader reader = loadSample("integers-strings-comma-noheader.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertEquals(DataImporterWarning.NUMBER_FORMAT_WARNING, warning);
		assertEquals(5, warningOrErrorRow);
		assertEquals(5, currentRow);
		assertEquals(5, totalRowCount);
		assertEquals(5, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(3, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals("x", tableValuesView.getTableValuesModel().getHeaderAt(0));
		assertEquals("y1", tableValuesView.getTableValuesModel().getHeaderAt(1));
		assertEquals("y2", tableValuesView.getTableValuesModel().getHeaderAt(2));
		assertEquals(1.0, tableValuesView.getTableValuesModel().getValueAt(0, 1), 0.0);
		assertEquals(4.0, tableValuesView.getTableValuesModel().getValueAt(1, 1), 0.0);
		assertEquals(9.0, tableValuesView.getTableValuesModel().getValueAt(2, 1), 0.0);
		assertEquals("a", tableValuesView.getTableValuesModel().getCellAt(0, 2).getInput());
		assertEquals("b", tableValuesView.getTableValuesModel().getCellAt(1, 2).getInput());
		assertEquals("c", tableValuesView.getTableValuesModel().getCellAt(2, 2).getInput());
	}

	@Test
	public void testCancelCSVValidation() {
		Reader reader = loadSample("integers-comma-noheader.csv");
		cancelValidationAfterRow = 2;
		boolean success = dataImporter.importCSV(reader, '.');
		assertFalse(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(2, currentRow);
		assertEquals(-1, totalRowCount);
	}

	@Test
	public void testCancelCSVImport() {
		Reader reader = loadSample("integers-comma-noheader.csv");
		cancelImportAfterRow = 3;
		boolean success = dataImporter.importCSV(reader, '.');
		assertFalse(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(3, currentRow);
		assertEquals(10, totalRowCount);
	}

	// Helper methods

	private Reader loadSample(String filename) {
		InputStream inputStream = getClass().getResourceAsStream(filename);
		if (inputStream == null) {
			return null;
		}
		return new InputStreamReader(inputStream);
	}

	// DataImporterDelegate

	@Override
	public boolean onValidationProgress(int currentRow) {
		this.currentRow = currentRow;
		if (cancelValidationAfterRow != -1 && currentRow == cancelValidationAfterRow) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onImportProgress(int currentRow, int totalRowCount) {
		this.currentRow = currentRow;
		this.totalRowCount = totalRowCount;
		if (cancelImportAfterRow != -1 && currentRow == cancelImportAfterRow) {
			return false;
		}
		return true;
	}

	@Override
	public void onImportWarning(DataImporterWarning warning, int currentRow) {
		this.warning = warning;
		this.warningOrErrorRow = currentRow;
	}

	@Override
	public void onImportError(DataImporterError error, int currentRow) {
		this.error = error;
		this.warningOrErrorRow = currentRow;
	}
}