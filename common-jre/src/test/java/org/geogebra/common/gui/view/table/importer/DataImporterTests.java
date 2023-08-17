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
	private DataImporter dataImporter;
	private int currentRow;
	private int totalNrOfRows;
	private DataImporterError error;
	private DataImporterWarning warning;
	private int warningOrErrorRow;

	//	@Before
	@Override
	public void setup() {
		super.setup();

		Kernel kernel = getKernel();
		tableValuesView = new TableValuesView(kernel);
		kernel.attach(tableValuesView);
		TableValuesModel model = tableValuesView.getTableValuesModel();
		/*TableValuesPoints tableValuesPoints =*/ TableValuesPointsImpl.create(kernel.getConstruction(),
				tableValuesView, model);
		kernel.notifyAddAll(tableValuesView);

		dataImporter = new DataImporter(tableValuesView);
		dataImporter.delegate = this;
		currentRow = -1;
		totalNrOfRows = -1;
		error = null;
		warning = null;
		warningOrErrorRow = -1;
	}

	@Test
	public void testImportCSVHeader() {
		Reader reader = loadSample("integers-comma-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow); // header row should be discarded
		assertEquals(10, totalNrOfRows);
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testImportCSVNoHeader() {
		Reader reader = loadSample("integers-comma-noheader.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalNrOfRows);
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testImportCSVWithInconsistentSeparator() {
		Reader reader = loadSample("inconsistent-separator.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertFalse(success);
		assertNotNull(error);
		assertEquals(3, warningOrErrorRow);
		assertEquals(3, currentRow);
		assertEquals(-1, totalNrOfRows);
	}

	@Test
	public void testImportCSVWithCorrectDecimalSeparator1() {
		Reader reader = loadSample("dotdecimals-comma-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalNrOfRows);
		assertEquals(1.1, tableValuesView.getTableValuesModel().getValueAt(0,1), 0.0);
		assertEquals(2.2, tableValuesView.getTableValuesModel().getValueAt(1,1), 0.0);
		assertEquals(3.3, tableValuesView.getTableValuesModel().getValueAt(2,1), 0.0);
	}

	@Test
	public void testImportCSVWithCorrectDecimalSeparator2() {
		Reader reader = loadSample("dotdecimals-semicolon-header.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalNrOfRows);
		assertEquals(1.1, tableValuesView.getTableValuesModel().getValueAt(0,1), 0.0);
		assertEquals(2.2, tableValuesView.getTableValuesModel().getValueAt(1,1), 0.0);
		assertEquals(3.3, tableValuesView.getTableValuesModel().getValueAt(2,1), 0.0);
	}

	@Test
	public void testImportCSVWithWrongSeparator1() {
		Reader reader = loadSample("dotdecimals-semicolon-header.csv"); // e.g. "1.1"
		// using wrong decimal separator, but (default) dot separator should still give correct results
		boolean success = dataImporter.importCSV(reader, ',');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalNrOfRows);
		assertEquals(1.1, tableValuesView.getTableValuesModel().getValueAt(0,1), 0.0);
		assertEquals(2.2, tableValuesView.getTableValuesModel().getValueAt(1,1), 0.0);
		assertEquals(3.3, tableValuesView.getTableValuesModel().getValueAt(2,1), 0.0);
	}

	@Test
	public void testImportCSVWithWrongSeparator2() {
		Reader reader = loadSample("commadecimals-semicolon-header.csv"); // e.g. "1,1"
		// using wrong decimal separator, decimal parsing should fail
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, currentRow);
		assertEquals(10, totalNrOfRows);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(0,1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(1,1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(2,1), 0.0);
	}

	@Test
	public void testImportCSVWithHeaderWithDataSizeLimits() {
		Reader reader = loadSample("integers-comma-header.csv");
		dataImporter.setDataSizeLimits(2, 10);
		dataImporter.setsDiscardHeader(false);
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNotNull(warning);
		assertEquals(1, currentRow); // header row is row 0
		assertEquals(2, totalNrOfRows);
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
		assertEquals(2, totalNrOfRows);
	}

	@Test
	public void testImportCSVStrings() {
		Reader reader = loadSample("strings-comma-noheader.csv");
		boolean success = dataImporter.importCSV(reader, '.');
		assertTrue(success);
		assertNull(error);
		assertNull(warning);
		assertEquals(5, currentRow);
		assertEquals(5, totalNrOfRows);
		assertEquals(5, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(0,1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(1,1), 0.0);
		assertEquals(Double.NaN, tableValuesView.getTableValuesModel().getValueAt(4,1), 0.0);
		assertEquals("a", tableValuesView.getTableValuesModel().getCellAt(0,1).getInput());
		assertEquals("b", tableValuesView.getTableValuesModel().getCellAt(1,1).getInput());
		assertEquals("e", tableValuesView.getTableValuesModel().getCellAt(4,1).getInput());
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
		return true;
	}

	@Override
	public boolean onImportProgress(int currentRow, int totalNrOfRows) {
		this.currentRow = currentRow;
		this.totalNrOfRows = totalNrOfRows;
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
