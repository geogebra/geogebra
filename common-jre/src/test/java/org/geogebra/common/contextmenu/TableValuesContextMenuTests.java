package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.*;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

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
	private final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

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
				List.of(Edit.toContextMenuItem(),
						ClearColumn.toContextMenuItem()),
				contextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, true, false)
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
				contextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false, false)
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
				contextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false, true)
		);
	}

	@Test
	public void testFirstColumnInExamModeWithRestrictedStatisticsItem() {
		GeoEvaluatable geoEvaluatable = new GeoLine(getConstruction());

		contextMenuFactory.addFilter(contextMenuItem ->
				!contextMenuItem.equals(Statistics1.toContextMenuItem(new String[]{ "x" })));

		assertEquals(
				List.of(Edit.toContextMenuItem(),
						ClearColumn.toContextMenuItem(),
						ImportData.toContextMenuItem()),
				contextMenuFactory.makeTableValuesContextMenu(
						geoEvaluatable, 0, tableValuesModel, false, false)
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

		contextMenuFactory.addFilter(contextMenuItem -> !List.of(
			Statistics1.toContextMenuItem(new String[]{ "y_{1}" }),
			Statistics2.toContextMenuItem(new String[]{ "x y_{1}" }),
			Regression.toContextMenuItem()
		).contains(contextMenuItem));

		assertEquals(
				List.of(HidePoints.toContextMenuItem(),
						RemoveColumn.toContextMenuItem()),
				contextMenuFactory.makeTableValuesContextMenu(
						geoList, 1, tableValuesModel, false, false)
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
				contextMenuFactory.makeTableValuesContextMenu(
						geoFunction, 1, tableValuesModel, false, false)
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
				contextMenuFactory.makeTableValuesContextMenu(
						geoList, 1, tableValuesModel, false, false)
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
				contextMenuFactory.makeTableValuesContextMenu(
						geoList, 1, tableValuesModel, false, false)
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
