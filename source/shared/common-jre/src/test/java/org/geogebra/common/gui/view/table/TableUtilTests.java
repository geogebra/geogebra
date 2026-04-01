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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.util.AttributedString;
import org.geogebra.common.util.Range;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class TableUtilTests extends BaseAppTestSetup {
	@Test
	void getColumnHeaderForXColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}");
		AttributedString columnHeader = TableUtil.getColumnHeader(
				tableValues.getTableValuesModel(), 0);
		assertEquals("x", columnHeader.getRawValue());
		assertTrue(columnHeader.getAttribute(AttributedString.Attribute.Subscript).isEmpty());
	}

	@Test
	void getColumnHeaderForY1Column() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		AttributedString columnHeader = TableUtil.getColumnHeader(
				tableValues.getTableValuesModel(), 1);
		assertEquals("y1", columnHeader.getRawValue());
		assertEquals(Set.of(new Range(1, 2)),
				columnHeader.getAttribute(AttributedString.Attribute.Subscript));
	}

	@Test
	void getLabeledColumnHeaderWithOneVariableWithoutSubscript() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}");
		AttributedString columnHeader = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), 0, false, getApp().getLocalization());
		assertEquals("Column x", columnHeader.getRawValue());
		assertTrue(columnHeader.getAttribute(AttributedString.Attribute.Subscript).isEmpty());
	}

	@Test
	void getLabeledColumnHeaderWithOneVariableAndSubscript() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		AttributedString columnHeader = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), 1, false, getApp().getLocalization());
		assertEquals("Column y1", columnHeader.getRawValue());
		assertEquals(Set.of(new Range(8, 9)),
				columnHeader.getAttribute(AttributedString.Attribute.Subscript));
	}

	@Test
	void getLabeledColumnHeaderWithTwoVariablesAndSubscript() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		AttributedString columnHeader = TableUtil.getLabeledColumnHeader(
				tableValues.getTableValuesModel(), 1, true, getApp().getLocalization());
		assertEquals("Column x y1", columnHeader.getRawValue());
		assertEquals(Set.of(new Range(10, 11)),
				columnHeader.getAttribute(AttributedString.Attribute.Subscript));
	}

	@Test
	void testToHtmlWithSingleAttribute() {
		AttributedString attributedString = new AttributedString("Column x y10");
		attributedString.add(AttributedString.Attribute.Subscript, new Range(10, 12));
		assertEquals("Column x y<sub>10</sub>", TableUtil.toHtml(attributedString));
	}

	@Test
	void testToHtmlWithMultipleAttributes() {
		AttributedString attributedString = new AttributedString("6CO2 + 6H2O → C6H12O6 + 6O2");
		attributedString.add(AttributedString.Attribute.Subscript, new Range(3, 4));
		attributedString.add(AttributedString.Attribute.Subscript, new Range(9, 10));
		attributedString.add(AttributedString.Attribute.Subscript, new Range(15, 16));
		attributedString.add(AttributedString.Attribute.Subscript, new Range(17, 19));
		attributedString.add(AttributedString.Attribute.Subscript, new Range(20, 21));
		attributedString.add(AttributedString.Attribute.Subscript, new Range(26, 27));
		assertEquals("6CO<sub>2</sub> + 6H<sub>2</sub>O → C<sub>6</sub>H<sub>12</sub>O<sub>6</sub>"
						+ " + 6O<sub>2</sub>", TableUtil.toHtml(attributedString));
	}
}
