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
	}

	@Test
	public void testValidateCSVWithHeader() {
		Reader reader = loadSample("sample-with-header.csv");
		boolean success = dataImporter.validateCSV(reader);
		assertTrue(success);
	}

	@Test
	public void testValidateCSVWithInconsistentSeparator() {
		Reader reader = loadSample("inconsistent-separator.csv");
		boolean success = dataImporter.validateCSV(reader);
		assertFalse(success);
		assertNotNull(error);
		assertEquals(3, currentRow);
	}

	@Test
	public void testValidateDataSizeLimitsInCSVWithHeader() {
		Reader reader = loadSample("sample-with-header.csv");
		dataImporter.setDataSizeLimits(2, 10);
		boolean success = dataImporter.validateCSV(reader);
		assertTrue(success);
		assertEquals(2, currentRow);
		assertNull(error);
		assertNotNull(warning);
	}

	@Test
	public void testValidateDataSizeLimitsInCSVWithoutHeader() {
		Reader reader = loadSample("sample-no-header.csv");
		dataImporter.setDataSizeLimits(2, 10);
		boolean success = dataImporter.validateCSV(reader);
		assertTrue(success);
		assertEquals(2, currentRow);
		assertNull(error);
		assertNotNull(warning);
	}

	@Test
	public void testImportBeforeValidation() {
		Reader reader = loadSample("sample-with-header.csv");
		boolean success = dataImporter.loadCSV(reader);
		assertFalse(success); // load() before import() should fail
	}

	@Test
	public void testImportCSVWithHeader() {
		Reader reader = loadSample("sample-with-header.csv");
		dataImporter.validateCSV(reader);

		reader = loadSample("sample-with-header.csv");
		boolean success = dataImporter.loadCSV(reader);
		assertTrue(success);
		assertEquals(10, currentRow); // header row should be discarded
		assertEquals(10, totalNrOfRows);
		assertNull(error);
		assertNull(warning);
		assertEquals(10, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}
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
	public void onImportWarning(DataImporterWarning warning) {
		this.warning = warning;
	}

	@Override
	public void onImportError(DataImporterError error) {
		this.error = error;
	}
}
