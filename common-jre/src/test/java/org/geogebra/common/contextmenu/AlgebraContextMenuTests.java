package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.*;
import static org.geogebra.common.contextmenu.ContextMenu.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class AlgebraContextMenuTests extends BaseUnitTest {
	private AlgebraProcessor algebraProcessor;

	@Override
	public void setup() {
		super.setup();
		algebraProcessor = getKernel().getAlgebraProcessor();
		getApp().getSettingsUpdater().resetSettingsOnAppStart();
	}

	@Test
	public void testAlgebraContextMenuWithInvalidGeoElement() {
		assertEquals(
				List.of(Delete),
				makeAlgebraContextMenu(null, algebraProcessor,
						GeoGebraConstants.GRAPHING_APPCODE, items -> {}).getItems()
		);
	}

	// Geometry app

	@Test
	public void testForDefaultAlgebraInputInGeometryApp() {
		assertEquals(
				List.of(DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("x"), algebraProcessor,
						GeoGebraConstants.GEOMETRY_APPCODE, items -> {}).getItems()
		);
	}

	@Test
	public void testForInputWithStatisticsInGeometryApp() {
		assertEquals(
				List.of(Statistics, DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("{1, 2, 3}"), algebraProcessor,
						GeoGebraConstants.GEOMETRY_APPCODE, items -> {}).getItems()
		);
	}

	@Test
	public void testForInputWithOutputInGeometryApp() {
		assertEquals(
				List.of(DuplicateInput, DuplicateOutput, Delete, Settings),
				makeAlgebraContextMenu(add("1 + 2"), algebraProcessor,
						GeoGebraConstants.GEOMETRY_APPCODE, items -> {}).getItems()
		);
	}

	// Scientific app

	@Test
	public void testForInputWithNoLabelInScientificApp() {
		GeoElement geoElement = add("5");
		geoElement.setAlgebraLabelVisible(false);
		assertEquals(
				List.of(AddLabel, DuplicateInput, Delete),
				makeAlgebraContextMenu(geoElement, algebraProcessor,
						GeoGebraConstants.SCIENTIFIC_APPCODE, items -> {}).getItems()
		);
	}

	@Test
	public void testForInputWithLabelInScientificApp() {
		GeoElement geoElement = add("5");
		geoElement.setAlgebraLabelVisible(true);
		assertEquals(
				List.of(RemoveLabel, DuplicateInput, Delete),
				makeAlgebraContextMenu(geoElement, algebraProcessor,
						GeoGebraConstants.SCIENTIFIC_APPCODE, items -> {}).getItems()
		);
	}

	// Graphing app

	@Test
	public void testForInputWithSpecialPointsAndTableValuesInGraphingApp() {
		assertEquals(
				List.of(CreateTableValues, SpecialPoints, DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("x"), algebraProcessor,
						GeoGebraConstants.GRAPHING_APPCODE, items -> {}).getItems()
		);
	}

	@Test
	public void testForInputWithStatisticsInGraphingApp() {
		assertEquals(
				List.of(CreateTableValues, Statistics, DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("{1, 2, 3}"), algebraProcessor,
						GeoGebraConstants.GRAPHING_APPCODE, items -> {}).getItems()
		);
	}

	// Graphing 3D app

	@Test
	public void testForInputWithSolutionInGraphing3DApp() {
		assertEquals(
				List.of(Solve, DuplicateInput, DuplicateOutput, Delete, Settings),
				makeAlgebraContextMenu(add("x^(2) - 5x + 6 = 0"), algebraProcessor,
						GeoGebraConstants.G3D_APPCODE, items -> {}).getItems()
		);
	}

	// CAS app

	// TODO setup?
	@Test
	public void testForSimpleInputInCasApp() {
		assertEquals(
				List.of(AddLabel, CreateSlider, DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("5"), algebraProcessor,
						GeoGebraConstants.CAS_APPCODE, items -> {}).getItems()
		);
	}

	@Test
	public void testForSimpleInputWithSliderAndLabel() {
		assertEquals(
				List.of(RemoveLabel, RemoveSlider, DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("5"), algebraProcessor,
						GeoGebraConstants.CAS_APPCODE, items -> {}).getItems()
		);
	}

	@Test
	public void testForSimpleInputWithLabel() {
		assertEquals(
				List.of(RemoveLabel, CreateSlider, DuplicateInput, Delete, Settings),
				makeAlgebraContextMenu(add("5"), algebraProcessor,
						GeoGebraConstants.CAS_APPCODE, items -> {}).getItems()
		);
	}
}
