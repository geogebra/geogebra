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
