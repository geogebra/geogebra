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

package org.geogebra.common.gui.view.spreadsheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

/** Tests for {@link SpreadsheetToolProcessor}. */
public class SpreadsheetToolProcessorTest extends BaseAppTestSetup {

	private SpreadsheetToolProcessor processor;
	private CellFormat cellFormat;

	@BeforeAll
	public static void setupOnce() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
	}

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		// prepare tabular data and attach to kernel
		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(getApp());
		getKernel().attach(tabularData);
		// default format handler: can be mocked in tests that need it
		cellFormat = new CellFormat(null);
		processor = new SpreadsheetToolProcessor(getApp(), cellFormat);
		Construction construction = getKernel().getConstruction();
		tabularData.setContent(0, 0, new GeoNumeric(construction, 1));
		tabularData.setContent(1, 0, new GeoNumeric(construction, 2));
		tabularData.setContent(0, 1, new GeoNumeric(construction, 3));
		tabularData.setContent(1, 1, new GeoNumeric(construction, 4));
	}

	@Test
	public void testCreateMatrix_happyPath() {
		// put numeric values into a 2x2 block: A1..B2 corresponds to (0,0)-(1,1)

		// create matrix by reference (not by value)
		assertEquals("m1 = {{A1, B1}, {A2, B2}}",
				processor.createMatrix(0, 1, 0, 1, false)
						.getDefinitionForInputBar());
		// create matrix by value
		assertEquals("m2 = {{1, 3}, {2, 4}}",
				processor.createMatrix(0, 1, 0, 1, true)
						.getDefinitionForInputBar());
	}

	@Test
	public void testCreateMatrix_transpose() {
		assertEquals("m1 = {{A1, A2}, {B1, B2}}",
				processor.createMatrix(0, 1, 0, 1, false, true).getDefinitionForInputBar());
	}

	@Test
	public void testCreateTableText() {
		GeoElementND table = processor.createTableText(0, 1, 0, 1, false, false);
		assertEquals("TableText({{A1, B1}, {A2, B2}}, \"|_ll\")",
				table.getDefinition(StringTemplate.defaultTemplate));
	}

	@Test
	public void testCreateTableTextAligned() {
		cellFormat.setFormat(new TabularRange(0, 0),
				CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		cellFormat.setFormat(new TabularRange(0, 1),
				CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_RIGHT);
		GeoElementND table = processor.createTableText(0, 1, 0, 1, false, false);
		assertEquals("TableText({{A1, B1}, {A2, B2}}, \"|_cr\")",
				table.getDefinition(StringTemplate.defaultTemplate));
	}

	@Test
	public void testCreatePolyline() {
		ArrayList<TabularRange> ranges = new ArrayList<>();
		ranges.add(new TabularRange(0, 0, 1, 1));
		GeoElementND list = processor.createPolyLine(ranges, true, true);
		assertEquals("f = Polyline({A, B})", list.getDefinitionForInputBar());
	}

	@Test
	public void testCreatePointGeoList() {
		ArrayList<TabularRange> ranges = new ArrayList<>();
		ranges.add(new TabularRange(0, 0, 1, 1));
		GeoList list = processor.createPointGeoList(ranges, true, true, false, false);
		// label should be probably set by processor, out of scope for APPS-7254
		list.setLabel(null);
		assertEquals("l1 = {(1, 3), (2, 4)}", list.getDefinitionForInputBar());
	}

	@Test
	public void testCreatePointGeoListWithNamedPoints() {
		ArrayList<TabularRange> ranges = new ArrayList<>();
		ranges.add(new TabularRange(0, 0, 1, 1));
		GeoList list = processor.createPointGeoList(ranges, false, true, false, true);
		// label should be probably set by processor, out of scope for APPS-7254
		list.setLabel(null);
		assertEquals("l1 = {A, B}", list.getDefinitionForInputBar());
	}

	@Test
	public void testCreateMatrix_nullWhenCellUndefined() {
		// if there is no content, the range should be null
		lookup("A1").remove();
		assertNull(processor.createMatrix(0, 0, 0, 0, true));
	}

	@Test
	public void testCreateOperationTable() {
		evaluate("A1(x,y)=x+y");
		lookup("B2").remove();
		processor.createOperationTable(new TabularRange(0, 0, 1, 1));
		assertEquals("B2 = A1(A2, B1)", lookup("B2").getDefinitionForInputBar());
	}
}
