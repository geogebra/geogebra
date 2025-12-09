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

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.AddLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateSlider;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateOutput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveSlider;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Solve;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.SpecialPoints;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Statistics;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@SuppressWarnings("checkstyle:RegexpSinglelineCheck") // Tabs in MockedCasValues
@ExtendWith(MockedCasValuesExtension.class)
public class AlgebraContextMenuTests extends BaseAppTestSetup {
	private final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	@Test
	public void testAlgebraContextMenuWithInvalidGeoElement() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(Delete),
				contextMenuFactory.makeAlgebraContextMenu(
						null, getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	// Geometry app

	@Test
	public void testForDefaultAlgebraInputInGeometryApp() {
		setupApp(SuiteSubApp.GEOMETRY);
		assertEquals(
				List.of(SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("x"),
						getAlgebraProcessor(), GEOMETRY_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithStatisticsInGeometryApp() {
		setupApp(SuiteSubApp.GEOMETRY);
		assertEquals(
				List.of(Statistics,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						getAlgebraProcessor(), GEOMETRY_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithOutputInGeometryApp() {
		setupApp(SuiteSubApp.GEOMETRY);
		assertEquals(
				List.of(DuplicateInput,
						DuplicateOutput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("1 + 2"),
						getAlgebraProcessor(), GEOMETRY_APPCODE, getAlgebraSettings()));
	}

	// Scientific app

	@Test
	public void testForInputWithNoLabelInScientificApp() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		GeoElement geoElement = evaluateGeoElement("5");
		geoElement.setAlgebraLabelVisible(false);
		assertEquals(
				List.of(AddLabel,
						DuplicateInput,
						Delete),
				contextMenuFactory.makeAlgebraContextMenu(geoElement,
						getAlgebraProcessor(), SCIENTIFIC_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithLabelInScientificApp() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		GeoElement geoElement = evaluateGeoElement("5");
		geoElement.setAlgebraLabelVisible(true);
		assertEquals(
				List.of(RemoveLabel,
						DuplicateInput,
						Delete),
				contextMenuFactory.makeAlgebraContextMenu(geoElement,
						getAlgebraProcessor(), SCIENTIFIC_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithDuplicateOutputInScientificApp() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		GeoElement geoElement = evaluateGeoElement("1 + 2");
		assertEquals(
				List.of(RemoveLabel,
						DuplicateInput,
						DuplicateOutput,
						Delete),
				contextMenuFactory.makeAlgebraContextMenu(geoElement,
						getAlgebraProcessor(), SCIENTIFIC_APPCODE, getAlgebraSettings()));
	}

	// Graphing app

	@Test
	public void testForInputWithSpecialPointsAndTableValuesInGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(CreateTableValues,
						SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("x"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithSliderInGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(CreateSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("1"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForSliderCommandInGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(RemoveSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("Slider(-5,5,1)"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithStatisticsInGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(CreateTableValues,
						Statistics,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForPotentialSliderGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(CreateSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("1"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForSliderGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		assertEquals(
				List.of(RemoveSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("Slider(0, 5, 1)"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithOnlyEngineeringNotationOutputInGraphingApp() {
		setupApp(SuiteSubApp.GRAPHING);
		getAlgebraSettings().setEngineeringNotationEnabled(true);
		assertEquals(
				List.of(CreateSlider,
						DuplicateInput,
						DuplicateOutput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("1234567"),
						getAlgebraProcessor(), GRAPHING_APPCODE, getAlgebraSettings()));
	}

	// Graphing 3D app

	@Test
	public void testForInputWithSolutionInGraphing3DApp() {
		setupApp(SuiteSubApp.G3D);
		assertEquals(
				List.of(Solve,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("x^(2) - 5x + 6 = 0"),
						getAlgebraProcessor(), G3D_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithSpecialPointsInGraphing3DApp() {
		setupApp(SuiteSubApp.G3D);
		assertEquals(
				List.of(SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("x"),
						getAlgebraProcessor(), G3D_APPCODE, getAlgebraSettings()));
	}

	@Test
	public void testForInputWithStatisticsInGraphing3DApp() {
		setupApp(SuiteSubApp.G3D);
		assertEquals(
				List.of(Statistics,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						getAlgebraProcessor(), G3D_APPCODE, getAlgebraSettings()));
	}

	// CAS app

	@Test
	@MockedCasValues({
			"Evaluate(5) 	-> 5",
			"Round(5, 13) 	-> 5.0",
	})
	public void testForSimpleInputInCasApp() {
		setupApp(SuiteSubApp.CAS);
		assertEquals(
				List.of(AddLabel,
						CreateSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("5"),
						getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}

	@Test
	@MockedCasValues({
			"Evaluate(5) 	-> 5",
			"Round(5, 13) 	-> 5.0",
	})
	public void testForSimpleInputWithSliderInCasApp() {
		setupApp(SuiteSubApp.CAS);
		GeoElement number = evaluateGeoElement("slider=5");
		new CreateSlider(getAlgebraProcessor(), new LabelController()).execute(number);
		assertEquals(
				List.of(RemoveSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(getKernel().lookupLabel("slider"),
						getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}

	@Test
	@MockedCasValues({
			"Evaluate(5) 	-> 5",
			"Round(5, 13) 	-> 5.0",
	})
	public void testForSimpleInputWithLabelInCasApp() {
		setupApp(SuiteSubApp.CAS);
		GeoElement geoElement = evaluateGeoElement("5");
		new LabelController().showLabel(geoElement);
		assertEquals(
				List.of(RemoveLabel,
						CreateSlider,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						geoElement, getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}

	@Test
	@MockedCasValues({"Evaluate({1, 2, 3}) -> {1,2,3}"})
	public void testForInputWithStatisticsInCasApp() {
		setupApp(SuiteSubApp.CAS);
		assertEquals(
				List.of(CreateTableValues,
						AddLabel,
						Statistics,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}

	@Test
	@MockedCasValues({"Evaluate(x) -> x"})
	public void testForInputWithSpecialPointsInCasApp() {
		setupApp(SuiteSubApp.CAS);
		assertEquals(
				List.of(CreateTableValues,
						AddLabel,
						SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("x"),
						getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}
}
