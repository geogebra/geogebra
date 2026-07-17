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

package org.geogebra.common.spreadsheet.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KernelTabularDataProcessorTest extends BaseUnitTest {

	private KernelTabularDataProcessor processor;
	private KernelTabularDataAdapter adapter;
	private GeoElement geo1;
	private GeoElement geo2;
	private GeoElement geo3;
	private GeoElement geo4;
	private GeoElement bottomRight;

	@BeforeEach
	void setUp() throws Exception {
		adapter = new KernelTabularDataAdapter(getApp());
		getKernel().attach(adapter);
		geo1 = add("1");
		adapter.setContent(0, 0, geo1);
		geo2 = add("2");
		adapter.setContent(0, 1, geo2);
		geo3 = add("Text(\"foo\")");
		adapter.setContent(1, 0, geo3);
		geo4 = add("Button()");
		adapter.setContent(1, 1, geo4);
		bottomRight = add("3");
		adapter.setContent(99, 25, bottomRight);
		processor = new KernelTabularDataProcessor(adapter);
	}

	@Test
	void testInsertRowAtBeginning() {
		processor.insertRowAt(0);
		cellShouldBeEmpty(0, 0);
		cellShouldBeEmpty(0, 1);
		contentShouldBe(1, 0, geo1, "A2");
		contentShouldBe(1, 1, geo2, "B2");
		contentShouldBe(2, 0, geo3, "A3");
		contentShouldBe(2, 1, geo4, "B3");
		contentShouldBe(100, 25, bottomRight, "Z101");
	}

	private void cellShouldBeEmpty(int row, int column) {
		assertNull(adapter.contentAt(row, column));
	}

	private void contentShouldBe(int row, int column, GeoElementND expectedContent,
			String expectedLabel) {
		GeoElement actualContent = adapter.contentAt(row, column);
		assertEquals(expectedContent, actualContent);

		if (expectedContent != null) {
			assertEquals(expectedLabel,
					Objects.requireNonNull(actualContent).getLabelSimple());
		}
	}

	@Test
	void testInsertRowAtMiddle() {
		processor.insertRowAt(1);
		contentShouldBe(0, 0, geo1, "A1");
		contentShouldBe(0, 1, geo2, "B1");
		cellShouldBeEmpty(1, 0);
		cellShouldBeEmpty(1, 1);
		contentShouldBe(2, 0, geo3, "A3");
		contentShouldBe(2, 1, geo4, "B3");
	}

	@Test
	void testInsertRowAtEnd() {
		processor.insertRowAt(2);
		contentShouldBe(0, 0, geo1, "A1");
		contentShouldBe(0, 1, geo2, "B1");
		contentShouldBe(1, 0, geo3, "A2");
		contentShouldBe(1, 1, geo4, "B2");
		cellShouldBeEmpty(2, 0);
		cellShouldBeEmpty(2, 1);
	}

	@Test
	void testDeleteFirstRow() {
		adapter.deleteRowAt(0);
		contentShouldBe(0, 0, geo3, "A1");
		contentShouldBe(0, 1, geo4, "B1");
	}

	@Test
	void testDeleteSecondRow() {
		adapter.deleteRowAt(1);
		contentShouldBe(0, 0, geo1, "A1");
		contentShouldBe(0, 1, geo2, "B1");
		cellShouldBeEmpty(1, 0);
		cellShouldBeEmpty(1, 1);
	}

	@Test
	void testInsertColumnAtBeginning() {
		processor.insertColumnAt(0);
		cellShouldBeEmpty(0, 0);
		cellShouldBeEmpty(1, 0);
		contentShouldBe(0, 1, geo1, "B1");
		contentShouldBe(0, 2, geo2, "C1");
		contentShouldBe(1, 1, geo3, "B2");
		contentShouldBe(1, 2, geo4, "C2");
		contentShouldBe(99, 26, bottomRight, "AA100");
	}

	@Test
	void testInsertColumnAtMiddle() {
		processor.insertColumnAt(1);
		contentShouldBe(0, 0, geo1, "A1");
		contentShouldBe(0, 2, geo2, "C1");
		cellShouldBeEmpty(0, 1);
		cellShouldBeEmpty(1, 1);
		contentShouldBe(1, 0, geo3, "A2");
		contentShouldBe(1, 2, geo4, "C2");
	}

	@Test
	void testInsertColumnAtEnd() {
		processor.insertColumnAt(2);
		contentShouldBe(0, 0, geo1, "A1");
		contentShouldBe(0, 1, geo2, "B1");
		contentShouldBe(1, 0, geo3, "A2");
		contentShouldBe(1, 1, geo4, "B2");
		cellShouldBeEmpty(0, 2);
		cellShouldBeEmpty(1, 2);
	}

	@Test
	void testDeleteFirstColumn() {
		processor.deleteColumnAt(0);
		contentShouldBe(0, 0, geo2, "A1");
		contentShouldBe(1, 0, geo4, "A2");
	}

	@Test
	void testDeleteSecondColumn() {
		processor.deleteColumnAt(1);
		contentShouldBe(0, 0, geo1, "A1");
		contentShouldBe(1, 0, geo3, "A2");
		cellShouldBeEmpty(0, 1);
		cellShouldBeEmpty(1, 1);
	}
}
