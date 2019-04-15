package org.geogebra.common.kernel.commands;

import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.gui.dialog.ToolInputOutputListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.BeforeClass;
import org.junit.Test;

public class MacroTest {
	static AppDNoGui app;
	static AlgebraProcessor ap;
	private static String syntax;

	@BeforeClass
	public static void setupApp() {
		app = CommandsTest.createApp();
		ap = app.getKernel().getAlgebraProcessor();
	}

	private static void t(String input, String expected) {
		CommandsTest.testSyntaxSingle(input, new String[] { expected }, ap,
				StringTemplate.xmlTemplate);
	}

	@Test
	public void lineMacro() {
		t("A=(1,1)", "(1, 1)");
		t("B=(2,2)", "(2, 2)");
		t("f=Line(A,B)", "-x + y = 0");
		ToolCreationDialogModel macroBuilder = new ToolCreationDialogModel(app,
				new ToolInputOutputListener() {

					@Override
					public void updateLists() {
						// no UI to update
					}
				});
		macroBuilder.addToInput(get("A"));
		macroBuilder.addToInput(get("B"));
		macroBuilder.addToOutput(get("f"));
		macroBuilder.createTool();
		macroBuilder.finish(app, "TestLine", "TestLine", "two points", false,
				null);
		t("g=TestLine((1,3),(2,3))", "y = 3");
	}

	private GeoElement get(String string) {
		return app.getKernel().lookupLabel(string);
	}
}
