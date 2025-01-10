package org.geogebra.common.gui.view.table;

import static org.mockito.Mockito.when;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.mockito.Mockito;

public class MockedTableValuesUnitTest extends BaseUnitTest {

	protected TableValues view;
	protected TableValuesModel model;

	@Before
	public void createMockedObjects() {
		view = Mockito.mock(TableValues.class);
		model = Mockito.mock(TableValuesModel.class);
	}

	protected void mockModelCell(int row, int column, String value) {
		when(model.getCellAt(row, column)).thenReturn(new TableValuesCell(value, false));
	}

	protected void mockModelValue(int row, int column, double value) {
		when(model.getValueAt(row, column)).thenReturn(value);
	}

	protected void mockRowCount(int row) {
		when(model.getRowCount()).thenReturn(row);
	}

	protected void mockColumnCount(int column) {
		when(model.getColumnCount()).thenReturn(column);
	}

}
