package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.ContextMenu.makeTableValuesContextMenu;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.junit.Before;
import org.junit.Test;

public class TableValuesContextMenuTests extends BaseUnitTest {
	private TableValuesView tableValuesView;
	private TableValuesModel tableValuesModel;

	@Before
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
				List.of(Edit, ClearColumn),
				makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, true,
						false, item -> {}).getItems()
		);
	}

	@Test
	public void testFirstColumn() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		assertEquals(
				List.of(Edit, ClearColumn, ImportData, Separator, Statistics1),
				makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false,
						false, item -> {}).getItems()
		);
	}

	@Test
	public void testFirstColumnInExamMode() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		assertEquals(
				List.of(Edit, ClearColumn, Separator, Statistics1),
				makeTableValuesContextMenu(geoEvaluatable, 0, tableValuesModel,
						false, true, item -> {}).getItems()
		);
	}

	@Test
	public void testWithFunctionColumn() throws InvalidValuesException {
		tableValuesView.setValues(-2.0, 2.0, 1.0);
		GeoFunction geoFunction = new GeoFunction(getConstruction());
		tableValuesView.addAndShow(geoFunction);

		assertEquals(
				List.of(HidePoints, Edit, RemoveColumn),
				makeTableValuesContextMenu(geoFunction, 1, tableValuesModel,
						false, false, item -> {}).getItems()
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
				List.of(HidePoints, RemoveColumn, Separator, Statistics1, Statistics2, Regression),
				makeTableValuesContextMenu(geoList, 1, tableValuesModel,
						false, false, item -> {}).getItems()
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
				List.of(ShowPoints, RemoveColumn, Separator, Statistics1, Statistics2, Regression),
				makeTableValuesContextMenu(geoList, 1, tableValuesModel,
						false, false, item -> {}).getItems()
		);
	}
}
