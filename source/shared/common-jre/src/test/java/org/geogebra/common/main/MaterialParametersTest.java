package org.geogebra.common.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MaterialParametersTest {
	private AppCommon3D app;
	private MaterialParameters parameters;
	private GuiManagerMock gui;

	@BeforeEach
	void setUp() {
		app = AppCommonFactory.create3D();
		gui = new GuiManagerMock(app);
		parameters = new MaterialParameters(app, gui);
	}

	@Test
	void testViewsDictionaryNoGui() {
		MaterialParameters parametersNoGui = new MaterialParameters(app, null);
		assertEquals("{'is3D': 0, 'macro': 0}",
				parametersNoGui.viewsToDictionary());
	}

	@Test
	void testViewsDictionary() {
		gui.withAlgebraView()
				.withCasView()
				.withConstructionProtocol();
		parameters.update();
		assertEquals("{'is3D': 0, 'AV': 1, 'CP': 1, 'CV': 1, 'DA': 0, 'EV2': 0, "
						+ "'FI': 0, 'PC': 0, 'SV': 0, 'PV': 0, 'macro': 0}",
				parameters.viewsToDictionary());
	}

	@Test
	void testViewsSpreadsheetAndDaDictionary() {
		gui.withAlgebraView()
				.withSpreadsheetView()
				.withDataAnalysisView();
		parameters.update();
		assertEquals("{'is3D': 0, 'AV': 1, 'CP': 0, 'CV': 0, 'DA': 1, 'EV2': 0, "
						+ "'FI': 0, 'PC': 0, 'SV': 1, 'PV': 0, 'macro': 0}",
				parameters.viewsToDictionary());
	}

	@Test
	void testViewsJSON() {
		gui.withAlgebraView()
				.withCasView()
				.withConstructionProtocol();
		parameters.update();
		assertEquals("{\"is3D\":false,\"AV\":true,\"CP\":true,\"CV\":true,\"DA\":false,"
						+ "\"EV2\":false,\"FI\":false,\"PC\":false,\"SV\":false,"
						+ "\"PV\":false,\"macro\":false}",
				parameters.viewsToJSON().toString());
	}

	@Test
	void testSettingsToHtml() {
		assertEquals(
				"\"enableLabelDrags\": false,\n\"enableRightClick\": false,\n"
						+ "\"enableUndoRedo\": false,\n\"enableShiftDragZoom\": true,\n"
						+ "\"allowStyleBar\": false,\n\"showZoomButtons\": false,\n"
						+ "\"showAlgebraInput\": true,\n\"showMenuBar\": true,\n"
						+ "\"showResetIcon\": false,\n\"showToolBar\": true,\n"
						+ "\"customToolBar\": \"\",\n\"showToolBarHelp\": false,\n"
						+ "\"height\" : 0.0,\n\"width\" : 0.0,\n\"scale\" : 1",
				parameters.settingsToHtml());
	}

	@Test
	void testSettingsToJSON() {
		assertEquals(
				"{\"enableLabelDrags\":false,\"enableRightClick\":false,"
						+ "\"enableUndoRedo\":false,\"enableShiftDragZoom\":true,"
						+ "\"allowStyleBar\":false,\"showZoomButtons\":false,"
						+ "\"showAlgebraInput\":true,\"showMenuBar\":true,"
						+ "\"showResetIcon\":false,\"showToolBar\":true,\"height\":0,\"width\""
						+ ":0,\"appName\":\"classic\"}",
				parameters.settingsToJSON().toString());
	}
}
