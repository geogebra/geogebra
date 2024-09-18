package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.*;
import static org.geogebra.common.contextmenu.ContextMenuFactory.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.test.TestErrorHandler;
import org.junit.Test;

public class AlgebraContextMenuTests {
	private AlgebraProcessor algebraProcessor;
	private String appCode;

	@Test
	public void testAlgebraContextMenuWithInvalidGeoElement() {
		setupApp(GeoGebraConstants.GRAPHING_APPCODE);
		assertEquals(
				List.of(Delete),
				makeAlgebraContextMenu(null, algebraProcessor, appCode)
		);
	}

	// Geometry app

	@Test
	public void testForDefaultAlgebraInputInGeometryApp() {
		setupApp(GeoGebraConstants.GEOMETRY_APPCODE);
		assertEquals(
				List.of(DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("x"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithStatisticsInGeometryApp() {
		setupApp(GeoGebraConstants.GEOMETRY_APPCODE);
		assertEquals(
				List.of(Statistics,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("{1, 2, 3}"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithOutputInGeometryApp() {
		setupApp(GeoGebraConstants.GEOMETRY_APPCODE);
		assertEquals(
				List.of(DuplicateInput,
						DuplicateOutput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("1 + 2"), algebraProcessor, appCode)
		);
	}

	// Scientific app

	@Test
	public void testForInputWithNoLabelInScientificApp() {
		setupApp(GeoGebraConstants.SCIENTIFIC_APPCODE);
		GeoElement geoElement = add("5");
		geoElement.setAlgebraLabelVisible(false);
		assertEquals(
				List.of(AddLabel,
						DuplicateInput,
						Delete),
				makeAlgebraContextMenu(geoElement, algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithLabelInScientificApp() {
		setupApp(GeoGebraConstants.SCIENTIFIC_APPCODE);
		GeoElement geoElement = add("5");
		geoElement.setAlgebraLabelVisible(true);
		assertEquals(
				List.of(RemoveLabel,
						DuplicateInput,
						Delete),
				makeAlgebraContextMenu(geoElement, algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithDuplicateOutputInScientificApp() {
		setupApp(GeoGebraConstants.SCIENTIFIC_APPCODE);
		GeoElement geoElement = add("1 + 2");
		assertEquals(
				List.of(RemoveLabel,
						DuplicateInput,
						DuplicateOutput,
						Delete),
				makeAlgebraContextMenu(geoElement, algebraProcessor, appCode)
		);
	}

	// Graphing app

	@Test
	public void testForInputWithSpecialPointsAndTableValuesInGraphingApp() {
		setupApp(GeoGebraConstants.GRAPHING_APPCODE);
		assertEquals(
				List.of(CreateTableValues,
						SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("x"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithStatisticsInGraphingApp() {
		setupApp(GeoGebraConstants.GRAPHING_APPCODE);
		assertEquals(
				List.of(CreateTableValues,
						Statistics,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("{1, 2, 3}"), algebraProcessor, appCode)
		);
	}

	// Graphing 3D app

	@Test
	public void testForInputWithSolutionInGraphing3DApp() {
		setupApp(GeoGebraConstants.G3D_APPCODE);
		assertEquals(
				List.of(Solve,
						DuplicateInput,
						DuplicateOutput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("x^(2) - 5x + 6 = 0"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithSpecialPointsInGraphing3DApp() {
		setupApp(GeoGebraConstants.G3D_APPCODE);
		assertEquals(
				List.of(SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("x"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithStatisticsInGraphing3DApp() {
		setupApp(GeoGebraConstants.G3D_APPCODE);
		assertEquals(
				List.of(Statistics,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("{1, 2, 3}"), algebraProcessor, appCode)
		);
	}

	// CAS app

	@Test
	public void testForSimpleInputWithSliderAndLabelInCasApp() {
		setupApp(GeoGebraConstants.CAS_APPCODE);
		assertEquals(
				List.of(RemoveLabel,
						RemoveSlider,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("5"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForSimpleInputWithoutLabelInCasApp() {
		setupApp(GeoGebraConstants.CAS_APPCODE);
		GeoElement geoElement = add("5");
		new LabelController().hideLabel(geoElement);
		assertEquals(
				List.of(AddLabel,
						RemoveSlider,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(geoElement, algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithStatisticsInCasApp() {
		setupApp(GeoGebraConstants.CAS_APPCODE);
		assertEquals(
				List.of(CreateTableValues,
						RemoveLabel,
						Statistics,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("{1, 2, 3}"), algebraProcessor, appCode)
		);
	}

	@Test
	public void testForInputWithSpecialPointsInCasApp() {
		setupApp(GeoGebraConstants.CAS_APPCODE);
		assertEquals(
				List.of(CreateTableValues,
						RemoveLabel,
						SpecialPoints,
						DuplicateInput,
						Delete,
						Settings),
				makeAlgebraContextMenu(add("x"), algebraProcessor, appCode)
		);
	}

	private void setupApp(String appCode) {
		this.appCode = appCode;
		App app = AppCommonFactory.create(makeAppConfig(appCode));
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		app.getSettingsUpdater().resetSettingsOnAppStart();

	}

	private AppConfig makeAppConfig(String appCode) {
		switch (appCode) {
		case GeoGebraConstants.CAS_APPCODE: return new AppConfigCas();
		case GeoGebraConstants.G3D_APPCODE: return new AppConfigGraphing3D();
		case GeoGebraConstants.GEOMETRY_APPCODE: return new AppConfigGeometry();
		case GeoGebraConstants.SCIENTIFIC_APPCODE: return new AppConfigScientific();
		default: return new AppConfigGraphing();
		}
	}

	private GeoElement add(String command) {
		GeoElementND[] geoElements = algebraProcessor.processAlgebraCommandNoExceptionHandling(
				command, false, TestErrorHandler.INSTANCE, false, null);
		return (GeoElement) geoElements[0];
	}
}
