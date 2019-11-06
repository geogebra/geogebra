package org.geogebra.common.kernel;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.gui.dialog.ToolCreationDialogModel;
import org.geogebra.common.gui.dialog.ToolInputOutputListener;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Before;
import org.junit.Test;

public class MacroTest {
	private AppCommon3D app;
	private AlgebraProcessor ap;
	private String syntax;

	@Before
	public void setupApp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		ap = app.getKernel().getAlgebraProcessor();
	}

	private void t(String input, String expected) {
		AlgebraTestHelper.testSyntaxSingle(input, new String[] { expected }, ap,
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
