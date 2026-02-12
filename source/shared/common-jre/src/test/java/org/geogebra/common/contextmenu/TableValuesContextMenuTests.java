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

package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.ClearColumn;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Edit;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.HidePoints;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.ImportData;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.RemoveColumn;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Separator;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.ShowPoints;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics1;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics2;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.util.AttributedString;
import org.geogebra.common.util.Range;
import org.junit.Before;
import org.junit.Test;

public class TableValuesContextMenuTests extends BaseUnitTest {

	private final Set<ContextMenuItemFilter> filters = Set.of();

	private TableValuesView tableValuesView;
	private TableValuesModel tableValuesModel;

	@Before
	@Override
	public void setup() {
		super.setup();
		tableValuesView = new TableValuesView(getKernel());
		getKernel().attach(tableValuesView);
		tableValuesModel = tableValuesView.getTableValuesModel();
	}

	@Test
	public void testInScientificCalculator() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		assertEquals(
				List.of(Edit.toContextMenuItem(),
						ClearColumn.toContextMenuItem()),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, true, false, Set.of())
		);
	}

	@Test
	public void testFirstColumn() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		assertEquals(
				List.of(Edit.toContextMenuItem(),
						ClearColumn.toContextMenuItem(),
						ImportData.toContextMenuItem(),
						Separator.toContextMenuItem(),
						Statistics1.toContextMenuItem(new String[]{ "x" })),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false, false, Set.of())
		);
	}

	@Test
	public void testFirstColumnInExamMode() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		assertEquals(
				List.of(Edit.toContextMenuItem(),
						ClearColumn.toContextMenuItem(),
						Separator.toContextMenuItem(),
						Statistics1.toContextMenuItem(new String[]{ "x" })),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false, true, Set.of())
		);
	}

	@Test
	public void testFirstColumnInExamModeWithRestrictedStatisticsItem() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		Set<ContextMenuItemFilter> filters = Set.of(contextMenuItem ->
				!contextMenuItem.equals(Statistics1.toContextMenuItem(new String[]{ "x" })));

		assertEquals(
				List.of(Edit.toContextMenuItem(),
						ClearColumn.toContextMenuItem(),
						ImportData.toContextMenuItem()),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false, false, filters)
		);
	}

	@Test
	public void testInExamModeWithRestrictedStatisticsAndRegressionItems()
			throws InvalidValuesException {
		tableValuesView.setValues(0.0, 2.0, 1.0);
		GeoList geoList = new GeoList(getConstruction());
		geoList.add(new GeoNumeric(getConstruction(), 1.0));
		geoList.add(new GeoNumeric(getConstruction(), 2.0));
		tableValuesView.addAndShow(geoList);

		Set<ContextMenuItemFilter> filters = Set.of(
				contextMenuItem -> !List.of(
						Statistics1.toContextMenuItem(new String[]{"y_{1}"}),
						Statistics2.toContextMenuItem(new String[]{"x y_{1}"}),
						Regression.toContextMenuItem()
				).contains(contextMenuItem));

		assertEquals(
				List.of(HidePoints.toContextMenuItem(),
						RemoveColumn.toContextMenuItem()),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoList, 1, tableValuesModel, false, false, filters)
		);
	}

	@Test
	public void testWithFunctionColumn() throws InvalidValuesException {
		tableValuesView.setValues(-2.0, 2.0, 1.0);
		GeoFunction geoFunction = new GeoFunction(getConstruction());
		tableValuesView.addAndShow(geoFunction);

		assertEquals(
				List.of(HidePoints.toContextMenuItem(),
						Edit.toContextMenuItem(),
						RemoveColumn.toContextMenuItem()),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoFunction, 1, tableValuesModel, false, false, Set.of())
		);
	}

	@Test
	public void testWithVisibleGeoListPoints() throws InvalidValuesException {
		tableValuesView.setValues(0.0, 2.0, 1.0);
		GeoList geoList = new GeoList(getConstruction());
		geoList.add(new GeoNumeric(getConstruction(), 1.0));
		geoList.add(new GeoNumeric(getConstruction(), 2.0));
		tableValuesView.addAndShow(geoList);

		assertEquals(
				List.of(HidePoints.toContextMenuItem(),
						RemoveColumn.toContextMenuItem(),
						Separator.toContextMenuItem(),
						Statistics1.toContextMenuItem(new String[]{ "y_{1}" }),
						Statistics2.toContextMenuItem(new String[] { "x y_{1}" }),
						Regression.toContextMenuItem()),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoList, 1, tableValuesModel, false, false, Set.of())
		);
	}

	@Test
	public void testWithHiddenGeoListPoints() throws InvalidValuesException {
		tableValuesView.setValues(0.0, 2.0, 1.0);
		GeoList geoList = new GeoList(getConstruction());
		geoList.add(new GeoNumeric(getConstruction(), 1.0));
		geoList.add(new GeoNumeric(getConstruction(), 2.0));
		tableValuesView.addAndShow(geoList);
		geoList.setPointsVisible(false);

		assertEquals(
				List.of(ShowPoints.toContextMenuItem(),
						RemoveColumn.toContextMenuItem(),
						Separator.toContextMenuItem(),
						Statistics1.toContextMenuItem(new String[]{ "y_{1}" }),
						Statistics2.toContextMenuItem(new String[]{ "x y_{1}" }),
						Regression.toContextMenuItem()),
				ContextMenuFactory.makeTableValuesContextMenu(
						geoList, 1, tableValuesModel, false, false, Set.of())
		);
	}

	@Test
	public void testStatisticsItemTitleSubscript() {
		TableValuesContextMenuItem item = Statistics1.toContextMenuItem(new String[]{ "y_{1}" });
		AttributedString title = item.getLocalizedTitle(getLocalization());

		assertEquals("y1 Statistics", title.getRawValue());
		assertEquals(
				Set.of(new Range(1, 2)),
				title.getAttribute(AttributedString.Attribute.Subscript)
		);
	}

	@Test
	public void testStatisticsItemTitleWithMultipleSubscripts() {
		TableValuesContextMenuItem item = Statistics2.toContextMenuItem(
				new String[] { "y_{1} value_{subscript}"});
		AttributedString title = item.getLocalizedTitle(getLocalization());

		assertEquals("y1 valuesubscript Statistics", title.getRawValue());
		assertEquals(
				Set.of(new Range(1, 2), new Range(8, 17)),
				title.getAttribute(AttributedString.Attribute.Subscript)
		);
	}
}
